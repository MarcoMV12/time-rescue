package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import Juegos.Juego;

public class Atcher extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private ArrayList<Proyectil3> proyectiles = new ArrayList<>();
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY = 40;

    public Atcher(float x, float y) {
        super(x, y, ATCHER_WIDTH, ATCHER_HEIGHT, ATCHER);
        initHitbox(x, y,(int)(20 * Juego.SCALE), (int)(30 * Juego.SCALE));
        
        initAttackBox();
    }

    private void initAttackBox() {
            attackBox = new Rectangle2D.Float(x, y, (int)(130 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update3(int [][] lvlData, Jugador3 jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();

        updateProyectiles(jugador);

        if (shootCooldown > 0)
            shootCooldown--;
    }

    private void updateProyectiles(Jugador3 jugador) {
        for (Proyectil3 p : proyectiles)
        if (p.isActive())
            p.update3(attackBox, jugador);
    }

    private void shoot() {
        if (shootCooldown > 0) return;
            float px = walkDir == LEFT ? hitbox.x : hitbox.x + hitbox.width;
            float py = hitbox.y + hitbox.height / 2;
            int   dir = walkDir == LEFT ? -1 : 1;
            proyectiles.add(new Proyectil3(px, py, dir));
            shootCooldown = SHOOT_DELAY;
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
                case ATCHER_IDLE:
                    newState(ATCHER_RUN);
                    break;
                case ATCHER_RUN:
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        turnTowardsPlayer(jugador);
                        if(attackBox.intersects(jugador.hitbox))
                                newState(ATCHER_ATTACK);
                    }
                    move(lvlData);
                    break;
                case ATCHER_ATTACK:
                    if(animIndex == 0)
                        attackChecked = false;
                    if(animIndex == 3 && !attackChecked) {
                        shoot();
                        attackChecked = true;
                    }
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
            newState(ATCHER_DIE);
        } else {
            newState(ATCHER_HURT);
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
                    case ATCHER_ATTACK, ATCHER_HURT -> enemyState = ATCHER_IDLE;
                    case ATCHER_DIE -> alive = false;
                }
                
            }
        }
    }

    public ArrayList<Proyectil3> getProyectiles() { return proyectiles; }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(ATCHER_IDLE);
        alive = true;
        airSpeed = 0;
        proyectiles.clear();
    }

    public void drawHitbox(Graphics g, int xLvlOffset,int yLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)(hitbox.y-yLvlOffset), (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX,int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)(attackBox.y-lvlOffsetY), (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return ATCHER_WIDTH/2;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == LEFT)
            return -1;
        else
            return 1;
    }

}
