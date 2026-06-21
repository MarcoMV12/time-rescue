package Elementos;

import GameStates.Playing;
import Juegos.Juego;
import Objects.Arena;
import Objects.Coco;
import Objects.Spike;
import utilz.AudioPlayer;
import utilz.LoadSave;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constantes.ConstantesJugador2.*;
import static utilz.Constantes.PowerUp.*;
import static utilz.MetodosAyuda.*;

public class Jugador2 extends Cascaron {
    private Playing playing;
    private BufferedImage[][] idLeAni;
    private BufferedImage[][] normalAni;
    private BufferedImage[][] anubisAni;
    private BufferedImage[][] mummyAni;
    private BufferedImage[][] baseAni;  // set elegido via aplicarPersonaje, sobrevive a resetAll
    private int transformacion = NINGUNO;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO2;
    private int playerDirec = -1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean interact = false;
    private boolean up, down, left, right, jump;
    private float playerSpeed = 1.0f;
    private int[][] lvlData;
    private float xDrawOffset;
    private float yDrawOffset;
    private int   sentryDrawW;
    private int   sentryDrawH;
    private boolean facingLeft = false;
    private Rectangle2D.Float playerAttackBox;
    private boolean attackHitChecked = false;

    private int maxHealth     = 160;
    private int currentHealth = maxHealth;

    // UI barra de vida 
    private BufferedImage barraImg;
    private static final float UI_SCALE  = 0.6f;
    private final int barraX           = (int)(10  * Juego.SCALE);
    private final int barraY           = (int)(10  * Juego.SCALE);
    private final int barraWidth       = (int)(235 * Juego.SCALE * UI_SCALE);
    private final int barraHeight      = (int)(100 * Juego.SCALE * UI_SCALE);
    private final int healthBarXStart  = (int)(55  * Juego.SCALE * UI_SCALE);
    private final int healthBarYStart  = (int)(25  * Juego.SCALE * UI_SCALE);
    private final int healthBarMaxW    = (int)(160 * Juego.SCALE * UI_SCALE);
    private final int healthBarHeight  = (int)(8   * Juego.SCALE * UI_SCALE);
    private final int staminaBarXStart = (int)(55  * Juego.SCALE * UI_SCALE);
    private final int staminaBarYStart = (int)(70  * Juego.SCALE * UI_SCALE);
    private final int staminaBarMaxW   = (int)(160 * Juego.SCALE * UI_SCALE);
    private final int staminaBarHeight = (int)(8   * Juego.SCALE * UI_SCALE);
    private int dañoTick      = 0;
    private int dañoCooldown  = 60;
    private boolean isHurt  = false;
    private boolean isDying = false;

    private float airSpeed = 0f;
    private float previousY;
    private float gravity  = 0.04f * Juego.SCALE;
    private float jumpSpeed = -2.3f * Juego.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Juego.SCALE;
    private boolean inAir = false;

    // --- Variables para la Cadena ---
    private boolean isClimbing = false;
    private float climbSpeed = 1.0f * Juego.SCALE; 
    private final int[] ID_CADENA = {31,43,78,66,54} ;

    public Jugador2(float x, float y, int w, int h, Playing playing) {
        super(x, y, w, h);
        this.playing = playing;
        loadAnimation();
        initHitbox(x, y, 20 * Juego.SCALE, 30 * Juego.SCALE);
        playerAttackBox = new Rectangle2D.Float(0, 0,
            (int)(25* Juego.SCALE), (int)(20 * Juego.SCALE));
    }

    public void update(ArrayList<Arena> arenas, ArrayList<Spike> spikes,
                       ArrayList<Coco> cocos) {
        if (isDying)             { actualizarAnim(); return; }
        if (currentHealth <= 0)  { isDying = true; newState(DEAD2); return; }
        actualizarAnim();
        colocarAnim();
        ActuPosicion();
        checkArenaCollision(arenas);
        checkSpikeCollision(spikes);
        checkCocoCollision(cocos);
        if (dañoTick > 0) dañoTick--;
    }

    private void newState(int action) {
        playerAction = action;
        animInd      = 0;
        animTick     = 0;
    }

    public void changeHealth(int value) {
        if (isDying) return;
        currentHealth += value;
        if (currentHealth < 0)              currentHealth = 0;
        else if (currentHealth > maxHealth) currentHealth = maxHealth;
        if (value < 0 && currentHealth > 0) isHurt = true;
    }

    public void checkSpikeCollision(ArrayList<Spike> spikes) {
        if (dañoTick > 0) return;
        for (Spike s : spikes) {
            if (!s.isActivo()) continue;
            if (hitbox.intersects(s.getHitbox())) {
                changeHealth(-40);
                dañoTick = dañoCooldown;
                break;
            }
        }
    }

   public void checkCocoCollision(ArrayList<Coco> cocos) {
    if (dañoTick > 0) return;
    for (Coco c : cocos) {
        if (!c.isActivo() || !c.isCayendo()) continue;
        if (hitbox.intersects(c.getHitbox())) {
            changeHealth(-maxHealth); // mata de un golpe
            dañoTick = dañoCooldown;
            break;
        }
    }
}

    public void checkArenaCollision(ArrayList<Arena> arenas) {
        for (Arena a : arenas) {
            if (!a.isActivo() || a.getAnimIndex() > 2) continue;
            Rectangle2D.Float ah = a.getHitbox();
            boolean dentroX = hitbox.x + hitbox.width > ah.x &&
                              hitbox.x < ah.x + ah.width;
            boolean encima  = hitbox.y + hitbox.height >= ah.y &&
                              hitbox.y + hitbox.height <= ah.y + 10;
            if (dentroX && encima && inAir && airSpeed > 0) {
                hitbox.y = ah.y - hitbox.height;
                resetInAir();
            }
            boolean dentroY = hitbox.y + hitbox.height > ah.y &&
                              hitbox.y < ah.y + ah.height;
            if (dentroY) {
                if (hitbox.x + hitbox.width > ah.x && hitbox.x < ah.x)
                    hitbox.x = ah.x - hitbox.width;
                if (hitbox.x < ah.x + ah.width &&
                    hitbox.x + hitbox.width > ah.x + ah.width)
                    hitbox.x = ah.x + ah.width;
            }
        }
    }

    public void loadLvlData(int[][] getLevelData) {
        this.lvlData = getLevelData;
    }

    private void colocarAnim() {
        int startAnim = playerAction;

        if (moving)     playerAction = CORRER2;
        else            playerAction = INACTIVO2;
        if (inAir)      playerAction = airSpeed < 0 ? SALTAR2 : CAYENDO2;
        if (isClimbing) playerAction = INACTIVO2;

        if (attacking) {
            if (inAir)       playerAction = ATACAR_BRINCAR2;
            else if (moving) playerAction = ATACAR_RUN2;
            else             playerAction = ATACAR2;
        }

        if (isHurt) playerAction = HURT2;           // Hurt

        if (startAnim != playerAction) resetAnimTick();
    }

    private void resetAnimTick() { animTick = 0; animInd = 0; }

    private void updateAttackBox() {
        playerAttackBox.x = facingLeft
            ? hitbox.x - playerAttackBox.width
            : hitbox.x + hitbox.width;
        playerAttackBox.y = hitbox.y + (hitbox.height - playerAttackBox.height) / 2;
    }

    private void actualizarAnim() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;

            // hit check al frame 5 (ataques tierra/corriendo) o frame 2 (aire)
            int hitFrame = (playerAction == ATACAR_BRINCAR2) ? 2 : 5;
            if ((playerAction == ATACAR2 || playerAction == ATACAR_RUN2 || playerAction == ATACAR_BRINCAR2)
                    && animInd == hitFrame && !attackHitChecked) {
                updateAttackBox();
                playing.checkEnemyHit(playerAttackBox);
                playing.checkObjectHit(playerAttackBox);
                AudioPlayer.playSfx(AudioPlayer.Sound.PLAYER_ATTACK2, AudioPlayer.SFX_VOLUME);
                attackHitChecked = true;
            }

            if (animInd >= idLeAni[playerAction].length) {
                animInd = 0;
                attacking = false;
                attackHitChecked = false;
                if (playerAction == HURT2) isHurt = false;
                if (playerAction == DEAD2) playing.setGameOver(true);
            }
        }
    }

   public void ActuPosicion() {
    previousY = hitbox.y;
    moving = false;

    // CADENA 
    boolean enCadena = false;
 int tileX  = (int)((hitbox.x + hitbox.width / 2) / Juego.TILES_SIZE);
int tileY  = (int)((hitbox.y + hitbox.height / 2) / Juego.TILES_SIZE);
int tileY2 = (int)((hitbox.y + hitbox.height * 0.3f) / Juego.TILES_SIZE);
    if (tileY >= 0 && tileY < lvlData.length &&
    tileX >= 0 && tileX < lvlData[0].length) {
    int val  = lvlData[tileY][tileX];
    int val2 = (tileY2 >= 0 && tileY2 < lvlData.length) ? lvlData[tileY2][tileX] : 0;
    if (val == 31 || val == 43 || val2 == 31 || val2 == 43 || val2== 54|| val== 66|| val== 78)
        enCadena = true;
}

    if (enCadena) {
        isClimbing = true;
        inAir    = false;
        airSpeed = 0;
    } else {
        isClimbing = false;
    }
if (enCadena && !isClimbing) {
    // solo centra UNA VEZ al entrar
    isClimbing = true;
    inAir      = false;
    airSpeed   = 0;
    int tileXCentro = tileX * Juego.TILES_SIZE + Juego.TILES_SIZE / 2;
    hitbox.x = tileXCentro - (int)(hitbox.width / 2);
} else if (!enCadena) {
    isClimbing = false;
}

if (isClimbing) {
    if (jump) {
        isClimbing = false;
        inAir      = true;
        airSpeed   = jumpSpeed;
    } else {
        if (up && CanMoveHere(hitbox.x, hitbox.y - climbSpeed,
                (int)hitbox.width, (int)hitbox.height, lvlData)) {
            hitbox.y -= climbSpeed;
            moving = true;
        }
        if (down && CanMoveHere(hitbox.x, hitbox.y + climbSpeed,
                (int)hitbox.width, (int)hitbox.height, lvlData)) {
            hitbox.y += climbSpeed;
            moving = true;
        }
        if (left || right) {
            isClimbing = false;
            inAir      = true;
            airSpeed   = 0;
        }
        return;
    }
}

    // --- MOVIMIENTO NORMAL ---
    if (jump) jump();
    if (!inAir && !IsEntityOnFloor(hitbox, lvlData)) inAir = true;
    if (!left && !right && !inAir) return;
    float xSpeed = 0;
    if (left)  { xSpeed -= playerSpeed; facingLeft = true;  }
    if (right) { xSpeed += playerSpeed; facingLeft = false; }
    if (inAir) {
        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                (int)hitbox.width, (int)hitbox.height, lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += gravity;
            updateXPos(xSpeed);
        } else {
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
            if (airSpeed > 0) resetInAir();
            else airSpeed = fallSpeedAfterCollision;
            updateXPos(xSpeed);
        }
    } else updateXPos(xSpeed);
    moving = true;
}
    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y,
                (int)hitbox.width, (int)hitbox.height, lvlData))
            hitbox.x += xSpeed;
        else
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
    }

    public void resetInAir() { inAir = false; airSpeed = 0; }
    private void jump() { 
        if (inAir) return; 
        inAir = true; 
        airSpeed = jumpSpeed; 
        AudioPlayer.playSfx(AudioPlayer.Sound.SALTO_PLAYER2, AudioPlayer.SFX_VOLUME);
    }
    public void setJump(boolean jump)       { this.jump = jump; }
    public void setLeft(boolean left)       { this.left = left; }
    public void setRight(boolean right)     { this.right = right; }
    public void setUp(boolean up)           { this.up = up; }
    public void setDown(boolean down)       { this.down = down; }
    public void setAttacking(boolean a)     { this.attacking = a; }
    public void setInteract(boolean interact) { this.interact = interact; }
    public boolean isAttacking()            { return attacking; }
    public void aplicarPersonaje(Personaje p) {
        this.maxHealth    = p.maxHp;
        this.currentHealth = p.maxHp;
        if (transformacion == NINGUNO) this.playerSpeed = p.velocidad;

        // Elegir el set de animaciones según el personaje
        String seqBase = p.frameSeqBase;
        if (seqBase == null) {
            baseAni = normalAni;
        } else if (seqBase.equals("Personajes/Anubis/PNG/PNG Sequences")) {
            baseAni = anubisAni;
        } else if (seqBase.equals("Personajes/Egyptian Mummy/PNG/PNG Sequences")) {
            baseAni = mummyAni;
        } else {
            // Egyptian Sentry (EGIPCIO) u otro → normalAni
            baseAni = normalAni;
        }
        // Aplicar sólo si no hay transformación activa
        if (transformacion == NINGUNO) idLeAni = baseAni;
    }

    public void resetDirBoolean()           { left = right = up = down = false; }

    public void resetAll() {
        resetDirBoolean();
        inAir = false; attacking = false; moving = false;
        isHurt = false; isDying = false;
        playerAction = INACTIVO2;
        currentHealth = maxHealth;
        dañoTick = 0;
        animInd = 0; animTick = 0;
        hitbox.x = x; hitbox.y = y;
        transformar(NINGUNO); // Llamamos esto para que resetee correctamente las velocidades al morir
        if (!IsEntityOnFloor(hitbox, lvlData)) inAir = true;
    }

    public void render(Graphics g, int LvlOffset) {
        int drawX = (int)(hitbox.x - xDrawOffset) - LvlOffset;
        int drawY = (int)(hitbox.y - yDrawOffset);
        if (facingLeft)
            g.drawImage(idLeAni[playerAction][animInd],
                    drawX + sentryDrawW, drawY, -sentryDrawW, sentryDrawH, null);
        else
            g.drawImage(idLeAni[playerAction][animInd],
                    drawX, drawY, sentryDrawW, sentryDrawH, null);
       // drawHitbox(g);
        if (attacking) {
            updateAttackBox();
           // g.setColor(Color.yellow);
           // g.drawRect((int)(playerAttackBox.x - LvlOffset), (int)playerAttackBox.y,
                  //     (int)playerAttackBox.width, (int)playerAttackBox.height);
        }
        drawUI(g);

    }

    private void drawUI(Graphics g) {
        int healthWidth = (int)(healthBarMaxW * (currentHealth / (float) maxHealth));

        g.setColor(Color.red);
        g.fillRect(barraX + healthBarXStart, barraY + healthBarYStart,
                   healthWidth, healthBarHeight);

        g.setColor(Color.blue);
        g.fillRect(barraX + staminaBarXStart, barraY + staminaBarYStart,
                   staminaBarMaxW, staminaBarHeight);

        g.drawImage(barraImg, barraX, barraY, barraWidth, barraHeight, null);
    }

    private void loadAnimation() {
        normalAni = new BufferedImage[9][];
        normalAni[INACTIVO2]        = LoadSave.GetFrameSequence("Idle",                "Idle",                1, 17);
        normalAni[CORRER2]          = LoadSave.GetFrameSequence("Running",             "Running",             0, 12);
        normalAni[SALTAR2]          = LoadSave.GetFrameSequence("Jump Start",          "Jump Start",          0, 6);
        normalAni[CAYENDO2]         = LoadSave.GetFrameSequence("Falling Down",        "Falling Down",        0, 6);
        normalAni[ATACAR_RUN2]      = LoadSave.GetFrameSequence("Run Slashing",        "Run Slashing",        0, 12);
        normalAni[ATACAR2]          = LoadSave.GetFrameSequence("Slashing",            "Slashing",            0, 12);
        normalAni[ATACAR_BRINCAR2]  = LoadSave.GetFrameSequence("Slashing in The Air", "Slashing in The Air", 0, 6);
        normalAni[HURT2]            = LoadSave.GetFrameSequence("Hurt",                "Hurt",                0, 12);
        normalAni[DEAD2]            = LoadSave.GetFrameSequence("Dying",               "Dying",               0, 15);

        String baseAnubis = "Personajes/Anubis/PNG/PNG Sequences";
        anubisAni = new BufferedImage[9][];
        anubisAni[INACTIVO2]       = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Idle",                "Idle",                0, 18);
        anubisAni[CORRER2]         = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Running",             "Running",             0, 12);
        anubisAni[SALTAR2]         = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Jump Start",          "Jump Start",          0, 6);
        anubisAni[CAYENDO2]        = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Falling Down",        "Falling Down",        0, 6);
        anubisAni[ATACAR_RUN2]     = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Run Slashing",        "Run Slashing",        0, 12);
        anubisAni[ATACAR2]         = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Slashing",            "Slashing",            0, 12);
        anubisAni[ATACAR_BRINCAR2] = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Slashing in The Air", "Slashing in The Air", 0, 12);
        anubisAni[HURT2]           = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Hurt",                "Hurt",                0, 12);
        anubisAni[DEAD2]           = LoadSave.GetFrameSequenceGeneric(baseAnubis, "Dying",               "Dying",               0, 15);

        String baseMummy = "Personajes/Egyptian Mummy/PNG/PNG Sequences";
        mummyAni = new BufferedImage[9][];
        mummyAni[INACTIVO2]       = LoadSave.GetFrameSequenceGeneric(baseMummy, "Idle",                "Idle",                0, 18);
        mummyAni[CORRER2]         = LoadSave.GetFrameSequenceGeneric(baseMummy, "Running",             "Running",             0, 12);
        mummyAni[SALTAR2]         = LoadSave.GetFrameSequenceGeneric(baseMummy, "Jump Start",          "Jump Start",          0, 6);
        mummyAni[CAYENDO2]        = LoadSave.GetFrameSequenceGeneric(baseMummy, "Falling Down",        "Falling Down",        0, 6);
        mummyAni[ATACAR_RUN2]     = LoadSave.GetFrameSequenceGeneric(baseMummy, "Run Slashing",        "Run Slashing",        0, 12);
        mummyAni[ATACAR2]         = LoadSave.GetFrameSequenceGeneric(baseMummy, "Slashing",            "Slashing",            0, 12);
        mummyAni[ATACAR_BRINCAR2] = LoadSave.GetFrameSequenceGeneric(baseMummy, "Slashing in The Air", "Slashing in The Air", 0, 12);
        mummyAni[HURT2]           = LoadSave.GetFrameSequenceGeneric(baseMummy, "Hurt",                "Hurt",                0, 12);
        mummyAni[DEAD2]           = LoadSave.GetFrameSequenceGeneric(baseMummy, "Dying",               "Dying",               0, 15);

        idLeAni = normalAni;

        sentryDrawW = (int)(60 * Juego.SCALE);
        sentryDrawH = (int)(60 * Juego.SCALE);
        xDrawOffset = (sentryDrawW - 20 * Juego.SCALE) / 2f;
        yDrawOffset = sentryDrawH - 31 * Juego.SCALE - 13 * Juego.SCALE;

        barraImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void transformar(int tipo) {
        transformacion = tipo;
        switch (tipo) {
            case ANUBIS -> {
                idLeAni = anubisAni;
                playerSpeed = 1.3f;
                jumpSpeed = -2.5f * Juego.SCALE;
                playerAttackBox.width = 35 * Juego.SCALE; // Mayor rango de alcance
                playerAttackBox.height = 30 * Juego.SCALE; // Área de golpe más grande
            }
            case MUMMY  -> {
                idLeAni = mummyAni;
                playerSpeed = 1.2f;
                jumpSpeed = -2.4f * Juego.SCALE;
                playerAttackBox.width = 30 * Juego.SCALE; // Alcance intermedio
                playerAttackBox.height = 20 * Juego.SCALE;
            }
            default     -> {
                idLeAni = (baseAni != null) ? baseAni : normalAni;
                playerSpeed = 1.0f;
                jumpSpeed = -2.3f * Juego.SCALE;
                playerAttackBox.width = 30 * Juego.SCALE; // Alcance base (normal)
                playerAttackBox.height = 20 * Juego.SCALE;
            }
        }
        newState(INACTIVO2);
    }

    public int getDañoAtaque() {
        return switch (transformacion) {
            case ANUBIS -> 30; // 10 daño base + 20
            case MUMMY  -> 20; // 10 daño base + 10
            default     -> 10; // Daño normal
        };
    }

    public int getTransformacion() { return transformacion; }
     private boolean tocandoCadena() {
    float xCentro = hitbox.x + (hitbox.width / 2);
    float yCentro = hitbox.y + (hitbox.height / 2);

    int tileX = (int)(xCentro / Juego.TILES_SIZE);
    int tileY = (int)(yCentro / Juego.TILES_SIZE);

    if (tileY < 0 || tileY >= lvlData.length ||
        tileX < 0 || tileX >= lvlData[0].length)
        return false;

    int valor = lvlData[tileY][tileX];
    for (int id : ID_CADENA)
        if (valor == id) return true;
    return false;
}
public float getAirSpeed() { return airSpeed; }
    public boolean isInteract() {
    return interact;
}
public float getPreviousY() {
    return previousY;
}
}