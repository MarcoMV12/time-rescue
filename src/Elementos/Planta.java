package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Planta extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    public Planta(float x, float y) {
        super(x, y, PLANT_WIDTH, PLANT_HEIGHT, PLANT);
                              //PLANT_WIDTH/3            //PLANT_HEIGHT/3 arreglao
        initHitbox(x+10, y+5, (int)(20 * Juego.SCALE), (int)(33 * Juego.SCALE));

        inAir = true;
        initAttackBox();
    }

    protected boolean isPlayerinRange(Jugador jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        
        return absVal <= attackDistance+50;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update(int [][] lvlData, Jugador jugador) {
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width ;
        else
            attackBox.x = hitbox.x + hitbox.width ;
        attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case PLANT_IDLE:
                    if (canSeePlayer(lvlData, jugador)) {
                        turnTowardsPlayer(jugador);
                            newState(PLANT_ATTACK);
                    }
                    break;
                case PLANT_CHOP:
                    if (animIndex >= GetSpriteAmount(enemyType, PLANT_CHOP) - 1)
                        newState(PLANT_IDLE);
                    break;
                case PLANT_ATTACK:
                    if (animIndex == 0)
                        attackChecked = false;
                    if (animIndex == 1 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);
                    if (animIndex >= GetSpriteAmount(enemyType, PLANT_ATTACK) - 1)
                        newState(PLANT_CHOP);
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
            newState(PLANT_DIE);
        } else {
            newState(PLANT_HURT);
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
                    case PLANT_ATTACK, PLANT_HURT, PLANT_CHOP -> enemyState = PLANT_IDLE;
                    case PLANT_DIE -> alive = false;
                }          
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(PLANT_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return PLANT_WIDTH;
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
