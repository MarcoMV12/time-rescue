package Elementos;

import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.CanMoveHere;
import static utilz.MetodosAyuda.GetEntityYPosUnderRoofOrAboveFloor;
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

public class Velociraptor extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    public Velociraptor(float x, float y) {
        super(x, y, VELO_WIDTH, VELO_HEIGHT, VELO);
                              //VELO_WIDTH/3            //VELO_HEIGHT/3 arreglao
            initHitbox(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update(int [][] lvlData, Jugador jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        //updateAttackBox();
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

    public void updateBehavior(int [][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        }else{
            switch (enemyState) {
                case VELO_IDLE:
                    newState(VELO_RUN);
                    break;
                case VELO_RUN:
                    if(canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)){
                        turnTowardsPlayer(jugador);
                        if(isPlayerCloseForAttack(jugador))
                                newState(VELO_ATTACK);  
                    }
                    move(lvlData);
                    break;
                case VELO_ATTACK:
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
            newState(VELO_DIE);
        } else {
            newState(VELO_HURT);
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
                    case VELO_ATTACK, VELO_HURT -> enemyState = VELO_IDLE;
                    case VELO_DIE -> alive = false;
                }
                
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(VELO_IDLE);
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
            return VELO_WIDTH/2;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == RIGHT)//return walkDir == LEFT ? -1 : 1;
            return -1;
        else
            return 1;
    }

    protected boolean isPlayerCloseForAttack(Jugador jugador) {
        if (walkDir == RIGHT) {
            // Compara el borde derecho (cabeza) del raptor con el borde izquierdo del jugador
            float dist = jugador.hitbox.x - (hitbox.x + hitbox.width);
            return Math.abs(dist) <= attackDistance;
        }
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        return absVal <= attackDistance;
    }

    // public void setAnimIndex(int i) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'setAnimIndex'");
    // }

}
