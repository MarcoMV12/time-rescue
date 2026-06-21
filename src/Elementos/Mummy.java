package Elementos;

import Juegos.Juego;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.*;

public class Mummy extends Enemigo {

    private Rectangle2D.Float attackBox;

    public Mummy(float x, float y) {
        super(x, y, MUMMY_WIDTH, MUMMY_HEIGHT, MUMMY);
        initHitbox(x, y, (int)(30 * Juego.SCALE), (int)(31 * Juego.SCALE));
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
            case MUMMY_IDLE:
                newState(MUMMY_RUN);
                break;
            case MUMMY_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (isPlayerCloseForAttack(jugador) && attackCooldown <= 0)
                        newState(MUMMY_ATTACK);
                }
                move(lvlData);
                break;
            case MUMMY_ATTACK:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox, jugador);
                break;
            case MUMMY_HURT:
                break;
            case MUMMY_DEAD:
                alive = false;
                break;
        }
    }

    private void updateBehavior(int[][] lvlData, Jugador2 jugador) {
        if (firstUpdate) firstUpdateCheck(lvlData);
        if (inAir) { updateInAir(lvlData); return; }
        switch (enemyState) {
            case MUMMY_IDLE: newState(MUMMY_RUN); break;
            case MUMMY_RUN:
                if (attackCooldown > 0) attackCooldown--;
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (isPlayerCloseForAttack(jugador) && attackCooldown <= 0)
                        newState(MUMMY_ATTACK);
                }
                move(lvlData);
                break;
            case MUMMY_ATTACK:
                if (animIndex == 0) attackChecked = false;
                if (animIndex == 3 && !attackChecked) checkEnemyHit(attackBox, jugador);
                break;
            case MUMMY_HURT: break;
            case MUMMY_DEAD: alive = false; break;
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(MUMMY_DEAD);
        } else {
            newState(MUMMY_HURT);
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
                    case MUMMY_ATTACK:
                        attackCooldown = attackCooldownMax;
                        newState(MUMMY_RUN);
                        break;
                    case MUMMY_HURT:
                        newState(MUMMY_RUN);
                        break;
                    case MUMMY_DEAD:
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
        newState(MUMMY_IDLE);
        alive    = true;
        airSpeed = 0;
    }

    public int flipX() {
        return walkDir == RIGHT ? MUMMY_WIDTH : 0;
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
