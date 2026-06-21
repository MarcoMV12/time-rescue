package Elementos;

import Juegos.Juego;
import utilz.LoadSave;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;

import static utilz.Constantes.GetNoSprite3;
import static utilz.Constantes.ConstantesJugador3.*;
import static utilz.Constantes.Direccion.RIGHT;
import static utilz.MetodosAyuda.*;

public class Jugador3 extends Cascaron {
    private BufferedImage[][] idLeAni;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO;
    private int playerDirec = -1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean up, down, left, right, jump;
    private float playerSpeed = 1.5f;
    private int[][] lvlData;
    private float xDrawOffset = 35 * Juego.SCALE;
    private float yDrawOffset = 32 * Juego.SCALE;

    //Saltar / Gravedad
    private float airSpeed = 0f;
    private float gravity = .04f * Juego.SCALE;
    private float jumpSpeed = -2.8f * Juego.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Juego.SCALE;
    private boolean inAir = false;

    //BarraUI
    private BufferedImage barraImg;
    private int barraWidth = (int)(235 * Juego.SCALE);
    private int barraHeight = (int)(100 * Juego.SCALE);
    private int barraX = (int)(10 * Juego.SCALE);//10 es el margen que quiero que tenga la barra con el borde de la pantalla
    private int barraY = (int)(10 * Juego.SCALE);
    //Barra de vida
    private int healthBarWidth = (int)(160 * Juego.SCALE);
    private int healthBarHeight = (int)(8 * Juego.SCALE);
    private int healthBarXStart = (int)(55 * Juego.SCALE); //55 es el margen que quiero que tenga la barra de salud con el borde de la barra de estado, es decir, con el borde de la barra roja, para que quede centrada dentro de la barra de estado.
    private int healthBarYStart = (int)(25 * Juego.SCALE);
    //Barra de estamina
    private int manaBarWidth = (int)(160 * Juego.SCALE);
    private int manaBarHeight = (int)(8 * Juego.SCALE);
    private int manaBarXStart = (int)(55 * Juego.SCALE);
    private int manaBarYStart = (int)(70 * Juego.SCALE);
    //Vida
    private int maxHealth = 160;
    private int currentHealth = maxHealth;
    private int healthWidth = (int)(100 * Juego.SCALE);
    //Estamina
    private int maxMana = 160;
    private int currentMana = maxMana;
    private int manaWidth = (int)(100 * Juego.SCALE);
    //attackBox
    private Rectangle2D.Float attackBox;
    //voltear
    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private boolean shielding = false;
    private boolean shieldHit = false;
    private boolean hurting = false;
    private boolean hasShield = false;
    private Playing playing;

    public Jugador3(float x, float y, int w, int h, Playing playing) {
        super(x, y, w, h);
        this.playing = playing;
        loadAnimation();
        initHitbox(x, y, (int)(20 * Juego.SCALE), (int)(30 * Juego.SCALE));
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(40 * Juego.SCALE), (int)(20 * Juego.SCALE));

    }

    public void update() {
        updateHealthBar();
        if(currentHealth <= 0){
            if(playerAction!=MORIR){
                playerAction=MORIR;
                animInd=0;
                animTick=0;
            }
            actualizarAnim();
            if(playerAction==MORIR&&animInd>=GetNoSprite3(MORIR)-1)
            playing.setGameOver(true);
            return;
        }
        
         /*if(hitbox.y > Juego.GAME_HEIGHT){
             playing.setGameOver(true); 
             return;
         }*/

        updateAttackBox();

        if(attacking)
            checkAttack();

        actualizarAnim();
        colocarAnim();
        ActuPosicion();
        // loadLvlData(LoadSave.GetLevelData());
    }

    private void checkAttack() {
        if(attackChecked || animInd != 1)
            return;
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
    }

    private void updateAttackBox() {
        if(right){
            attackBox.x = hitbox.x + hitbox.width + (int)(-10 * Juego.SCALE);
            
        }
        else if(left){
            attackBox.x = hitbox.x - attackBox.width - (int)(-10 * Juego.SCALE);
        }
        
        attackBox.y = hitbox.y + (Juego.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g,int LvlOffsetX,int LvlOffsetY) {
        int drawX = (int)(hitbox.x - xDrawOffset) - LvlOffsetX + flipX;
        if (flipW == -1)
            drawX -= (int)(w - (2 * xDrawOffset) -18);

        int safeAction = playerAction;
        if (safeAction < 0 || safeAction >= idLeAni.length) safeAction = INACTIVO;
        int safeIdx = animInd;
        if (safeIdx < 0 || safeIdx >= idLeAni[safeAction].length) safeIdx = 0;
        g.drawImage(idLeAni[safeAction][safeIdx],
                drawX,
                (int) (hitbox.y - yDrawOffset)-LvlOffsetY+3,
                w * flipW, h, null);
        //drawHitbox(g, LvlOffsetX,LvlOffsetY);
        //drawAttackBox(g, LvlOffsetX,LvlOffsetY);

        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX,int lvlOffsetY) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)(attackBox.y-lvlOffsetY), (int)attackBox.width, (int)attackBox.height);
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + barraX, healthBarYStart + barraY, healthWidth, healthBarHeight);

        g.setColor(Color.blue);
        g.fillRect(manaBarXStart + barraX, manaBarYStart + barraY, manaBarWidth, manaBarHeight);

        g.drawImage(barraImg, barraX, barraY, barraWidth, barraHeight, null);
        
    }

    public void loadLvlData(int[][] getLevelData) {
        this.lvlData = getLevelData;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;
    }

    private void colocarAnim() {
        int startAnim = playerAction;
        /*if (attacking && inAir) {
            playerAction = ATACAR_BRINCAR;
        }*/
        //else if (attacking && down) {
        //    playerAction = ATACAR_AGACHAR;
        // }
        if (shielding && !inAir) {
            playerAction = CUBRISE;
            attacking = false;
        }
        else if (attacking) 
            playerAction = ATACAR1;
            // if(startAnim != ATACAR){
            //     animInd = 1;
            //     animTick = 0;
            //     return;
            // }
        
        else if (hurting) 
            playerAction = HURT;
        
        else if (inAir) {
            if (airSpeed < 0)
                playerAction = SALTAR;
            else
                playerAction = CAYENDO;
        }
        else if (down && !inAir)
            playerAction = AGACHAR;
        else if (moving)
            playerAction = CORRER;
        else
            playerAction = INACTIVO;

        if (startAnim != playerAction)
            resetAnimTick();
    }

    private void resetAnimTick() {
        this.animTick = 0;
        animInd = 0;
    }

    private void actualizarAnim() {
        if (shielding && !inAir && animInd >= 1 && !shieldHit) {
            animInd = 1;
            return;
        }
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            if (animInd >= GetNoSprite3(playerAction)) {
                if (playerAction==MORIR){
                    animInd=GetNoSprite3(MORIR)-1;
                    return;
                }
                if (playerAction == CUBRISE && shielding) {
                    animInd = 1;
                    shieldHit = false;
                    return;
                }
                animInd = 0;
                attacking = false;
                attackChecked = false;
                hurting = false;
            }
        }
    }

    public int getPlayerDirec() {
        return playerDirec;
    }

    public void setPlayerDirec(int playerDirec) {
        this.playerDirec = playerDirec;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
        //System.out.println("abajo " + moving);
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setShielding(boolean shielding) {
        if (!hasShield) return;
        this.shielding = shielding;
    }
    public boolean isShielding() { return shielding; }
    public boolean hasShield() { return hasShield; }

    public void pickUpShield() {
        hasShield = true;
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.CABALLERO2_ATLAS);
        for (int j = 0; j < idLeAni.length; j++)
            for (int i = 0; i < idLeAni[j].length; i++)
                idLeAni[j][i] = img.getSubimage(i * 128, j * 112, 128, 112);
    }

    public void ActuPosicion() {
        moving = false;
        if (shielding && !inAir) return;
        if (jump)
            jump();
        if (!left && !right && !inAir)
            return;
        float xSpeed = 0;// ySpeed = 0;
        if (left){
            xSpeed -= playerSpeed;
            flipX = (int)(w * Juego.SCALE);
            flipW = -1;
        }
        if (right){
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }
        if (!inAir && !IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                    (int) hitbox.width, (int) hitbox.height, lvlData)) {
                hitbox.y +=airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed, lvlData);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }
        }else
            updateXPos(xSpeed);
        moving=true;
    }

    private void updateXPos(float xSpeed) {
        if(CanMoveHere(hitbox.x+xSpeed, hitbox.y, 
            (int)hitbox.width, (int)hitbox.height, lvlData))
           hitbox.x+=xSpeed;
        else{
           hitbox.x=GetEntityXPosNextToWall(hitbox,xSpeed);  
          /* if(airSpeed>0)
             resetInAir();*/
        }
    }

    public void changeHealth(int value) {
        if (value < 0 && shielding && !inAir) {
            shieldHit = true;
            return;
        }
        if (value < 0)
            hurting = true;
        currentHealth += value;

        if (currentHealth < 0)
            currentHealth = 0;
            //gameOve();
        else if (currentHealth > maxHealth)
            currentHealth = maxHealth;
    }

    private void resetInAir() {
        inAir=false;
        airSpeed=0;        
    }
    

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    private void jump() {
        if(inAir) return;
        inAir=true;
        airSpeed=jumpSpeed;        
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.CABALLERO_ATLAS);
        idLeAni = new BufferedImage[10][8];
        for (int j = 0; j < idLeAni.length; j++) {
            for (int i = 0; i < idLeAni[j].length; i++) {
                idLeAni[j][i] = img.getSubimage(i * 128, j * 112, 128, 112);
            }
        }

        barraImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    // Retorna 1 si mira a la derecha, -1 si mira a la izquierda
    public int getFacingDir() {
        return flipW;
    }

    public void aplicarPersonaje(Personaje p) {
        this.maxHealth    = p.maxHp;
        this.currentHealth = p.maxHp;
        this.playerSpeed  = p.velocidad;
        // El sistema de animación de Jugador3 es específico de su spritesheet;
        // sólo se aplican los stats del personaje seleccionado.
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

    public void resetAll() {
        resetDirBoolean();
        inAir = false;
        attacking = false;
        shielding = false;
        shieldHit = false;
        hurting = false;
        moving = false;
        playerAction = INACTIVO;
        currentHealth = maxHealth;
        playerDirec = RIGHT;

        hitbox.x = x;
        hitbox.y = y;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;
    }

}
