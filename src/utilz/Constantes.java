package utilz;
import static utilz.Constantes.ConstantesObjetos.*;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.ConstantesCatapulta.*;
import static utilz.Constantes.Objetos.*;
import static utilz.Constantes.ConstantesJugador2.*;
import static utilz.Constantes.ConstantesJugador3.ATACAR1;
import static utilz.Constantes.ConstantesJugador3.CUBRISE;
import static utilz.Constantes.ConstantesJugador.*;
import static utilz.Constantes.ConstantesJugador4.*;

import Juegos.Juego;
import ui.startButton;
public class Constantes {
    public static final float GRAVITY = 0.04f * Juego.SCALE;
    public static final int ANI_SPEED = 25;

    public static class ConstantesObjetos{
        public static final int COIN = 173;
        public static final int HP = 172;

        public static final int SHIELD = 3;
        public static final int ALCOHOL = 1;
        public static final int CLORO = 2;
        public static final int RESORTERA = 4;

        public static final int BARRIL = 160;
        public static final int BARRIL_B = 161;
        public static final int CAJA = 162;
        public static final int CAJA_B = 163;
        public static final int POT = 164;
        public static final int POT_B = 165;
        public static final int LETRERO1 = 166;
        public static final int LETRERO1_B = 167;
        public static final int LETRERO2 = 168;
        public static final int LETRERO2_B = 169;
        public static final int COFRE = 170;
        public static final int COFRE_B = 171;

        public static final int COIN_VALUE = 1;
        public static final int HP_VALUE = 10;

        public static final int HP_SIZE_DEFAULT = 20;
        public static final int HP_SIZE = (int)(HP_SIZE_DEFAULT * Juego.SCALE);

        public static final int COIN_SIZE_DEFAULT = 20;
        public static final int COIN_SIZE = (int)(COIN_SIZE_DEFAULT * Juego.SCALE);

        public static final int CONTENEDOR_SIZE_DEFAULT = 64;
        public static final int CONTENEDOR_SIZE = (int)(CONTENEDOR_SIZE_DEFAULT * Juego.SCALE);

        public static int GetSpriteAmount(int tipo){
            switch (tipo) {
                case HP:
                    return 4;
                case COIN:
                    return 7;
                case RESORTERA: return 3;
                case BARRIL_B:
                case CAJA_B:
                case POT_B: return 7;
                case LETRERO1_B:
                case LETRERO2_B: return 6;
                case COFRE_B: return 5;
                case BARRIL:
                case CAJA:
                case POT:
                case LETRERO1:
                case LETRERO2:
                case COFRE: return 3;   // ajusta al número real de frames de tu sprite sheet
                default: return 1;
            }
        }
        
    }

    public static class Objetos {
        public static final int SPIKE = 6;    // Blue = (0,0,6)  RGB
        public static final int PALMERA = 5;  // Blue = (0,0,5)
        public static final int ARENA = 7;    // Blue = (0,0,7)
        public static final int COCO    = 8;
        public static final int CONTEDORANUBIS = 9;
        public static final int CONTENEDORMUMMY = 10;
    }
    public static class PowerUp {
    public static final int NINGUNO = 0;
    public static final int ANUBIS  = 1;
    public static final int MUMMY   = 2;
}

    public static class ConstantesCatapulta{
        // Dimensiones del spritesheet
        public static final int CAT_FRAME_W = 140;
        public static final int CAT_FRAME_H = 133;

        // Índices de fila (acciones)
        public static final int CAT_IDLE_SIN_PROY = 0;
        public static final int CAT_IDLE_CON_PROY = 1;
        public static final int CAT_LANZAR = 2;

        // Velocidades de animación (ticks entre frames)
        public static final int CAT_ANIM_IDLE       = 20;
        public static final int CAT_ANIM_LANZAR     = 8;

        public static int GetFrameCount(int accion) {
            switch (accion) {
                case CAT_IDLE_SIN_PROY:
                case CAT_IDLE_CON_PROY: return 2;
                case CAT_LANZAR: return 16;
                default: return 1;
            }
        }

        public static int GetAnimSpeed(int accion) {
            switch (accion) {
                case CAT_IDLE_SIN_PROY:
                case CAT_IDLE_CON_PROY: return CAT_ANIM_IDLE;
                case CAT_LANZAR:        return CAT_ANIM_LANZAR;
                default:                return 15;
            }
        }
    }

    public static class ConstantesEnemy {
        public static final int VELO = 0;
        public static final int PLANT = 10;
        public static final int INDIO = 20;
        public static final int BOSS = 30;
        public static final int MOSCO = 40;
        public static final int FLYER = 5;
        //Acciones Velo
        public static final int VELO_IDLE = 0;
        public static final int VELO_RUN = 1;
        public static final int VELO_ATTACK = 2;
        public static final int VELO_HURT = 3;
        public static final int VELO_DIE = 4;
        //Dimensiones y offsets Velociraptor
        public static final int VELO_WIDTH_DEFAULT = 128;
        public static final int VELO_HEIGHT_DEFAULT = 102;
        public static final int VELO_WIDTH = (int)(VELO_WIDTH_DEFAULT * Juego.SCALE);
        public static final int VELO_HEIGHT = (int)(VELO_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del velociraptor
        public static final int VELO_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int VELO_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Planta
        public static final int PLANT_IDLE = 0;
        public static final int PLANT_ATTACK = 1;
        public static final int PLANT_CHOP = 2;
        public static final int PLANT_HURT = 3;
        public static final int PLANT_DIE = 4;
        //Dimensiones y offsets Planta
        public static final int PLANT_WIDTH_DEFAULT = 48;
        public static final int PLANT_HEIGHT_DEFAULT = 48;
        public static final int PLANT_WIDTH = (int)(PLANT_WIDTH_DEFAULT * Juego.SCALE);
        public static final int PLANT_HEIGHT = (int)(PLANT_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox de la planta
        public static final int PLANT_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);//26
        public static final int PLANT_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Indio
        public static final int INDIO_IDLE = 0;
        public static final int INDIO_RUN = 1;
        public static final int INDIO_ATTACK = 2;
        public static final int INDIO_HURT = 3;
        public static final int INDIO_DIE = 4;
        //Dimensiones y offsets Indio
        public static final int INDIO_WIDTH_DEFAULT = 128;
        public static final int INDIO_HEIGHT_DEFAULT = 96;
        public static final int INDIO_WIDTH = (int)(INDIO_WIDTH_DEFAULT * Juego.SCALE);
        public static final int INDIO_HEIGHT = (int)(INDIO_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del indio
        public static final int INDIO_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int INDIO_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Boss
        public static final int BOSS_IDLE = 7;
        public static final int BOSS_RUN = 0;
        public static final int BOSS_ATTACK = 1;
        public static final int BOSS_HURT = 2;
        public static final int BOSS_ATTACK2 = 3;
        public static final int BOSS_DIE = 4;
        public static final int BOSS_JUMP   = 6;
        public static final int BOSS_CHARGE = 5;
        //Dimensiones y offsets Boss
        public static final int BOSS_WIDTH_DEFAULT = 105;
        public static final int BOSS_HEIGHT_DEFAULT = 89;
        public static final int BOSS_WIDTH = (int)(BOSS_WIDTH_DEFAULT * Juego.SCALE);
        public static final int BOSS_HEIGHT = (int)(BOSS_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del boss
        public static final int BOSS_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int BOSS_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones FLYER
        public static final int FLYER_RUN = 0;
        public static final int FLYER_HURT = 1;
        public static final int FLYER_DIE = 2;
        public static final int FLYER_ATTACK = 3;
        
        //Dimensiones y offsets FLYER
        public static final int FLYER_WIDTH_DEFAULT = 128;
        public static final int FLYER_HEIGHT_DEFAULT = 140;
        public static final int FLYER_WIDTH = (int)(FLYER_WIDTH_DEFAULT * Juego.SCALE);
        public static final int FLYER_HEIGHT = (int)(FLYER_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del FLYER
        public static final int FLYER_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int FLYER_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Mosco
        public static final int MOSCO_RUN = 0;
        public static final int MOSCO_HURT = 1;
        public static final int MOSCO_IDLE = 2;
        public static final int MOSCO_HURT2 = 3;
        public static final int MOSCO_DIE = 4;
        public static final int MOSCO_DIE2 = 5;
        public static final int MOSCO_ATTACK = 6;
        public static final int MOSCO_HURT3 = 7;
        public static final int MOSCO_HURT4 = 8;
        public static final int MOSCO_CHOK = 9;
        public static final int MOSCO_KCNOK = 10;
        
        //Dimensiones y offsets Mosco
        public static final int MOSCO_WIDTH_DEFAULT = 58;
        public static final int MOSCO_HEIGHT_DEFAULT = 58;
        public static final int MOSCO_WIDTH = (int)(MOSCO_WIDTH_DEFAULT * Juego.SCALE);
        public static final int MOSCO_HEIGHT = (int)(MOSCO_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del Mosco
        public static final int MOSCO_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int MOSCO_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        // ------- Nivel 2 -------

        //Tipos
        public static final int SERPIENTE = 60;
        public static final int SCORPIO   = 61;
        public static final int MUMMY     = 62;
        public static final int DECEASED  = 63;
        public static final  int EGYPTIANARCHER = 66;
        public static final int MANTICORA = 64;
        public static final int ANUBIS    = 65;
        public static final int EGYPTIANFIGHTER = 67;
        public static final int FARAON = 68;

        //Estados Anubis
        public static final int ANUBIS_IDLE          = 0;
        public static final int ANUBIS_WALK          = 1;
        public static final int ANUBIS_ATTACK_1      = 2;
        public static final int ANUBIS_ATTACK_2      = 3;
        public static final int ANUBIS_ATTACK_RANGED = 4;
        public static final int ANUBIS_HURT          = 5;
        public static final int ANUBIS_DEAD          = 6;
        public static final int ANUBIS_SNEER         = 7;

        //Dimensiones Anubis (hoja de sprite 432x648, frames 72x72)
        public static final int ANUBIS_WIDTH_DEFAULT  = 72;
        public static final int ANUBIS_HEIGHT_DEFAULT = 72;
        public static final int ANUBIS_WIDTH  = (int)(ANUBIS_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int ANUBIS_HEIGHT = (int)(ANUBIS_HEIGHT_DEFAULT * Juego.SCALE);

        //Estados serpiente
        public static final int SERPIENTE_IDLE   = 0;
        public static final int SERPIENTE_RUN    = 1;
        public static final int SERPIENTE_ATTACK = 2;
        public static final int SERPIENTE_HURT   = 3;
        public static final int SERPIENTE_DEAD   = 4;

        //Estados scorpio
        public static final int SCORPIO_IDLE   = 0;
        public static final int SCORPIO_RUN    = 1;
        public static final int SCORPIO_ATTACK = 2;
        public static final int SCORPIO_HURT   = 3;
        public static final int SCORPIO_DEAD   = 4;

        //Estados mummy
        public static final int MUMMY_IDLE   = 0;
        public static final int MUMMY_RUN    = 1;
        public static final int MUMMY_ATTACK = 2;
        public static final int MUMMY_HURT   = 3;
        public static final int MUMMY_DEAD   = 4;

        //Estados deceased
        public static final int DECEASED_IDLE   = 0;
        public static final int DECEASED_RUN    = 1;
        public static final int DECEASED_ATTACK = 2;
        public static final int DECEASED_HURT   = 3;
        public static final int DECEASED_DEAD   = 4;

        //Estados EgyptianArcher
        public static final int EGYPTIANARCHER_IDLE   = 0;
        public static final int EGYPTIANARCHER_RUN    = 1;
        public static final int EGYPTIANARCHER_ATTACK = 2;
        public static final int EGYPTIANARCHER_HURT   = 3;
        public static final int EGYPTIANARCHER_DEAD   = 4;


        // Dimensiones Serpiente
        public static final int SERPIENTE_WIDTH_DEFAULT  = 48;
        public static final int SERPIENTE_HEIGHT_DEFAULT = 48;
        public static final int SERPIENTE_WIDTH  = (int)(SERPIENTE_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int SERPIENTE_HEIGHT = (int)(SERPIENTE_HEIGHT_DEFAULT * Juego.SCALE);

        //Dimensiones Scorpio
        public static final int SCORPIO_WIDTH_DEFAULT  = 48;
        public static final int SCORPIO_HEIGHT_DEFAULT = 48;
        public static final int SCORPIO_WIDTH  = (int)(SCORPIO_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int SCORPIO_HEIGHT = (int)(SCORPIO_HEIGHT_DEFAULT * Juego.SCALE);

        //Dimensiones Mummy
        public static final int MUMMY_WIDTH_DEFAULT  = 48;
        public static final int MUMMY_HEIGHT_DEFAULT = 48;
        public static final int MUMMY_WIDTH  = (int)(MUMMY_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int MUMMY_HEIGHT = (int)(MUMMY_HEIGHT_DEFAULT * Juego.SCALE);

        //Dimensiones Deceased
        public static final int DECEASED_WIDTH_DEFAULT  = 48;
        public static final int DECEASED_HEIGHT_DEFAULT = 48;
        public static final int DECEASED_WIDTH  = (int)(DECEASED_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int DECEASED_HEIGHT = (int)(DECEASED_HEIGHT_DEFAULT * Juego.SCALE);

        //Dimensiones EgyptianArcher
        public static final int EGYPTIANARCHER_WIDTH_DEFAULT  = 48;
        public static final int EGYPTIANARCHER_HEIGHT_DEFAULT = 48;
        public static final int EGYPTIANARCHER_WIDTH  = (int)(EGYPTIANARCHER_WIDTH_DEFAULT  * Juego.SCALE);
        public static final int EGYPTIANARCHER_HEIGHT = (int)(EGYPTIANARCHER_HEIGHT_DEFAULT * Juego.SCALE);

        // ── EgyptianFighter ──────────────────────────────────────────────
    public static final int EGYPTIANFIGHTER_IDLE   = 0;
    public static final int EGYPTIANFIGHTER_RUN    = 1;
    public static final int EGYPTIANFIGHTER_ATTACK = 2;
    public static final int EGYPTIANFIGHTER_HURT   = 3;
    public static final int EGYPTIANFIGHTER_DEAD   = 4;
    public static final int EGYPTIANFIGHTER_WIDTH_DEFAULT  = 48;
    public static final int EGYPTIANFIGHTER_HEIGHT_DEFAULT = 48;
    public static final int EGYPTIANFIGHTER_WIDTH  = (int)(EGYPTIANFIGHTER_WIDTH_DEFAULT  * Juego.SCALE);
    public static final int EGYPTIANFIGHTER_HEIGHT = (int)(EGYPTIANFIGHTER_HEIGHT_DEFAULT * Juego.SCALE);

    // ── Faraon ───────────────────────────────────────────────────────
    public static final int FARAON_IDLE     = 0;
    public static final int FARAON_RUN      = 1;
    public static final int FARAON_ATTACK_1 = 2;
    public static final int FARAON_ATTACK_2 = 3;
    public static final int FARAON_HURT     = 4;
    public static final int FARAON_DEAD     = 5;
    public static final int FARAON_WIDTH_DEFAULT  = 48;
    public static final int FARAON_HEIGHT_DEFAULT = 48;
    public static final int FARAON_WIDTH  = (int)(FARAON_WIDTH_DEFAULT  * Juego.SCALE);
    public static final int FARAON_HEIGHT = (int)(FARAON_HEIGHT_DEFAULT * Juego.SCALE);

    // ── Manticora (Boss) ─────────────────────────────────────────────
    public static final int MANTICORA_IDLE     = 0;
    public static final int MANTICORA_WALK     = 1;
    public static final int MANTICORA_SNEER    = 2;
    public static final int MANTICORA_ATTACK_1 = 3;
    public static final int MANTICORA_ATTACK_2 = 4;
    public static final int MANTICORA_ATTACK_3 = 5;
    public static final int MANTICORA_ATTACK_4 = 6;
    public static final int MANTICORA_HURT     = 7;
    public static final int MANTICORA_DEATH    = 8;
    public static final int MANTICORA_WIDTH_DEFAULT  = 72;
    public static final int MANTICORA_HEIGHT_DEFAULT = 72;
    public static final int MANTICORA_WIDTH  = (int)(MANTICORA_WIDTH_DEFAULT  * Juego.SCALE);
    public static final int MANTICORA_HEIGHT = (int)(MANTICORA_HEIGHT_DEFAULT * Juego.SCALE);


        // Nivel 3
        public static final int ARMADURA = 6;
            public static final int RAT = 1;
            public static final int ATCHER = 2;
            public static final int HYENA = 3;
            public static final int BAT = 4;
            public static final int MAGO = 7;
            //Acciones Velo
            public static final int ARMAD_IDLE = 0;
            public static final int ARMAD_RUN = 1;
            public static final int ARMAD_ATTACK = 4;
            public static final int ARMAD_HURT = 3;
            public static final int ARMAD_DIE = 5;
            //Dimensiones y offsets Velociraptor
            public static final int ARMAD_WIDTH_DEFAULT = 128;
            public static final int ARMAD_HEIGHT_DEFAULT = 128;
            public static final int ARMAD_WIDTH = (int)(ARMAD_WIDTH_DEFAULT * Juego.SCALE);
            public static final int ARMAD_HEIGHT = (int)(ARMAD_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox del velociraptor
            public static final int ARMAD_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
            public static final int ARMAD_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

            //Acciones Planta
            public static final int RAT_IDLE = 0;
            public static final int RAT_RUN = 1;
            public static final int RAT_ATTACK = 2;
            public static final int RAT_HURT = 3;
            public static final int RAT_DIE = 4;
            //Dimensiones y offsets Planta
            public static final int RAT_WIDTH_DEFAULT = 62;
            public static final int RAT_HEIGHT_DEFAULT = 44;
            public static final int RAT_WIDTH = (int)(RAT_WIDTH_DEFAULT * Juego.SCALE);
            public static final int RAT_HEIGHT = (int)(RAT_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox de la planta
            public static final int RAT_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);//26
            public static final int RAT_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

            //Acciones Indio
            public static final int ATCHER_IDLE = 0;
            public static final int ATCHER_RUN = 1;
            public static final int ATCHER_ATTACK = 4;
            public static final int ATCHER_HURT = 2;
            public static final int ATCHER_DIE = 5;
            //Dimensiones y offsets Indio
            public static final int ATCHER_WIDTH_DEFAULT = 128;
            public static final int ATCHER_HEIGHT_DEFAULT = 128;
            public static final int ATCHER_WIDTH = (int)(ATCHER_WIDTH_DEFAULT * Juego.SCALE);
            public static final int ATCHER_HEIGHT = (int)(ATCHER_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox del indio
            public static final int ATCHER_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
            public static final int ATCHER_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

            //Acciones Boss
            public static final int MAGO_IDLE = 0;
            public static final int MAGO_RUN = 1;
            public static final int MAGO_ATTACK = 2;
            public static final int MAGO_HURT = 3;
            public static final int MAGO_DIE = 4;
            //Dimensiones y offsets Boss
            public static final int MAGO_WIDTH_DEFAULT = 128;
            public static final int MAGO_HEIGHT_DEFAULT = 105;
            public static final int MAGO_WIDTH = (int)(MAGO_WIDTH_DEFAULT * Juego.SCALE);
            public static final int MAGO_HEIGHT = (int)(MAGO_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox del boss
            public static final int MAGO_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
            public static final int MAGO_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

            //Acciones FLYER
            public static final int BAT_IDLE = 0;
            public static final int BAT_RUN = 1;
            public static final int BAT_ATTACK = 2;
            public static final int BAT_HURT = 3;
            public static final int BAT_DIE = 4;
            //Dimensiones y offsets FLYER
            public static final int BAT_WIDTH_DEFAULT = 32;
            public static final int BAT_HEIGHT_DEFAULT = 32;
            public static final int BAT_WIDTH = (int)(BAT_WIDTH_DEFAULT * Juego.SCALE);
            public static final int BAT_HEIGHT = (int)(BAT_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox del FLYER
            public static final int BAT_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
            public static final int BAT_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

            //Acciones Mosco
            public static final int HYENA_IDLE = 0;
            public static final int HYENA_RUN = 1;
            public static final int HYENA_ATTACK = 2;
            public static final int HYENA_HURT = 3;
            public static final int HYENA_DIE = 4;
            //Dimensiones y offsets Mosco
            public static final int HYENA_WIDTH_DEFAULT = 48;
            public static final int HYENA_HEIGHT_DEFAULT = 48;
            public static final int HYENA_WIDTH = (int)(HYENA_WIDTH_DEFAULT * Juego.SCALE);
            public static final int HYENA_HEIGHT = (int)(HYENA_HEIGHT_DEFAULT * Juego.SCALE);
            //Offsets para hitbox del Mosco
            public static final int HYENA_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
            public static final int HYENA_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //----Nivel 4
        public static final int DOG = 70;
        public static final int SPIDER = 85;
        public static final int BOSS4 = 75;
        public static final int DRON = 120;
        public static final int TRAP = 150;
        public static final int TRAP2 = 155;
        public static final int DOOR = 215;
        public static final int BUTTON = 205; 
        public static final int NINJA = 195;
        public static final int WEAPON = 180; 
        public static final int TORRET = 80; 

        //Acciones Dog
        public static final int DOG_IDLE = 0;
        public static final int DOG_ATTACK = 1;
        public static final int DOG_JUMP = 2;
        public static final int DOG_HURT = 3;
        public static final int DOG_RUN = 4;
        public static final int DOG_DIE = 5;
        //Dimensiones y offsets Dogs
        public static final int DOG_WIDTH_DEFAULT = 142;
        public static final int DOG_HEIGHT_DEFAULT = 100;
        public static final int DOG_WIDTH = (int)(DOG_WIDTH_DEFAULT * Juego.SCALE);
        public static final int DOG_HEIGHT = (int)(DOG_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del Dogs
        public static final int DOG_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int DOG_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Torreta (orden por columnas del sprite sheet)
        public static final int TORRET_IDLE = 0;
        public static final int TORRET_HURT = 1;
        public static final int TORRET_ATTACK = 2;
        public static final int TORRET_DIE = 3;
        //Dimensiones Torreta
        public static final int TORRET_WIDTH_DEFAULT = 192;
        public static final int TORRET_HEIGHT_DEFAULT = 96;
        public static final int TORRET_WIDTH = (int)(TORRET_WIDTH_DEFAULT * Juego.SCALE);
        public static final int TORRET_HEIGHT = (int)(TORRET_HEIGHT_DEFAULT * Juego.SCALE);

        // En ConstantesEnemy, junto a las otras constantes de Torret:
        public static final int TORRET_BULLET_WIDTH = 32;
        public static final int TORRET_BULLET_HEIGHT = 32;
        //Offsets hitbox
        public static final int TORRET_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int TORRET_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Indio
        public static final int SPIDER_ATTACK = 0;
        public static final int SPIDER_JUMP = 1;
        public static final int SPIDER_RUN = 2;    
        public static final int SPIDER_HURT = 3;
        public static final int SPIDER_IDLE = 4;
        public static final int SPIDER_DIE = 5;
        //Dimensiones y offsets Indio
        public static final int SPIDER_WIDTH_DEFAULT = 100;
        public static final int SPIDER_HEIGHT_DEFAULT = 100;
        public static final int SPIDER_WIDTH = (int)(SPIDER_WIDTH_DEFAULT * Juego.SCALE);
        public static final int SPIDER_HEIGHT = (int)(SPIDER_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del indio
        public static final int SPIDER_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int SPIDER_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones Boss
        public static final int BOSS4_IDLE = 0;
        public static final int BOSS4_ATTACK = 1;
        public static final int BOSS4_RUN = 2;  
        public static final int BOSS4_HURT = 3;
        public static final int BOSS4_DIE = 4;
        //Dimensiones y offsets Boss
        public static final int BOSS4_WIDTH_DEFAULT = 128;
        public static final int BOSS4_HEIGHT_DEFAULT = 128;
        public static final int BOSS4_WIDTH = (int)(BOSS4_WIDTH_DEFAULT * Juego.SCALE);
        public static final int BOSS4_HEIGHT = (int)(BOSS4_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del boss
        public static final int BOSS4_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int BOSS4_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Acciones FLYER
        public static final int DRON_ATTACK = 0;
        public static final int DRON_DIE = 1; 
        public static final int DRON_RUN = 2;
        public static final int DRON_IDLE = 3;
        public static final int DRON_HURT = 4;             
        
        //Dimensiones y offsets FLYER
        public static final int DRON_WIDTH_DEFAULT = 96;
        public static final int DRON_HEIGHT_DEFAULT = 98;
        public static final int DRON_WIDTH = (int)(DRON_WIDTH_DEFAULT * Juego.SCALE);
        public static final int DRON_HEIGHT = (int)(DRON_HEIGHT_DEFAULT * Juego.SCALE);
        //Offsets para hitbox del FLYER
        public static final int DRON_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int DRON_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);


        //Acciones Trap
        public static final int TRAP_IDLE = 0;

        //Acciones Ninja
        //Acciones Ninja
        public static final int NINJA_IDLE = 0;
        public static final int NINJA_ATTACK = 1;
        public static final int NINJA_HURT = 2;
        public static final int NINJA_DIE = 3;
        public static final int NINJA_RUN = 4;
        //Dimensiones y offsets Ninja
        public static final int NINJA_WIDTH_DEFAULT = 140;
        public static final int NINJA_HEIGHT_DEFAULT = 100;

        public static final int NINJA_WIDTH = (int)(NINJA_WIDTH_DEFAULT * Juego.SCALE);
        public static final int NINJA_HEIGHT = (int)(NINJA_HEIGHT_DEFAULT * Juego.SCALE);

        //Offsets (ajústalos si ves que no encaja bien)
        public static final int NINJA_DRAWOFFSET_X = (int)(-26 * Juego.SCALE);
        public static final int NINJA_DRAWOFFSET_Y = (int)(-26 * Juego.SCALE);

        //Dimensiones
        public static final int TRAP_WIDTH_DEFAULT = 32;
        public static final int TRAP_HEIGHT_DEFAULT = 32;

        public static final int TRAP_WIDTH = (int)(TRAP_WIDTH_DEFAULT * Juego.SCALE);
        public static final int TRAP_HEIGHT = (int)(TRAP_HEIGHT_DEFAULT * Juego.SCALE);
        
        public static int GetSpriteAmount(int enemy_type, int enemy_state){
            switch (enemy_type) {
                case VELO:
                    switch (enemy_state) {
                        case VELO_IDLE: return 6;
                        case VELO_RUN: return 9;
                        case VELO_ATTACK: return 5;
                        case VELO_HURT: return 3;
                        case VELO_DIE: return 7;
                        default: return 1;
                    }
                case PLANT:
                    switch (enemy_state) {
                        case PLANT_IDLE: 
                        case PLANT_HURT: return 4;
                        case PLANT_CHOP: return 16;
                        case PLANT_ATTACK: return 6;
                        case PLANT_DIE: return 5;
        
                        default: return 1;
                    }
                case INDIO:
                    switch (enemy_state) {
                        case INDIO_IDLE:
                        case INDIO_ATTACK:    
                        return 4;
                        case INDIO_RUN: return 6;
                        case INDIO_HURT:
                        case INDIO_DIE: 
                        default: return 1;
                    }
                case FLYER:
                    switch (enemy_state) {
                        case FLYER_RUN:
                        case FLYER_ATTACK:    
                        return 4;
                        case FLYER_HURT: return 3;
                        case FLYER_DIE: return 2;
                        default: return 1;
                    }
                case MOSCO:
                    switch (enemy_state) {
                        case MOSCO_ATTACK:
                        case MOSCO_HURT:
                        case MOSCO_DIE2:    
                        return 13;
                        case MOSCO_IDLE:
                        case MOSCO_HURT3:
                        case MOSCO_HURT4: 
                        return 4;
                        case MOSCO_HURT2:
                        case MOSCO_RUN:
                        case MOSCO_KCNOK:
                        return 6; 
                        case MOSCO_CHOK: return 12;
                        case MOSCO_DIE: return 14;
                        default: return 1;
                    }
                case BOSS:
                    switch (enemy_state) {
                        case BOSS_ATTACK:    
                        case BOSS_RUN: 
                        case BOSS_HURT: return 12;
                        case BOSS_DIE: 
                        case BOSS_ATTACK2: return 11;
                        case BOSS_CHARGE: return 8;
                        case BOSS_JUMP: return 4;
                        case BOSS_IDLE:
                        default: return 1;
                    }
                case SERPIENTE:
                    switch (enemy_state) {
                        case SERPIENTE_IDLE:   return 4;
                        case SERPIENTE_RUN:    return 4;
                        case SERPIENTE_ATTACK: return 6;
                        case SERPIENTE_HURT:   return 2;
                        case SERPIENTE_DEAD:   return 6;
                        default: return 1;
                    }
                case SCORPIO:
                    switch (enemy_state) {
                        case SCORPIO_IDLE:
                        case SCORPIO_RUN:    return 4;
                        case SCORPIO_ATTACK: return 6;
                        case SCORPIO_HURT:   return 6;
                        case SCORPIO_DEAD:   return 5;
                        default: return 1;
                    }
                case MUMMY:
                    switch (enemy_state) {
                        case MUMMY_IDLE:   return 4;
                        case MUMMY_RUN:    return 4;
                        case MUMMY_ATTACK: return 6;
                        case MUMMY_HURT:   return 2;
                        case MUMMY_DEAD:   return 6;
                        default: return 1;
                    }
                case DECEASED:
                    switch (enemy_state) {
                        case DECEASED_IDLE:   return 4;
                        case DECEASED_RUN:    return 4;
                        case DECEASED_ATTACK: return 6;
                        case DECEASED_HURT:   return 2;
                        case DECEASED_DEAD:   return 6;
                        default: return 1;
                    }
                case EGYPTIANARCHER:
                    switch (enemy_state) {
                        case EGYPTIANARCHER_IDLE:   return 4;
                        case EGYPTIANARCHER_RUN:    return 4; 
                        case EGYPTIANARCHER_ATTACK: return 6;
                        case EGYPTIANARCHER_HURT:   return 2;
                        case EGYPTIANARCHER_DEAD:   return 6;
                        default: return 1;
                    }
                case ANUBIS:
                    switch (enemy_state) {
                        case ANUBIS_ATTACK_RANGED: return 4;
                        default:                   return 6;
                    }
                case ARMADURA:
                    switch (enemy_state) {
                        case ARMAD_IDLE: return 9;
                        case ARMAD_RUN: return 8;
                        case ARMAD_ATTACK: return 5;
                        case ARMAD_HURT:
                        case ARMAD_DIE: return 7;
                        default: return 1;
                    }
                case HYENA:
                    switch (enemy_state) {
                        case HYENA_IDLE: return 4;
                        case HYENA_HURT: return 2;
                        case HYENA_RUN:
                        case HYENA_DIE:
                        case HYENA_ATTACK: return 6;
        
                        default: return 1;
                    }
                case ATCHER:
                    switch (enemy_state) {
                        case ATCHER_IDLE: return 2;
                        case ATCHER_ATTACK: return 10;
                        case ATCHER_RUN: return 8;
                        case ATCHER_HURT:return 3;
                        case ATCHER_DIE: return 7;
                        default: return 1;
                    }
                case MAGO:
                    switch (enemy_state) {
                        case MAGO_IDLE:return 6;
                        case MAGO_ATTACK:    
                        case MAGO_RUN: return 8;
                        case MAGO_HURT: return 4;
                        case MAGO_DIE: return 7;
                        default: return 1;
                    }
                case BAT:
                    switch (enemy_state) {
                        case BAT_IDLE:
                        case BAT_RUN: return 4;
                        case BAT_HURT: return 3;
                        case BAT_ATTACK:
                        case BAT_DIE: return 10;                    
                        default: return 1;
                    }
                case RAT:
                    switch (enemy_state) {
                        case RAT_IDLE:
                        case RAT_DIE: return 8;                    
                        case RAT_RUN:
                        case RAT_ATTACK: return 7;
                        case RAT_HURT: return 3;
                        default: return 1;
                    }
                case BOSS4:
                switch (enemy_state) {
                    case BOSS4_IDLE:   return 6;  
                    case BOSS4_ATTACK: return 14;
                    case BOSS4_RUN:    return 12;                   
                    case BOSS4_HURT:   return 3;
                    case BOSS4_DIE:    return 4;
                    default: return 1;
                }

                case DOG:
                    switch (enemy_state) {
                        case DOG_IDLE: return 5;
                        case DOG_RUN: return 12;
                        case DOG_ATTACK: return 9;
                        case DOG_HURT: return 4;
                        case DOG_JUMP: return 9;
                        case DOG_DIE: return 7;
                        default: return 1;
                    }
                case TORRET:
                    switch (enemy_state) {
                        case TORRET_IDLE: return 4;
                        case TORRET_HURT: return 4;
                        case TORRET_ATTACK: return 6;
                        case TORRET_DIE: return 5;

                        default: return 1;
                    }
                case SPIDER:
                    switch (enemy_state) {
                        case SPIDER_JUMP: return 9;
                        case SPIDER_ATTACK: return 11;
                        case SPIDER_RUN: return 8;
                        case SPIDER_HURT: 
                        case SPIDER_IDLE: 
                        return 4;
                        case SPIDER_DIE: return 8;
                        
                        default: return 1;
                    }
                case DRON:
                    switch (enemy_state) {
                        case DRON_ATTACK: return 8;
                        case DRON_DIE: return 10;
                        case DRON_RUN: return 4;
                        case DRON_IDLE: return 5;
                        case DRON_HURT: return 12;                       
                        default: return 1;
                    }
                case NINJA:
                switch (enemy_state) {
                    case NINJA_IDLE: return 14;
                    case NINJA_ATTACK: return 5;
                    case NINJA_HURT: return 5;
                    case NINJA_DIE: return 10;
                    case NINJA_RUN: return 8;
                    default: return 1;
                }

                case TRAP:
                    switch (enemy_state) {
                        case 0: return 1; // o más si animas
                        default: return 1;
                }

                case TRAP2:
                    switch (enemy_state) {
                        case TRAP_IDLE: return 10; // porque tienes 10 frames
                        default: return 1;
                }

                case EGYPTIANFIGHTER:
                    switch (enemy_state) {
                        case EGYPTIANFIGHTER_IDLE:   return 5;
                        case EGYPTIANFIGHTER_RUN:    return 5;
                        case EGYPTIANFIGHTER_ATTACK: return 6;
                        case EGYPTIANFIGHTER_HURT:   return 2;
                        case EGYPTIANFIGHTER_DEAD:   return 5;
                        default: return 1;
                    }

                case FARAON:
                    switch (enemy_state) {
                        case FARAON_IDLE:     return 4;
                        case FARAON_RUN:      return 4;
                        case FARAON_ATTACK_1: return 5;
                        case FARAON_ATTACK_2: return 5;
                        case FARAON_HURT:     return 2;
                        case FARAON_DEAD:     return 6;
                        default: return 1;
                    }

                case MANTICORA:
                    switch (enemy_state) {
                        case MANTICORA_IDLE:     return 4;
                        case MANTICORA_WALK:     return 6;
                        case MANTICORA_SNEER:    return 4;
                        case MANTICORA_ATTACK_1: return 4;
                        case MANTICORA_ATTACK_2: return 4;
                        case MANTICORA_ATTACK_3: return 5;
                        case MANTICORA_ATTACK_4: return 4;
                        case MANTICORA_HURT:     return 2;
                        case MANTICORA_DEATH:    return 3;
                        default: return 1;
                    }

            }
            return 1;
        }


    }

    public static class UI {
        public static class Buttons{
            public static final int B_WIDTH_DEFAULT = 375;
            public static final int B_HEIGHT_DEFAULT = 120;
            public static final int B_WIDTH = (int)(B_WIDTH_DEFAULT * Juego.SCALE);
            public static final int B_HEIGHT = (int)(B_HEIGHT_DEFAULT * Juego.SCALE);
        }
        
    }

    public static int GetMaxHealt(int enemy_type){ 
        switch (enemy_type) {
            case VELO: return 10;
            case INDIO: return 10;
            case PLANT: return 10;
            case MOSCO: return 10;
            case FLYER: return 10;
            case BOSS: return 50;//
            case MAGO: return 600;
        
            default:
                return 1;
        }
    }

    public static int GetEnemyDamage(int enemy_type){
        switch (enemy_type) {
            case VELO: return 2;
            case INDIO: return 2;
            case PLANT: return 2;
            case MOSCO: return 2;
            case FLYER: return 2;
            case BOSS: return 2;//

            case SERPIENTE: return 2;
            case SCORPIO:   return 3;
            case MUMMY:     return 1;
            case DECEASED:  return 2;
            case EGYPTIANARCHER: return 1;//4
            case ANUBIS:    return 1;//4
            case EGYPTIANFIGHTER: return 1;

            case ARMADURA: return 10;
            case RAT: return 10;    
            case HYENA: return 10;
            case BAT: return 10;
            case MAGO: return 3;

            case DOG: return 15;
            case SPIDER: return 20;
            case TORRET: return 25;
            case DRON: return 20;
            case BOSS4: return 35;//
            case NINJA: return 30;

            default:
                return 0;
        }
    }

    // public static class Enviroment {
    //     public static final int BIG_CLOUDS_WIDTH_DEFAULT=448;
    //     public static final int BIG_CLOUDS_HEIGHT_DEFAULT=101;
    //     public static final int BIG_CLOUDS_WIDTH = (int)(BIG_CLOUDS_WIDTH_DEFAULT*Juego.SCALE);
    //     public static final int BIG_CLOUDS_HEIGHT = (int)(BIG_CLOUDS_HEIGHT_DEFAULT*Juego.SCALE);
        
    //     public static final int SMALL_CLOUDS_WIDTH_DEFAULT=74;
    //     public static final int SMALL_CLOUDS_HEIGHT_DEFAULT=24;
    //     public static final int SMALL_CLOUDS_WIDTH = (int)(SMALL_CLOUDS_WIDTH_DEFAULT*Juego.SCALE);
    //     public static final int SMALL_CLOUDS_HEIGHT = (int)(SMALL_CLOUDS_HEIGHT_DEFAULT*Juego.SCALE);
    // }
    public static class Direccion{
        public static final int LEFT=0;
        public static final int UP=1;
        public static final int RIGHT=2;
        public static final int DOWN=3;
    }
    public static class ConstantesJugador{
        public static final int INACTIVO=0;
        public static final int CORRER=1;
        public static final int SALTAR=2;
        public static final int CAYENDO=3;
        public static final int ATACAR_BRINCAR=4;
        public static final int ATACAR=5;
        public static final int HURT=6;
        public static final int MORIR=7;
        public static final int ESCALAR=8;
        public static final int CAMINAR=9;
        public static final int AGACHAR=10;
        public static final int CAMINAR_RESORTERA  = 11;
        public static final int CAMINAR_AGACHADO   = 12;
        public static final int INACTIVO_RESORTERA = 13;
        public static final int SALTAR_RESORTERA   = 14;
        public static final int ATACAR_RESORTERA   = 15;
        public static final int CAYENDO_RESORTERA  = 16;

    }

    public static class ConstantesJugador2{
        public static final int INACTIVO2=0;
        public static final int CORRER2=1;
        public static final int SALTAR2=2;
        public static final int CAYENDO2=3;
        public static final int ATACAR_RUN2=4;
        public static final int ATACAR2=5;
        public static final int ATACAR_BRINCAR2=6;
        public static final int HURT2=7;
        public static final int DEAD2=8;

    }

    public static class ConstantesJugador3{
        public static final int INACTIVO=0;
        public static final int CORRER=1;
        public static final int SALTAR=2;
        public static final int CAYENDO=3;
        public static final int ATACAR1=4;
        public static final int AGACHAR=5;
        public static final int HURT=7;
        public static final int MORIR=8;
        public static final int CUBRISE=9;
        
    }

    public static class ConstantesJugador4{
        public static final int INACTIVO4 = 0;
        public static final int ATACAR4 = 1;
        public static final int CAMINAR4 = 2;
        public static final int CORRER4 = 3;
        public static final int HURT4 = 4;
        public static final int MORIR4 = 5;
        public static final int AGACHAR4 = 6;
        public static final int CAYENDO4 = 7;
        public static final int ATACAR_BRINCAR4 = 8;
        public static final int SALTAR4 = 9;
        public static final int CORRER_ARMA4 = 10;
        public static final int ATACAR_ARMA4 = 11;
    }

    public static int GetNoSprite(int playerAction){
        switch (playerAction) {
            case INACTIVO:
            case SALTAR: 
            case ATACAR_BRINCAR:
            case ATACAR:
            case MORIR:
            case ESCALAR:
            case CAMINAR: return 10;

            case CORRER:
            case CAYENDO:
            case HURT: return 8;
            case AGACHAR: return 3;

            case CAMINAR_AGACHADO: 
            case INACTIVO_RESORTERA: return 6;
            case SALTAR_RESORTERA:
            case ATACAR_RESORTERA: return 5;
            case CAYENDO_RESORTERA:
            case CAMINAR_RESORTERA: return 4;
            default:return 1;
        }
    }
    public static int GetNoSprite3(int playerAction){
        switch (playerAction) {
            case INACTIVO:
            case MORIR: return 7;
            case AGACHAR:
            case SALTAR: return 2;
            case ATACAR1:
            case CUBRISE: return 6;
            case CORRER: return 8;
            case CAYENDO: return 3;
            case HURT: return 4;
            default:return 1;
        }
    }

    public static int GetNoSprite4(int player_action) {
            switch (player_action) {
                case INACTIVO4:      return 6;
                case ATACAR4:        return 5;
                case CAMINAR4:       return 10;
                case CORRER4:        return 10;
                case HURT4:          return 3;
                case MORIR4:         return 4;
                case AGACHAR4:       return 5;
                case CAYENDO4:       return 4;
                case ATACAR_BRINCAR4: return 4;
                case SALTAR4:        return 5;
                case CORRER_ARMA4:   return 5; 
                case ATACAR_ARMA4:   return 5;
                default: return 1;
            }
        }


}
