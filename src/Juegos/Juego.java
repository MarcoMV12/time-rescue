package Juegos;

import GameStates.Start;
import GameStates.CharacterSelect;
import GameStates.LevelCompleted;
import GameStates.Playing;
import GameStates.gameState;
import ui.pauseOverlay;
import utilz.LoadSave;
import utilz.AudioPlayer;


import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Juego extends Thread{
    private VtaJuego vta;
    private PanelJuego pan;
    private int FPS_SET=120;
    private int UPS_SET=200;

    private Playing jugando;
    private pauseOverlay pausa;
    private Start inicio;

    public int xLvlOffset;
    private gameState lastState = gameState.state;

    public final static int TILES_DEF_SIZE=32;
    public final static float SCALE=1.0f;
    public final static int TILES_WIDTH=50;
    public final static int TILES_HEIGHT=20;
    public final static int TILES_SIZE=(int)(TILES_DEF_SIZE*SCALE);
    public final static int GAME_WIDTH=TILES_DEF_SIZE*TILES_WIDTH;
    public final static int GAME_HEIGHT=TILES_DEF_SIZE*TILES_HEIGHT;
    public final static double DISPLAY_SCALE;
    static {
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double sx = screen.width  / (double) GAME_WIDTH;
        double sy = (screen.height - 80) / (double) GAME_HEIGHT; // -80 para barra de tareas
        double s  = Math.min(sx, sy);
        s = Math.floor(s * 4) / 4.0; // redondear hacia abajo en pasos de 0.25
        DISPLAY_SCALE = Math.max(1.0, s);
    }
    private BufferedImage[] bgimgs;

    private LevelCompleted levelCompleted;
    private CharacterSelect characterSelect;    

    public Juego() {
        inicializar();
        pan=new PanelJuego(this);
        vta=new VtaJuego(pan);
        pan.setFocusable(true);
        pan.requestFocus();
        comenzarJuego();
    }

    private void inicializar() {
        inicio = new Start(this);
        pausa = new pauseOverlay(this);
        jugando = new Playing(this);

        levelCompleted = new LevelCompleted(this);
        characterSelect = new CharacterSelect(this);

    //   player=new Jugador(200,200,(int)(64*SCALE),(int)(40*SCALE));
    //   levelMan=new LevelManager(this);
    //   smallCloudPos=new int[8];
    //   for (int i = 0; i < smallCloudPos.length; i++) {
    //     smallCloudPos[i]=(int)(90*Juego.SCALE)+ran.nextInt((int)(100*Juego.SCALE));
    //   }
    //   player.loadLvlData(levelMan.currentLevel().getLvlData());
       bgimgs = new BufferedImage[] {
           LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG),
           LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG2),
           LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG3),
           LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG4)
       };
    //   bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
    //   smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
    }

    private void comenzarJuego() {
        start();
    }

    public void run(){
        double framePorTiempo=1000000000.0/FPS_SET;
        double updatePorTiempo=1000000000.0/UPS_SET;
     
        int update=0;
        int frame=0;
        long previusTime=System.nanoTime();
        double deltaU=0, deltaF=0;
        long lastCheck=System.currentTimeMillis();
        while (true) {
            long currentTime=System.nanoTime();
            deltaU+=(currentTime-previusTime)/updatePorTiempo;
            deltaF+=(currentTime-previusTime)/framePorTiempo;
            previusTime=currentTime;
            if(deltaU>=1)
            {
                update();
                update++;
                deltaU--;
            }
            if(deltaF>=1)
            {
                pan.repaint();
                frame++;
                deltaF--;
            }
           if(System.currentTimeMillis()-lastCheck>=1000){
            lastCheck=System.currentTimeMillis();
            System.out.println("FPS "+frame+" UPS "+update);
            frame=0;
            update=0;
        }            
       }
    }

    private void update() {
        if (gameState.state != lastState) {
            handleStateChange(lastState, gameState.state);
            lastState = gameState.state;
        }
        switch (gameState.state) {
            case START: inicio.update();
                break;
            case PLAYING:  jugando.update();
                //levelMan.update();
                //player.update();    
                //checkCloseToBorder();
                break;
            case LEVEL_COMPLETED: levelCompleted.update();
                 break;
            case CHARACTER_SELECT: characterSelect.update();
                 break;
            case GAME_COMPLETED: /*Pantalla final */
                 break; 
            default:
                break;
        }
    }  

    private void handleStateChange(gameState prev, gameState next) {
        switch (next) {
            case PLAYING:
                //AudioPlayer.resumeMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
                break;
            case PAUSE:
            case START:
                AudioPlayer.pauseMusic(AudioPlayer.Sound.AMBIENT);
                break;
            default:
                break;
        }
    }

    void render(Graphics g){
       int nivelIdx = (gameState.state == gameState.PLAYING || gameState.state == gameState.PAUSE)
           ? jugando.getLevelManager().getNivelActual() : 0;
       if (nivelIdx < 0 || nivelIdx >= bgimgs.length) nivelIdx = 0;
       g.drawImage(bgimgs[nivelIdx], 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
       //drawClouds(g);

       switch (gameState.state) {
            case START: inicio.draw(g);
                break;
            case PLAYING: jugando.draw(g);
                //levelMan.update();
                //player.update();    
                //checkCloseToBorder();
                break;
            case PAUSE: jugando.draw(g);
                        pausa.draw(g);
                break;
            case LEVEL_COMPLETED: levelCompleted.draw(g);
                break;
            case CHARACTER_SELECT: characterSelect.draw(g);
                break;
            default:
                break;
        }
       //levelMan.draw(g,xLvlOffset);
       //player.render(g,xLvlOffset);
    }

    // private void drawClouds(Graphics g) {
    //     for (int i = 0; i <= 3; i++) {
    //         g.drawImage(bigCloud, i*BIG_CLOUDS_WIDTH-(int)(xLvlOffset*0.3)
    //         , (int)(204*Juego.SCALE), BIG_CLOUDS_WIDTH, BIG_CLOUDS_HEIGHT, null);

    //     }

    //     for (int i = 0; i < smallCloudPos.length; i++) {
    //         g.drawImage(smallCloud, 4*i*SMALL_CLOUDS_WIDTH-(int)(xLvlOffset*0.7)
    //         , smallCloudPos[i], SMALL_CLOUDS_WIDTH, SMALL_CLOUDS_HEIGHT, null);

    //     }
       
    // }

    /*public Jugador getPlayer() {
        return player;
    }*/
    
    public void windowFocusLost(){
        if (gameState.state == gameState.PLAYING) {
            jugando.getPlayer().resetDirBoolean();
        }
    }

    public pauseOverlay getPause(){
        return pausa;
    }

    public Playing getPlaying(){
        return jugando;
    }

    public Start getStart(){
        return inicio;
    }

    public LevelCompleted getLevelCompleted() { return levelCompleted; }
    public CharacterSelect getCharacterSelect() { return characterSelect; }
}
