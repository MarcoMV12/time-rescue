package utilz;

import static utilz.Constantes.ConstantesObjetos.*;
import static utilz.Constantes.Objetos.*;

import utilz.LoadSave;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Elementos.Door;
//import Elementos.Mosco;
import Juegos.Juego;
import Objects.Coin;
import Objects.Hp;
import Objects.contenedor;
import Objects.Urna;
import Objects.Arena;
import Objects.Spike;
import Objects.Palmera;
import Objects.Coco;

public class MetodosAyuda {
    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData) {
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y, lvlData))
                if (!IsSolid(x, y + height, lvlData))
                    if (!IsSolid(x + width, y + height, lvlData))
                        return true;
        return false;
    }

    // Igual que CanMoveHere pero ignora tiles semi-sólidos (para saltar hacia arriba)
    public static boolean CanMoveHereGoingUp(float x, float y,
            float width, float height, int[][] lvlData) {
        if (!IsSolidIgnoreSemiSolid(x, y, lvlData))
            if (!IsSolidIgnoreSemiSolid(x + width, y, lvlData))
                if (!IsSolidIgnoreSemiSolid(x, y + height, lvlData))
                    if (!IsSolidIgnoreSemiSolid(x + width, y + height, lvlData))
                        return true;
        return false;
    }

    private static boolean IsSolidIgnoreSemiSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Juego.TILES_SIZE;
        if (x < 0 || x >= maxWidth) return true;
        if (y < 0 || y >= Juego.GAME_HEIGHT) return true;
        int xIndex = (int)(x / Juego.TILES_SIZE);
        int yIndex = (int)(y / Juego.TILES_SIZE);
        int valor  = lvlData[yIndex][xIndex];
        if (IsSemiSolidValue(valor)) return false; // siempre pasable al subir
        return IsTileSolid(xIndex, yIndex, lvlData);
    }

    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData, ArrayList<Door> doors) {

        if (!IsSolid(x, y, lvlData, doors))
            if (!IsSolid(x + width, y, lvlData, doors))
                if (!IsSolid(x, y + height, lvlData, doors))
                    if (!IsSolid(x + width, y + height, lvlData, doors))
                        return true;

        return false;
    }    

    public static boolean IsSemiSolidValue(int valor) {
        return valor == 6 || valor == 7 || valor == 8 || valor == 38 || valor == 48 || valor == 58;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData, ArrayList<Door> doors) {
        int maxWidth = lvlData[0].length * Juego.TILES_SIZE;

        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= Juego.GAME_HEIGHT)
            return true;

        int xIndex = (int)(x / Juego.TILES_SIZE);
        int yIndex = (int)(y / Juego.TILES_SIZE);
        int valor = lvlData[yIndex][xIndex];

        if (IsSemiSolidValue(valor)) {
            float yInTile = y - yIndex * Juego.TILES_SIZE;
            return yInTile < Juego.TILES_SIZE / 2.0f;
        }

        // 🔥 AQUÍ VA TU PUERTA (SEGURO)
        if (doors != null) {
            Rectangle2D.Float point = new Rectangle2D.Float(x, y, 1, 1);

            for (Door d : doors) {
                if (d.isSolid() && d.getHitbox().intersects(point)) {
                    return true;
                }
            }
        }
            
        

        return IsTileSolid(xIndex, yIndex, lvlData);
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData) {
        int maxWidth=lvlData[0].length*Juego.TILES_SIZE;
        int maxHeight = lvlData.length * Juego.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= maxHeight)
            return true;
        int xIndex = (int)(x / Juego.TILES_SIZE);
        int yIndex = (int)(y / Juego.TILES_SIZE);
        int valor = lvlData[yIndex][xIndex];

        if (IsSemiSolidValue(valor)) {
            float yInTile = y - yIndex * Juego.TILES_SIZE;
            return yInTile < Juego.TILES_SIZE / 2.0f;
        }

        return IsTileSolid(xIndex, yIndex, lvlData);
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        if (xTile < 0 || xTile >= lvlData[0].length || yTile < 0 || yTile >= lvlData.length)
            return true;
        int valor = lvlData[yTile][xTile];
        if (EMPTY_TILE == 11) {
            for (int t : EGYPT_TRANSPARENT)
                if (valor == t) return false;
            return true;
        }
        return valor >= 48 | valor < 0 | valor != EMPTY_TILE;
    }

    /** Valor de tile considerado vacío (no sólido) para el nivel actual. */
    public static int EMPTY_TILE = 18;

    /** Tiles transparentes del mundo Egipto (igual que el proyecto standalone). */
    private static final int[] EGYPT_TRANSPARENT = {11, 84, 49, 52, 40, 31, 43, 53, 41, 78, 66, 54};

    /** Configura el id de tile vacío según el nivel (0=N1, 1=N2, 2=N3, 3=N4). */
    public static void setEmptyTileForLevel(int nivelIdx) {
        EMPTY_TILE = switch (nivelIdx) {
            case 0 -> 18;
            case 1 -> 11;
            default -> 13; // niveles 3 y 4
        };
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed, int[][] lvlData) {
        if (airSpeed > 0) {
            int currentTile = (int) ((hitbox.y + airSpeed) / Juego.TILES_SIZE);
            int tileYPos = currentTile * Juego.TILES_SIZE;
            int yOffset = (int) (Juego.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else {
            int headTileY = (int)((hitbox.y + airSpeed) / Juego.TILES_SIZE);
            int headTileX1 = (int)(hitbox.x / Juego.TILES_SIZE);
            int headTileX2 = (int)((hitbox.x + hitbox.width) / Juego.TILES_SIZE);

            boolean semiSolid = false;
            if (headTileY >= 0 && headTileY < lvlData.length) {
                if (headTileX1 >= 0 && headTileX1 < lvlData[0].length && IsSemiSolidValue(lvlData[headTileY][headTileX1]))
                    semiSolid = true;
                if (!semiSolid && headTileX2 >= 0 && headTileX2 < lvlData[0].length && IsSemiSolidValue(lvlData[headTileY][headTileX2]))
                    semiSolid = true;
            }

            if (semiSolid)
                return headTileY * Juego.TILES_SIZE + Juego.TILES_SIZE / 2;

            int currentTile = (int)(hitbox.y / Juego.TILES_SIZE);
            return currentTile * Juego.TILES_SIZE;
        }
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / Juego.TILES_SIZE);
        if (airSpeed > 0) {
            int tileYPos = currentTile * Juego.TILES_SIZE;
            int yOffset = (int) (Juego.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else
            return currentTile * Juego.TILES_SIZE;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Juego.TILES_SIZE);
        if (xSpeed > 0) {
            int tileXPos = currentTile * Juego.TILES_SIZE;
            int xOffset = (int) (Juego.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else
            return currentTile * Juego.TILES_SIZE;

    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        if(!IsSolid(hitbox.x, hitbox.y+hitbox.height+1, lvlData))
            if(!IsSolid(hitbox.x+hitbox.width, hitbox.y+hitbox.height+1, lvlData))
                return false;
        return true;

    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if (IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height, lvlData))
            return true;
        if (IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData))
            return true;
        return false;

        // if (xSpeed > 0)
        // return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
        // else
        // return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
    }

    public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) {
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
            if(!IsTileSolid(xStart + i, y + 1, lvlData))
                return false;
        }
        return true;
    }

    public static boolean IfIsSightClear(int[][] lvlData, Rectangle2D.Float hitbox, Rectangle2D.Float playerHitbox, int yTile) {
        int firstXTile = (int) (hitbox.x / Juego.TILES_SIZE);
        int secondXTile = (int) ((hitbox.x + hitbox.width) / Juego.TILES_SIZE);
        
        if (firstXTile > secondXTile) {
            return IsAllTileWalkable(secondXTile, firstXTile, yTile, lvlData);
        }else {
            return IsAllTileWalkable(firstXTile, secondXTile, yTile, lvlData);
        }
    }

    public static ArrayList<Coin> GetCoins(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Coin> monedas = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getBlue();
                if (valor == COIN) {
                    monedas.add(new Coin(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE, COIN));
                }
            }
        }
        return monedas;
    }

    public static ArrayList<contenedor> GetContenedores(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<contenedor> rompibles = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getBlue();

                // Reconoce todos los tipos de contenedores
                if (valor == BARRIL || valor == CAJA || valor == POT
                || valor == LETRERO1 || valor == LETRERO2 || valor == COFRE) {
                    rompibles.add(new contenedor(
                        i * Juego.TILES_SIZE,
                        j * Juego.TILES_SIZE,
                        valor   // ← el valor del azul ES el tipo
                    ));
                }
            }
        }
        return rompibles;
    }

    public static ArrayList<Urna> GetUrnas(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Urna> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int valor = new Color(img.getRGB(i, j)).getBlue();
                if (valor == CONTEDORANUBIS || valor == CONTENEDORMUMMY)
                    lista.add(new Urna(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE, valor));
            }
        return lista;
    }

    public static ArrayList<Hp> GetVidas(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Hp> vidas = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getBlue();
                if (valor == HP) {
                    vidas.add(new Hp(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE, HP));
                }
            }
        }
        return vidas;
    }

    public static ArrayList<Arena> GetArenas(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Arena> lista = new ArrayList<>();
        int orden = 0;
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                int valor = new Color(img.getRGB(i, j)).getBlue();
                if (valor == ARENA) {
                    Arena a = new Arena(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE, orden++);
                    a.aparecer();
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    public static ArrayList<Spike> GetSpikes(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Spike> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                int valor = new Color(img.getRGB(i, j)).getBlue();
                if (valor == SPIKE)
                    lista.add(new Spike(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        }
        return lista;
    }

    public static ArrayList<Palmera> GetPalmeras(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Palmera> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                int valor = new Color(img.getRGB(i, j)).getBlue();
                if (valor == PALMERA)
                    lista.add(new Palmera(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        }
        return lista;
    }

    public static ArrayList<Coco> GetCocos(String levelName) {
        BufferedImage img = LoadSave.GetSpriteAtlas(levelName);
        ArrayList<Coco> lista = new ArrayList<>();
        int delay = 0;
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                int valor = new Color(img.getRGB(i, j)).getBlue();
                if (valor == COCO) {
                    lista.add(new Coco(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE, delay));
                    delay += 30;
                }
            }
        }
        return lista;
    }

    //4
    public static boolean IsTrapTile(float x, float y, int[][] lvlData) {
        int xIndex = (int)(x / Juego.TILES_SIZE);
        int yIndex = (int)(y / Juego.TILES_SIZE);

        // evitar errores fuera del mapa
        if (xIndex < 0 || yIndex < 0 || yIndex >= lvlData.length || xIndex >= lvlData[0].length)
            return false;

        return lvlData[yIndex][xIndex] == 50; //ID de tu trampa
    }
}
