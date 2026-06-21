package Elementos;
import Elementos.Personaje;
import Juegos.Juego;
import utilz.LoadSave;

import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;

//import Elementos.ItemPeso;
//mport Elementos.Contrapeso;

import static utilz.Constantes.GetNoSprite;
import static utilz.Constantes.ConstantesJugador.*;
import static utilz.Constantes.Direccion.RIGHT;
import static utilz.MetodosAyuda.*;

public class Jugador extends Cascaron {
    private BufferedImage[][] idLeAni;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO;
    private int playerDirec = -1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean up, down, left, right, jump, interact;
    private float playerSpeed = 1.5f;
    private int[][] lvlData;
    private float xDrawOffset = 12 * Juego.SCALE;
    private float yDrawOffset = 4 * Juego.SCALE;

    //Saltar / Gravedad
    private float airSpeed = 0f;
    private float gravity = 0.04f * Juego.SCALE;
    private float jumpSpeed = -2.25f * Juego.SCALE;
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
    private int daño;
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
    private Playing playing;
    private ItemPeso itemEnMano = null;
    private Contrapeso contrapesEnMano  = null;

    private boolean modoResortera  = false;
    private boolean disparar       = false;
    private boolean disparoComprobado = false;

    private Personaje personajeActual = Personaje.CAVERNICOLA;

    public Jugador(float x, float y, int w, int h, Playing playing) {
        super(x, y, w, h);
        this.playing = playing;
        aplicarStats(personajeActual);
        loadAnimation();
        initHitbox(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        initAttackBox();
    }

    private void aplicarStats(Personaje p) {
        this.maxHealth = p.maxHp;
        this.currentHealth = p.maxHp;
        this.playerSpeed = p.velocidad;
        this.daño = p.dano;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(20 * Juego.SCALE));

    }

    public void aplicarPersonaje(Personaje p) {
        this.personajeActual = p;
        aplicarStats(p);
        cargarSpritesPersonaje(p);
        resetAll();
    }

    private void cargarSpritesPersonaje(Personaje p) {
        if (p == Personaje.CABALLERO) {
            cargarSpritesGuerrero();
            return;
        }
        if (p == Personaje.CAVERNICOLA) {
            cargarSpritesCaveman();
            return;
        }
        BufferedImage img = LoadSave.GetSpriteAtlas(p.spriteAtlas);
        if (img == null) {
            // Fallback al sprite del CAVEMAN si no existe el .png del personaje
            System.out.println("[Jugador] Sprite no encontrado: " + p.spriteAtlas
                + " — usando fallback CAVERNICOLA");
            img = LoadSave.GetSpriteAtlas(Personaje.CAVERNICOLA.spriteAtlas);
        }
        idLeAni = new BufferedImage[11][10];
        if (img == null) return; // ni siquiera el fallback existe

        int sw = 75, sh = 65;
        int maxCols = Math.min(idLeAni[0].length, img.getWidth()  / sw);
        int maxRows = Math.min(idLeAni.length,    img.getHeight() / sh);
        for (int j = 0; j < maxRows; j++) {
            for (int i = 0; i < maxCols; i++) {
                idLeAni[j][i] = img.getSubimage(i * sw, j * sh, sw, sh);
            }
        }
    }

    private void cargarSpritesCaveman() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS_CAVEMAN_R);
        if (img == null) {
            System.out.println("[Jugador] cavemanR.png no encontrado — usando fallback caveman3.png");
            img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        }
        idLeAni = new BufferedImage[17][10];
        if (img == null) return;
        int sw = 75, sh = 65;
        int maxCols = Math.min(idLeAni[0].length, img.getWidth() / sw);
        int maxRows = Math.min(idLeAni.length, img.getHeight() / sh);
        for (int j = 0; j < maxRows; j++)
            for (int i = 0; i < maxCols; i++)
                idLeAni[j][i] = img.getSubimage(i * sw, j * sh, sw, sh);
    }

    private void cargarSpritesGuerrero() {
        idLeAni = new BufferedImage[11][10];
        // Mapea cada acción del jugador a la carpeta/prefijo del Egyptian Sentry.
        // El conteo coincide con GetNoSprite(playerAction).
        cargarFila(INACTIVO,        "Idle",                "Idle",                5);
        cargarFila(CORRER,          "Running",             "Running",             8);
        cargarFila(SALTAR,          "Jump Start",          "Jump Start",          5);
        cargarFila(CAYENDO,         "Falling Down",        "Falling Down",        6);
        cargarFila(ATACAR_BRINCAR,  "Slashing in The Air", "Slashing in The Air", 5);
        cargarFila(ATACAR,          "Slashing",            "Slashing",            5);
        cargarFila(AGACHAR,         "Hurt",                "Hurt",                3);
    }

    private void cargarFila(int fila, String folder, String prefix, int count) {
        BufferedImage[] frames = LoadSave.GetFrameSequence(folder, prefix, 0, count);
        int max = Math.min(frames.length, idLeAni[fila].length);
        for (int i = 0; i < max; i++) {
            idLeAni[fila][i] = frames[i];
        }
    }

    public void update() {
        updateHealthBar();
        if(currentHealth <= 0){
            playing.setGameOver(true);
            return;
        }

        // if(hitbox.y > Juego.GAME_HEIGHT){
        //     playing.setGameOver(true); 
        //     return;
        // }

        updateAttackBox();

        if(attacking) {
            if (modoResortera) {
                // projectile spawn is driven by disparar flag, set in actualizarAnim()
            } else {
                checkAttack();
            }
        }

        actualizarAnim();
        colocarAnim();
        ActuPosicion();
        if (moving) {
            checkObjectTouched();
        }
        // loadLvlData(LoadSave.GetLevelData());
    }

    private void checkObjectTouched() {
        playing.checkObjectTouched(hitbox);
    }

    private void checkAttack() {
        if(attackChecked || animInd < 1)
            return;
        attackChecked = true;
        //System.out.println("ATAQUE EJECUTADO. attackBox=" + attackBox);
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    private void updateAttackBox() {
        // if(right){
        //     attackBox.x = hitbox.x + hitbox.width + (int)(10 * Juego.SCALE);  
        // }
        // else if(left){
        //     attackBox.x = hitbox.x - attackBox.width - (int)(10 * Juego.SCALE);
        // }

        if (flipW == 1) {  // mira a la derecha
            attackBox.x = hitbox.x + hitbox.width + (int)(2 * Juego.SCALE);
        } else {            // mira a la izquierda
            attackBox.x = hitbox.x - attackBox.width - (int)(2 * Juego.SCALE);
        }
        
        attackBox.y = hitbox.y + (Juego.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g,int LvlOffset) {
        int drawX = (int)(hitbox.x - xDrawOffset) - LvlOffset + flipX;
        if (flipW == -1)
            drawX -= (int)(w - (2 * xDrawOffset) -18);

        g.drawImage(idLeAni[playerAction][animInd],
                drawX,
                (int) (hitbox.y - yDrawOffset),
                w * flipW, h, null);
        //drawHitbox(g, LvlOffset);
        //drawAttackBox(g, LvlOffset);

        drawUI(g);

        //       Dibuja el item sobre la cabeza del jugador
        if (itemEnMano != null)
            itemEnMano.drawEnMano(g, hitbox.x, hitbox.y, LvlOffset);

        if (contrapesEnMano != null)
            contrapesEnMano.drawEnMano(g, hitbox.x, hitbox.y, LvlOffset);
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

        if (modoResortera) {
            if (attacking) {
                playerAction = ATACAR_RESORTERA;
            } else if (inAir) {
                playerAction = airSpeed < 0 ? SALTAR_RESORTERA : CAYENDO_RESORTERA;
            } else if (down && moving) {
                playerAction = CAMINAR_AGACHADO;
            } else if (down) {
                playerAction = AGACHAR;
            } else if (moving) {
                playerAction = CAMINAR_RESORTERA;
            } else {
                playerAction = INACTIVO_RESORTERA;
            }
        } else {
            if (attacking && inAir) {
                playerAction = ATACAR_BRINCAR;
            } else if (attacking) {
                playerAction = ATACAR;
            } else if (inAir) {
                if (airSpeed < 0)
                    playerAction = SALTAR;
                else
                    playerAction = CAYENDO;
            } else if (down && moving) {
                playerAction = CAMINAR_AGACHADO;
            } else if (down) {
                playerAction = AGACHAR;
            } else if (moving) {
                playerAction = CORRER;
            } else {
                playerAction = INACTIVO;
            }
        }

        if (startAnim != playerAction)
            resetAnimTick();
    }

    private void resetAnimTick() {
        this.animTick = 0;
        animInd = 0;
    }

    private void actualizarAnim() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            if (animInd >= GetNoSprite(playerAction)) {
                animInd = 0;
                attacking = false;
                attackChecked = false;
                disparoComprobado = false;
            }
        }
        if (playerAction == ATACAR_RESORTERA && animInd == 4 && !disparoComprobado) {
            disparar = true;
            disparoComprobado = true;
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

    public void ActuPosicion() {
        moving = false;
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

    public void setInteract(boolean v)  { this.interact = v; }
    public boolean isInteract()         { return interact; }
    public float getAirSpeed()          { return airSpeed; }

    private void jump() {
        if(inAir) return;
        inAir=true;
        airSpeed=jumpSpeed;        
    }

    // private void loadAnimation() {
    //     BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
    //     idLeAni = new BufferedImage[11][10];
    //     for (int j = 0; j < idLeAni.length; j++) {
    //         for (int i = 0; i < idLeAni[j].length; i++) {
    //             idLeAni[j][i] = img.getSubimage(i * 75, j * 65, 75, 65);
    //         }
    //     }

    //     barraImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    // }

    private void loadAnimation() {
        cargarSpritesPersonaje(personajeActual);
        barraImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

    public void resetAll() {
        resetDirBoolean();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = INACTIVO;
        currentHealth = maxHealth;
        playerDirec = RIGHT;

        hitbox.x = x;
        hitbox.y = y;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;

        itemEnMano      = null;
        contrapesEnMano = null;
    }

    public boolean tieneItem() {
        return itemEnMano != null;
    }

    public void recogerItem(ItemPeso item) {
        if (itemEnMano != null) return;
        itemEnMano = item;
        item.setRecogido(true);
    }
    
    public ItemPeso soltarItem() {
        ItemPeso temp = itemEnMano;
        itemEnMano = null;
        return temp;
    }
    
    public ItemPeso getItemEnMano() {
        return itemEnMano;
    }

    public void recogerContrapeso(Contrapeso c) {
        if (contrapesEnMano != null) return;
        contrapesEnMano = c;
        c.setEnMano(true);
    }
    
    public Contrapeso getContrapeso() {
        return contrapesEnMano;
    }
    
    public void soltarContrapeso() {
        if (contrapesEnMano == null) return;
        // Lo deja caer en frente del jugador
        contrapesEnMano.getHitbox().x = hitbox.x + (flipW == 1 ? hitbox.width + 5 : -30);
        contrapesEnMano.getHitbox().y = hitbox.y;
        contrapesEnMano.setEnMano(false);
        contrapesEnMano = null;
    }

    public int getDano() {
        return daño;
    }

    public Personaje getPersonajeActual() {
        return personajeActual;
    }

    public void toggleModoResortera() {
        modoResortera = !modoResortera;
    }

    public void setModoResortera(boolean b) {
        modoResortera = b;
    }

    public boolean isDisparar() {
        return disparar;
    }

    public void resetDisparar() {
        disparar = false;
    }

    public int getFlipW() {
        return flipW;
    }

}
