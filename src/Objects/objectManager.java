package Objects;

import static utilz.Constantes.ConstantesObjetos.*;
import static utilz.Constantes.Objetos.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Elementos.Jugador;
import Elementos.Jugador2;
import GameStates.Playing;
import Juegos.Juego;
import utilz.LoadSave;
import utilz.ScoreManager;
import static utilz.Constantes.PowerUp.*;
import Objects.Urna;

public class objectManager {

    private Playing playing;
    private BufferedImage[][] contenedorImg, coinImg, hpImg;
    private BufferedImage[] arenaImg, spikeImg, palmeraImg, cocoImg;
    private BufferedImage imgContenedorAnubis, imgContenedorMummy;
    private BufferedImage imgWeaponAnubis, imgWeaponMummy;
    private ArrayList<ArmaItem> armas  = new ArrayList<>();

    private ArrayList<ObjAlcohol> alcoholes = new ArrayList<>();
    private ArrayList<ObjCloro>   cloros    = new ArrayList<>();
    private ArrayList<ObjEscudo>  escudos   = new ArrayList<>();
    private ArrayList<Posion>     posiones  = new ArrayList<>();
    private ArrayList<PiedraResortera> piedras = new ArrayList<>();
    private int loadedFor = -1;

    // ── Resortera pickup (nivel 0) ───────────────────────────────────────────
    // Para cambiar la posición modifica estos dos valores (en píxeles)
    private static final int RESORTERA_X = 1600;
    private static final int RESORTERA_Y = 320;
    private ObjResortera objResortera = new ObjResortera(RESORTERA_X, RESORTERA_Y);

    private boolean alcoholRecogido = false;
    private boolean cloroRecogido   = false;
    private boolean primeraBotellaDialogo = false;
    private int pocionesDisponibles = 0;
    private static final int MAX_POCIONES = 2;

    // ── Arena phase management (solo nivel Egipto) ───────────────────────────
    private enum Fase { CONSTRUYENDO, CAYENDO, ESPERA }
    private Fase fase            = Fase.CONSTRUYENDO;
    private int  delayTick       = 0;
    private static final int DELAY_CONSTRUCCION = 30;
    private static final int DELAY_CAIDA        = 25;
    private int  esperaTick      = 0;
    private static final int ESPERA_MAX         = 120;
    private int  indiceActual    = 0;

    // ── Spike hurt cooldown ──────────────────────────────────────────────────
    private int spikeCooldown = 0;
    private static final int SPIKE_COOLDOWN_MAX = 90;
    private static final int SPIKE_DAMAGE       = 40;

    public objectManager(Playing playing){
        this.playing = playing;
        loadImgs();
    }

    public void checkObjectTouched(Rectangle2D.Float hitbox){
        Niveles.Level lvl = playing.getLevelManager().currentLevel();

        for (Coin c : lvl.getCoins()) {
            if (c.isActive() && hitbox.intersects(c.getHitbox())) {
                c.setActive(false);
                applyEffect(c);
            }
        }

        for (Hp v : lvl.getVidas()) {
            if (v.isActive() && hitbox.intersects(v.getHitbox())) {
                v.setActive(false);
                applyEffect(v);
            }
        }

        if (playing.getLevelManager().getNivelActual() == 1) {
            checkPlayerPickupArma(hitbox, playing.getPlayer2());
        }

        if (playing.getLevelManager().getNivelActual() == 2) {
            for (ObjAlcohol a : alcoholes)
                if (a.isActive() && hitbox.intersects(a.getHitbox())) {
                    a.setActive(false);
                    alcoholRecogido = true;
                    actualizarPociones();
                    ScoreManager.getInstance().addXP(ScoreManager.XP_RECOGER_ITEM);
                    if (!primeraBotellaDialogo) {
                        primeraBotellaDialogo = true;
                        playing.mostrarDialogoLvl3();
                    } else {
                        playing.mostrarMensaje("Mezclando alcohol y cloro se puede hacer cloroformo para vencer al mago malo");
                    }
                }
            for (ObjCloro c : cloros)
                if (c.isActive() && hitbox.intersects(c.getHitbox())) {
                    c.setActive(false);
                    cloroRecogido = true;
                    actualizarPociones();
                    ScoreManager.getInstance().addXP(ScoreManager.XP_RECOGER_ITEM);
                    if (!primeraBotellaDialogo) {
                        primeraBotellaDialogo = true;
                        playing.mostrarDialogoLvl3();
                    } else {
                        playing.mostrarMensaje("Mezclando alcohol y cloro se puede hacer cloroformo para vencer al mago malo");
                    }
                }
            for (ObjEscudo e : escudos)
                if (e.isActive() && hitbox.intersects(e.getHitbox())) {
                    e.setActive(false);
                    playing.getPlayer3().pickUpShield();
                    ScoreManager.getInstance().addXP(ScoreManager.XP_RECOGER_ITEM);
                    playing.mostrarMensaje("Mantén presionado SHIFT para cubrirte con el escudo");
                }
        }

        if (playing.getLevelManager().getNivelActual() == 0) {
            if (objResortera.isActive() && hitbox.intersects(objResortera.getHitbox())) {
                objResortera.setActive(false);
                playing.getPlayer().setModoResortera(true);
                ScoreManager.getInstance().addXP(ScoreManager.XP_RECOGER_ITEM);
                playing.mostrarMensaje("¡Resortera recogida! Usa clic izquierdo para disparar. R para cambiar modo.");
            }
        }
    }

    public void applyEffect(objeto n){
        if (n.getObjType() == COIN) {
            ScoreManager.getInstance().addMoneda();
        } else {
            ScoreManager.getInstance().addXP(ScoreManager.XP_RECOGER_ITEM);
            int nivel = playing.getLevelManager().getNivelActual();
            if (nivel == 1)      playing.getPlayer2().changeHealth(HP_VALUE);
            else if (nivel == 2) playing.getPlayer3().changeHealth(HP_VALUE);
            else                 playing.getPlayer().changeHealth(HP_VALUE);
        }
    }

    public void checkObjectHit(Rectangle2D.Float attackbox){
        Niveles.Level lvl = playing.getLevelManager().currentLevel();

        for (contenedor c : lvl.getContenedores()){
            if (!c.isActive()) continue;
            if (c.getObjType() == BARRIL_B || c.getObjType() == CAJA_B
             || c.getObjType() == POT_B    || c.getObjType() == LETRERO1_B
             || c.getObjType() == LETRERO2_B || c.getObjType() == COFRE_B)
                continue;
            if (c.getHitbox().intersects(attackbox)) {
                c.quebrar();
                break;
            }
        }
        checkUrnaHit(attackbox);
    }

    // ── Spike hurt check ─────────────────────────────────────────────────────
    public void checkSpikeHurt(Jugador player) {
        if (spikeCooldown > 0) return;
        ArrayList<Spike> spikes = playing.getLevelManager().currentLevel().getSpikes();
        for (Spike s : spikes) {
            if (s.getHitbox() != null && player.getHitbox().intersects(s.getHitbox())) {
                player.changeHealth(-SPIKE_DAMAGE);
                spikeCooldown = SPIKE_COOLDOWN_MAX;
                return;
            }
        }
    }

    // ── Arena phase management ────────────────────────────────────────────────
    private void updateFases() {
        ArrayList<Arena> arenas = playing.getLevelManager().currentLevel().getArenas();
        if (arenas.isEmpty()) return;

        switch (fase) {
            case CONSTRUYENDO -> {
                delayTick++;
                if (delayTick >= DELAY_CONSTRUCCION) {
                    delayTick = 0;
                    int idx = arenas.size() - 1 - indiceActual;
                    arenas.get(idx).aparecer();
                    indiceActual++;
                    if (indiceActual >= arenas.size()) {
                        indiceActual = 0;
                        delayTick    = 0;
                        fase         = Fase.CAYENDO;
                    }
                }
            }
            case CAYENDO -> {
                delayTick++;
                if (delayTick >= DELAY_CAIDA) {
                    delayTick = 0;
                    int idx = arenas.size() - 1 - indiceActual;
                    arenas.get(idx).activarCaida();
                    indiceActual++;
                    if (indiceActual >= arenas.size()) {
                        indiceActual = 0;
                        esperaTick   = 0;
                        fase         = Fase.ESPERA;
                    }
                }
            }
            case ESPERA -> {
                boolean todosOcultos = arenas.stream().allMatch(Arena::isOculto);
                if (todosOcultos) {
                    esperaTick++;
                    if (esperaTick >= ESPERA_MAX) {
                        esperaTick   = 0;
                        indiceActual = 0;
                        delayTick    = 0;
                        fase         = Fase.CONSTRUYENDO;
                    }
                }
            }
        }
    }

    public void loadImgs(){
        BufferedImage coinSprites = LoadSave.GetSpriteAtlas(LoadSave.COIN_SPRITES);
        coinImg = new BufferedImage[1][7];
        for (int j = 0; j < coinImg.length; j++)
            for (int i = 0; i < coinImg[j].length; i++)
                coinImg[j][i] = coinSprites.getSubimage(20 * i, 20 * j, 20, 20);

        BufferedImage hpSprites = LoadSave.GetSpriteAtlas(LoadSave.HP_SPRITES);
        hpImg = new BufferedImage[1][4];
        for (int j = 0; j < hpImg.length; j++)
            for (int i = 0; i < hpImg[j].length; i++)
                hpImg[j][i] = hpSprites.getSubimage(20 * i, 20 * j, 20, 20);

        BufferedImage contenedorSprites = LoadSave.GetSpriteAtlas(LoadSave.CONTENEDORES_ATLAS);
        contenedorImg = new BufferedImage[12][7];
        for (int j = 0; j < contenedorImg.length; j++)
            for (int i = 0; i < contenedorImg[j].length; i++)
                contenedorImg[j][i] = contenedorSprites.getSubimage(64 * i, 64 * j, 64, 64);

        arenaImg   = sliceHorizontal(LoadSave.ARENA_ATLAS,  11);
        spikeImg   = sliceHorizontal(LoadSave.SPIKE_ATLAS,  11);
        palmeraImg = sliceHorizontal(LoadSave.PALMERA_ATLAS,  1);
        cocoImg    = sliceHorizontal(LoadSave.COCO_ATLAS,    7);

        imgContenedorAnubis = LoadSave.GetSpriteAtlas(LoadSave.CONTENEDOR_ANUBIS_IMG);
        imgContenedorMummy  = LoadSave.GetSpriteAtlas(LoadSave.CONTENEDOR_MUMMY_IMG);
        imgWeaponAnubis     = LoadSave.GetSpriteAtlas(LoadSave.WEAPON_ANUBIS_IMG);
        imgWeaponMummy      = LoadSave.GetSpriteAtlas(LoadSave.WEAPON_MUMMY_IMG);
    }

    private BufferedImage[] sliceHorizontal(String atlasName, int frames) {
        BufferedImage atlas = LoadSave.GetSpriteAtlas(atlasName);
        if (atlas == null) return new BufferedImage[0];
        int fw = atlas.getWidth() / frames;
        int fh = atlas.getHeight();
        BufferedImage[] out = new BufferedImage[frames];
        for (int i = 0; i < frames; i++)
            out[i] = atlas.getSubimage(i * fw, 0, fw, fh);
        return out;
    }

    private void actualizarPociones() {
        if (alcoholRecogido && cloroRecogido)
            pocionesDisponibles = MAX_POCIONES;
    }

    public void spawnPocion(float x, float y, int dir) {
        if (!alcoholRecogido || !cloroRecogido) return;
        if (pocionesDisponibles <= 0) return;
        posiones.add(new Posion(x, y, dir));
        pocionesDisponibles--;
    }

    public void spawnPiedra(float x, float y, int dir) {
        piedras.add(new PiedraResortera(x, y, dir));
    }

    private void ensureMundo3Loaded() {
        int n = playing.getLevelManager().getNivelActual();
        if (loadedFor == n) return;
        loadedFor = n;
        if (n == 2) {
            alcoholes = LoadSave.GetAlcoholes(LoadSave.LEVEL_3);
            cloros    = LoadSave.GetCloros(LoadSave.LEVEL_3);
            escudos   = LoadSave.GetEscudos(LoadSave.LEVEL_3);
        } else {
            alcoholes.clear();
            cloros.clear();
            escudos.clear();
        }
    }

    public void update(){
        ensureMundo3Loaded();
        Niveles.Level lvl = playing.getLevelManager().currentLevel();

        for (Coin m : lvl.getCoins())
            if (m.isActive()) m.update();

        for (contenedor c : lvl.getContenedores())
            if (c.isActive()) c.update();

        for (Hp v : lvl.getVidas())
            if (v.isActive()) v.update();

        int[][] lvlData = lvl.getLvlData();
        for (Arena a   : lvl.getArenas())    a.update();
        for (Spike s   : lvl.getSpikes())    s.update();
        for (Palmera p : lvl.getPalmeras())  p.update();
        for (Coco c    : lvl.getCocos())     c.update(lvlData);

        for (Urna c : lvl.getUrnas())
            c.update();
        for (ArmaItem a : armas)
            if (a.isActivo()) a.update();

        // Sistema de fases de arena (solo nivel Egipto)
        if (playing.getLevelManager().getNivelActual() == 1)
            updateFases();

        // Resortera levitando (solo nivel 0)
        if (playing.getLevelManager().getNivelActual() == 0)
            objResortera.update();

        // Pociones (proyectiles del mundo 3)
        for (Posion p : posiones)
            if (p.isActive()) {
                p.update(lvlData);
                if (p.isActive() && playing.checkEnemyHitMundo3(p.getHitbox()))
                    p.setActive(false);
            }

        // Piedras de resortera (solo nivel 0)
        if (playing.getLevelManager().getNivelActual() == 0)
            for (PiedraResortera p : piedras)
                if (p.isActive()) {
                    p.update(lvlData);
                    if (p.isActive() && playing.checkEnemyHitNivel0(p.getHitbox()))
                        p.setActive(false);
                }

        if (spikeCooldown > 0)
            spikeCooldown--;
    }

    public void draw(Graphics g, int xLvlOffSet){
        Niveles.Level lvl = playing.getLevelManager().currentLevel();
        drawCoins(g, xLvlOffSet, lvl.getCoins());
        drawContainers(g, xLvlOffSet, lvl.getContenedores());
        drawUrnas(g, xLvlOffSet, lvl.getUrnas());
        drawArmas(g, xLvlOffSet);
        drawHp(g, xLvlOffSet, lvl.getVidas());
        drawPalmeras(g, xLvlOffSet, lvl.getPalmeras());
        drawArenas(g, xLvlOffSet, lvl.getArenas());
        drawSpikes(g, xLvlOffSet, lvl.getSpikes());
        drawCocos(g, xLvlOffSet, lvl.getCocos());
        if (playing.getLevelManager().getNivelActual() == 0)
            for (PiedraResortera p : piedras) p.draw(g, xLvlOffSet);
        if (playing.getLevelManager().getNivelActual() == 0)
            objResortera.draw(g, xLvlOffSet);
    }

    public void draw(Graphics g, int xLvlOffSet, int yLvlOffSet){
        g.translate(0, -yLvlOffSet);
        try {
            draw(g, xLvlOffSet);
        } finally {
            g.translate(0, yLvlOffSet);
        }
        for (ObjAlcohol a : alcoholes) a.draw(g, xLvlOffSet, yLvlOffSet);
        for (ObjCloro   c : cloros)    c.draw(g, xLvlOffSet, yLvlOffSet);
        for (ObjEscudo  e : escudos)   e.draw(g, xLvlOffSet, yLvlOffSet);
        for (Posion     p : posiones)  p.draw(g, xLvlOffSet, yLvlOffSet);
    }

    private void drawArenas(Graphics g, int xLvlOffSet, ArrayList<Arena> arenas) {
        if (arenaImg.length == 0) return;
        for (Arena a : arenas) {
            if (a.isOculto() || a.getHitbox() == null) continue;
            int idx = Math.min(a.getAnimIndex(), arenaImg.length - 1);
            g.drawImage(arenaImg[idx],
                (int)(a.getHitbox().x - xLvlOffSet), (int)a.getHitbox().y,
                32, 64, null);
        }
    }

    private void drawSpikes(Graphics g, int xLvlOffSet, ArrayList<Spike> spikes) {
        if (spikeImg.length == 0) return;
        for (Spike s : spikes) {
            if (s.getHitbox() == null) continue;
            int idx = Math.min(s.getAnimIndex(), spikeImg.length - 1);
            g.drawImage(spikeImg[idx],
                (int)(s.getHitbox().x - xLvlOffSet), (int)s.getHitbox().y,
                (int)s.getHitbox().width, (int)s.getHitbox().height, null);
        }
    }

    private void drawPalmeras(Graphics g, int xLvlOffSet, ArrayList<Palmera> palmeras) {
        if (palmeraImg.length == 0) return;
        for (Palmera p : palmeras) {
            if (p.getHitbox() == null) continue;
            int idx = Math.min(p.getAnimIndex(), palmeraImg.length - 1);
            g.drawImage(palmeraImg[idx],
                (int)(p.getHitbox().x - xLvlOffSet), (int)p.getHitbox().y,
                128, 160, null);
        }
    }

    private void drawCocos(Graphics g, int xLvlOffSet, ArrayList<Coco> cocos) {
        if (cocoImg.length == 0) return;
        for (Coco c : cocos) {
            if (!c.isActivo() || c.getHitbox() == null) continue;
            int idx = Math.min(c.getAnimIndex(), cocoImg.length - 1);
            g.drawImage(cocoImg[idx],
                (int)(c.getHitbox().x - xLvlOffSet) - 6,
                (int)c.getHitbox().y - 32,
                32, 64, null);
        }
    }

    private void drawContainers(Graphics g, int xLvlOffSet, ArrayList<contenedor> contenedores) {
        for (contenedor c : contenedores)
            if (c.isActive() && c.getHitbox() != null) {
                int tipo = mapTipoToFrameIndex(c.getObjType());
                int[] drawOffsets = getDrawOffsets(c.getObjType());
                g.drawImage(contenedorImg[tipo][c.getAnimIndex()],
                    (int)(c.getHitbox().x - drawOffsets[0] - xLvlOffSet),
                    (int)(c.getHitbox().y - drawOffsets[1]),
                    (int)(CONTENEDOR_SIZE * 1.3), (int)(CONTENEDOR_SIZE * 1.3), null);
                //c.drawHitbox(g, xLvlOffSet);
            }
    }

    private int[] getDrawOffsets(int objType) {
        switch (objType) {
            case BARRIL:  case BARRIL_B:   return new int[]{ 21, 27 };
            case CAJA:    case CAJA_B:     return new int[]{ 22, 25 };
            case POT:     case POT_B:      return new int[]{ 21, 19 };
            case LETRERO1: case LETRERO1_B:
            case LETRERO2: case LETRERO2_B: return new int[]{ 20, 22 };
            case COFRE:   case COFRE_B:    return new int[]{ 19, 18 };
            default:                        return new int[]{  0,  0 };
        }
    }

    private void drawHp(Graphics g, int xLvlOffSet, ArrayList<Hp> vidas) {
        for (Hp v : vidas)
            if (v.isActive() && v.getHitbox() != null)
                g.drawImage(hpImg[0][v.getAnimIndex()],
                    (int)(v.getHitbox().x - v.getxDrawOffSet() - xLvlOffSet),
                    (int)(v.getHitbox().y - v.getyDrawOffSet()),
                    HP_SIZE, HP_SIZE, null);
    }

    private int mapTipoToFrameIndex(int objType) {
        switch (objType) {
            case BARRIL:     return 0;  case BARRIL_B:   return 1;
            case CAJA:       return 2;  case CAJA_B:     return 3;
            case POT:        return 4;  case POT_B:      return 5;
            case LETRERO1:   return 6;  case LETRERO1_B: return 7;
            case LETRERO2:   return 8;  case LETRERO2_B: return 9;
            case COFRE:      return 10; case COFRE_B:    return 11;
            default:         return 0;
        }
    }

    private void drawCoins(Graphics g, int xLvlOffSet, ArrayList<Coin> monedas) {
        for (Coin m : monedas)
            if (m.isActive() && m.getHitbox() != null)
                g.drawImage(coinImg[0][m.getAnimIndex()],
                    (int)(m.getHitbox().x - m.getxDrawOffSet() - xLvlOffSet),
                    (int)(m.getHitbox().y - m.getyDrawOffSet()),
                    COIN_SIZE, COIN_SIZE, null);
    }

    private void drawUrnas(Graphics g, int xLvlOffSet, ArrayList<Urna> urnas) {
        if (imgContenedorAnubis == null && imgContenedorMummy == null) return;
        int drawW = (int)(Juego.TILES_SIZE * 0.9f);
        int drawH = (int)(Juego.TILES_SIZE * 1.2f);
        for (Urna c : urnas) {
            if (!c.isActivo()) continue;
            int drawX = (int)(c.getHitbox().x - xLvlOffSet) - (drawW - c.getHitbox().width) / 2;
            int drawY = (int)(c.getHitbox().y + c.getHitbox().height - drawH);
            BufferedImage img = (c.getTipo() == CONTEDORANUBIS) ? imgContenedorAnubis : imgContenedorMummy;
            if (img != null) g.drawImage(img, drawX, drawY, drawW, drawH, null);
            if (c.isFlashing()) {
                g.setColor(new Color(255, 255, 255, 80));
                g.fillRect(drawX, drawY, drawW, drawH);
            }
        }
    }

    private void drawArmas(Graphics g, int xLvlOffSet) {
        if (imgWeaponAnubis == null && imgWeaponMummy == null) return;
        int drawW = (int)(Juego.TILES_SIZE * 2.2f);
        int drawH = (int)(Juego.TILES_SIZE * 0.7f);
        for (ArmaItem a : armas) {
            if (!a.isActivo()) continue;
            int centerX = a.getHitbox().x + a.getHitbox().width / 2 - xLvlOffSet;
            int drawX   = centerX - drawW / 2;
            int drawY   = a.getHitbox().y - drawH / 2 + a.getBobOffset();
            BufferedImage img = (a.getTipo() == ANUBIS) ? imgWeaponAnubis : imgWeaponMummy;
            if (img != null) g.drawImage(img, drawX, drawY, drawW, drawH, null);
        }
    }

    public void checkUrnaHit(Rectangle2D.Float attackBox) {
        Niveles.Level lvl = playing.getLevelManager().currentLevel();
        for (Urna c : lvl.getUrnas()) {
            if (!c.isActivo()) continue;
            if (attackBox.intersects(c.getHitbox())) {
                c.hurt(1);
                if (c.isRota()) {
                    int itemTipo = (c.getTipo() == CONTEDORANUBIS) ? ANUBIS : MUMMY;
                    armas.add(new ArmaItem(c.getDropX(), c.getDropY(), itemTipo));
                }
                return;
            }
        }
    }

    public void checkPlayerPickupArma(Rectangle2D.Float playerHitbox, Jugador2 jugador) {
        for (ArmaItem a : armas) {
            if (!a.isActivo()) continue;
            if (playerHitbox.intersects(a.getHitbox())) {
                jugador.transformar(a.getTipo());
                a.recoger();
            }
        }
    }

    public void resetAllObject(){
        Niveles.Level lvl = playing.getLevelManager().currentLevel();

        for (Coin c : lvl.getCoins())         c.reset();
        for (contenedor con : lvl.getContenedores()) con.reset();
        for (Urna c : lvl.getUrnas()) c.resetUrna();
        for (Hp v : lvl.getVidas())           v.reset();
        armas.clear();

        // Resetear arenas al estado inicial — el sistema de fases las hará aparecer
        for (Arena a : lvl.getArenas())        a.resetParaFase();

        // Reiniciar sistema de fases
        fase         = Fase.CONSTRUYENDO;
        indiceActual = 0;
        delayTick    = 0;
        esperaTick   = 0;
        spikeCooldown = 0;
        piedras.clear();
        objResortera = new ObjResortera(RESORTERA_X, RESORTERA_Y);
    }

    /** Reseteo completo: incluye objetos recogibles Y banderas de estado internas. */
    public void resetarEstadoCompleto() {
        resetAllObject();
        alcoholes.clear();
        cloros.clear();
        escudos.clear();
        posiones.clear();
        alcoholRecogido      = false;
        cloroRecogido        = false;
        primeraBotellaDialogo = false;
        pocionesDisponibles  = 0;
        loadedFor            = -1;   // fuerza recarga en próximo update
    }
}
