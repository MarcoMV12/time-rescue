package GameStates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import Elementos.EnemyManager;
import Elementos.Jugador;
import Elementos.Jugador2;
import Elementos.Jugador3;
import Elementos.Jugador4;
import Elementos.Ninja;
import Elementos.Proyectil;
import Elementos.Spider;
import Elementos.Torret;
import Juegos.Juego;
import Niveles.LevelManager;
import Objects.objectManager;
import Objects.AcertijoBinario;
import ui.GameOverOverlay;
import ui.pauseOverlay;
import utilz.LoadSave;
import utilz.AudioPlayer;

import java.util.ArrayList;
import Elementos.Catapulta;
import Elementos.ItemPeso;
import Elementos.Boss;
import Elementos.CajaRompible;
import Elementos.ItemNivel;
import Elementos.Contrapeso;
import Elementos.Dog;
import Elementos.Dron;

public class Playing extends State implements StateMethods {
    private Jugador player;
    private Jugador2 player2;
    private Jugador3 player3;
    private Jugador4 player4;
    private LevelManager levelMan;
    private EnemyManager enemyMan;
    private objectManager objManager;
    private pauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private boolean paused = false;

    private int xLvlOffset;
    private int yLvlOffset;
    private int topBorder    = (int)(0.2*Juego.GAME_HEIGHT);
    private int bottomBorder = (int)(0.8*Juego.GAME_HEIGHT);
    private int leftBorder=(int)(0.2*Juego.GAME_WIDTH);
    private int rightBorder=(int)(0.8*Juego.GAME_WIDTH);
    private int lvlTileWide=LoadSave.GetLevelData()[0].length;
    private int maxLvlOffset=lvlTileWide-Juego.TILES_WIDTH;
    private int maxLvlOffsetX=maxLvlOffset*Juego.TILES_SIZE;

    private boolean gameOver;

    // Mensaje temporal estilo diálogo (mundo 3)
    private String mensajeTexto = null;
    private int mensajeTicks = 0;
    private static final int MENSAJE_DURACION = 1000; // ~3s a 60fps

    // Diálogo imagen nivel 3 (primera botella)
    private java.awt.image.BufferedImage dialogoLvl3Img;
    private boolean mostrandoDialogoLvl3 = false;

    // Instrucciones nivel 4 (tile 11,4)
    private java.awt.image.BufferedImage instrucciones4Img;
    private boolean mostrandoInstrucciones4  = false;
    private boolean instrucciones4Mostrado   = false;

    private AcertijoBinario acertijo;

    private Catapulta catapulta;
    private ArrayList<Contrapeso> contrapesos = new ArrayList<>();
    private ItemPeso proyectilItem;
    private CajaRompible cajaObjetivo;
    private boolean nivelDesbloqueado = false;
    private float proyectilSpawnX, proyectilSpawnY;

    private final int MAQUINA_W = 200;
    private final int MAQUINA_H = 150;
    private final int MAQUINA_MARGIN_X = 40;

    private final String[] niveles = {
        LoadSave.LEVEL_1, LoadSave.LEVEL_2, LoadSave.LEVEL_3, LoadSave.LEVEL_4
    };

    public Playing(Juego game) {
        super(game);
        inicializar();
    }

    private void inicializar() {
        levelMan = new LevelManager(game);
        enemyMan = new EnemyManager(this);
        objManager = new objectManager(this);

        enemyMan.cargarEnemigosDelNivel();
        
        Point spawn = levelMan.currentLevel().getPlayerSpawn();
        player = new Jugador(spawn.x - 32, spawn.y +32,
            (int)(64*Juego.SCALE), (int)(40*Juego.SCALE), this);
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        player.aplicarPersonaje(Elementos.Personaje.seleccionado);

        player2 = new Jugador2(spawn.x - 32, spawn.y + 32,
            (int)(64*Juego.SCALE), (int)(40*Juego.SCALE), this);
        player2.loadLvlData(levelMan.currentLevel().getLvlData());

        player3 = new Jugador3(spawn.x, spawn.y,
            (int)(91*Juego.SCALE), (int)(80*Juego.SCALE), this);
        player3.loadLvlData(levelMan.currentLevel().getLvlData());

        player4 = new Jugador4(spawn.x, spawn.y,
        (int)(48 * Juego.SCALE), (int)(96 * Juego.SCALE), this);
        player4.loadLvlData(levelMan.currentLevel().getLvlData());

        pauseOverlay = new pauseOverlay(game);
        gameOverOverlay = new GameOverOverlay(this);

        dialogoLvl3Img    = utilz.LoadSave.GetSpriteAtlas(utilz.LoadSave.DIALOGO_LVL3);
        instrucciones4Img = utilz.LoadSave.GetSpriteAtlas(utilz.LoadSave.INSTRUCCIONES4);

        acertijo = new AcertijoBinario();
        acertijo.cargarBloquesDesdeNivel(levelMan.currentLevel().getLvlData());

        if (levelMan.getNivelActual() == 1) {
            AudioPlayer.stopMusic(AudioPlayer.Sound.AMBIENT);
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
        }

        //objManager.cargarObjetosDelNivel(getMapaActual());

        crearMecanicaCatapulta();
    }

    @Override
    public void update() {
        if (!paused && !gameOver && !mostrandoDialogoLvl3 && !mostrandoInstrucciones4) {
            if (mensajeTicks > 0) mensajeTicks--;
            objManager.update();
            if (levelMan.getNivelActual() == 1) {
                Niveles.Level lvl = levelMan.currentLevel();
                if (!acertijo.isTutorialActivo()) {
                    player2.update(lvl.getArenas(), lvl.getSpikes(), lvl.getCocos());
                    objManager.checkObjectTouched(player2.getHitbox());
                    enemyMan.update2(lvl.getLvlData(), player2);
                    acertijo.update(player2);
                } else {
                    player2.resetDirBoolean();
                }
            } else if (levelMan.getNivelActual() == 2) {
                player3.update();
                objManager.checkObjectTouched(player3.getHitbox());
                enemyMan.update3(levelMan.currentLevel().getLvlData(), player3);
            } else if (levelMan.getNivelActual() == 3) {
                player4.update4(levelMan.currentLevel().getLvlData(), enemyMan.getDoors()); // 🔥 player4
                enemyMan.update4(levelMan.currentLevel().getLvlData(), player4);
                // Mostrar instrucciones al pasar por tile X=11, Y=4
                if (!instrucciones4Mostrado) {
                    int tileX = (int)(player4.getHitbox().x / Juego.TILES_SIZE);
                    int tileY = (int)(player4.getHitbox().y / Juego.TILES_SIZE);
                    if (tileX == 16 && tileY == 3) {
                        instrucciones4Mostrado = true;
                        mostrandoInstrucciones4 = true;
                    }
                }
            } else {
                // mundo 0
                objManager.checkSpikeHurt(player);
                player.update();
                if (player.isDisparar()) {
                    int dir = player.getFlipW(); // 1 = right, -1 = left
                    float spawnX = dir == 1
                        ? player.getHitbox().x + player.getHitbox().width
                        : player.getHitbox().x;
                    objManager.spawnPiedra(spawnX, player.getHitbox().y + player.getHitbox().height / 2, dir);
                    player.resetDisparar();
                }
                enemyMan.update(levelMan.currentLevel().getLvlData(), player);
            }
            //enemyMan.handlePlayerEnemyCollision(player, levelMan.currentLevel().getLvlData());
            checkCloseToBorder();
            checkVacioMuerte();

            // Catapulta y caja
            if (catapulta != null && cajaObjetivo != null) {
                catapulta.update(player, levelMan.currentLevel().getLvlData());
                cajaObjetivo.update();
                //checkProyectilPickup();       // jugador recoge la roca
                checkContrapesosUpdate();     // actualiza físicamente los contrapesos
                //checkContrapesoCatapulta();   // detecta si un contrapeso llegó al extremo
                checkCatapultaImpacto();      // proyectil vs caja
                checkProyectilFallido();
                checkItemNivelPickup();       // jugador recoge item que cayó de la caja
            }

            // Verificación independiente de la máquina del tiempo
            if (nivelDesbloqueado && jugadorEnMaquinaTiempo()) {
                completarNivel();
            }
        }
        else {
            pauseOverlay.update();
        }

        // if (gameState.state == gameState.PLAYING) {
        //     player.update();
        //     enemyMan.update();
        //     checkCloseToBorder();
        // }

    }

    @Override
    public void draw(Graphics g) {
        levelMan.drawBackground(g, xLvlOffset);
        // Máquina del tiempo (placeholder visual)
        drawMaquinaTiempo(g);

        if (levelMan.getNivelActual() == 2) {
            levelMan.draw(g, xLvlOffset, yLvlOffset);
            objManager.draw(g, xLvlOffset, yLvlOffset);
            player3.render(g, xLvlOffset, yLvlOffset);
            enemyMan.draw(g, xLvlOffset, yLvlOffset);
        } else {
            levelMan.draw(g, xLvlOffset);
            objManager.draw(g, xLvlOffset);
            if (levelMan.getNivelActual() == 1) {
                player2.render(g, xLvlOffset);
                acertijo.draw(g, xLvlOffset);
            } else if (levelMan.getNivelActual() == 3) {
                player4.render(g, xLvlOffset);
            } else {
                player.render(g, xLvlOffset);
            }
            enemyMan.draw(g, xLvlOffset);
            if (levelMan.getNivelActual() == 1) {
                acertijo.drawOverlay(g);
            }
        }

        //checkCloseToBorder();

        // Proyectil único en el suelo (antes de ser recogido)
        if (proyectilItem != null && !proyectilItem.isRecogido())
            proyectilItem.draw(g, xLvlOffset);
        
        // Contrapesos en el nivel
        for (Contrapeso c : contrapesos)
            c.draw(g, xLvlOffset);
        
        // Item en mano del jugador (proyectil o nada — ya lo dibuja Jugador.render)
        
        // Caja objetivo
        if (cajaObjetivo != null)
            cajaObjetivo.draw(g, xLvlOffset);
        
        // Catapulta encima de todo
        if (catapulta != null)
            catapulta.draw(g, xLvlOffset, levelMan.currentLevel().getLvlData());
        
        if (nivelDesbloqueado)
            drawNivelDesbloqueado(g);        

        drawMensaje(g);
        drawHUD(g);
        drawDialogoLvl3(g);
        drawDialogoImagen(g, instrucciones4Img, mostrandoInstrucciones4);

        if (paused) {
            g.setColor(new Color(0,0,0, 150));
            g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
            pauseOverlay.draw(g);
        }
        else if(gameOver)
            gameOverOverlay.draw(g);
    }

    /** Dibuja el HUD de monedas y XP en la esquina superior izquierda. */
    private void drawHUD(Graphics g) {
        utilz.ScoreManager sm = utilz.ScoreManager.getInstance();
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int py = 18;
        int fontSize = Math.max(12, (int)(10 * Juego.SCALE));
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String txtMonedas = "Monedas: " + sm.getMonedas();
        String txtXP      = "XP: "      + sm.getXP();
        int textW = Math.max(fm.stringWidth(txtMonedas), fm.stringWidth(txtXP));
        int hudW  = textW + 16;
        int px    = (Juego.GAME_WIDTH - hudW) / 2;

        // Fondo semitransparente
        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillRoundRect(px - 4, py - fontSize, hudW, fontSize * 2 + 6, 8, 8);

        // Monedas
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(txtMonedas, px, py);

        // XP
        g2.setColor(new Color(100, 220, 100));
        g2.drawString(txtXP, px, py + fontSize + 2);
    }

    private void resetContrapesosUsados() {
        for (Contrapeso c : contrapesos)
            if (c.isColocado()) c.reset();
    }

    /** Mata al jugador si cae por debajo del límite inferior del nivel. */
    private void checkVacioMuerte() {
        int nivel = levelMan.getNivelActual();
        int limitY;
        switch (nivel) {
            case 0 -> limitY = (20 * Juego.TILES_SIZE)-10;
            case 2 -> limitY = (32 * Juego.TILES_SIZE)-10;
            case 3 -> limitY = (20 * Juego.TILES_SIZE)-10;
            default -> { return; } // nivel 1: sin vacío
        }
        float playerY;
        switch (nivel) {
            case 0 -> playerY = player.getHitbox().y+player.getHitbox().height;
            case 2 -> playerY = player3.getHitbox().y+player3.getHitbox().height;
            case 3 -> playerY = player4.getHitbox().y+player4.getHitbox().height;
            default -> { return; }
        }
        if (playerY > limitY) {
            setGameOver(true);
        }
    }

    private void checkProyectilFallido() {
        if (catapulta == null) return;
        if (catapulta.getEstado() != Catapulta.Estado.DISPARADA) return;
        if (cajaObjetivo.isDestruida()) return;
        if (catapulta.isProyectilConsumido()) return;  // ya impactó la caja, no respawn

        Proyectil p = catapulta.getProyectil();
        if (p == null) return;

        // Caso 1: cayó al vacío → respawn de la roca en posición inicial
        if (p.isPerdido()) {
            respawnProyectil();
            catapulta.resetParaReintentar();
            resetContrapesosUsados();
            return;
        }

        // Caso 2: cayó al suelo → convertirlo en ItemPeso recogible
        if (p.isEnSuelo()) {
            proyectilItem = new ItemPeso(
                p.getHitbox().x,
                p.getHitbox().y,
                ItemPeso.Tipo.ROCA
            );
            catapulta.resetParaReintentar();
            resetContrapesosUsados();
        }
    }

    private void respawnProyectil() {
        proyectilItem = new ItemPeso(proyectilSpawnX, proyectilSpawnY, ItemPeso.Tipo.ROCA);
    }

    private void crearMecanicaCatapulta() {
        if (levelMan.getNivelActual() != 0) {
            catapulta      = null;
            cajaObjetivo   = null;
            proyectilItem  = null;
            if (contrapesos != null) contrapesos.clear();
            nivelDesbloqueado = false;
            return;
        }

        // Coloca la mecánica al final del nivel
        int[][] data = levelMan.currentLevel().getLvlData();
        int nivelAnchoPx = data[0].length * Juego.TILES_SIZE;
        
        // Catapulta cerca del final, caja un poco más allá
        float catX  = nivelAnchoPx - 1150;
        float cajaX = nivelAnchoPx - 640;
        float catY  = 226;//encontrarSueloEnX(catX, data) - 80;   // catapulta sobre el suelo
        float cajaY = 192;//encontrarSueloEnX(cajaX, data) - 32;  // caja sobre el suelo
        
        catapulta = new Catapulta(catX, catY, cajaX - catX);
        cajaObjetivo = new CajaRompible(cajaX, cajaY, getItemTipoParaNivel(levelMan.getNivelActual()));
        
        // Roca proyectil — guardamos su posición de spawn para el respawn si cae al vacío
        float rocaX = catX - 150;
        float rocaY = 300;//encontrarSueloEnX(rocaX, data) - 100;
        proyectilSpawnX = rocaX;
        proyectilSpawnY = rocaY;
        proyectilItem = new ItemPeso(rocaX, rocaY, ItemPeso.Tipo.ROCA);
        
        // Contrapesos esparcidos cerca de la catapulta
        if (contrapesos == null) contrapesos = new ArrayList<>();
        else contrapesos.clear();
        
        float baseY = catY + 60;
        contrapesos.add(new Contrapeso(catX - 400, baseY, Contrapeso.Tipo.BOLSA_ARENA));
        contrapesos.add(new Contrapeso(catX - 250, baseY, Contrapeso.Tipo.BARRIL));
        contrapesos.add(new Contrapeso(catX - 100, baseY + 40, Contrapeso.Tipo.CAJA_PIEDRA));
        contrapesos.add(new Contrapeso(catX + 50,  baseY,      Contrapeso.Tipo.ROCA_GRANDE));
        
        nivelDesbloqueado = false;
    }

    // private float encontrarSueloEnX(float x, int[][] data) {
    //     int col = (int)(x / Juego.TILES_SIZE);
    //     if (col < 0) col = 0;
    //     if (col >= data[0].length) col = data[0].length - 1;
    //     for (int row = 0; row < data.length; row++) {
    //         int valor = data[row][col];
    //         if (valor >= 48 || valor < 0) {
    //             return row * Juego.TILES_SIZE;
    //         }
    //     }
    //     return Juego.GAME_HEIGHT;
    // }

    private ItemNivel.Tipo getItemTipoParaNivel(int nivel) {
        switch (nivel) {
            case 0:  return ItemNivel.Tipo.LLAVE;
            case 1:  return ItemNivel.Tipo.CRISTAL;
            case 2:  return ItemNivel.Tipo.PERGAMINO;
            default: return ItemNivel.Tipo.LLAVE;
        }
    }

    public void completarNivel() {
        int nivelCompletado = levelMan.getNivelActual();
        utilz.ScoreManager.getInstance().addXP(utilz.ScoreManager.XP_NIVEL_COMPLETO);

        if (nivelCompletado >= LevelManager.TOTAL_NIVELES - 1) {
            // Era el último nivel: guardar partida y mostrar pantalla final
            utilz.ScoreManager.getInstance().guardarPartida(
                utilz.ScoreManager.getInstance().getNickname());
            utilz.ScoreManager.getInstance().resetSesion();
            gameState.state = gameState.GAME_COMPLETED;
        } else {
            // Inicia transición
            game.getLevelCompleted().iniciar(nivelCompletado);
            gameState.state = gameState.LEVEL_COMPLETED;
        }
    }

    private void drawMaquinaTiempo(Graphics g) {
        Rectangle2D.Float h = getMaquinaTiempoHitbox();

        if (nivelDesbloqueado) {
            // g.setColor(new Color(80, 220, 255, 200));    // azul brillante = activa
            // g.fillRect((int)(h.x - xLvlOffset +40), (int)h.y -50, 150, 120);
            g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.MAQUINA_TIME_ENCENDIDA).getSubimage(0, 0, 400, 300),
                (int)(h.x - xLvlOffset + 15), (int)h.y - 54, MAQUINA_W, MAQUINA_H, null);
        } else {
            //g.setColor(new Color(120, 120, 120, 180));   // gris = inactiva
            g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.MAQUINA_TIME_APAGADA).getSubimage(0, 0, 400, 300),
                (int)(h.x - xLvlOffset + 15), (int)h.y - 54, MAQUINA_W, MAQUINA_H, null);
        }
        

        // g.setColor(Color.WHITE);
        // g.drawRect((int)(h.x - xLvlOffset), (int)h.y, (int)h.width, (int)h.height);

        // g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        // g.drawString("MÁQUINA",     (int)(h.x - xLvlOffset) + 4, (int)h.y + 14);
        // g.drawString("DEL TIEMPO",  (int)(h.x - xLvlOffset) + 2, (int)h.y + 26);
    }

    public void cargarSiguienteNivel() {
        levelMan.avanzarNivel();

        xLvlOffset = 0;
        yLvlOffset = 0;

        Point spawn = levelMan.currentLevel().getPlayerSpawn();
        int nivel = levelMan.getNivelActual();

        if (nivel == 1) {
            AudioPlayer.pauseMusic(AudioPlayer.Sound.AMBIENT);
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
            player2.getHitbox().x = spawn.x - 32;
            player2.getHitbox().y = spawn.y + 32;
            player2.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player2.loadLvlData(levelMan.currentLevel().getLvlData());
            player2.resetAll();
            acertijo.cargarBloquesDesdeNivel(levelMan.currentLevel().getLvlData());
        } else if (nivel == 2) {
            AudioPlayer.pauseMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
            AudioPlayer.pauseMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player3.getHitbox().x = spawn.x;
            player3.getHitbox().y = spawn.y;
            player3.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player3.loadLvlData(levelMan.currentLevel().getLvlData());
            player3.resetAll();
        } else if (nivel == 3) {
            AudioPlayer.pauseMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
            AudioPlayer.pauseMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player4.getHitbox().x = spawn.x;
            player4.getHitbox().y = spawn.y;
            player4.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player4.loadLvlData(levelMan.currentLevel().getLvlData());
            player4.resetAll();
        } else {
            AudioPlayer.pauseMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
            AudioPlayer.pauseMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player.getHitbox().x = spawn.x;
            player.getHitbox().y = spawn.y;
            player.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player.loadLvlData(levelMan.currentLevel().getLvlData());
        }

        enemyMan.cargarEnemigosDelNivel();
        enemyMan.resetAllEnemies();

        crearMecanicaCatapulta();
        }

    /**
     * Recarga el nivel actual sin avanzar. Mismo setup que cargarSiguienteNivel
     * pero sobre el nivel en el que ya se encuentra el jugador.
     */
    public void recargarNivelActual() {
        xLvlOffset = 0;
        yLvlOffset = 0;
        nivelDesbloqueado = false;

        Point spawn = levelMan.currentLevel().getPlayerSpawn();
        int nivel = levelMan.getNivelActual();

        if (nivel == 1) {
            AudioPlayer.stopMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
            player2.getHitbox().x = spawn.x - 32;
            player2.getHitbox().y = spawn.y + 32;
            player2.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player2.loadLvlData(levelMan.currentLevel().getLvlData());
            player2.resetAll();
            acertijo.resetOnDeath();
            acertijo.cargarBloquesDesdeNivel(levelMan.currentLevel().getLvlData());
        } else if (nivel == 2) {
            AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player3.getHitbox().x = spawn.x;
            player3.getHitbox().y = spawn.y;
            player3.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player3.loadLvlData(levelMan.currentLevel().getLvlData());
            player3.resetAll();
        } else if (nivel == 3) {
            AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player4.getHitbox().x = spawn.x;
            player4.getHitbox().y = spawn.y;
            player4.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player4.loadLvlData(levelMan.currentLevel().getLvlData());
            player4.resetAll();
        } else {
            AudioPlayer.playMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
            player.getHitbox().x = spawn.x;
            player.getHitbox().y = spawn.y;
            player.aplicarPersonaje(Elementos.Personaje.seleccionado);
            player.loadLvlData(levelMan.currentLevel().getLvlData());
            player.resetAll();
        }

        enemyMan.cargarEnemigosDelNivel();
        enemyMan.resetAllEnemies();
        crearMecanicaCatapulta();
        gameOver = false;
    }

    private Elementos.Personaje personajeParaNivel(int idx) {
        return switch (idx) {
            case 0 -> Elementos.Personaje.CAVERNICOLA;
            case 1 -> Elementos.Personaje.EGIPCIO;
            case 2 -> Elementos.Personaje.CABALLERO;
            case 3 -> Elementos.Personaje.CIENTIFICO;
            default -> Elementos.Personaje.seleccionado;
        };
    }

        public void checkObjectTouched(Rectangle2D.Float hitbox){
            objManager.checkObjectTouched(hitbox);
        }

        public void checkObjectHit(Rectangle2D.Float attackBox){
            objManager.checkObjectHit(attackBox);
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        player.resetAll();
        player2.resetAll();
        player3.resetAll();
        player4.resetAll();
        enemyMan.resetAllEnemies();

        crearMecanicaCatapulta();

        int nivel = levelMan.getNivelActual();
        if (nivel == 1) {
            acertijo.resetOnDeath();
            AudioPlayer.stopMusic(AudioPlayer.Sound.AMBIENT);
            AudioPlayer.stopMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
        } else if (nivel == 0) {
            AudioPlayer.playMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
        }
    }

    /**
     * Reinicia completamente: vuelve al nivel 0, resetea todo y limpia el score de sesión.
     * Usado al salir al menú principal desde la pausa.
     */
    public void reiniciarCompleto() {
        AudioPlayer.stopMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
        AudioPlayer.stopMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
        AudioPlayer.stopMusic(AudioPlayer.Sound.AMBIENT);

        levelMan.reiniciar();
        xLvlOffset = 0;
        yLvlOffset = 0;
        nivelDesbloqueado = false;

        gameOver = false;
        paused   = false;

        Point spawn = levelMan.currentLevel().getPlayerSpawn();
        player.getHitbox().x = spawn.x;
        player.getHitbox().y = spawn.y;
        player.aplicarPersonaje(Elementos.Personaje.seleccionado);
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        player.resetAll();
        player2.resetAll();
        player3.resetAll();
        player4.resetAll();
        enemyMan.cargarEnemigosDelNivel();
        enemyMan.resetAllEnemies();
        objManager.resetarEstadoCompleto();
        mostrandoDialogoLvl3    = false;
        mostrandoInstrucciones4 = false;
        instrucciones4Mostrado  = false;
        crearMecanicaCatapulta();
    }

    // Actualiza la física de los contrapesos en el suelo
    private void checkContrapesosUpdate() {
        for (Contrapeso c : contrapesos)
            c.update(levelMan.currentLevel().getLvlData());
    }

    // E cerca de un contrapeso → recogerlo
    private void checkRecogerContrapeso() {
        if (player.tieneItem() || player.getContrapeso() != null) return;
        for (Contrapeso c : contrapesos) {
            if (c.isColocado() || c.isEnMano()) continue;
            if (player.getHitbox().intersects(c.getHitbox())) {
                player.recogerContrapeso(c);
                return;
            }
        }
    }

    // E cerca de la catapulta con contrapeso en mano → colocarlo y disparar
    private void colocarContrapesoEnCatapulta() {
        Contrapeso c = player.getContrapeso();
        if (c == null) return;
        if (catapulta == null || !catapulta.isJugadorCerca()) return;
        if (catapulta.getEstado() != Catapulta.Estado.CON_PROYECTIL) return;

        c.setColocado(true);
        c.setEnMano(false);
        player.soltarContrapeso();          // limpia la referencia del jugador
        catapulta.recibirContrapeso(c);     // dispara
    }
    
    // Proyectil impacta la caja
    private void checkCatapultaImpacto() {
        if (catapulta == null || cajaObjetivo == null) return;
        if (!catapulta.proyectilActivo()) return;
        if (cajaObjetivo.isDestruida()) return;

        if (cajaObjetivo.colisionaCon(catapulta.getProyectil().getHitbox())) {
            cajaObjetivo.recibirImpacto(1);
            catapulta.getProyectil().detener();

            if (cajaObjetivo.isDestruida()) {
                // Caja destruida: la roca queda consumida, no hay que recogerla
                catapulta.marcarProyectilConsumido();
            } else {
                // Caja sobrevivió: la roca vuelve al suelo como item recogible
                Proyectil p = catapulta.getProyectil();
                proyectilItem = new ItemPeso(p.getHitbox().x, p.getHitbox().y, ItemPeso.Tipo.ROCA);
                catapulta.resetParaReintentar();
                resetContrapesosUsados();
            }
        }
    }
    
    // Jugador recoge el item que cayó de la caja
    private void checkItemNivelPickup() {
        if (!cajaObjetivo.isItemSoltado()) return;
    
        ItemNivel item = cajaObjetivo.getItem();
        item.update();
        if (item.isRecogido()) return;
    
        Rectangle2D.Float ph = levelMan.getNivelActual() == 1
            ? player2.getHitbox() : player.getHitbox();
        if (ph.intersects(item.getHitbox())) {
            item.setRecogido(true);
            nivelDesbloqueado = true;
        }
    }

    private boolean jugadorEnMaquinaTiempo() {
        // int nivel = levelMan.getNivelActual();
        // if (nivel < 0 || nivel >= maquinaTiempoPos.length) return false;

        // float mx = maquinaTiempoPos[nivel][0];
        // float my = maquinaTiempoPos[nivel][1];

        // Rectangle2D.Float maquinaHitbox = new Rectangle2D.Float(mx, my, MAQUINA_W, MAQUINA_H);
        // return player.getHitbox().intersects(maquinaHitbox);

        Rectangle2D.Float maquinaHitbox = getMaquinaTiempoHitbox();
        Rectangle2D.Float ph = levelMan.getNivelActual() == 1
            ? player2.getHitbox() : player.getHitbox();
        return ph.intersects(maquinaHitbox);
    }

    private Rectangle2D.Float getMaquinaTiempoHitbox() {
        if (levelMan.getNivelActual() == 1)
            return new Rectangle2D.Float(132 * Juego.TILES_SIZE, 17 * Juego.TILES_SIZE - MAQUINA_H, MAQUINA_W, MAQUINA_H);

        int[][] data = levelMan.currentLevel().getLvlData();
        int nivelAnchoPx = data[0].length * Juego.TILES_SIZE;
        float mx = nivelAnchoPx - MAQUINA_W - MAQUINA_MARGIN_X;
        return new Rectangle2D.Float(mx, 500, MAQUINA_W, MAQUINA_H);
    }
    
    private void drawNivelDesbloqueado(Graphics g) {
        g.setColor(new Color(255, 220, 80, 200));
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, (int)(11 * Juego.SCALE)));
        String msg = "¡Nivel desbloqueado!  Busca la salida";
        int msgW = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (Juego.GAME_WIDTH - msgW) / 2, 40);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver) {
            int nivel = levelMan.getNivelActual();
            if (nivel == 1) {
                player2.setAttacking(true);
            } else if (nivel == 2) {
                player3.setAttacking(true);
                AudioPlayer.playSfx(AudioPlayer.Sound.PLAYER_ATTACK, AudioPlayer.SFX_VOLUME);
            } else if (nivel == 3) {
                player4.setAttacking(true);
                AudioPlayer.playSfx(AudioPlayer.Sound.PLAYER_ATTACK, AudioPlayer.SFX_VOLUME);
            } else {
                player.setAttacking(true);
                AudioPlayer.playSfx(AudioPlayer.Sound.PLAYER_ATTACK, AudioPlayer.SFX_VOLUME);
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        // if(!gameOver)
        //     if(paused)
        //         pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON3 && levelMan.getNivelActual() == 2) {
                objManager.spawnPocion(
                    player3.getHitbox().x,
                    player3.getHitbox().y,
                    player3.getFacingDir());
            }
            if(paused)
                pauseOverlay.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver)
            if(paused)
                pauseOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver)
            if(paused)
                pauseOverlay.mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (mostrandoDialogoLvl3) {
            int k = e.getKeyCode();
            if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE || k == KeyEvent.VK_SPACE)
                mostrandoDialogoLvl3 = false;
            return;
        }
        if (mostrandoInstrucciones4) {
            int k = e.getKeyCode();
            if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE || k == KeyEvent.VK_SPACE)
                mostrandoInstrucciones4 = false;
            return;
        }
        if(gameOver)
            gameOverOverlay.keyPressed(e);
        else
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setUp(true);
                else if (n == 2) player3.setJump(true);
                else if (n == 3) player4.setUp(true);
                else player.setUp(true);
                break;
            }
            case KeyEvent.VK_S: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setDown(true);
                else if (n == 2) player3.setDown(true);
                else if (n == 3) player4.setDown(true);
                else player.setDown(true);
                break;
            }
            case KeyEvent.VK_A: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setLeft(true);
                else if (n == 2) player3.setLeft(true);
                else if (n == 3) player4.setLeft(true);
                else player.setLeft(true);
                break;
            }
            case KeyEvent.VK_D: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setRight(true);
                else if (n == 2) player3.setRight(true);
                else if (n == 3) player4.setRight(true);
                else player.setRight(true);
                break;
            }
            case KeyEvent.VK_SPACE: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setJump(true);
                else if (n == 2) player3.setJump(true);
                else if (n == 3) player4.setJump(true);
                else player.setJump(true);
                break;
            }
            case KeyEvent.VK_SHIFT:
                if (levelMan.getNivelActual() == 2) player3.setShielding(true);
                break;
            case KeyEvent.VK_BACK_SPACE:
                gameState.state = gameState.PAUSE;
                break;
            case KeyEvent.VK_T:
                // Activa la máquina del tiempo y avanza al siguiente nivel
                nivelDesbloqueado = true;
                completarNivel();
                break;
            case KeyEvent.VK_E:
                if (levelMan.getNivelActual() == 1) {
                    player2.setInteract(true);
                    break;
                }
                // Prioridad 1: si tengo contrapeso en mano y estoy cerca de la catapulta
                if (player.getContrapeso() != null
                        && catapulta != null
                        && catapulta.isJugadorCerca()
                        && catapulta.getEstado() == Catapulta.Estado.CON_PROYECTIL) {
                    colocarContrapesoEnCatapulta();
                }
                // Prioridad 2: estoy cerca de la catapulta con la roca → colocar proyectil
                else if (catapulta != null && catapulta.isJugadorCerca()
                        && catapulta.getEstado() == Catapulta.Estado.SIN_PROYECTIL
                        && player.tieneItem()) {
                    catapulta.colocarProyectil(player);
                }
                // Prioridad 3: recoger contrapeso o roca del suelo
                else {
                    checkRecogerContrapeso();
                    checkProyectilPickup();  // este ya lo tienes
                }
                break;
            case KeyEvent.VK_Q:
                // Soltar el contrapeso (por si te equivocaste y quieres tomar otro)
                if (player.getContrapeso() != null)
                    player.soltarContrapeso();
                break;
            case KeyEvent.VK_R:
                if (levelMan.getNivelActual() == 0)
                    player.toggleModoResortera();
                break;
            case KeyEvent.VK_I:
                if (levelMan.getNivelActual() == 0
                        && catapulta != null && catapulta.isJugadorCerca())
                    catapulta.toggleInfo();
                break;
            case KeyEvent.VK_ENTER:
                if (levelMan.getNivelActual() == 1)
                    acertijo.cerrarTutorial();
                break;
        }
    }

    private void checkProyectilPickup() {
        if (player.tieneItem() || player.getContrapeso() != null) return;
        if (player.getHitbox().intersects(proyectilItem.getHitbox()))
            player.recogerItem(proyectilItem);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setUp(false);
                else if (n == 2) player3.setJump(false);
                else if (n == 3) player4.setUp(false);
                else player.setUp(false);
                break;
            }
            case KeyEvent.VK_S: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setDown(false);
                else if (n == 2) player3.setDown(false);
                else if (n == 3) player4.setDown(false);
                else player.setDown(false);
                break;
            }
            case KeyEvent.VK_A: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setLeft(false);
                else if (n == 2) player3.setLeft(false);
                else if (n == 3) player4.setLeft(false);
                else player.setLeft(false);
                break;
            }
            case KeyEvent.VK_D: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setRight(false);
                else if (n == 2) player3.setRight(false);
                else if (n == 3) player4.setRight(false);
                else player.setRight(false);
                break;
            }
            case KeyEvent.VK_SPACE: {
                int n = levelMan.getNivelActual();
                if (n == 1) player2.setJump(false);
                else if (n == 2) player3.setJump(false);
                else if (n == 3) player4.setJump(false);
                else player.setJump(false);
                break;
            }
            case KeyEvent.VK_SHIFT:
                if (levelMan.getNivelActual() == 2) player3.setShielding(false);
                break;
            case KeyEvent.VK_E:
                if (levelMan.getNivelActual() == 1)
                    player2.setInteract(false);
                break;
            }
    }

    private void checkCloseToBorder(){
        int nivel = levelMan.getNivelActual();
        int playerX;
        if (nivel == 1)      playerX = (int) player2.getHitbox().x;
        else if (nivel == 2) playerX = (int) player3.getHitbox().x;
        else if (nivel == 3) playerX = (int) player4.getHitbox().x;
        else                 playerX = (int) player.getHitbox().x;

        int diff = playerX - xLvlOffset;
        if(diff>rightBorder)
            xLvlOffset+=diff-rightBorder;
        else if(diff<leftBorder)
            xLvlOffset+=diff-leftBorder;

        int maxOffset = levelMan.currentLevel().getMaxLevelOffSetX();
        if(xLvlOffset > maxOffset)
            xLvlOffset = maxOffset;
        else if(xLvlOffset < 0)
            xLvlOffset = 0;

        if (nivel == 2) {
            int playerY = (int) player3.getHitbox().y;
            int diffY = playerY - yLvlOffset;
            if (diffY > bottomBorder)
                yLvlOffset += diffY - bottomBorder;
            else if (diffY < topBorder)
                yLvlOffset += diffY - topBorder;

            int maxOffsetY = levelMan.currentLevel().getMaxLevelOffSetY();
            if (yLvlOffset > maxOffsetY) yLvlOffset = maxOffsetY;
            else if (yLvlOffset < 0)     yLvlOffset = 0;
        } else {
            yLvlOffset = 0;
        }
    }

    // private void checkItemPickup() {
    //     for (ItemPeso item : itemsPeso) {
    //         if (item.isRecogido()) continue;
    //         if (player.getHitbox().intersects(item.getHitbox()))
    //             player.recogerItem(item);
    //     }
    // }

    public void setGameOver(boolean gameOver) {
        if (gameOver && !this.gameOver) {
            AudioPlayer.stopMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
            AudioPlayer.stopMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.playSfx(AudioPlayer.Sound.GAME_OVER, AudioPlayer.SFX_VOLUME);
        }
        this.gameOver = gameOver;
    }

    public Jugador getPlayer() {
        return player;
    }

    public Jugador2 getPlayer2() {
        return player2;
    }

    public Jugador3 getPlayer3() {
        return player3;
    }

    public Jugador4 getPlayer4() {
        return player4;
    }
    
    public void windowFocusLost(){
        player.resetDirBoolean();
        player2.resetDirBoolean();
        player3.resetDirBoolean();
        player4.resetDirBoolean();
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyMan.checkEnemyHit(attackBox);
    }

    public objectManager getObjManager() {
        return objManager;
    }

    public LevelManager getLevelManager() {
        return levelMan;
    }

    private String getMapaActual() {
        return niveles[levelMan.getNivelActual()];
    }

    private void drawDialogoImagen(Graphics g, java.awt.image.BufferedImage img, boolean show) {
        if (!show || img == null) return;

        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;

        java.awt.Composite oldC = g2d.getComposite();
        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.6f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        g2d.setComposite(oldC);

        int maxW = (int)(Juego.GAME_WIDTH  * 0.90);
        int maxH = (int)(Juego.GAME_HEIGHT * 0.90);
        float escala = Math.min((float)maxW / img.getWidth(), (float)maxH / img.getHeight());
        int drawW = (int)(img.getWidth()  * escala);
        int drawH = (int)(img.getHeight() * escala);
        int drawX = (Juego.GAME_WIDTH  - drawW) / 2;
        int drawY = (Juego.GAME_HEIGHT - drawH) / 2;

        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                             java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, drawX, drawY, drawW, drawH, null);

        g2d.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, (int)(13 * Juego.SCALE)));
        g2d.setColor(new Color(200, 200, 200, 200));
        String hint = "[ Enter / Espacio ] Continuar";
        int hw = g2d.getFontMetrics().stringWidth(hint);
        g2d.drawString(hint, (Juego.GAME_WIDTH - hw) / 2, drawY + drawH + 18);
    }

    public void mostrarDialogoLvl3() {
        mostrandoDialogoLvl3 = true;
    }

    private void drawDialogoLvl3(Graphics g) {
        drawDialogoImagen(g, dialogoLvl3Img, mostrandoDialogoLvl3);
    }

    //Nivel 4
    public boolean checkEnemyHitAndReturn(Rectangle2D.Float attackBox) {
        return enemyMan.checkEnemyHitAndReturn(attackBox);
    }

    public boolean checkEnemyHitMundo3(Rectangle2D.Float attackBox) {
        return enemyMan.checkEnemyHitMundo3(attackBox);
    }

    public boolean checkEnemyHitNivel0(Rectangle2D.Float attackBox) {
        return enemyMan.checkEnemyHitNivel0(attackBox);
    }

    public void mostrarMensaje(String texto) {
        this.mensajeTexto = texto;
        this.mensajeTicks = MENSAJE_DURACION;
    }

    private void drawMensaje(Graphics g) {
        if (mensajeTexto == null || mensajeTicks <= 0) return;

        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                             java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        java.awt.Font font = new java.awt.Font("Monospaced", java.awt.Font.BOLD, (int)(16 * Juego.SCALE));
        g2d.setFont(font);
        int textW = g2d.getFontMetrics().stringWidth(mensajeTexto);
        int padX = (int)(20 * Juego.SCALE);
        int padY = (int)(14 * Juego.SCALE);
        int panelW = textW + padX * 2;
        int panelH = (int)(44 * Juego.SCALE);
        int panelX = (Juego.GAME_WIDTH - panelW) / 2;
        int panelY = (int)(40 * Juego.SCALE);

        java.awt.Composite oldC = g2d.getComposite();
        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.88f));
        g2d.setColor(new Color(15, 20, 35));
        g2d.fillRoundRect(panelX, panelY, panelW, panelH, 14, 14);
        g2d.setComposite(oldC);

        g2d.setColor(new Color(180, 150, 70));
        g2d.drawRoundRect(panelX, panelY, panelW, panelH, 14, 14);

        g2d.setColor(Color.WHITE);
        g2d.drawString(mensajeTexto, panelX + padX, panelY + padY + (int)(16 * Juego.SCALE));
    }
    
}