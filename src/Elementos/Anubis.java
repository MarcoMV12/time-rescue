package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.Constantes.GetEnemyDamage;
import static utilz.MetodosAyuda.*;

public class Anubis extends Enemigo {

    // Melee attack boxes (ATTACK_1 close, ATTACK_2 wider)
    private Rectangle2D.Float attackBox1;
    private Rectangle2D.Float attackBox2;

    // Ranged projectile
    private Rectangle2D.Float ballHitbox;
    private boolean ballActive = false;
    private int ballDir;
    private float ballSpeed = 2.0f * Juego.SCALE;

    // Impatience: triggers SNEER if player is not visible for too long
    private int impatienceTick = 0;
    private static final int IMPATIENCE_MAX = 300;

    private boolean attackHitChecked2 = false;

    public Anubis(float x, float y) {
        super(x, y, ANUBIS_WIDTH, ANUBIS_HEIGHT, ANUBIS);
        initHitbox(x, y, (int) (24 * Juego.SCALE), (int) (31 * Juego.SCALE)); // Ajustado a 24 para centrado perfecto
        attackDistance = Juego.TILES_SIZE;
        attackCooldownMax = 200;
        attackBox1 = new Rectangle2D.Float(x, y,
                (int) (40 * Juego.SCALE), (int) (20 * Juego.SCALE));
        attackBox2 = new Rectangle2D.Float(x, y,
                (int) (70 * Juego.SCALE), (int) (25 * Juego.SCALE));
        ballHitbox = new Rectangle2D.Float(0, 0,
                (int) (14 * Juego.SCALE), (int) (14 * Juego.SCALE));
    }

    public void update(int[][] lvlData, Jugador jugador) {
        updateAttackBoxes();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
    }

    public void update(int[][] lvlData, Jugador2 jugador) {
        updateAttackBoxes();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
    }

    private void updateAttackBoxes() {
        if (walkDir == LEFT) {
            attackBox1.x = hitbox.x - attackBox1.width;
            attackBox2.x = hitbox.x - attackBox2.width;
        } else {
            attackBox1.x = hitbox.x + hitbox.width;
            attackBox2.x = hitbox.x + hitbox.width;
        }
        attackBox1.y = hitbox.y;
        attackBox2.y = hitbox.y;
    }

    private void spawnBall() {
        ballActive = true;
        ballDir = walkDir;
        ballHitbox.x = walkDir == RIGHT
                ? hitbox.x + hitbox.width
                : hitbox.x - ballHitbox.width;
        ballHitbox.y = hitbox.y + (hitbox.height - ballHitbox.height) / 2f;
    }

    private void updateBall(int[][] lvlData, Jugador jugador) {
        if (!ballActive)
            return;
        float speed = ballDir == RIGHT ? ballSpeed : -ballSpeed;
        if (CanMoveHere(ballHitbox.x + speed, ballHitbox.y,
                (int) ballHitbox.width, (int) ballHitbox.height, lvlData))
            ballHitbox.x += speed;
        else
            ballActive = false;

        if (ballActive && ballHitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            ballActive = false;
        }
    }

    private void updateBall(int[][] lvlData, Jugador2 jugador) {
        if (!ballActive)
            return;
        float speed = ballDir == RIGHT ? ballSpeed : -ballSpeed;
        if (CanMoveHere(ballHitbox.x + speed, ballHitbox.y,
                (int) ballHitbox.width, (int) ballHitbox.height, lvlData))
            ballHitbox.x += speed;
        else
            ballActive = false;

        if (ballActive && ballHitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            ballActive = false;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);
        if (inAir) {
            updateInAir(lvlData);
            return;
        }

        switch (enemyState) {
            case ANUBIS_IDLE:
                newState(ANUBIS_WALK);
                break;

            case ANUBIS_WALK:
                if (attackCooldown > 0)
                    attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    impatienceTick = 0;
                    turnTowardsPlayer(jugador);
                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);
                        if (dist <= Juego.TILES_SIZE)
                            newState(ANUBIS_ATTACK_1);
                        else if (dist <= 3 * Juego.TILES_SIZE)
                            newState(ANUBIS_ATTACK_2);
                        else
                            newState(ANUBIS_ATTACK_RANGED);
                    }
                } else {
                    impatienceTick++;
                    if (impatienceTick >= IMPATIENCE_MAX) {
                        impatienceTick = 0;
                        newState(ANUBIS_SNEER);
                        return;
                    }
                }
                move(lvlData);
                break;

            case ANUBIS_SNEER:
                // visual only, animation tick handles the transition back to WALK
                break;

            case ANUBIS_ATTACK_1:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox1, jugador);
                break;

            case ANUBIS_ATTACK_2:
                if (animIndex == 0)
                    attackHitChecked2 = false;
                if (animIndex == 5 && !attackHitChecked2) {
                    checkEnemyHit(attackBox2, jugador);
                    attackHitChecked2 = true;
                }
                break;

            case ANUBIS_ATTACK_RANGED:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;

            case ANUBIS_HURT:
                break;

            case ANUBIS_DEAD:
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);
        if (inAir) {
            updateInAir(lvlData);
            return;
        }

        switch (enemyState) {
            case ANUBIS_IDLE:
                newState(ANUBIS_WALK);
                break;

            case ANUBIS_WALK:
                if (attackCooldown > 0)
                    attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    impatienceTick = 0;
                    turnTowardsPlayer(jugador);
                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);
                        if (dist <= Juego.TILES_SIZE)
                            newState(ANUBIS_ATTACK_1);
                        else if (dist <= 3 * Juego.TILES_SIZE)
                            newState(ANUBIS_ATTACK_2);
                        else
                            newState(ANUBIS_ATTACK_RANGED);
                    }
                } else {
                    impatienceTick++;
                    if (impatienceTick >= IMPATIENCE_MAX) {
                        impatienceTick = 0;
                        newState(ANUBIS_SNEER);
                        return;
                    }
                }
                move(lvlData);
                break;

            case ANUBIS_SNEER:
                // visual only, animation tick handles the transition back to WALK
                break;

            case ANUBIS_ATTACK_1:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox1, jugador);
                break;

            case ANUBIS_ATTACK_2:
                if (animIndex == 0)
                    attackHitChecked2 = false;
                if (animIndex == 5 && !attackHitChecked2) {
                    checkEnemyHit(attackBox2, jugador);
                    attackHitChecked2 = true;
                }
                break;

            case ANUBIS_ATTACK_RANGED:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;

            case ANUBIS_HURT:
                break;

            case ANUBIS_DEAD:
                break;
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(ANUBIS_DEAD);
        } else {
            newState(ANUBIS_HURT);
        }
    }

    protected void updateAnimationTick() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case ANUBIS_ATTACK_1:
                    case ANUBIS_ATTACK_2:
                    case ANUBIS_ATTACK_RANGED:
                        attackCooldown = attackCooldownMax;
                        newState(ANUBIS_WALK);
                        break;
                    case ANUBIS_HURT:
                    case ANUBIS_SNEER:
                        newState(ANUBIS_WALK);
                        break;
                    case ANUBIS_DEAD:
                        alive = false;
                        break;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(ANUBIS_IDLE);
        alive = true;
        airSpeed = 0;
        ballActive = false;
        impatienceTick = 0;
        attackCooldown = 0;
    }

    public int flipX() {
        // Voltea el sprite y suma 32 píxeles extra para compensar el margen asimétrico
        return walkDir == RIGHT ? ANUBIS_WIDTH + (int)(32 * Juego.SCALE) : 0;
    }

    public int flipW() {
        return walkDir == RIGHT ? -1 : 1;
    }

    public boolean isBallActive() {
        return ballActive;
    }

    public Rectangle2D.Float getBallHitbox() {
        return ballHitbox;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int) (hitbox.x - xLvlOffset), (int) hitbox.y,
                (int) hitbox.width, (int) hitbox.height);
    }

    public void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.blue);
        g.drawRect((int) (attackBox1.x - xLvlOffset), (int) attackBox1.y,
                (int) attackBox1.width, (int) attackBox1.height);
        g.setColor(Color.cyan);
        g.drawRect((int) (attackBox2.x - xLvlOffset), (int) attackBox2.y,
                (int) attackBox2.width, (int) attackBox2.height);
    }

}
