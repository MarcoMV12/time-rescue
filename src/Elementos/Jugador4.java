package Elementos;

import Juegos.Juego;
import utilz.LoadSave;
import utilz.MetodosAyuda;
import Elementos.Door;
import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;
import static utilz.Constantes.ConstantesJugador4.*;
import static utilz.Constantes.Direccion.RIGHT;
import static utilz.Constantes.*;
import static utilz.MetodosAyuda.*;


public class Jugador4 extends Cascaron {
    private BufferedImage[][] idLeAni;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO4;
    private int playerDirec = -1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean up, down, left, right, jump;
    private float playerSpeed = 1.8f;
    private int[][] lvlData;
    private float xDrawOffset = 6 * Juego.SCALE;
    private float yDrawOffset = 2 * Juego.SCALE;
    //private boolean hasWeapon = false;

    //Saltar / Gravedad
    private float airSpeed = 0f;
    private float gravity = 0.04f * Juego.SCALE;
    private float jumpSpeed = -2.7f * Juego.SCALE;
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
    private int maxHealth = 100;
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
    private ArrayList<PlayerBullet> bullets = new ArrayList<>();

    private boolean attackChecked;
    private Playing playing;

    private boolean hasWeapon = false;

    public Jugador4(float x, float y, int w, int h, Playing playing) {
        super(x, y, w, h);
        this.playing = playing;
        loadAnimation();
        initHitbox(x, y, (int)(18 * Juego.SCALE), (int)(30 * Juego.SCALE)); // ancho 18, alto 30
        initAttackBox();
    }

    public void setHasWeapon(boolean value) {
        hasWeapon = value;
    }

    public boolean hasWeapon() {
        return hasWeapon;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(15 * Juego.SCALE), (int)(15 * Juego.SCALE));

    }

    private java.util.ArrayList<Door> doors;
    

    public void update4(int[][] lvlData, java.util.ArrayList<Door> doors) {

        this.lvlData = lvlData;
        this.doors = doors;

        if (MetodosAyuda.IsTrapTile(hitbox.x, hitbox.y + hitbox.height, lvlData)) {
            changeHealth(-10);
        }
        updateHealthBar();
        if(currentHealth <= 0){
            playing.setGameOver(true);
            return;
        }

        updateAttackBox();

        if(attacking)
            checkAttack();

        actualizarAnim();
        colocarAnim();
        ActuPosicion();
        for (int i = 0; i < bullets.size(); i++) {
            PlayerBullet b = bullets.get(i);
            b.update();

            if (!b.isActive()) {
                bullets.remove(i);
                i--;
                continue;
            }


            if (playing.checkEnemyHitAndReturn(b.getHitbox())) {
                b.deactivate();
                bullets.remove(i);
                i--;
            } 
        }
    }

    

    private void checkAttack() {
        if (attackChecked) return;
        attackChecked = true;

        if (hasWeapon) {
            shoot();
        } else {
            playing.checkEnemyHit(attackBox);
        }
    }

    private void shoot() {
        float bulletX;
        float bulletY = hitbox.y + hitbox.height / 2 - 16;

        int dir = flipW; 

        if (flipW == -1) {
            bulletX = hitbox.x - 32;
        } else {
            bulletX = hitbox.x + hitbox.width;
        }

        bullets.add(new PlayerBullet(bulletX, bulletY, dir));
    }

    
    private void updateAttackBox() {
        if(right){
            attackBox.x = hitbox.x + hitbox.width + (int)(10 * Juego.SCALE);
            
        }
        else if(left){
            attackBox.x = hitbox.x - attackBox.width - (int)(10 * Juego.SCALE);
        }
        
        attackBox.y = hitbox.y + (Juego.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int LvlOffset) {
        int spriteW = 48;
        int spriteH = 96;

        float hitboxCenterX = hitbox.x + hitbox.width / 2f;
        int drawX = (int)(hitboxCenterX - spriteW / 2f) - LvlOffset;
        int drawY = (int)(hitbox.y + hitbox.height - spriteH) + 15;

        // offset por animación para compensar diferencias en el spritesheet
        switch (playerAction) {
            case ATACAR_ARMA4: drawY += 12; break; 
            case ATACAR4:      drawY += 5;  break;
            case AGACHAR4:     drawY += 15; break;
            case SALTAR4:      drawY -= 5;  break;
            case CORRER4:       drawY += 5;  break;
            case CORRER_ARMA4:  drawY += 15;  break;
            default: break;
        }

        if (flipW == -1)
            drawX = (int)(hitboxCenterX + spriteW / 2f) - LvlOffset;

        g.drawImage(idLeAni[playerAction][animInd],
                drawX, drawY,
                spriteW * flipW, spriteH, null);

        //g.setColor(Color.GREEN);
        //g.drawRect((int)(hitbox.x - LvlOffset), (int)hitbox.y,
        //        (int)hitbox.width, (int)hitbox.height);

        drawUI(g);
        for (PlayerBullet b : bullets)
            b.render(g, LvlOffset);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
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

        if (attacking && inAir) {
            playerAction = ATACAR_BRINCAR4;
        }
        else if (attacking) {
            playerAction = hasWeapon ? ATACAR_ARMA4 : ATACAR4;
        }
        else if (inAir) {
            if (airSpeed < 0)
                playerAction = SALTAR4  ;
            else
                playerAction = CAYENDO4;
        }
        else if (down)
            playerAction = AGACHAR4 ;
        else if (moving) {
            playerAction = hasWeapon ? CORRER_ARMA4 : CORRER4;
        }
        else
            playerAction = INACTIVO4;

        if (startAnim != playerAction)
            resetAnimTick();
    }

    private void actualizarAnim() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            if (animInd >= GetNoSprite4(playerAction)) {
                animInd = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    private void resetAnimTick() {
        this.animTick = 0;
        animInd = 0;
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
        if (attacking) {
            attackChecked = false; 
        }
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

    public void ActuPosicion() {
        moving = false;
        if (jump)
            jump();
        if (!left && !right && !inAir)
            return;
        float xSpeed = 0;
        if (left){
            xSpeed -= playerSpeed;
            flipW = -1; // 🔥 quitado flipX
        }
        if (right){
            xSpeed += playerSpeed;
            flipW = 1; // 🔥 quitado flipX
        }
        if (!inAir && !IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                    (int) hitbox.width, (int) hitbox.height, lvlData, doors)) {
                hitbox.y += airSpeed;
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
        } else
            updateXPos(xSpeed);
        moving = true;
    }

    private void updateXPos(float xSpeed) {
        // if (CanMoveHere(hitbox.x + xSpeed, hitbox.y,
        //         (int)hitbox.width, (int)hitbox.height, lvlData, doors))
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y,
            (int)hitbox.width, (int)hitbox.height, lvlData)) 
            hitbox.x += xSpeed;
        else
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
    }

    public void changeHealth(int value) {
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
    BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS4);
    idLeAni = new BufferedImage[12][10];
    for (int j = 0; j < 12; j++) {
        for (int i = 0; i < idLeAni[j].length; i++) {
            idLeAni[j][i] = img.getSubimage(i * 64, j * 128, 64, 128);
        }
    }
    barraImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
}

    public void aplicarPersonaje(Personaje p) {
        this.maxHealth    = p.maxHp;
        this.currentHealth = p.maxHp;
        this.playerSpeed  = p.velocidad;
        // El sistema de animación de Jugador4 es específico de su spritesheet;
        // sólo se aplican los stats del personaje seleccionado.
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

    public void resetAll() {
        resetDirBoolean();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = INACTIVO4;
        currentHealth = maxHealth;
        playerDirec = RIGHT;
        hasWeapon = false;

        hitbox.x = x;
        hitbox.y = y;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;
    }
    

}
