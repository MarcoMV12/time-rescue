package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.Constantes.GetEnemyDamage;
import static utilz.MetodosAyuda.*;

public class Faraon extends Enemigo {

    private Rectangle2D.Float attackBox;

    // Proyectil
    private Rectangle2D.Float ballHitbox;
    private boolean ballActive = false;
    private int     ballDir;
    private float   ballSpeed = 2.0f * Juego.SCALE;

    public Faraon(float x, float y) {
        super(x, y, FARAON_WIDTH, FARAON_HEIGHT, FARAON);
        initHitbox(x, y, (int)(24 * Juego.SCALE), (int)(31 * Juego.SCALE));
        attackDistance    = 2 * Juego.TILES_SIZE;
        attackCooldownMax = 120;
        attackBox = new Rectangle2D.Float(x, y,
            (int)(50 * Juego.SCALE), (int)(24 * Juego.SCALE));
        ballHitbox = new Rectangle2D.Float(0, 0,
            (int)(10 * Juego.SCALE), (int)(10 * Juego.SCALE));
    }

    public void update(int[][] lvlData, Jugador jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
    }

    public void update(int[][] lvlData, Jugador2 jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateBall(lvlData, jugador);
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

    private void spawnBall() {
        ballActive = true;
        ballDir    = walkDir;
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

    private void updateBehavior(int[][] lvlData, Jugador jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }

        switch (enemyState) {
            case FARAON_IDLE:
                newState(FARAON_RUN);
                break;

            case FARAON_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);
                        if (dist <= 1.5f * Juego.TILES_SIZE)
                            newState(FARAON_ATTACK_1);
                        else
                            newState(FARAON_ATTACK_2);
                    }
                }
                move(lvlData);
                break;

            // Ataque cuerpo a cuerpo (guadaña): golpe en frame 3
            case FARAON_ATTACK_1:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox, jugador);
                break;

            // Ataque a distancia: dispara proyectil en frame 3
            case FARAON_ATTACK_2:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;

            case FARAON_HURT:
                break;
            case FARAON_DEAD:
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }

        switch (enemyState) {
            case FARAON_IDLE:
                newState(FARAON_RUN);
                break;
            case FARAON_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (attackCooldown <= 0) {
                        float dist = Math.abs(jugador.getHitbox().x - hitbox.x);
                        if (dist <= 1.5f * Juego.TILES_SIZE)
                            newState(FARAON_ATTACK_1);
                        else
                            newState(FARAON_ATTACK_2);
                    }
                }
                move(lvlData);
                break;
            case FARAON_ATTACK_1:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox, jugador);
                break;
            case FARAON_ATTACK_2:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) { spawnBall(); attackChecked = true; }
                break;
            case FARAON_HURT:
                break;
            case FARAON_DEAD:
                break;
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(FARAON_DEAD);
        } else {
            newState(FARAON_HURT);
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
                    case FARAON_ATTACK_1:
                    case FARAON_ATTACK_2:
                        attackCooldown = attackCooldownMax;
                        newState(FARAON_RUN);
                        break;
                    case FARAON_HURT:
                        newState(FARAON_RUN);
                        break;
                    case FARAON_DEAD:
                        alive = false;
                        break;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x; hitbox.y = y;
        firstUpdate   = true;
        currentHealth = maxHealth;
        newState(FARAON_IDLE);
        alive      = true;
        airSpeed   = 0;
        ballActive = false;
        attackCooldown = 0;
    }

    public int flipX() {
        return walkDir == LEFT ? FARAON_WIDTH : 0;
    }

    public int flipW() {
        return walkDir == LEFT ? -1 : 1;
    }

    public boolean isBallActive()         { return ballActive; }
    public Rectangle2D.Float getBallHitbox() { return ballHitbox; }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                   (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.blue);
        g.drawRect((int)(attackBox.x - xLvlOffset), (int)attackBox.y,
                   (int)attackBox.width, (int)attackBox.height);
    }
}
