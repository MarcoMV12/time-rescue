package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Boss4 extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int attackDir; 

    public Boss4(float x, float y) {
        super(x, y, BOSS4_WIDTH, BOSS4_HEIGHT, BOSS4);
        attackDistance = 45 * Juego.SCALE;
        initHitbox(x, y, (int)(45 * Juego.SCALE), (int)(21 * Juego.SCALE));
        initAttackBox();
    }

    private void initAttackBox() {
    attackBox = new Rectangle2D.Float(x, y,
        (int)(45 * Juego.SCALE),
        (int)(21 * Juego.SCALE));
    attackBoxOffsetX = (int)(10 * Juego.SCALE);
}

    public void update4(int[][] lvlData, Jugador4 jugador) {
        updateBehavior(lvlData, jugador);
        updateAttackBox();
        updateAnimationTick();
    }

    private void updateAttackBox() {
        if (attackDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width + 10;
        else
            attackBox.x = hitbox.x + hitbox.width - 10;
        attackBox.y = hitbox.y;
    }

    public void updateBehavior(int[][] lvlData, Jugador4 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case BOSS4_IDLE:
                    newState(BOSS4_RUN);
                    break;

                case BOSS4_RUN:
                    if (canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        if (isPlayerCloseForAttack(jugador)) {
                            attackDir = walkDir;      
                            attackChecked = false;   
                            newState(BOSS4_ATTACK);
                            break;                 
                        }
                        turnTowardsPlayer(jugador);   
                    }
                    move(lvlData);
                    break;

                case BOSS4_ATTACK:

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
        newState(BOSS4_DIE); 
    }

    @Override
    protected void onHurt() {
        newState(BOSS4_HURT);
    }

    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return;
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(BOSS4_DIE);
        } else {
            newState(BOSS4_HURT);
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
                    case BOSS4_ATTACK, BOSS4_HURT -> enemyState = BOSS4_RUN;
                    case BOSS4_DIE -> alive = false;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        inAir = false;        
        attackChecked = false;
        attackDir = RIGHT;     
        currentHealth = maxHealth;
        newState(BOSS4_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                   (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y,
                   (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        int dir = (enemyState == BOSS_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return BOSS_WIDTH * 3 / 4;
        else
            return 0;
    }

    public int flipW() {
        int dir = (enemyState == BOSS_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return -1;
        else
            return 1;
    }
}