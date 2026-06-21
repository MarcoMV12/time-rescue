package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Indio extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int cooldownTick = 0;
    private int cooldownMax = 120; // DURACION DEL COOLDOWN (ticks): cambia este valor

    public Indio(float x, float y) {
        super(x, y, INDIO_WIDTH, INDIO_HEIGHT, INDIO);
        attackDistance = 32 * Juego.SCALE;
        int drawW = INDIO_WIDTH / 2;
        int drawH = INDIO_HEIGHT / 2;
        int hbW = (int)(drawW * 0.35f);
        int hbH = (int)(drawH * 0.65f);

        // Mantiene la posicion visual previa y centra la hitbox en el sprite
        float spriteX = x + 50 + 35 - drawW;
        float spriteY = y - 40 - 16;
        float hbX = spriteX + (drawW - hbW) / 2f;
        float hbY = spriteY + drawH - hbH;

        initHitbox(hbX, hbY, hbW, hbH);
        
        initAttackBox();
    }

    private void initAttackBox() {
        // if(walkDir == RIGHT)
        //     attackBox = new Rectangle2D.Float(x+100, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        // else
        attackBox = new Rectangle2D.Float(x, y, (int)(35 * Juego.SCALE), (int)(20 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update(int [][] lvlData, Jugador jugador) {
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width + 5;
        else
            attackBox.x = hitbox.x + hitbox.width - 5;
        attackBox.y = hitbox.y + (hitbox.height / 2f) - (attackBox.height / 2f);
        // attackBox.x = hitbox.x -attackBoxOffsetX;
        // attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        }else{
            switch (enemyState) {
                case INDIO_IDLE:
                    newState(INDIO_RUN);
                    break;
                case INDIO_RUN:
                    if (cooldownTick > 0)
                        cooldownTick--;
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        turnTowardsPlayer(jugador);
                        if(isPlayerCloseForAttack(jugador)) {
                            if(cooldownTick == 0)
                                newState(INDIO_ATTACK);
                            // En rango: se queda quieto hasta que el jugador se aleje
                        } else {
                            move(lvlData);
                        }
                    } else {
                        move(lvlData);
                    }
                    break;
                case INDIO_ATTACK:
                    if(animIndex == 0)
                        attackChecked = false;
                    if(animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);
                    break;

                default:
                    break;
            }
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(INDIO_DIE);
        } else {
            newState(INDIO_HURT);
        }
    }

    protected void updateAnimationTick(){
        animTick++;
        if(animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if(animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;

                switch (enemyState) {
                    case INDIO_ATTACK, INDIO_HURT -> { enemyState = INDIO_IDLE; cooldownTick = cooldownMax; }
                    case INDIO_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(INDIO_IDLE);
        alive = true;
        airSpeed = 0;
        cooldownTick = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        // if(walkDir == LEFT)
        //     attackBox = new Rectangle2D.Float(x-10, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        // else
        //     attackBox = new Rectangle2D.Float(x+10, y-30, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));

        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return INDIO_WIDTH/2;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == LEFT)//return walkDir == LEFT ? -1 : 1;
            return -1;
        else
            return 1;
    }

}
