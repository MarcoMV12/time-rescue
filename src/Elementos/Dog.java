package Elementos;

import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import Juegos.Juego;

public class Dog extends Enemigo{

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int attackDir;

    public Dog(float x, float y) {
        super(x, y, DOG_WIDTH, DOG_HEIGHT, DOG);
        initHitbox(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackDistance = 80 * Juego.SCALE; 
        initAttackBox();
    }

        @Override
    protected void onDeath() {
        newState(DOG_DIE);
    }

    @Override
    protected void onHurt() {
        newState(DOG_HURT);
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update4(int[][] lvlData, Jugador4 jugador4) {
        updateBehavior(lvlData, jugador4);   
        updateAttackBox();                  
        updateAnimationTick();
    }

    private void updateAttackBox() {
        if (attackDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

        public void updateBehavior(int[][] lvlData, Jugador4 jugador4) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case DOG_DIE: 
                    break;

                case DOG_IDLE:
                    newState(DOG_RUN);
                    break;

                case DOG_RUN:
                    if (canSeePlayer(lvlData, jugador4) && IsFloor(hitbox, airSpeed, lvlData)) {
                        if (isPlayerCloseForAttack(jugador4)) {
                            attackDir = walkDir;
                            attackChecked = false;
                            newState(DOG_ATTACK);
                            break;
                        }
                        turnTowardsPlayer(jugador4);
                    }
                    move(lvlData);
                    break;

                case DOG_ATTACK:
                    if (animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador4);
                    break;

                default:
                    break;
            }
        }
    }
    
    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return; // 🔥
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            onDeath();
        } else {
            onHurt();
        }
    }

    protected void updateAnimationTick(){
        animTick++;
        if(animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if(animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                switch (enemyState) {
                    case DOG_ATTACK, DOG_HURT -> {
                        animIndex = 0;
                        enemyState = DOG_IDLE;
                    }
                    case DOG_DIE -> {
                        animIndex = GetSpriteAmount(enemyType, DOG_DIE) - 1;
                        alive = false;
                    }
                    default -> animIndex = 0;
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
        newState(DOG_IDLE);
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
    int dir = (enemyState == DOG_ATTACK) ? attackDir : walkDir; 
    if (dir == LEFT)
        return DOG_WIDTH / 2;
    else
        return 0;
}

    public int flipW() {
        int dir = (enemyState == DOG_ATTACK) ? attackDir : walkDir; 
        if (dir == LEFT)
            return -1;
        else
            return 1;
    }
}
