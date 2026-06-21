package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import Elementos.*;
import GameStates.Playing;
import Juegos.Juego;
//import Juegos.Juego;
import Juegos.PanelJuego;

import static utilz.Constantes.ConstantesEnemy.*;


public class LoadSave {
    public static final String LEVEL_ATLAS = "Mundos/Tileset2.png";
    public static final String LEVEL_ATLAS2 = "Mundos/TileSetEgypt.png";
    public static final String LEVEL_ATLAS3 = "Mundos/TileSetMedv.png";
    public static final String LEVEL_ATLAS4 = "Mundos/TiltlesFut2.png";

    // Tamaño en píxeles de cada tile dentro de su spritesheet.
    // Si un atlas usa tiles de 16x16, poner 16. Si usa 32x32, poner 32.
    // El juego siempre renderiza a Juego.TILES_SIZE (32px), el escalado es automático.
    public static final int[] ATLAS_TILE_W = { 32, 32, 32, 32 };
    public static final int[] ATLAS_TILE_H = { 32, 32, 32, 32 };

    public static final String PLAYER_ATLAS = "Personajes/caveman3.png";
    public static final String PLAYER_ATLAS_CAVEMAN_R = "Personajes/cavemanR.png";

    public static final String LEVEL_1 = "Mundos/MundoF8.png"; //Nivel dibujado
    public static final String LEVEL_2 = "Mundos/MundoEgypt2.png"; //Nivel 2
    public static final String LEVEL_3 = "Mundos/MundoMedieval.png"; //Nivel 3
    public static final String LEVEL_4 = "Mundos/WorldSprite2.png"; 

    // GIFs de transición entre niveles
    public static final String TRANSICION_GIF_1 = "uis/transicion1.gif";
    public static final String TRANSICION_GIF_2 = "uis/transicion1.gif";
    public static final String TRANSICION_GIF_3 = "uis/transicion1.gif";
    public static final String CREDITOS_GIF = "uis/creditos.gif";

    // Sprites del selector de personajes
    public static final String CHAR_SELECT_BG = "uis/selectorBg.jpg";
    public static final String CHAR_IMG_1    = "uis/Character1.png";
    public static final String CHAR_IMG_2    = "uis/Character2.png";
    public static final String CHAR_IMG_3    = "uis/Character3.png";
    public static final String CHAR_IMG_4    = "uis/Character4.png";
    public static final String NOT_CHAR_IMG_2 = "uis/NotCharacter2.png";
    public static final String NOT_CHAR_IMG_3 = "uis/NotCharacter3.png";
    public static final String NOT_CHAR_IMG_4 = "uis/NotCharacter4.png";

    public static final String PLAYING_BG_IMG = "Mundos/Background.png";

    // public static final String BIG_CLOUDS = "Mundos/big_clouds.png";
    // public static final String SMALL_CLOUDS = "Mundos/small_clouds.png";
    public static final String PAUSE_BUTTONS = "uis/pauseBtns2.png";
    public static final String PAUSE_BACKGROUND = "uis/Pausa1024x384.png";
    public static final String GAME_OVER_BG = "uis/PantallaMuerte.png";
    public static final String DIALOGO_LVL3     = "uis/DialogoLvl3_896x512.png";
    public static final String INSTRUCCIONES4    = "uis/instrucciones4.jpg";

    // Enemigos Nivel 1
    public static final String VELO_SPRITES = "Personajes/veloSprites.png";
    public static final String PLANT_SPRITES = "Personajes/plantSprite3.png";
    public static final String INDIO_SPRITES = "Personajes/IndioSprites.jpg";
    public static final String ALA_SPRITES = "Personajes/flayerSprites2.png";
    public static final String MOSCO_SPRITES = "Personajes/mosco2.png";
    public static final String BOSS_SPRITES = "Personajes/bossSpritesU.jpg";
    
    public static final String MAQUINA_TIME_APAGADA = "ass/portal0.png";
    public static final String MAQUINA_TIME_ENCENDIDA = "ass/portal1.png";
    
    // Nivel 2
    public static final String SERPIENTE_SPRITES = "Personajes/SnakeSprite.png";
    public static final String SCORPIO_SPRITES = "Personajes/Scorpio_Sprite.png";
    public static final String MUMMY_SPRITES = "Personajes/Mummy_Sprite.png";
    
    public static final String DECEASED_SPRITES = "Personajes/Deceased_Sprite.png";
    public static final String DECEASED_BALL = "ass/Ball.png";
     public static final String MANTICORA_BALL    = "ass/Acid.png";
    public static final String EGYPTIANARCHER_SPRITES = "Personajes/ArqueroEgyptian.png";
    public static final String EGYPTIANARCHER_ARROW = "ass/Arrow.png";
    public static final String ANUBIS_SPRITES          = "Personajes/AnubisEnemy.png";
    public static final String WARRIOR_SPRITES         = "Personajes/Warrior.png";
    public static final String FARAON_SPRITES          = "Personajes/Pharaoh.png";
    public static final String FARAON_BALL             = "ass/FaraonBall.png";
    public static final String ANUBIS_BALL             = "ass/BallAnubis.png";
    public static final String MANTICORA_SPRITES       = "Personajes/Manticore.png";
    public static final String CONTENEDOR_ANUBIS_IMG   = "ContenedorAnubis.png";
    public static final String CONTENEDOR_MUMMY_IMG    = "ContenedorMummy.png";
    public static final String WEAPON_ANUBIS_IMG       = "WeaponAnubis.png";
    public static final String WEAPON_MUMMY_IMG        = "WeaponMummy.png";

    public static final String PLAYING_BG_IMG2 = "Mundos/playing_bg_img.png";
    public static final String BACKGROUND_THUMB = "Mundos/Backgroundtumb.png";
    public static final String SPIKE_ATLAS = "ass/spike.png"; 
    public static final String PALMERA_ATLAS = "ass/Palm.png"; 
    public static final String ARENA_ATLAS = "ass/GroundBreaking.png"; 
    public static final String COCO_ATLAS = "ass/Coconut_Falling.png";
    
    public static final String STATUS_BAR = "uis/bar2.png";

    //Nivel 3
    public static final String CABALLERO_ATLAS = "Personajes/NoEscudo.png";
    public static final String CABALLERO2_ATLAS = "Personajes/Caballero.png";
    public static final String PLAYING_BG_IMG3 = "Mundos/FondoMed.png";
    
    public static final String ARMADURA_SPRITES = "Personajes/ARMADO128x128.png";
    public static final String RAT_SPRITES = "Personajes/RatSheet62x44.png";
    public static final String ATCHER_SPRITES = "Personajes/ATCHER.png";
    public static final String BAT_SPRITES = "Personajes/BAT32x32.png";
    public static final String HYENA_SPRITES = "Personajes/Hyena_Sprite.png";
    public static final String MAGO_SPRITES = "Personajes/MAGO_SPRITE128x105.png";
    
    public static final String SHIELD_ATLAS = "ass/EscudoSprite20x23.png";
    public static final String POSION_ATLAS = "ass/PosionAzul.png";
    public static final String ALCOHOL_ATLAS = "ass/Alcohol.png";
    public static final String RESORTERA_ATLAS = "ass/resortera.png";
    public static final String CLORO_ATLAS = "ass/Cloro.png";

    // -----NIvel 4-----
    public static final String PLAYER_ATLAS4 = "Personajes/CientificoSprite8.png";
    public static final String PLAYING_BG_IMG4 = "Mundos/bg1.png";
    public static final String TRAP2_SPRITE = "ass/Spike_B.png";
    public static final String TRAP_SPRITES = "ass/trap_atlas.png";
    public static final String DOG_SPRITES = "Personajes/DogSprite3.png";
    public static final String TORRET_SPRITES = "Personajes/torretSprite.png";
    public static final String SPIDER_SPRITES = "Personajes/SpiderSprite5.png";
    public static final String DRON_SPRITES = "Personajes/DronSprite2.png";
    public static final String BOSS_SPRITES4 = "Personajes/BossSprite2.png";
    public static final String DOOR_SPRITES = "ass/door.png";
    public static final String BUTTON_SPRITES = "ass/button.png";
    public static final String NINJA_SPRITES = "Personajes/NinjaSprite2.png";
    public static final String BULLET_SPRITES = "ass/bulletSprite.png";
    public static final String CBULLET_SPRITES = "ass/bulletSprite.png";
    public static final String WEAPON_SPRITES = "ass/Weapon.png";
    public static final String SHOOT_TORRET_SPRITES = "ass/ShootTorret.png";


    //Pantalla de inicio
    public static final String GIF = "uis/pantallaC.gif";
    public static final String TITLE = "uis/TituloGame.jpg";
    public static final String START_TEXT = "uis/StartPress.jpg";

    public static final String HP_SPRITES = "hp.png";
    public static final String COIN_SPRITES = "moneda.png";
    public static final String CONTENEDORES_ATLAS = "container.png";

    // Sprites de items de nivel (colocar en res/items/)
    public static final String ITEM_LLAVE     = "items/llave.png";
    public static final String ITEM_CRISTAL   = "items/cristal.png";
    public static final String ITEM_PERGAMINO = "items/pergamino.png";


    public static BufferedImage GetSpriteAtlas(String name) {
        BufferedImage img = null;
        InputStream is = LoadSave.class
                .getResourceAsStream("/res/" + name);
        if (is == null) {
            System.err.println("[LoadSave] Recurso no encontrado: /res/" + name);
            return null;
        }
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            Logger.getLogger(PanelJuego.class.getName())
                    .log(Level.SEVERE, null, e);
        }
        return img;
    }

    public static ArrayList<Velociraptor> GetVelociraptors(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Velociraptor> raptors = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == VELO) {
                    raptors.add(new Velociraptor(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return raptors;
    }

    public static ArrayList<Indio> GetIndios(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Indio> indios = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == INDIO) {
                    indios.add(new Indio(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return indios;
    }

    public static ArrayList<Planta> GetPlantas(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Planta> plantas = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == PLANT) {
                    plantas.add(new Planta(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return plantas;
    }

    public static ArrayList<Boss> GetBoss(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Boss> boss = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == BOSS) {
                    boss.add(new Boss(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return boss;
    }

    public static ArrayList<Mosco> GetMoscos(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Mosco> moscos = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == MOSCO) {
                    moscos.add(new Mosco(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return moscos;
    }

    public static ArrayList<Flyer> GetFlyers(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Flyer> flyers = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == FLYER) {
                    flyers.add(new Flyer(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return flyers;
    }

    public static int[][] GetLevelData(String levelName){

        BufferedImage img=LoadSave.GetSpriteAtlas(levelName);
        int [][]lvlData=new int[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color=new Color(img.getRGB(i, j));
                lvlData[j][i]=color.getRed();
            }
        }
        return lvlData;

    }

    public static int[][] GetLevelData(){
        return GetLevelData(LEVEL_1);
    }

    //Nivel 2
    
    public static BufferedImage[] GetFrameSequence(String folder, String prefix, int startIndex, int count) {
        return GetFrameSequenceGeneric("Personajes/Egyptian Sentry/PNG/PNG Sequences", folder, prefix, startIndex, count);
    }

    public static BufferedImage[] GetFrameSequenceGeneric(String base, String folder, String prefix, int startIndex, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            String path = base + "/" + folder + "/" + prefix + "_" + String.format("%03d", startIndex + i) + ".png";
            frames[i] = GetSpriteAtlas(path);
        }
        return frames;
    }
    public static ArrayList<Snake> GetSerpientes() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Snake> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == SERPIENTE)
                    list.add(new Snake(i * Juego.TILES_SIZE,j * Juego.TILES_SIZE));
            }
        return list;
    }
    public static ArrayList<Scorpio> GetScorpio() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Scorpio> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == SCORPIO)
                    list.add(new Scorpio(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<Mummy> GetMummies() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Mummy> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == MUMMY)
                    list.add(new Mummy(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

  

    public static ArrayList<Anubis> GetAnubis() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Anubis> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == ANUBIS)
                    list.add(new Anubis(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<Deceased> GetDeceased() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Deceased> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == DECEASED)
                    list.add(new Deceased(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<EgyptianArcher> GetEgyptianArcher() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<EgyptianArcher> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == EGYPTIANARCHER)
                    list.add(new EgyptianArcher(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<EgyptianFighter> GetEgyptianFighters() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<EgyptianFighter> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == EGYPTIANFIGHTER)
                    list.add(new EgyptianFighter(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<Faraon> GetFaraones() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Faraon> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == FARAON)
                    list.add(new Faraon(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }

    public static ArrayList<Manticora> GetManticoras() {
        BufferedImage img = GetSpriteAtlas(LEVEL_2);
        ArrayList<Manticora> list = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int green = new Color(img.getRGB(i, j)).getGreen();
                if (green == MANTICORA)
                    list.add(new Manticora(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return list;
    }


    //Nivel 3

    public static ArrayList<Armado> GetArmados() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Armado> armados = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == ARMADURA) {
                    armados.add(new Armado(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return armados;
    }

    public static ArrayList<Atcher> GetAtchers() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Atcher> atchers = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == ATCHER) {
                    atchers.add(new Atcher(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return atchers;
    }

    public static ArrayList<Rata> GetRatas() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Rata> ratas = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == RAT) { 
                    ratas.add(new Rata(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return ratas;
    }

    public static ArrayList<Mago> GetMagos() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Mago> magos = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == MAGO) {
                    magos.add(new Mago(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return magos;
    }

    public static ArrayList<Hyena> GetHyenas() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Hyena> hyenas = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == HYENA) {
                    hyenas.add(new Hyena(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return hyenas;
    }

    public static ArrayList<Bat> GetBats() {
        BufferedImage img = GetSpriteAtlas(LEVEL_3);
        ArrayList<Bat> bats = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == BAT) {
                    bats.add(new Bat(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return bats;
    }

    public static ArrayList<Objects.ObjEscudo> GetEscudos(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Objects.ObjEscudo> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int blue = new Color(img.getRGB(i, j)).getBlue();
                if (blue == 3)
                    lista.add(new Objects.ObjEscudo(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return lista;
    }

    public static ArrayList<Objects.ObjAlcohol> GetAlcoholes(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Objects.ObjAlcohol> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int blue = new Color(img.getRGB(i, j)).getBlue();
                if (blue == 1)
                    lista.add(new Objects.ObjAlcohol(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return lista;
    }

    public static ArrayList<Objects.ObjCloro> GetCloros(String levelName) {
        BufferedImage img = GetSpriteAtlas(levelName);
        ArrayList<Objects.ObjCloro> lista = new ArrayList<>();
        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                int blue = new Color(img.getRGB(i, j)).getBlue();
                if (blue == 2)
                    lista.add(new Objects.ObjCloro(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
            }
        return lista;
    }

    // public static ArrayList<ObjEscudo> GetEscudos() {
    //     BufferedImage img = GetSpriteAtlas(LEVEL_3);
    //     ArrayList<ObjEscudo> lista = new ArrayList<>();
    //     for (int j = 0; j < img.getHeight(); j++)
    //         for (int i = 0; i < img.getWidth(); i++) {
    //             int blue = new Color(img.getRGB(i, j)).getBlue();
    //             if (blue == 0)
    //                 lista.add(new ObjEscudo(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
    //         }
    //     return lista;
    // }

    // public static ArrayList<ObjAlcohol> GetAlcoholes() {
    //     BufferedImage img = GetSpriteAtlas(LEVEL_3);
    //     ArrayList<ObjAlcohol> lista = new ArrayList<>();
    //     for (int j = 0; j < img.getHeight(); j++)
    //         for (int i = 0; i < img.getWidth(); i++) {
    //             int blue = new Color(img.getRGB(i, j)).getBlue();
    //             if (blue == 1)
    //                 lista.add(new ObjAlcohol(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
    //         }
    //     return lista;
    // }

    // public static ArrayList<ObjCloro> GetCloros() {
    //     BufferedImage img = GetSpriteAtlas(LEVEL_3);
    //     ArrayList<ObjCloro> lista = new ArrayList<>();
    //     for (int j = 0; j < img.getHeight(); j++)
    //         for (int i = 0; i < img.getWidth(); i++) {
    //             int blue = new Color(img.getRGB(i, j)).getBlue();
    //             if (blue == 2)
    //                 lista.add(new ObjCloro(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
    //         }
    //     return lista;
    // }

    public static ArrayList<Dog> GetDogs() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Dog> dogs = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == DOG) {
                    dogs.add(new Dog(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return dogs;
    }

    public static ArrayList<Spider> GetSpiders() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Spider> spiders = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == SPIDER) {
                    spiders.add(new Spider(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return spiders;
    }

    public static ArrayList<Torret> GetTorrets() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Torret> torrets = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();

                if (valor == TORRET) {
                    torrets.add(new Torret(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return torrets;
    }

    public static ArrayList<Boss4> GetBoss4() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Boss4> boss = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == BOSS4) { // 🔥 era BOSS, ahora BOSS4
                    boss.add(new Boss4(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return boss;
    }

    public static ArrayList<Dron> GetDrons() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Dron> drons = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == DRON) {
                    drons.add(new Dron(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return drons;
    }

    public static ArrayList<Trap> GetTraps() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Trap> traps = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen(); // igual que enemigos

                if (valor == TRAP) {
                    traps.add(new Trap(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return traps;
    }

    public static ArrayList<Trap2> GetTraps2() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Trap2> traps2 = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();

                if (valor == TRAP2) {
                    traps2.add(new Trap2(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return traps2;
    }

    public static ArrayList<Door> GetDoors() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Door> doors = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();

                if (valor == DOOR) {
                    doors.add(new Door(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return doors;
    }

        public static ArrayList<Ninja> GetNinjas() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Ninja> ninjas = new ArrayList<>();

        for (int j = 0; j < img.getHeight() ; j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();
                if (valor == NINJA) {
                    ninjas.add(new Ninja(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return ninjas;
    }



    public static ArrayList<ButtonFloor> GetButtons() {
    BufferedImage img = GetSpriteAtlas(LEVEL_4);
    ArrayList<ButtonFloor> buttons = new ArrayList<>();

    for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();

                if (valor == BUTTON) {
                    buttons.add(new ButtonFloor(
                        i * Juego.TILES_SIZE,
                        j * Juego.TILES_SIZE
                    ));
                }
            }
        }
        return buttons;
    }

    public static ArrayList<Weapon> GetWeapons() {
        BufferedImage img = GetSpriteAtlas(LEVEL_4);
        ArrayList<Weapon> weapons = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getGreen();

                if (valor == WEAPON) {
                    weapons.add(new Weapon(i * Juego.TILES_SIZE, j * Juego.TILES_SIZE));
                }
            }
        }
        return weapons;
    }

    public static int[][] GetLevelData4(){
        
        BufferedImage img=LoadSave.GetSpriteAtlas(LEVEL_4);
        int [][]lvlData=new int[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color=new Color(img.getRGB(i, j));
                lvlData[j][i]=color.getRed();
            }
        }
        return lvlData;

    }

    public static ArrayList<Dog> getDogs()             { return GetDogs(); }
    public static ArrayList<Spider> getSpiders()       { return GetSpiders(); }
    public static ArrayList<Dron> getDrons()           { return GetDrons(); }
    public static ArrayList<Torret> getTorrets()       { return GetTorrets(); }
    public static ArrayList<Ninja> getNinjas()         { return GetNinjas(); }
    public static ArrayList<Boss4> getBoss4()          { return GetBoss4(); }
    public static ArrayList<Door> getDoors()           { return GetDoors(); }
    public static ArrayList<ButtonFloor> getButtons()  { return GetButtons(); }
    public static ArrayList<Weapon> getWeapons()       { return GetWeapons(); }
    public static ArrayList<Trap> getTraps()           { return GetTraps(); }
    public static ArrayList<Trap2> getTraps2()         { return GetTraps2(); }

}
