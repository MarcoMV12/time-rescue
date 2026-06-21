package Elementos;
import static utilz.Constantes.GetEnemyDamage;
import static utilz.Constantes.GetMaxHealt;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.MetodosAyuda.*;
import utilz.AudioPlayer;
import utilz.ScoreManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import static utilz.Constantes.Direccion.*;

import Juegos.Juego;

public abstract class Enemigo extends Cascaron{
    protected int animIndex, enemyState, enemyType;
    protected int animTick, animSpeed = 15;
    protected boolean firstUpdate = true;
    protected boolean inAir = false;
    protected float airSpeed = 0f;
    protected float gravity = 0.04f * Juego.SCALE;
    protected float walkSpeed = 0.35f * Juego.SCALE;
    protected int walkDir = LEFT;
    protected int tileYPos;
    protected float attackDistance = Juego.SCALE;
    protected boolean chasingPlayer = false;
    protected int maxHealth;
    protected int currentHealth;
    protected boolean alive = true;
    protected boolean attackChecked;
    // Offsets de hitbox según dirección (subclases los configuran)
    protected float hitboxOffsetLeft  = 0f;
    protected float hitboxOffsetRight = 0f;

    protected float floorLookaheadLeft  = Juego.TILES_SIZE * 0.1f;
    protected float floorLookaheadRight = Juego.TILES_SIZE * 0.1f;

    protected int attackCooldown = 0;
    protected int attackCooldownMax = 90;

    public Enemigo(float x, float y, int w, int h, int enemyType) {
        super(x, y, w, h);
        this.enemyType = enemyType;
        //initHitbox(x, y, w, h);
        //drawHitbox(null, enemyType);
        maxHealth = GetMaxHealt(enemyType);
        currentHealth = maxHealth;

        
    }

    protected void updateHitboxOffset() {
        if (walkDir == LEFT)
            hitbox.x = x + hitboxOffsetLeft;
        else
            hitbox.x = x + hitboxOffsetRight;
    }

    protected void firstUpdateCheck(int [][] lvlData) {
        if (!IsEntityOnFloor(hitbox, lvlData)) 
                inAir = true;
        
        tileYPos = (int)(hitbox.y / Juego.TILES_SIZE);
            firstUpdate = false;
    }

    protected void updateInAir(int [][] lvlData) {
        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += gravity; 
        }
        else {
            inAir = false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed, lvlData);
            tileYPos = (int)(hitbox.y / Juego.TILES_SIZE);
            //airSpeed = 0;
        }
    }

    protected void move(int [][] lvlData) {
        boolean isChasingPlayer = chasingPlayer;
        chasingPlayer = false;
        //float xSpeed = 0f;

        float xSpeed   = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        float lookahead = (walkDir == LEFT) ? -floorLookaheadLeft : floorLookaheadRight;

        if (walkDir == LEFT) 
            xSpeed = -walkSpeed;
        else 
            xSpeed = walkSpeed;

        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            if (hasFloorAhead(lookahead, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }
            
            // if (IsFloor(hitbox, xSpeed, lvlData)) {
            //     hitbox.x += xSpeed;
            //     return;
            // }
            changeWalkDir();
            return;
        }
        if (!isChasingPlayer)
            changeWalkDir();

    }

    private boolean hasFloorAhead(float lookahead, int[][] lvlData) {
        if (walkDir == LEFT)
            // Solo revisa el borde frontal izquierdo
            return IsSolid(hitbox.x + lookahead,
                        hitbox.y + hitbox.height + 1, lvlData);
        else
            // Solo revisa el borde frontal derecho
            return IsSolid(hitbox.x + hitbox.width + lookahead,
                        hitbox.y + hitbox.height + 1, lvlData);
    }

    protected void turnTowardsPlayer(Jugador jugador) {
        chasingPlayer = true;
        if (jugador.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    protected boolean canSeePlayer(int[][] lvlData, Jugador3 jugador) {
        int playerTileY = (int) (jugador.getHitbox().y / Juego.TILES_SIZE);
        
        if (Math.abs(playerTileY - tileYPos)<=1) 
            if (isPlayerinRange(jugador)){
                if(IfIsSightClear(lvlData, hitbox, jugador.hitbox, tileYPos))
                    return true;
            }
        return false;
    }

    protected boolean isPlayerinRange(Jugador3 jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        
        return absVal <= attackDistance * 120;
    }

    protected void turnTowardsPlayer(Jugador3 jugador) {
        chasingPlayer = true;
        if (jugador.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Jugador3 jugador) {
        if(attackBox.intersects(jugador.hitbox)){//
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            attackChecked = true;
        }
    }


    protected void turnTowardsPlayer(Jugador4 jugador) {
        chasingPlayer = true;
        if (jugador.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    protected boolean canSeePlayer(int[][] lvlData, Jugador jugador) {
        int playerTileY = (int) (jugador.getHitbox().y / Juego.TILES_SIZE);
        
        if (Math.abs(playerTileY - tileYPos) <= 1) 
            if (isPlayerinRange(jugador)){
                if(IfIsSightClear(lvlData, hitbox, jugador.hitbox, tileYPos))
                    return true;
            }
        return false;
    }

    protected boolean canSeePlayer(int[][] lvlData, Jugador4 jugador) {
        int playerTileY = (int) (jugador.getHitbox().y / Juego.TILES_SIZE);
        
        if (playerTileY == tileYPos) 
        if (Math.abs(playerTileY - tileYPos) <= 1) 
            if (isPlayerinRange(jugador)){
                if(IfIsSightClear(lvlData, hitbox, jugador.hitbox, tileYPos))
                    return true;
            }
        return false;
    }

    protected boolean isPlayerinRange(Jugador jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        
        return absVal <= attackDistance * 120;
    }

    protected boolean isPlayerinRange(Jugador4 jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        
        return absVal <= attackDistance * 120;
    }

    protected boolean isPlayerCloseForAttack(Jugador jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        return absVal <= attackDistance;
        
    }

    protected boolean isPlayerCloseForAttack(Jugador4 jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        return absVal <= attackDistance;
    }

    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        animIndex = 0;
        animTick = 0;
    }

    public void hurt(int damage) {

    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Jugador jugador) {
        if(attackBox.intersects(jugador.hitbox)){//
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            attackChecked = true;
        }
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Jugador4 jugador) {
        if(attackBox.intersects(jugador.hitbox)){//
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            attackChecked = true;
        }
    }
    
    protected void updateAnimationTick(){

    }

    protected void changeWalkDir() {
        if (walkDir == LEFT) {
            walkDir = RIGHT;
        } else {
            walkDir = LEFT;
        }
    }

    public void resetEnemy(){

    }

    public int getAnimIndex() {
        // Nunca devolver un índice que pueda romper el arreglo
        return animIndex % Math.max(1, GetSpriteAmount(enemyType, enemyState));
    }
    public int getEnemyState() {
        return enemyState;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        
    }
    
    protected boolean isPlayerCloseForAttack(Jugador2 jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        return absVal <= attackDistance;
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Jugador2 jugador) {
        if(attackBox.intersects(jugador.hitbox)){//
            jugador.changeHealth(-GetEnemyDamage(enemyType));
            attackChecked = true;
        }
    }

    protected void turnTowardsPlayer(Jugador2 jugador) {
        chasingPlayer = true;
        if (jugador.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    protected boolean canSeePlayer(int[][] lvlData, Jugador2 jugador) {
        int playerTileY = (int) (jugador.getHitbox().y / Juego.TILES_SIZE);

        if (Math.abs(playerTileY - tileYPos) <= 1)
            if (isPlayerinRange(jugador))
                if (IfIsSightClear(lvlData, hitbox, jugador.hitbox, tileYPos))
                    return true;
        return false;
    }

    protected boolean isPlayerinRange(Jugador2 jugador) {
        int absVal = (int) Math.abs(jugador.hitbox.x - hitbox.x);
        
        return absVal <= attackDistance * 120;
    }

    // 4
    protected void onDeath() {
        alive = false;
        ScoreManager.getInstance().addXP(ScoreManager.XP_MATAR_ENEMIGO);
    }

    protected void onHurt() {

    }


}
