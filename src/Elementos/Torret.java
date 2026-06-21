package Elementos;

import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import Juegos.Juego;

public class Torret extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY = 90; // 1.5 seg entre disparos

    private ArrayList<TorretBullet> bullets = new ArrayList<>();

    public Torret(float x, float y) {
        super(x, y, TORRET_WIDTH, TORRET_HEIGHT, TORRET);
        initHitbox(x + 10, y + 5, (int)(40 * Juego.SCALE), (int)(22 * Juego.SCALE));
        attackDistance = 150 * Juego.SCALE;
        initAttackBox();
        newState(TORRET_IDLE);
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
            (int)(30 * Juego.SCALE), (int)(20 * Juego.SCALE));
    }

    public void update4(int[][] lvlData, Jugador4 jugador) {
        updateBehavior(jugador);
        updateAnimationTick();
        updateAttackBox();
        updateBullets(jugador);
        if (shootCooldown > 0) shootCooldown--;
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

    private void updateBullets(Jugador4 jugador) {
        bullets.removeIf(b -> !b.isActive());
        for (TorretBullet b : bullets)
            b.update4(jugador);
    }

    private void shoot(Jugador4 jugador) {
        if (shootCooldown > 0) return;

        float bx = (walkDir == RIGHT)
            ? hitbox.x + hitbox.width
            : hitbox.x - TORRET_BULLET_WIDTH;

        float by = hitbox.y + hitbox.height / 2f - TORRET_BULLET_HEIGHT / 2f;

        int dir = (walkDir == RIGHT) ? 1 : -1;
        bullets.add(new TorretBullet(bx, by, dir));
        shootCooldown = SHOOT_DELAY;
    }

    private void updateBehavior(Jugador4 jugador) {
        switch (enemyState) {
            case TORRET_IDLE:
                if (isPlayerinRange(jugador)) {
                    turnTowardsPlayer(jugador);
                    newState(TORRET_ATTACK);
                }
                break;

            case TORRET_ATTACK:
                if (animIndex == 0)
                    attackChecked = false;

                if (animIndex == 2 && !attackChecked) {
                    attackChecked = true;
                    shoot(jugador);
                }

                if (animIndex >= GetSpriteAmount(enemyType, TORRET_ATTACK) - 1)
                    newState(TORRET_IDLE);
                break;

            case TORRET_HURT:
                if (animIndex >= GetSpriteAmount(enemyType, TORRET_HURT) - 1)
                    newState(TORRET_IDLE);
                break;

            case TORRET_DIE:
                break;
        }
    }

    @Override
    protected boolean isPlayerinRange(Jugador4 jugador) {
        float distance = Math.abs(jugador.getHitbox().x - hitbox.x);
        return distance <= attackDistance;
    }

    public ArrayList<TorretBullet> getBullets() {
        return bullets;
    }

    @Override
    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return; 
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(TORRET_DIE);
        } else {
            newState(TORRET_HURT);
        }
    }

    @Override
    protected void onDeath() { newState(TORRET_DIE); }

    @Override
    protected void onHurt() { newState(TORRET_HURT); }

    @Override
    protected void updateAnimationTick() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case TORRET_ATTACK, TORRET_HURT -> newState(TORRET_IDLE);
                    case TORRET_DIE -> alive = false;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        currentHealth = maxHealth;
        newState(TORRET_IDLE);
        alive = true;
        shootCooldown = 0;
        bullets.clear();
    }

    public int flipX() { return walkDir == LEFT ? TORRET_WIDTH / 2 : 0; }
    public int flipW() { return walkDir == LEFT ? -1 : 1; }
}