package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Spider extends Enemigo {
    

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private float hitboxOffsetX;
    private float hitboxOffsetY;
    private int attackDir;

    public Spider(float x, float y) {
        super(x, y, SPIDER_WIDTH, SPIDER_HEIGHT, SPIDER);
        attackDistance = 32 * Juego.SCALE;

        int hbW = 35;
        int hbH = 30;
        hitboxOffsetX = 7;
        hitboxOffsetY = 0;

        initHitbox(x + hitboxOffsetX, y + hitboxOffsetY, hbW, hbH);
        tileYPos = (int)((y + hitboxOffsetY) / Juego.TILES_SIZE);
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(45 * Juego.SCALE), (int)(20 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update4(int[][] lvlData, Jugador4 jugador) {
        updateBehavior(lvlData, jugador);
        updateAttackBox();
        updateAnimationTick();
    }

    private void updateAttackBox() {
        if (attackDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width + 5;
        else
            attackBox.x = hitbox.x + hitbox.width - 5;
        attackBox.y = hitbox.y + (hitbox.height / 2f) - (attackBox.height / 2f);
    }

    public void updateBehavior(int[][] lvlData, Jugador4 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case SPIDER_IDLE:
                    newState(SPIDER_RUN);
                    break;

                case SPIDER_RUN:


                    if (canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        
                        
                        if (isPlayerCloseForAttack(jugador)) {
                            attackDir = walkDir;
                            attackChecked = false;
                            newState(SPIDER_ATTACK);
                            break;
                        }
                        turnTowardsPlayer(jugador);
                    }
                    move(lvlData);
                    break;

                case SPIDER_ATTACK:
                    if (animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);
                    break;

                default:
                    break;
            }
        }
    }

        @Override
    protected void onDeath() {
        newState(SPIDER_DIE); 
    }

    @Override
    protected void onHurt() {
        newState(SPIDER_HURT); 
    }

    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return; 
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(SPIDER_DIE);
        } else {
            newState(SPIDER_HURT);
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
                    case SPIDER_ATTACK, SPIDER_HURT -> enemyState = SPIDER_IDLE;
                    case SPIDER_DIE -> alive = false;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x + hitboxOffsetX;
        hitbox.y = y + hitboxOffsetY;
        tileYPos = (int)((y + hitboxOffsetY) / Juego.TILES_SIZE);
        firstUpdate = true;
        inAir = false;
        attackChecked = false;
        attackDir = RIGHT;
        currentHealth = maxHealth;
        newState(SPIDER_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        // g.setColor(Color.red);
        // g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
        //            (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        // g.setColor(Color.red);
        // g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y,
        //            (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        int dir = (enemyState == SPIDER_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return SPIDER_WIDTH / 2;
        else
            return 0;
    }

    public int flipW() {
        int dir = (enemyState == SPIDER_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return -1;
        else
            return 1;
    }
}