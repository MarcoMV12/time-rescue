package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Bat extends Enemigo{
    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int cooldown = 0;
    private static final int COOLDOWN_MAX = 80;


    public Bat(float x, float y) {
        super(x, y, BAT_WIDTH, BAT_HEIGHT, BAT);
            initHitbox(x, y, (int)(20 * Juego.SCALE), (int)(20 * Juego.SCALE));
        
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(10 * Juego.SCALE), (int)(20 * Juego.SCALE));
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
            firstUpdate = false;

        tileYPos = (int)(hitbox.y / Juego.TILES_SIZE);

        switch (enemyState) {
            case BAT_IDLE:
                if (cooldown>0) break;
                newState(BAT_RUN);
                break;
            case BAT_RUN:
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    if (attackBox.intersects(jugador.hitbox)){
                        if(cooldown==0)
                            newState(BAT_ATTACK);
                        break;
                    }
                }
                moveBat(lvlData);
                break;
            case BAT_ATTACK:
                if (animIndex == 0)
                    attackChecked = false;
                if (animIndex == 3 && !attackChecked)
                    checkEnemyHit(attackBox, jugador);
                break;

            default:
                break;
        }
    }

    private void moveBat(int[][] lvlData) {
        float xSpeed = walkDir == LEFT ? -walkSpeed : walkSpeed;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            hitbox.x += xSpeed;
        else
            changeWalkDir();
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(BAT_DIE);
        } else {
            newState(BAT_HURT);
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
                    case BAT_ATTACK, BAT_HURT ->{ 
                        enemyState = BAT_IDLE;
                        cooldown=COOLDOWN_MAX;
                    }
                    case BAT_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(BAT_IDLE);
        alive = true;
        airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset, int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)(hitbox.y-lvlOffsetY), (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX, int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)(attackBox.y-lvlOffsetY), (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return BAT_WIDTH/2;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == RIGHT)
            return 1;
        else
            return -1;
    }

}
