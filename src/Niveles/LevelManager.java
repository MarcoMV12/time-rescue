package Niveles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import utilz.LoadSave;


public class LevelManager {
    private Juego game;
    private BufferedImage[][] levelSprites; // [nivel][frameIndex]
    private Level[] niveles;

    private int nivelActual = 0;
    public static final int TOTAL_NIVELES = 4;

    private BufferedImage[] bgImgs;
    private BufferedImage bgTumba;
    private static final int TOMB_WORLD_X = 105 * Juego.TILES_SIZE;

    public LevelManager(Juego game) {
        this.game = game;
        importOutsideSprite();
        cargarNiveles();
        utilz.MetodosAyuda.setEmptyTileForLevel(nivelActual);
        bgImgs = new BufferedImage[] {
            LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG),
            LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG2),
            LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG3),
            LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG4)
        };
        bgTumba = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_THUMB);
    }

    private void cargarNiveles() {
        niveles = new Level[TOTAL_NIVELES];
        niveles[0] = new Level(LoadSave.LEVEL_1);
        niveles[1] = new Level(LoadSave.LEVEL_2);
        niveles[2] = new Level(LoadSave.LEVEL_3);
        niveles[3] = new Level(LoadSave.LEVEL_4);
    }

    private void importOutsideSprite() {
        String[] atlases = {
            LoadSave.LEVEL_ATLAS,
            LoadSave.LEVEL_ATLAS2,
            LoadSave.LEVEL_ATLAS3,
            LoadSave.LEVEL_ATLAS4
        };

        levelSprites = new BufferedImage[TOTAL_NIVELES][];

        for (int n = 0; n < TOTAL_NIVELES; n++) {
            BufferedImage img = LoadSave.GetSpriteAtlas(atlases[n]);
            if (img == null) {
                levelSprites[n] = new BufferedImage[0];
                continue;
            }

            // Tamaño de tile definido por atlas en LoadSave.ATLAS_TILE_W/H
            int tileW  = LoadSave.ATLAS_TILE_W[n];
            int tileH  = LoadSave.ATLAS_TILE_H[n];
            int cols   = img.getWidth()  / tileW;
            int rows   = img.getHeight() / tileH;
            int frames = cols * rows;

            levelSprites[n] = new BufferedImage[frames];
            for (int j = 0; j < rows; j++)
                for (int i = 0; i < cols; i++)
                    levelSprites[n][j * cols + i] = img.getSubimage(i * tileW, j * tileH, tileW, tileH);
        }
    }

    public Level currentLevel() {
        return niveles[nivelActual];
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public boolean avanzarNivel() {
        if (nivelActual < TOTAL_NIVELES - 1) {
            nivelActual++;
            utilz.MetodosAyuda.setEmptyTileForLevel(nivelActual);
            return true;
        }
        return false;
    }

    public void reiniciar() {
        nivelActual = 0;
        utilz.MetodosAyuda.setEmptyTileForLevel(nivelActual);
    }

    public void draw(Graphics g, int LvlOffset) {
        Level lvl = currentLevel();
        BufferedImage[] sprites = levelSprites[nivelActual];
        for (int j = 0; j < Juego.TILES_HEIGHT; j++) {
            for (int i = 0; i < lvl.getLvlData()[0].length; i++) {
                int index = lvl.getSpriteIndex(i, j);
                if (index < 0 || index >= sprites.length || sprites[index] == null) continue;
                g.drawImage(sprites[index],
                    Juego.TILES_SIZE * i - LvlOffset,
                    Juego.TILES_SIZE * j,
                    Juego.TILES_SIZE, Juego.TILES_SIZE, null);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        Level lvl = currentLevel();
        BufferedImage[] sprites = levelSprites[nivelActual];
        int rows = lvl.getLvlData().length;
        int cols = lvl.getLvlData()[0].length;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                int index = lvl.getSpriteIndex(i, j);
                if (index < 0 || index >= sprites.length || sprites[index] == null) continue;
                g.drawImage(sprites[index],
                    Juego.TILES_SIZE * i - xLvlOffset,
                    Juego.TILES_SIZE * j - yLvlOffset,
                    Juego.TILES_SIZE, Juego.TILES_SIZE, null);
            }
        }
    }

    public void drawBackground(Graphics g, int xLvlOffset) {
        if (nivelActual == 1) {
            int splitX = TOMB_WORLD_X - xLvlOffset;
            if (splitX >= Juego.GAME_WIDTH) {
                g.drawImage(bgImgs[1], 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
            } else if (splitX <= 0) {
                g.drawImage(bgTumba, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
            } else {
                g.drawImage(bgImgs[1], 0,      0, splitX,                    Juego.GAME_HEIGHT, null);
                g.drawImage(bgTumba,   splitX, 0, Juego.GAME_WIDTH - splitX, Juego.GAME_HEIGHT, null);
            }
        } else {
            BufferedImage bg = bgImgs[nivelActual];
            if (bg != null)
                g.drawImage(bg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        }
    }

    public void update() {}
}
