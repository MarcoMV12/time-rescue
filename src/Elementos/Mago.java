package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Mago extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int cooldown = 0;
    private static final int COOLDOWN_MAX = 600;

    public Mago(float x, float y) {
        super(x, y, MAGO_WIDTH, MAGO_HEIGHT, MAGO);
        initHitbox(x, y, (int)(25*Juego.SCALE), (int)(32 * Juego.SCALE));
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(30 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update3(int [][] lvlData, Jugador3 jugador) {
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateAttackBox();
        if(cooldown>0)
            cooldown--;
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width + 10;
        else
            attackBox.x = hitbox.x + hitbox.width - 10;
        attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador3 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        }else{
            switch (enemyState) {
                case MAGO_IDLE:
                    newState(MAGO_RUN);
                    break;
                case MAGO_RUN:
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        turnTowardsPlayer(jugador);
                        if(attackBox.intersects(jugador.hitbox)){
                            if(cooldown==0)
                                newState(MAGO_ATTACK); 
                        }
                    }
                    move(lvlData);
                    break;
                    case MAGO_ATTACK:
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
            newState(MAGO_DIE);
        } else {
            if(cooldown>0){
                newState(MAGO_HURT);
                return;
            }
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
                    case MAGO_ATTACK, MAGO_HURT ->{
                        enemyState = MAGO_IDLE;
                        cooldown=COOLDOWN_MAX;
                    }
                    case MAGO_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(MAGO_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)(hitbox.y-yLvlOffset), (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX, int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)(attackBox.y-lvlOffsetY), (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return MAGO_WIDTH;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == RIGHT)//return walkDir == LEFT ? -1 : 1;
            return 1;
        else
            return -1;
    }

}
