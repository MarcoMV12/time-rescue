package Niveles;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Elementos.*;
import Juegos.Juego;
import Objects.Coin;
import Objects.Hp;
import Objects.contenedor;
import Objects.Urna;
import Objects.Arena;
import Objects.Spike;
import Objects.Palmera;
import Objects.Coco;
import static utilz.LoadSave.*;

import utilz.LoadSave;
import utilz.MetodosAyuda;

public class Level {
    private BufferedImage img;
    private String levelName;
    private int[][] lvlData;
    private ArrayList<Velociraptor> velos;
    private ArrayList<Planta> plantas;
    private ArrayList<Indio> indios;
    private ArrayList<Mosco> moscos;
    private ArrayList<Flyer> flyers;
    private ArrayList<Boss> boss;

    private ArrayList<Coin> coins;
    private ArrayList<contenedor> contenedores;
    private ArrayList<Urna> urnas;
    private ArrayList<Hp> vidas;
    private ArrayList<Arena> arenas;
    private ArrayList<Spike> spikes;
    private ArrayList<Palmera> palmeras;
    private ArrayList<Coco> cocos;
    
    private int lvlTilesWide;
    private int maxTileOffSet;
    private int maxLevelOffSetX;
    private int maxLevelOffSetY;
    private Point playerSpawn;

    public Level(String levelName) {
        this.levelName = levelName;
        this.img = LoadSave.GetSpriteAtlas(levelName);
        createLevelData();
        createEnemies();

        createCoins();
        createContenedores();
        urnas = MetodosAyuda.GetUrnas(levelName);
        createVidas();
        arenas    = MetodosAyuda.GetArenas(levelName);
        spikes    = MetodosAyuda.GetSpikes(levelName);
        palmeras  = MetodosAyuda.GetPalmeras(levelName);
        cocos     = new ArrayList<>();
        int cocoDelay = 0;
        for (Palmera p : palmeras) {
            int cx = (int)(p.getHitbox().x) + 30;
            int cy = (int)(p.getHitbox().y) + 10;
            cocos.add(new Coco(cx, cy, cocoDelay));
            cocoDelay += 30;
        }

        calcLevelOffSets();
        calcPlayerSpawn();
        //this.lvlData = lvlData;
    }

    private void createEnemies() {
        velos   = LoadSave.GetVelociraptors(levelName);
        indios  = LoadSave.GetIndios(levelName);
        plantas = LoadSave.GetPlantas(levelName);
        moscos  = LoadSave.GetMoscos(levelName);
        flyers  = LoadSave.GetFlyers(levelName);
        boss    = LoadSave.GetBoss(levelName);
    }

    private void createLevelData() {
        lvlData = LoadSave.GetLevelData(levelName);
    }

    private void createContenedores() {
        contenedores = MetodosAyuda.GetContenedores(levelName);
    }

    private void createCoins() {
        coins = MetodosAyuda.GetCoins(levelName);
    }

    private void createVidas() {
        vidas = MetodosAyuda.GetVidas(levelName);
    }

    private void calcLevelOffSets() {
        lvlTilesWide = img.getWidth();
        maxTileOffSet = lvlTilesWide - Juego.TILES_WIDTH;
        maxLevelOffSetX = Juego.TILES_SIZE * maxTileOffSet;

        int maxTileOffSetY = img.getHeight() - Juego.TILES_HEIGHT;
        if (maxTileOffSetY < 0) maxTileOffSetY = 0;
        maxLevelOffSetY = Juego.TILES_SIZE * maxTileOffSetY;
    }

    // Busca el pixel marcador del spawn en el mapa.
    // Sugerencia: usa un valor azul único, ej. azul=100
    private Point findPlayerSpawn() {
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                java.awt.Color c = new java.awt.Color(img.getRGB(i, j));
                if (c.getBlue() == 100) {  // ← define una constante PLAYER_SPAWN
                    return new Point(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE);
                }
            }
        }
        // Si no se encontró marcador, spawn por defecto
        return new Point(200, 200);
    }

    private void calcPlayerSpawn() {
        playerSpawn = findPlayerSpawn();
    }

    public int getSpriteIndex(int x, int y){
        return lvlData[y][x];
    }

    public int[][] getLvlData() {
        return lvlData;
    }

    public Point getPlayerSpawn(){
        return playerSpawn;
    }
    
    public ArrayList<Velociraptor> getVelos() {
        return velos;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public ArrayList<contenedor> getContenedores() {
        return contenedores;
    }

    public ArrayList<Urna> getUrnas() {
        return urnas;
    }

    public ArrayList<Hp> getVidas() {
        return vidas;
    }

    public int getMaxLevelOffSetX() {
        return maxLevelOffSetX;
    }

    public int getMaxLevelOffSetY() {
        return maxLevelOffSetY;
    }

    public ArrayList<Planta> getPlantas() {
        return plantas;
    }

    public ArrayList<Indio> getIndios() {
        return indios;
    }

    public ArrayList<Mosco> getMoscos() {
        return moscos;
    }

    public ArrayList<Flyer> getFlyers() {
        return flyers;
    }

    public ArrayList<Boss> getBoss() {
        return boss;
    }

    public ArrayList<Arena> getArenas()       { return arenas; }
    public ArrayList<Spike> getSpikes()       { return spikes; }
    public ArrayList<Palmera> getPalmeras()   { return palmeras; }
    public ArrayList<Coco> getCocos()         { return cocos; }

    

}
