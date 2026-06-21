package Elementos;

import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;
// import static utilz.MetodosAyuda.CanMoveHere;
// import static utilz.MetodosAyuda.GetEntityYPosUnderRoofOrAboveFloor;
// import static utilz.MetodosAyuda.IsEntityOnFloor;
// import static utilz.MetodosAyuda.IsFloor;
// import static utilz.MetodosAyuda.IsSolid;

public class Hyena extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int cooldown = 0;
    private static final int COOLDOWN_MAX = 80;

    public Hyena(float x, float y) {
        super(x, y, HYENA_WIDTH, HYENA_HEIGHT, HYENA);
                              //VELO_WIDTH/3            //VELO_HEIGHT/3 arreglao
            initHitbox(x, y, (int)(28 * Juego.SCALE), (int)(20 * Juego.SCALE));
        
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
        // if (walkDir == LEFT)
        //     attackBox.x = hitbox.x - attackBox.width +10;
        // else
        //     attackBox.x = hitbox.x + hitbox.width -10; // el attackbox se dibuja bien falta que funcione bien
        
        // attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador3 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        }else{
            switch (enemyState) {
                case HYENA_IDLE:
                    if (cooldown>0) break;
                    newState(HYENA_RUN);
                    break;
                case HYENA_RUN:
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)){
                        turnTowardsPlayer(jugador);
                        if(attackBox.intersects(jugador.hitbox)){
                            if(cooldown==0)
                                newState(HYENA_ATTACK);  
                            break;
                        }
                    }
                    move(lvlData);
                    break;
                case HYENA_ATTACK:
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
            newState(HYENA_DIE);
        } else {
            newState(HYENA_HURT);
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
                    case HYENA_ATTACK ,HYENA_HURT ->{
                        enemyState =HYENA_IDLE;
                        cooldown=COOLDOWN_MAX;
                    }
                    case HYENA_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(HYENA_IDLE);
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
            return (int)(HYENA_WIDTH/2.5);
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == LEFT)//return walkDir == LEFT ? -1 : 1;
            return 1;
        else
            return -1;
    }

    // public void setAnimIndex(int i) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'setAnimIndex'");
    // }

}
