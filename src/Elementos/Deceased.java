package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import static utilz.Constantes.GetEnemyDamage;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.*;

public class Deceased extends Enemigo {

    private Rectangle2D.Float attackBox;

    // Proyectil
    private Rectangle2D.Float ballHitbox;
    private boolean ballActive = false;
    private int     ballDir;
    private float   ballSpeed  = 2f * Juego.SCALE;

    public Deceased(float x, float y) {
        super(x, y, DECEASED_WIDTH, DECEASED_HEIGHT, DECEASED);
        initHitbox(x, y, (int)(30 * Juego.SCALE), (int)(31 * Juego.SCALE));
        attackDistance = Juego.TILES_SIZE;
        initAttackBox();
        ballHitbox = new Rectangle2D.Float(0, 0,
            (int)(12 * Juego.SCALE),
            (int)(12 * Juego.SCALE));
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
            (int)(40 * Juego.SCALE),
            (int)(20 * Juego.SCALE));
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
        ballHitbox.x = walkDir == RIGHT
            ? hitbox.x + hitbox.width
            : hitbox.x - ballHitbox.width;
        ballHitbox.y = hitbox.y + (hitbox.height - ballHitbox.height) / 2;
    }

    private void updateBall(int[][] lvlData, Jugador jugador) {
        if (!ballActive) return;
        float speed = ballDir == RIGHT ? ballSpeed : -ballSpeed;
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
        float speed = ballDir == RIGHT ? ballSpeed : -ballSpeed;
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
        if (firstUpdate)
            firstUpdateCheck(lvlData);
        if (inAir) {
            updateInAir(lvlData);
            return;
        }
        switch (enemyState) {
            case DECEASED_IDLE:
                newState(DECEASED_RUN);
                break;
            case DECEASED_RUN:
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (jugador.getHitbox().intersects(attackBox))
                        newState(DECEASED_ATTACK);
                }
                move(lvlData);
                break;
            case DECEASED_ATTACK:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked) {
                    spawnBall();
                    attackChecked = true;
                }
                break;
            case DECEASED_HURT:
                break;
            case DECEASED_DEAD:
                alive = false;
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }
        switch (enemyState) {
            case DECEASED_IDLE: newState(DECEASED_RUN); break;
            case DECEASED_RUN:
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (jugador.getHitbox().intersects(attackBox))
                        newState(DECEASED_ATTACK);
                }
                move(lvlData);
                break;
            case DECEASED_ATTACK:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) { spawnBall(); attackChecked = true; }
                break;
            case DECEASED_HURT: break;
            case DECEASED_DEAD: alive = false; break;
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(DECEASED_DEAD);
        } else {
            newState(DECEASED_HURT);
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
                    case DECEASED_ATTACK:
                    case DECEASED_HURT:
                        newState(DECEASED_RUN);
                        break;
                    case DECEASED_DEAD:
                        alive = false;
                        break;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate   = true;
        currentHealth = maxHealth;
        newState(DECEASED_IDLE);
        alive      = true;
        airSpeed   = 0;
        ballActive = false;
    }

    public int flipX() {
        return walkDir == RIGHT ? DECEASED_WIDTH : 0;
    }

    public int flipW() {
        return walkDir == RIGHT ? -1 : 1;
    }

    public boolean isBallActive()             { return ballActive; }
    public Rectangle2D.Float getBallHitbox()  { return ballHitbox; }

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
