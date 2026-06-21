package Elementos;

import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Armado extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int cooldown = 0;//este es el cooldown el otro es para reiniciarlo cuando el cooldown se termine
    private static final int COOLDOWN_MAX = 80;//200=1seg CAMBIAR DEPENDIENDO EL COOLDOWN ADECUADO

    public Armado(float x, float y) {
        super(x, y, ARMAD_WIDTH, ARMAD_HEIGHT, ARMADURA);
            initHitbox(x, y, (int)(18 * Juego.SCALE), (int)(30 * Juego.SCALE));
        
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update3(int [][] lvlData, Jugador3 jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        if(cooldown>0)
            cooldown--;
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width ;
        else
            attackBox.x = hitbox.x + hitbox.width ;
        attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador3 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        }else{
            switch (enemyState) {
                case ARMAD_IDLE:
                    if(cooldown > 0) break;
                    newState(ARMAD_RUN);
                    break;
                case ARMAD_RUN:
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)){
                        turnTowardsPlayer(jugador);
                        if(attackBox.intersects(jugador.hitbox)) {
                            if(cooldown == 0) newState(ARMAD_ATTACK);
                            break;
                        }
                    }
                    move(lvlData);
                    break;
                case ARMAD_ATTACK:
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
            newState(ARMAD_DIE);
        } else {
            newState(ARMAD_HURT);
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
                    case ARMAD_ATTACK , ARMAD_HURT ->{ 
                        enemyState = ARMAD_IDLE;
                        cooldown=COOLDOWN_MAX;
                    }
                    case ARMAD_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(ARMAD_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)(hitbox.y-yLvlOffset), (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX,int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)(attackBox.y-lvlOffsetY), (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == LEFT)
            return (int)(ARMAD_WIDTH/2.5);
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
