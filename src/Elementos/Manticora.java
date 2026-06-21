package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.Constantes.GetEnemyDamage;
import static utilz.MetodosAyuda.*;

public class Manticora extends Enemigo {

    // Hitboxes de ataque separadas
    private Rectangle2D.Float attackBox1; // ATTACK_1 cuerpo a cuerpo (corto)
    private Rectangle2D.Float attackBox4; // ATTACK_4 área amplia

    // Proyectil para ATTACK_4
    private Rectangle2D.Float ballHitbox;
    private boolean ballActive = false;
    private int     ballDir;
    private float   ballSpeed = 3.0f * Juego.SCALE;

    // Impaciencia: si no ve al jugador por un tiempo, hace SNEER
    private int impatienceTick = 0;
    private static final int IMPATIENCE_MAX = 280;

    // Cooldown corto para ataques visuales (ATTACK_2 y ATTACK_3)
    private int visualAttackCooldown    = 0;
    private static final int VISUAL_CD  = 60;

    private boolean attackHitChecked1 = false;
    private final Random rng = new Random();

    // Mapeo estado → fila en el spritesheet (72×72, 9 filas)
    // IDLE(0)→6, WALK(1)→8, SNEER(2)→1, ATTACK_1(3)→0, ATTACK_2(4)→2,
    // ATTACK_3(5)→7, ATTACK_4(6)→3, HURT(7)→5, DEATH(8)→4
    public static final int[] STATE_TO_ROW = { 6, 8, 1, 0, 2, 7, 3, 5, 4 };

    public Manticora(float x, float y) {
        super(x, y, MANTICORA_WIDTH, MANTICORA_HEIGHT, MANTICORA);
        initHitbox(x, y, (int)(24 * Juego.SCALE), (int)(30 * Juego.SCALE));
        attackDistance    = Juego.TILES_SIZE;
        attackCooldownMax = 150;

        attackBox1 = new Rectangle2D.Float(x, y,
            (int)(45 * Juego.SCALE), (int)(24 * Juego.SCALE));
        attackBox4 = new Rectangle2D.Float(x, y,
            (int)(80 * Juego.SCALE), (int)(28 * Juego.SCALE));
        ballHitbox = new Rectangle2D.Float(0, 0,
            (int)(14 * Juego.SCALE), (int)(14 * Juego.SCALE));
    }

    // ─────────────────────────────── Update ──────────────────────────
    public void update(int[][] lvlData, Jugador jugador) {
        updateAttackBoxes();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
        if (visualAttackCooldown > 0) visualAttackCooldown--;
    }

    public void update(int[][] lvlData, Jugador2 jugador) {
        updateAttackBoxes();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
        if (visualAttackCooldown > 0) visualAttackCooldown--;
    }

    private void updateAttackBoxes() {
        if (walkDir == LEFT) {
            attackBox1.x = hitbox.x - attackBox1.width;
            attackBox4.x = hitbox.x - attackBox4.width;
        } else {
            attackBox1.x = hitbox.x + hitbox.width;
            attackBox4.x = hitbox.x + hitbox.width;
        }
        attackBox1.y = hitbox.y;
        attackBox4.y = hitbox.y - hitbox.height * 0.2f;
    }

    private void spawnBall() {
        ballActive   = true;
        ballDir      = walkDir;
        ballHitbox.x = (walkDir == RIGHT)
                ? hitbox.x + hitbox.width
                : hitbox.x - ballHitbox.width;
        ballHitbox.y = hitbox.y + (hitbox.height - ballHitbox.height) / 2f;
    }

    private void updateBall(int[][] lvlData, Jugador jugador) {
        if (!ballActive) return;
        float speed = (ballDir == RIGHT) ? ballSpeed : -ballSpeed;
        if (CanMoveHere(ballHitbox.x + speed, ballHitbox.y,
                (int)ballHitbox.width, (int)ballHitbox.height, lvlData))
            ballHitbox.x += speed;
        else
            ballActive = false;

        if (ballActive && ballHitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            ballActive = false;
        }
    }

    private void updateBall(int[][] lvlData, Jugador2 jugador) {
        if (!ballActive) return;
        float speed = (ballDir == RIGHT) ? ballSpeed : -ballSpeed;
        if (CanMoveHere(ballHitbox.x + speed, ballHitbox.y,
                (int)ballHitbox.width, (int)ballHitbox.height, lvlData))
            ballHitbox.x += speed;
        else
            ballActive = false;

        if (ballActive && ballHitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            ballActive = false;
        }
    }

    // ─────────────────────── State Machine ───────────────────────────
    private void updateBehavior(int[][] lvlData, Jugador jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }

        switch (enemyState) {

            case MANTICORA_IDLE:
                newState(MANTICORA_WALK);
                break;

            case MANTICORA_WALK:
                if (attackCooldown > 0) attackCooldown--;

                if (canSeePlayer(lvlData, jugador)) {
                    impatienceTick = 0;
                    turnTowardsPlayer(jugador);

                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);

                        if (dist <= 1.5f * Juego.TILES_SIZE) {
                            // Ataque cuerpo a cuerpo
                            newState(MANTICORA_ATTACK_1);
                        } else if (dist > 4f * Juego.TILES_SIZE) {
                            // Ataque a distancia con proyectil
                            newState(MANTICORA_ATTACK_4);
                        } else {
                            // Distancia media: animación visual aleatoria
                            if (visualAttackCooldown <= 0)
                                newState(rng.nextBoolean() ? MANTICORA_ATTACK_2 : MANTICORA_ATTACK_3);
                        }
                    }
                } else {
                    impatienceTick++;
                    if (impatienceTick >= IMPATIENCE_MAX) {
                        impatienceTick = 0;
                        newState(MANTICORA_SNEER);
                        return;
                    }
                }
                move(lvlData);
                break;

            // Burla / taunt — solo animación, vuelve a WALK al terminar
            case MANTICORA_SNEER:
                break;

            // ── ATTACK_1: cuerpo a cuerpo, golpe en frame 3 ──────────
            case MANTICORA_ATTACK_1:
                if (animIndex == 0) attackHitChecked1 = false;
                if (animIndex == 3 && !attackHitChecked1) {
                    checkEnemyHit(attackBox1, jugador);
                    attackHitChecked1 = true;
                }
                break;

            // ── ATTACK_2: animación visual, SIN daño ─────────────────
            case MANTICORA_ATTACK_2:
                // No hay revisión de daño ni colisión.
                // updateAnimationTick maneja la transición cuando termina.
                break;

            // ── ATTACK_3: animación visual, SIN daño ─────────────────
            case MANTICORA_ATTACK_3:
                // Igual que ATTACK_2, puramente visual.
                break;

            // ── ATTACK_4: proyectil, disparo en frame 3 ──────────────
            case MANTICORA_ATTACK_4:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;

            case MANTICORA_HURT:
                break;

            case MANTICORA_DEATH:
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }

        switch (enemyState) {

            case MANTICORA_IDLE:
                newState(MANTICORA_WALK);
                break;

            case MANTICORA_WALK:
                if (attackCooldown > 0) attackCooldown--;

                if (canSeePlayer(lvlData, jugador)) {
                    impatienceTick = 0;
                    turnTowardsPlayer(jugador);

                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);

                        if (dist <= 1.5f * Juego.TILES_SIZE) {
                            // Ataque cuerpo a cuerpo
                            newState(MANTICORA_ATTACK_1);
                        } else if (dist > 4f * Juego.TILES_SIZE) {
                            // Ataque a distancia con proyectil
                            newState(MANTICORA_ATTACK_4);
                        } else {
                            // Distancia media: animación visual aleatoria
                            if (visualAttackCooldown <= 0)
                                newState(rng.nextBoolean() ? MANTICORA_ATTACK_2 : MANTICORA_ATTACK_3);
                        }
                    }
                } else {
                    impatienceTick++;
                    if (impatienceTick >= IMPATIENCE_MAX) {
                        impatienceTick = 0;
                        newState(MANTICORA_SNEER);
                        return;
                    }
                }
                move(lvlData);
                break;

            // Burla / taunt — solo animación, vuelve a WALK al terminar
            case MANTICORA_SNEER:
                break;

            // ── ATTACK_1: cuerpo a cuerpo, golpe en frame 3 ──────────
            case MANTICORA_ATTACK_1:
                if (animIndex == 0) attackHitChecked1 = false;
                if (animIndex == 3 && !attackHitChecked1) {
                    checkEnemyHit(attackBox1, jugador);
                    attackHitChecked1 = true;
                }
                break;

            // ── ATTACK_2: animación visual, SIN daño ─────────────────
            case MANTICORA_ATTACK_2:
                // No hay revisión de daño ni colisión.
                // updateAnimationTick maneja la transición cuando termina.
                break;

            // ── ATTACK_3: animación visual, SIN daño ─────────────────
            case MANTICORA_ATTACK_3:
                // Igual que ATTACK_2, puramente visual.
                break;

            // ── ATTACK_4: proyectil, disparo en frame 3 ──────────────
            case MANTICORA_ATTACK_4:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;

            case MANTICORA_HURT:
                break;

            case MANTICORA_DEATH:
                break;
        }
    }

    // ─────────────────────── Animation Tick ──────────────────────────
    protected void updateAnimationTick() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case MANTICORA_ATTACK_1:
                    case MANTICORA_ATTACK_4:
                        attackCooldown = attackCooldownMax;
                        newState(MANTICORA_WALK);
                        break;
                    case MANTICORA_ATTACK_2:
                    case MANTICORA_ATTACK_3:
                        // Cooldown corto para evitar spam de animaciones visuales
                        visualAttackCooldown = VISUAL_CD;
                        attackCooldown       = attackCooldownMax / 3;
                        newState(MANTICORA_WALK);
                        break;
                    case MANTICORA_SNEER:
                    case MANTICORA_HURT:
                        newState(MANTICORA_WALK);
                        break;
                    case MANTICORA_DEATH:
                        alive = false;
                        break;
                }
            }
        }
    }

    // ─────────────────────────── Misc ────────────────────────────────
    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(MANTICORA_DEATH);
        } else {
            newState(MANTICORA_HURT);
        }
    }

    public void resetEnemy() {
        hitbox.x = x; hitbox.y = y;
        firstUpdate   = true;
        currentHealth = maxHealth;
        newState(MANTICORA_IDLE);
        alive                = true;
        airSpeed             = 0;
        ballActive           = false;
        impatienceTick       = 0;
        visualAttackCooldown = 0;
        attackCooldown       = 0;
    }

    // La Manticora mira a la IZQUIERDA por defecto en el sprite
    public int flipX() { return 0; }
    public int flipW() { return walkDir == RIGHT ? -1 : 1; }

    public boolean isBallActive()            { return ballActive; }
    public Rectangle2D.Float getBallHitbox() { return ballHitbox; }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                   (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.blue);
        g.drawRect((int)(attackBox1.x - xLvlOffset), (int)attackBox1.y,
                   (int)attackBox1.width, (int)attackBox1.height);
        g.setColor(Color.cyan);
        g.drawRect((int)(attackBox4.x - xLvlOffset), (int)attackBox4.y,
                   (int)attackBox4.width, (int)attackBox4.height);
    }
}
