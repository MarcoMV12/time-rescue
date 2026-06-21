package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Ninja extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int attackDir; 

    public Ninja(float x, float y) {
        super(x, y, NINJA_WIDTH, NINJA_HEIGHT, NINJA);
        attackDistance = 80 * Juego.SCALE; 
        initHitbox(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        tileYPos = (int)(y / Juego.TILES_SIZE); 
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, 
            (int)(60 * Juego.SCALE), 
            (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update4(int[][] lvlData, Jugador4   jugador) {
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
                case NINJA_IDLE:
                    newState(NINJA_RUN);
                    break;

                case NINJA_RUN:
                    if (canSeePlayer(lvlData, jugador) && IsFloor(hitbox, airSpeed, lvlData)) {
                        if (isPlayerCloseForAttack(jugador)) {
                            attackDir = walkDir;    
                            attackChecked = false;   
                            newState(NINJA_ATTACK);
                        }
                        turnTowardsPlayer(jugador);   
                    }
                    move(lvlData);
                    break;

                case NINJA_ATTACK:
                    if (animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);

                    if (!isPlayerCloseForAttack(jugador)) {
                        newState(NINJA_RUN);
                        break;
                    }
                    break;

                default:
                    break;
            }
        }
    }

        @Override
    protected void onDeath() {
        newState(NINJA_DIE); 
    }

    @Override
    protected void onHurt() {
        newState(NINJA_HURT);
    }

    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return; 
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(NINJA_DIE);
        } else {
            newState(NINJA_HURT);
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
                    case NINJA_ATTACK, NINJA_HURT -> enemyState = NINJA_IDLE;
                    case NINJA_DIE -> alive = false;
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
        tileYPos = (int)(y / Juego.TILES_SIZE);
        currentHealth = maxHealth;
        newState(NINJA_IDLE);
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
        int dir = (enemyState == NINJA_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return NINJA_WIDTH / 2;
        else
            return 0;
    }

    public int flipW() {
        int dir = (enemyState == NINJA_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return -1;
        else
            return 1;
    }
}