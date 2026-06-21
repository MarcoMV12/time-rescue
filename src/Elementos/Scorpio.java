package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.*;

public class Scorpio extends Enemigo {

    private Rectangle2D.Float attackBox;

    public Scorpio(float x, float y) {
        super(x, y, SCORPIO_WIDTH, SCORPIO_HEIGHT, SCORPIO);
        initHitbox(x, y, (int)(30 * Juego.SCALE), (int)(20 * Juego.SCALE));
        attackDistance = Juego.TILES_SIZE;
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
            (int)(40 * Juego.SCALE),
            (int)(20 * Juego.SCALE));
    }

    public void update(int[][] lvlData, Jugador jugador) {
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateAttackBox();
    }

    public void update(int[][] lvlData, Jugador2 jugador) {
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

    private void updateBehavior(int[][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
            return;
        }

        switch (enemyState) {
            case SCORPIO_IDLE:
                newState(SCORPIO_RUN);
                break;
            case SCORPIO_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (isPlayerCloseForAttack(jugador) && attackCooldown <= 0)
                        newState(SCORPIO_ATTACK);
                }
                move(lvlData);
                break;
            case SCORPIO_ATTACK:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox, jugador);
                break;
            case SCORPIO_HURT:
                break;
            case SCORPIO_DEAD:
                alive = false;
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }
        switch (enemyState) {
            case SCORPIO_IDLE: newState(SCORPIO_RUN); break;
            case SCORPIO_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (isPlayerCloseForAttack(jugador) && attackCooldown <= 0)
                        newState(SCORPIO_ATTACK);
                }
                move(lvlData);
                break;
            case SCORPIO_ATTACK:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) checkEnemyHit(attackBox, jugador);
                break;
            case SCORPIO_HURT: break;
            case SCORPIO_DEAD: alive = false; break;
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(SCORPIO_DEAD);
        } else {
            newState(SCORPIO_HURT);
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
                    case SCORPIO_ATTACK:
                        attackCooldown = attackCooldownMax;
                        newState(SCORPIO_RUN);
                        break;
                    case SCORPIO_HURT:
                        newState(SCORPIO_RUN);
                        break;
                    case SCORPIO_DEAD:
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
        newState(SCORPIO_IDLE);
        alive    = true;
        airSpeed = 0;
    }

    public int flipX() {
        return walkDir == RIGHT ? SCORPIO_WIDTH : 0;
    }

    public int flipW() {
        return walkDir == RIGHT ? -1 : 1;
    }

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