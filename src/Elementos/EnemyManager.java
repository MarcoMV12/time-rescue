package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import GameStates.Playing;
import Juegos.Juego;
import minijuego.JuegoPanel;
import utilz.LoadSave;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.RIGHT;
import static utilz.MetodosAyuda.CanMoveHere;

public class EnemyManager {
    
    private Playing playing;
    private BufferedImage[][] veloArr;
    private ArrayList<Velociraptor> raptors = new ArrayList<>();

    private BufferedImage[][] indioArr;
    private ArrayList<Indio> indios = new ArrayList<>();

    private BufferedImage[][] plantaArr;
    private ArrayList<Planta> plantas = new ArrayList<>();

    private BufferedImage[][] moscoArr;
    private ArrayList<Mosco> moscos = new ArrayList<>();

    private BufferedImage[][] flyerArr;
    private ArrayList<Flyer> flyers = new ArrayList<>();

    private BufferedImage[][] bossArr;
    private ArrayList<Boss> boss = new ArrayList<>();

    //Nivel 2
    private BufferedImage[][] snakeArr;
    private ArrayList<Snake> snake = new ArrayList<>();

    private BufferedImage[][] scorpioArr;
    private ArrayList<Scorpio> scorpio = new ArrayList<>();

    private BufferedImage[][] mummyArr;
    private ArrayList<Mummy> mummy = new ArrayList<>();

    private BufferedImage[][] deceasedArr;
    private BufferedImage ballImg;
    private ArrayList<Deceased> deceased = new ArrayList<>();

    private BufferedImage[][] egyptianArcherArr;
    private BufferedImage arrowImg;
    private ArrayList<EgyptianArcher> egyptianArcher = new ArrayList<>();

    private BufferedImage[][] anubisArr;
    private BufferedImage anubisBallImg;
    private ArrayList<Anubis> anubis = new ArrayList<>();

    private BufferedImage[][] egyptianFighterArr;
    private ArrayList<EgyptianFighter> egyptianFighter = new ArrayList<>();

    private BufferedImage[][] faraonArr;
    private BufferedImage faraonBallImg;
    private ArrayList<Faraon> faraon = new ArrayList<>();

    private BufferedImage[][] manticoraArr;
    private BufferedImage manticoraBallImg;
    private ArrayList<Manticora> manticora = new ArrayList<>();

    //Nivel 3
    private BufferedImage[][] cabArr;
    private ArrayList<Armado> armados = new ArrayList<>();

    private BufferedImage[][] atcherArr;
    private ArrayList<Atcher> atchers = new ArrayList<>();

    private BufferedImage[][] ratArr;
    private ArrayList<Rata> rats = new ArrayList<>();

    private BufferedImage[][] hyenaArr;
    private ArrayList<Hyena> hyenas = new ArrayList<>();

    private BufferedImage[][] batArr;
    private ArrayList<Bat> bats = new ArrayList<>();

    private BufferedImage[][] magoArr;
    private ArrayList<Mago> magos = new ArrayList<>();

    //Nivel 4
    private int miniGameCooldown = 0;
    private static final int MINIGAME_COOLDOWN = 120;

    private BufferedImage[] torretBulletArr;
    private BufferedImage weaponImg;

    private ArrayList<Weapon> weapons = new ArrayList<>();

    private BufferedImage[] bulletArr;

    private ArrayList<ButtonFloor> buttons = new ArrayList<>();
    private BufferedImage[][] buttonArr;

    private ArrayList<Door> doors = new ArrayList<>();
    private BufferedImage[][] doorArr;

    private ArrayList<Trap2> traps2 = new ArrayList<>();
    private BufferedImage[][] trap2Arr;
    
    private ArrayList<Trap> traps = new ArrayList<>();
    private BufferedImage[][] trapArr;
    
    //private Playing playing;
    private BufferedImage[][] dogArr;
    private ArrayList<Dog> dogs = new ArrayList<>();

    private BufferedImage[][] spiderArr;
    private ArrayList<Spider> spiders = new ArrayList<>();

    private BufferedImage[][] torretArr;
    private ArrayList<Torret> torrets = new ArrayList<>();

    private BufferedImage[][] dronArr;
    private ArrayList<Dron> drons = new ArrayList<>();

    private BufferedImage[][] boss4Arr;
    private ArrayList<Boss4> boss4 = new ArrayList<>();

    private BufferedImage[][] ninjaArr;
    private ArrayList<Ninja> ninjas = new ArrayList<>();

    private boolean miniGameActivo = false;
    private boolean miniGameCompletado = false;

    private void abrirMiniJuego() {

        playing.getPlayer4().resetDirBoolean();
        JFrame frame = new JFrame("Mini Juego");
        
        JuegoPanel panel = new JuegoPanel();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (panel.gano()) {
                    miniGameCompletado = true;
                    for (Door d : doors) {
                        d.openDoor();
                    }
                } else {
                    miniGameCooldown = MINIGAME_COOLDOWN;
                }
                miniGameActivo = false;
            }
        });
    }


    public EnemyManager(Playing playing) {
        this.playing = playing; 
        loadEnemies();
        //addEnemies();
    }

    public void cargarEnemigosDelNivel() {
        Niveles.Level lvl = playing.getLevelManager().currentLevel();
        int idx = playing.getLevelManager().getNivelActual();

        // Limpia TODAS las listas; solo se rellenan las del nivel actual.
        raptors = new ArrayList<>();
        indios  = new ArrayList<>();
        plantas = new ArrayList<>();
        moscos  = new ArrayList<>();
        flyers  = new ArrayList<>();
        boss    = new ArrayList<>();

        snake          = new ArrayList<>();
        scorpio        = new ArrayList<>();
        mummy          = new ArrayList<>();
        deceased       = new ArrayList<>();
        egyptianArcher = new ArrayList<>();
        anubis         = new ArrayList<>();
        egyptianFighter = new ArrayList<>();
        faraon          = new ArrayList<>();
        manticora       = new ArrayList<>();

        armados = new ArrayList<>();
        atchers = new ArrayList<>();
        rats    = new ArrayList<>();
        hyenas  = new ArrayList<>();
        bats    = new ArrayList<>();
        magos   = new ArrayList<>();

        dogs    = new ArrayList<>();
        spiders = new ArrayList<>();
        drons   = new ArrayList<>();
        torrets = new ArrayList<>();
        ninjas  = new ArrayList<>();
        boss4   = new ArrayList<>();
        doors   = new ArrayList<>();
        buttons = new ArrayList<>();
        weapons = new ArrayList<>();
        traps   = new ArrayList<>();
        traps2  = new ArrayList<>();

        switch (idx) {
            case 0 -> { // Nivel 1
                raptors = lvl.getVelos();
                indios  = lvl.getIndios();
                plantas = lvl.getPlantas();
                moscos  = lvl.getMoscos();
                flyers  = lvl.getFlyers();
                boss    = lvl.getBoss();
            }
            case 1 -> { // Nivel 2
                snake           = LoadSave.GetSerpientes();
                scorpio         = LoadSave.GetScorpio();
                mummy           = LoadSave.GetMummies();
                deceased        = LoadSave.GetDeceased();
                egyptianArcher  = LoadSave.GetEgyptianArcher();
                anubis          = LoadSave.GetAnubis();
                egyptianFighter = LoadSave.GetEgyptianFighters();
                faraon          = LoadSave.GetFaraones();
                manticora       = LoadSave.GetManticoras();
            }
            case 2 -> { // Nivel 3
                armados = LoadSave.GetArmados();
                atchers = LoadSave.GetAtchers();
                rats    = LoadSave.GetRatas();
                hyenas  = LoadSave.GetHyenas();
                bats    = LoadSave.GetBats();
                magos   = LoadSave.GetMagos();
            }
            default -> { 
                dogs    = LoadSave.getDogs();
                spiders = LoadSave.getSpiders();
                drons   = LoadSave.getDrons();
                torrets = LoadSave.getTorrets();
                ninjas  = LoadSave.getNinjas();
                boss4   = LoadSave.getBoss4();
                doors   = LoadSave.getDoors();
                buttons = LoadSave.getButtons();
                weapons = LoadSave.getWeapons();
                traps   = LoadSave.getTraps();
                traps2  = LoadSave.getTraps2();
             }
        }
    }

    public void update(int [][] lvlData, Jugador jugador){
        for (Velociraptor raptor : raptors) {
            if(raptor.isAlive())
            raptor.update(lvlData, jugador);
        }

        for (Indio i : indios) {
            if(i.isAlive())
            i.update(lvlData, jugador);
        }

        for (Planta p : plantas) {
            if(p.isAlive())
            p.update(lvlData, jugador);
        }

        for (Mosco m : moscos) {
            if(m.isAlive())
            m.update(lvlData, jugador);
        }

        for (Flyer f : flyers) {
            if(f.isAlive())
            f.update(lvlData, jugador);
        }

        for (Boss b : boss) {
            if(b.isAlive())
            b.update(lvlData, jugador);
        }

        // Nivel 2
        for (Snake s : snake)
            if (s.isAlive())
                s.update(lvlData, jugador);

        for (Scorpio sc : scorpio)
            if (sc.isAlive())
                sc.update(lvlData, jugador);

        for (Mummy m : mummy)
            if (m.isAlive())
                m.update(lvlData, jugador);

        for (Deceased d : deceased)
            if (d.isAlive())
                d.update(lvlData, jugador);

        for (EgyptianArcher archer : egyptianArcher)
            if (archer.isAlive())
                archer.update(lvlData, jugador);

        for (Anubis a : anubis)
            if (a.isAlive())
                a.update(lvlData, jugador);
    }
    public void update2(int[][] lvlData, Jugador2 jugador) {
        for (Snake s : snake)
            if (s.isAlive()) s.update(lvlData, jugador);
        for (Scorpio sc : scorpio)
            if (sc.isAlive()) sc.update(lvlData, jugador);
        for (Mummy m : mummy)
            if (m.isAlive()) m.update(lvlData, jugador);
        for (Deceased d : deceased)
            if (d.isAlive()) d.update(lvlData, jugador);
        for (EgyptianArcher archer : egyptianArcher)
            if (archer.isAlive()) archer.update(lvlData, jugador);
        for (Anubis a : anubis)
            if (a.isAlive()) a.update(lvlData, jugador);
        for (EgyptianFighter ef : egyptianFighter)
            if (ef.isAlive()) ef.update(lvlData, jugador);
        for (Faraon f : faraon)
            if (f.isAlive()) f.update(lvlData, jugador);
        for (Manticora m : manticora)
            if (m.isAlive()) m.update(lvlData, jugador);
    }

    //Nivel 3

    public void update3(int [][] lvlData, Jugador3 jugador){
        for (Armado a : armados) {
            if(a.isAlive())
            a.update3(lvlData, jugador);
        }

        for (Atcher i : atchers) {
            if(i.isAlive())
            i.update3(lvlData, jugador);
        }

        for (Rata p : rats) {
            if(p.isAlive())
            p.update3(lvlData, jugador);
        }

        for (Hyena m : hyenas) {
            if(m.isAlive())
            m.update3(lvlData, jugador);
        }

        for (Bat f : bats) {
            if(f.isAlive())
            f.update3(lvlData, jugador);
        }

        for (Mago b : magos) {
            if(b.isAlive())
            b.update3(lvlData, jugador);
        }
    }

    // nivel 4
    public void update4(int [][] lvlData, Jugador4 jugador4){
        for (Dog dog : dogs) {
        // actualiza si está vivo O si está muriendo
            if (dog.isAlive() || dog.getEnemyState() == DOG_DIE)
                dog.update4(lvlData, jugador4);
        }

        for (Spider i : spiders) {
            if (i.isAlive() || i.getEnemyState() == SPIDER_DIE)
                i.update4(lvlData, jugador4);
        }

        for (Torret t : torrets) {
            if (t.isAlive() || t.getEnemyState() == TORRET_DIE)
                t.update4(lvlData, jugador4);
        }

        for (Dron f : drons) {
            if (f.isAlive() || f.getEnemyState() == DRON_DIE)
                f.update4(lvlData, jugador4);
        }

        for (Boss4 b : boss4) {
            if (b.isAlive() || b.getEnemyState() == BOSS4_DIE)
                b.update4(lvlData, jugador4);
        }

        for (Ninja n : ninjas) {
            if (n.isAlive() || n.getEnemyState() == NINJA_DIE)
                n.update4(lvlData, jugador4  );
        }

        for (Trap t : traps) {
            t.update4(lvlData, jugador4);
        }

        for (Trap2 t : traps2) {
            t.update4(jugador4);
        }

        for (ButtonFloor b : buttons) {
            b.update4(jugador4);
        }

        for (Door d : doors) {
            d.update4();
        }

        if (miniGameCooldown > 0) miniGameCooldown--;

        for (ButtonFloor b : buttons) {
            if (b.isPressed() && !miniGameActivo && !miniGameCompletado && miniGameCooldown == 0) {
                miniGameActivo = true;
                abrirMiniJuego();
            }
        }

        for (Weapon w : weapons) {
            if (!w.isCollected()) {
                if (jugador4.getHitbox().intersects(w.getHitbox())) {
                    w.setCollected(true);
                    jugador4.setHasWeapon(true);
                }
            }
        }

    }

    public void draw(Graphics g, int xlvlOffset, int ylvlOffset) {
        drawArma(g, xlvlOffset,ylvlOffset);
        drawHatc(g, xlvlOffset,ylvlOffset);
        drawRat(g, xlvlOffset,ylvlOffset);
        drawBat(g, xlvlOffset,ylvlOffset);
        drawHyena(g, xlvlOffset,ylvlOffset);
        drawMago(g, xlvlOffset,ylvlOffset);
    }

    public void draw(Graphics g, int xlvlOffset) {
        drawVelo(g, xlvlOffset);
        drawIndio(g, xlvlOffset);
        drawPlant(g, xlvlOffset);
        drawMosco(g, xlvlOffset);
        drawFlyer(g, xlvlOffset);
        drawBoss(g, xlvlOffset);
        //Nivel 3
        drawSnake(g, xlvlOffset);
        drawScorpio(g, xlvlOffset);
        drawMummy(g, xlvlOffset);
        drawDeceased(g, xlvlOffset);
        drawEgyptianArcher(g, xlvlOffset);
        drawAnubis(g, xlvlOffset);
        drawEgyptianFighter(g, xlvlOffset);
        drawFaraon(g, xlvlOffset);
        drawManticora(g, xlvlOffset);

        //Nivel 4
        drawDog(g, xlvlOffset);
        drawSpider(g, xlvlOffset);
        drawTorret(g, xlvlOffset);
        drawDron(g, xlvlOffset);
        drawBullets(g, xlvlOffset);
        drawBoss4(g, xlvlOffset);
        drawTrap(g, xlvlOffset);
        drawTrap2(g, xlvlOffset);
        drawDoor(g, xlvlOffset);
        drawButtons(g, xlvlOffset);
        drawNinja(g, xlvlOffset);
        drawWeapons(g, xlvlOffset);
        drawTorretBullets(g, xlvlOffset);

    }

    private void drawIndio(Graphics g, int xlvlOffset) {
        for (Indio i : indios) {
            if(i.isAlive()){
                int state = i.getEnemyState();
                int animIdx = i.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = indioArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
            if(i.walkDir == RIGHT) {
                    g.drawImage(indioArr[state][animIdx],
                    (int)i.getHitbox().x - xlvlOffset -20,
                    (int)i.getHitbox().y -16,
                    INDIO_WIDTH/2 * i.flipW(),
                    INDIO_HEIGHT/2, null);
                }
                else
                    g.drawImage(indioArr[state][animIdx],
                    (int)i.getHitbox().x - xlvlOffset + 40,
                    (int)i.getHitbox().y -16,
                    INDIO_WIDTH/2 * i.flipW(),
                    INDIO_HEIGHT/2, null);
                // g.drawImage(indioArr[state][animIdx],
                //     (int)i.getHitbox().x - xlvlOffset + i.flipX(),
                //     (int)i.getHitbox().y -16,
                //     INDIO_WIDTH/2 * i.flipW(),
                //     INDIO_HEIGHT/2, null);

                //i.drawHitbox(g, xlvlOffset);

                // if(r.walkDir == Velociraptor.LEFT)
                //     r.drawAttackBox(g, xlvlOffset);
                // else
                //i.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawVelo(Graphics g, int xlvlOffset) {
        for (Velociraptor r : raptors) {
            if(r.isAlive()){
                int state = r.getEnemyState();
                int animIdx = r.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = veloArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                g.drawImage(veloArr[state][animIdx],
                    (int)r.getHitbox().x - xlvlOffset + r.flipX(),
                    (int)r.getHitbox().y - 20,
                    VELO_WIDTH/2 * r.flipW(),
                    VELO_HEIGHT/2, null);

                //r.drawHitbox(g, xlvlOffset);

                // if(r.walkDir == Velociraptor.LEFT)
                //     r.drawAttackBox(g, xlvlOffset);
                // else
                //r.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawPlant(Graphics g, int xlvlOffset) {
        for (Planta p : plantas) {
            if(p.isAlive()){
                int state = p.getEnemyState();
                int animIdx = p.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = plantaArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                if (p.walkDir == RIGHT) {
                    g.drawImage(plantaArr[state][animIdx],
                    (int)p.getHitbox().x - xlvlOffset ,
                    (int)p.getHitbox().y -10,
                    PLANT_WIDTH * p.flipW(),
                    PLANT_HEIGHT, null);
                }
                else
                    g.drawImage(plantaArr[state][animIdx],
                    (int)p.getHitbox().x - xlvlOffset + 25,
                    (int)p.getHitbox().y -10,
                    PLANT_WIDTH * p.flipW(),
                    PLANT_HEIGHT, null);

                //p.drawHitbox(g, xlvlOffset);

                // if(r.walkDir == Velociraptor.LEFT)
                //     r.drawAttackBox(g, xlvlOffset);
                // else
                //p.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawBoss(Graphics g, int xlvlOffset) {
        for (Boss b : boss) {
            if(b.isAlive()){
                int state = b.getEnemyState();
                // BOSS_JUMP (6) y BOSS_IDLE (7) pueden estar fuera del array; usar BOSS_RUN (0) como fallback
                if (state == BOSS_IDLE || state >= bossArr.length) state = BOSS_RUN;

                int animIdx = b.getAnimIndex();
                int maxFrames = bossArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;

                g.drawImage(bossArr[state][animIdx],
                    (int)b.getHitbox().x - xlvlOffset + b.flipX() - 65,
                    (int)b.getHitbox().y - 115,
                    (BOSS_WIDTH + 65) * b.flipW(),
                    BOSS_HEIGHT + 65, null);

                b.drawLaser(g, xlvlOffset);
                b.drawWeakSpot(g, xlvlOffset);
                //b.drawHitbox(g, xlvlOffset);
                //b.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawMosco(Graphics g, int xlvlOffset) {
        for (Mosco m : moscos) {
            if(m.isAlive()){
                int state = m.getEnemyState();
                int animIdx = m.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = moscoArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                g.drawImage(moscoArr[state][animIdx], 
                    (int)m.getHitbox().x - xlvlOffset + m.flipX(),
                    (int)m.getHitbox().y - 20,
                    MOSCO_WIDTH * m.flipW(),
                    MOSCO_HEIGHT, null);

                //m.drawHitbox(g, xlvlOffset);

                //m.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawFlyer(Graphics g, int xlvlOffset) {
        for (Flyer f : flyers) {
            if(f.isAlive()){
                int state = f.getEnemyState();
                int animIdx = f.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = flyerArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                g.drawImage(flyerArr[state][animIdx],
                    (int)f.getHitbox().x - xlvlOffset + f.flipX(),//ajustar posición de dibujo para que el mosco vuele desde su hitbox
                    (int)f.getHitbox().y - 25,
                    FLYER_WIDTH/2 * f.flipW(),
                    FLYER_HEIGHT/2, null);

                //f.drawHitbox(g, xlvlOffset);

                //f.drawAttackBox(g, xlvlOffset);
            }
        }
    }
    //Nivel 2
    private void drawSnake(Graphics g, int xlvlOffset) {
        for (Snake s : snake) {
            if (!s.isAlive())
                continue;
            int state = s.getEnemyState();
            int animIdx = s.getAnimIndex();
            if (animIdx >= snakeArr[state].length)
                animIdx = 0;

            int xDrawOffset = (int) (10 * Juego.SCALE);
            int yDrawOffset = (int) (15 * Juego.SCALE);

            g.drawImage(snakeArr[state][animIdx],
                    (int) s.getHitbox().x - xlvlOffset - xDrawOffset + s.flipX(),
                    (int) s.getHitbox().y - yDrawOffset,
                    SERPIENTE_WIDTH * s.flipW(), SERPIENTE_HEIGHT, null);

            // s.drawHitbox(g, xlvlOffset);
            // s.drawAttackBox(g, xlvlOffset);
        }
    }

    private void drawScorpio(Graphics g, int xlvlOffset) {
        for (Scorpio sc : scorpio) {
            if (!sc.isAlive())
                continue;
            int state = sc.getEnemyState();
            int animIdx = sc.getAnimIndex();
            if (animIdx >= scorpioArr[state].length)
                animIdx = 0;

            int xDrawOffset = (int) (10 * Juego.SCALE);
            int yDrawOffset = (int) (25 * Juego.SCALE);

            g.drawImage(scorpioArr[state][animIdx],
                    (int) sc.getHitbox().x - xlvlOffset - xDrawOffset + sc.flipX(),
                    (int) sc.getHitbox().y - yDrawOffset,
                    SCORPIO_WIDTH * sc.flipW(), SCORPIO_HEIGHT, null);

            // sc.drawHitbox(g, xlvlOffset);
            // sc.drawAttackBox(g, xlvlOffset);
        }
    }

    private void drawMummy(Graphics g, int xlvlOffset) {
        for (Mummy m : mummy) {
            if (!m.isAlive())
                continue;
            int state = m.getEnemyState();
            int animIdx = m.getAnimIndex();
            if (animIdx >= mummyArr[state].length)
                animIdx = 0;

            int xDrawOffset = (int) (10 * Juego.SCALE);
            int yDrawOffset = (int) (27 * Juego.SCALE);

            g.drawImage(mummyArr[state][animIdx],
                    (int) m.getHitbox().x - xlvlOffset - xDrawOffset + m.flipX(),
                    (int) m.getHitbox().y - yDrawOffset,
                    (MUMMY_WIDTH+12) * m.flipW(), MUMMY_HEIGHT+12, null);

            //m.drawHitbox(g, xlvlOffset);
            //m.drawAttackBox(g, xlvlOffset);
        }
    }

    private void drawDeceased(Graphics g, int xlvlOffset) {
        for (Deceased d : deceased) {
            if (!d.isAlive())
                continue;
            int state = d.getEnemyState();
            int animIdx = d.getAnimIndex();
            if (animIdx >= deceasedArr[state].length)
                animIdx = 0;

            int xDrawOffset = (int) (10 * Juego.SCALE);
            int yDrawOffset = (int) (27 * Juego.SCALE);

            g.drawImage(deceasedArr[state][animIdx],
                    (int) d.getHitbox().x - xlvlOffset - xDrawOffset + d.flipX(),
                    (int) d.getHitbox().y - yDrawOffset,
                    (DECEASED_WIDTH+12) * d.flipW(), DECEASED_HEIGHT+12, null);

            // d.drawHitbox(g, xlvlOffset);
            // d.drawAttackBox(g, xlvlOffset);

            if (d.isBallActive()) {
                Rectangle2D.Float bh = d.getBallHitbox();
                int bSize = (int) (12 * Juego.SCALE);
                g.drawImage(ballImg,
                        (int) bh.x - xlvlOffset,
                        (int) bh.y,
                        bSize, bSize, null);
            }
        }
    }

    private void drawEgyptianArcher(Graphics g, int xlvlOffset) {
        for (EgyptianArcher archer : egyptianArcher) {
            if (!archer.isAlive())
                continue;
            int state = archer.getEnemyState();
            int animIdx = archer.getAnimIndex();
            if (animIdx >= egyptianArcherArr[state].length)
                animIdx = 0;

            int xDrawOffset = (int) (10 * Juego.SCALE);
            int yDrawOffset = (int) (27 * Juego.SCALE);

            g.drawImage(egyptianArcherArr[state][animIdx],
                    (int) archer.getHitbox().x - xlvlOffset - xDrawOffset + archer.flipX(),
                    (int) archer.getHitbox().y - yDrawOffset,
                    (EGYPTIANARCHER_WIDTH+12) * archer.flipW(), EGYPTIANARCHER_HEIGHT+12, null);

            // archer.drawHitbox(g, xlvlOffset);
            // archer.drawAttackBox(g, xlvlOffset);

            if (archer.isBallActive()) {
                Rectangle2D.Float archerh = archer.getBallHitbox();
                int archerSize = (int) (12 * Juego.SCALE);
                g.drawImage(arrowImg,
                        (int) archerh.x - xlvlOffset,
                        (int) archerh.y,
                        archerSize, archerSize, null);
            }
        }
    }


    private void drawEgyptianFighter(Graphics g, int xlvlOffset) {
        for (EgyptianFighter ef : egyptianFighter) {
            if (!ef.isAlive()) continue;
            int state   = ef.getEnemyState();
            int animIdx = ef.getAnimIndex();
            if (animIdx >= egyptianFighterArr[state].length) animIdx = 0;

            int xOff = (int)(10 * Juego.SCALE);
            int yOff = (int)(27 * Juego.SCALE);

            g.drawImage(egyptianFighterArr[state][animIdx],
                    (int)ef.getHitbox().x - xlvlOffset - xOff + ef.flipX(),
                    (int)ef.getHitbox().y - yOff,
                    (EGYPTIANFIGHTER_WIDTH+12) * ef.flipW(), EGYPTIANFIGHTER_HEIGHT+12, null);
        }
    }

    private void drawFaraon(Graphics g, int xlvlOffset) {
        for (Faraon f : faraon) {
            if (!f.isAlive()) continue;
            int state   = f.getEnemyState();
            int animIdx = f.getAnimIndex();
            if (animIdx >= faraonArr[state].length) animIdx = 0;

            int xOff = (int)(10 * Juego.SCALE);
            int yOff = (int)(27 * Juego.SCALE);

            g.drawImage(faraonArr[state][animIdx],
                    (int)f.getHitbox().x - xlvlOffset - xOff + f.flipX(),
                    (int)f.getHitbox().y - yOff,
                    (FARAON_WIDTH+12) * f.flipW(), FARAON_HEIGHT+12, null);

            if (f.isBallActive()) {
                Rectangle2D.Float bh = f.getBallHitbox();
                int bSize = (int)(10 * Juego.SCALE);
                g.drawImage(faraonBallImg, (int)bh.x - xlvlOffset, (int)bh.y, bSize, bSize, null);
            }
        }
    }

    private void drawManticora(Graphics g, int xlvlOffset) {
        for (Manticora m : manticora) {
            if (!m.isAlive()) continue;
            int state   = m.getEnemyState();
            int animIdx = m.getAnimIndex();
            if (animIdx >= manticoraArr[state].length) animIdx = 0;

            int xOff = (int)(21 * Juego.SCALE);
            int yOff = (int)(42 * Juego.SCALE);

            int drawX = (int)m.getHitbox().x - xlvlOffset - xOff;
            int drawW = MANTICORA_WIDTH;
            if (m.flipW() == -1) { drawX += drawW; drawW = -drawW; }
            g.drawImage(manticoraArr[state][animIdx], drawX, (int)m.getHitbox().y - yOff, drawW, MANTICORA_HEIGHT, null);

            if (m.isBallActive()) {
                Rectangle2D.Float bh = m.getBallHitbox();
                g.drawImage(manticoraBallImg, (int)bh.x - xlvlOffset, (int)bh.y,
                        (int)(14 * Juego.SCALE), (int)(14 * Juego.SCALE), null);
            }
        }
    }

    //Nivel 3
    private void drawHatc(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Atcher i : atchers) {
            if(i.isAlive()){
                int state = i.getEnemyState();
                int animIdx = i.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = atcherArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
            if(i.walkDir == RIGHT) 
                    g.drawImage(atcherArr[state][animIdx],
                    (int)i.getHitbox().x - xlvlOffset -20,
                    (int)i.getHitbox().y-ylvlOffset -23,
                    ATCHER_WIDTH/2 * i.flipW(),
                    ATCHER_HEIGHT/2, null);
                else
                    g.drawImage(atcherArr[state][animIdx],
                    (int)i.getHitbox().x - xlvlOffset + 40,
                    (int)i.getHitbox().y-ylvlOffset -23,
                    ATCHER_WIDTH/2 * i.flipW(),
                    ATCHER_HEIGHT/2, null);

                for (Proyectil3 p : i.getProyectiles())
                    if (p.isActive()) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(
                            (int)(p.getX() - xlvlOffset),
                            (int)(p.getY() - ylvlOffset),
                            p.getWidth(), p.getHeight());
                    }

                //i.drawHitbox(g, xlvlOffset,ylvlOffset);
                //i.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    private void drawArma(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Armado r : armados) {
            if(r.isAlive()){
                int state = r.getEnemyState();
                int animIdx = r.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                if (state>=cabArr.length) state=0;
                if (animIdx >= cabArr[state].length) animIdx = cabArr[state].length-1;
                
                g.drawImage(cabArr[state][animIdx],
                    (int)r.getHitbox().x - xlvlOffset + r.flipX()-15,
                    (int)r.getHitbox().y-ylvlOffset-17,
                    (int)(ARMAD_WIDTH/2.5 * r.flipW()),
                    (int)(ARMAD_HEIGHT/2.5), null);

                //r.drawHitbox(g, xlvlOffset,ylvlOffset);
                //r.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    private void drawRat(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Rata p : rats) {
            if(p.isAlive()){
                int state = p.getEnemyState();
                int animIdx = p.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = ratArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                if (p.walkDir == RIGHT) {
                    g.drawImage(ratArr[state][animIdx],
                    (int)p.getHitbox().x - xlvlOffset ,
                    (int)p.getHitbox().y-ylvlOffset +3,
                    (int)(RAT_WIDTH/2 * p.flipW()),
                    (int)(RAT_HEIGHT/2), null);
                }
                else
                    g.drawImage(ratArr[state][animIdx],
                    (int)p.getHitbox().x - xlvlOffset + 30,
                    (int)p.getHitbox().y-ylvlOffset +3,
                    (int)(RAT_WIDTH/2 * p.flipW()),
                    (int)(RAT_HEIGHT/2), null);

                //p.drawHitbox(g, xlvlOffset,ylvlOffset);
                //p.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    private void drawMago(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Mago b : magos) {
            if(b.isAlive()){
                int state = b.getEnemyState();
                int animIdx = b.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                if (state>=cabArr.length) state=0;
                if (animIdx >= magoArr[state].length) animIdx = magoArr[state].length-1;
                if (b.walkDir == RIGHT)
                    g.drawImage(magoArr[state][animIdx],
                    (int)b.getHitbox().x - xlvlOffset-50,
                    (int)b.getHitbox().y - ylvlOffset-42,
                    MAGO_WIDTH * b.flipW(),
                    MAGO_HEIGHT, null);
                else
                    g.drawImage(magoArr[state][animIdx],
                    (int)b.getHitbox().x - xlvlOffset+65,
                    (int)b.getHitbox().y - ylvlOffset-42,
                    MAGO_WIDTH * b.flipW(),
                    MAGO_HEIGHT, null);
                
                //b.drawHitbox(g, xlvlOffset,ylvlOffset);
                //b.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    private void drawHyena(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Hyena h : hyenas) {
            if(h.isAlive()){
                int state = h.getEnemyState();
                int animIdx = h.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                if (state>=hyenaArr.length) state=0;
                if (animIdx >= hyenaArr[state].length) animIdx = hyenaArr[state].length-1;
                
                if(h.walkDir == RIGHT) 
                    g.drawImage(hyenaArr[state][animIdx],
                    (int)h.getHitbox().x - xlvlOffset +35,
                    (int)h.getHitbox().y-ylvlOffset -5,
                    (int)(HYENA_WIDTH/1.5 * h.flipW()),
                    (int)(HYENA_HEIGHT/1.5), null);
                else
                    g.drawImage(hyenaArr[state][animIdx],
                    (int)h.getHitbox().x - xlvlOffset -6,
                    (int)h.getHitbox().y-ylvlOffset -5,
                    (int)(HYENA_WIDTH/1.5 * h.flipW()),
                    (int)(HYENA_HEIGHT/1.5), null);
                    


                //h.drawHitbox(g, xlvlOffset,ylvlOffset);
                //h.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    private void drawBat(Graphics g, int xlvlOffset, int ylvlOffset) {
        for (Bat f : bats) {
            if(f.isAlive()){
                int state = f.getEnemyState();
                int animIdx = f.getAnimIndex();

                // Clamp defensivo contra condición de carrera entre hilos
                int maxFrames = batArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                if(f.walkDir == RIGHT) 
                    g.drawImage(batArr[state][animIdx],
                        (int)f.getHitbox().x - xlvlOffset + f.flipX()-15,
                        (int)f.getHitbox().y-ylvlOffset - 20,
                        BAT_WIDTH/1 * f.flipW(),
                        BAT_HEIGHT/1, null);
                else
                    g.drawImage(batArr[state][animIdx],
                        (int)f.getHitbox().x - xlvlOffset + f.flipX()+23,
                        (int)f.getHitbox().y-ylvlOffset - 20,
                        BAT_WIDTH/1 * f.flipW(),
                        BAT_HEIGHT/1, null);

                //f.drawHitbox(g, xlvlOffset,ylvlOffset);
                //f.drawAttackBox(g, xlvlOffset,ylvlOffset);
            }
        }
    }

    //Nivel 4
    private void drawSpider(Graphics g, int xlvlOffset) {
    for (Spider i : spiders) {
            if (i.isAlive()) {
                int state = i.getEnemyState();
                int animIdx = i.getAnimIndex();
                int maxFrames = spiderArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;

                g.drawImage(spiderArr[state][animIdx],
                    (int)i.getHitbox().x - xlvlOffset + i.flipX(),
                    (int)i.getHitbox().y - 5,
                    SPIDER_WIDTH / 2 * i.flipW(),
                    SPIDER_HEIGHT / 2, null);

                // i.drawAttackBox(g, xlvlOffset);
            }
        }
    }

    private void drawDog(Graphics g, int xlvlOffset) {
    for (Dog r : dogs) {
        // dibuja si está vivo O si está en animación de muerte
        if (r.isAlive() || r.getEnemyState() == DOG_DIE) {
            int state = r.getEnemyState();
            int animIdx = r.getAnimIndex();
            int maxFrames = dogArr[state].length;
            if (animIdx >= maxFrames) animIdx = 0;
            
            g.drawImage(dogArr[state][animIdx],
                (int)r.getHitbox().x - xlvlOffset + r.flipX(),
                (int)r.getHitbox().y - 20,
                DOG_WIDTH/2 * r.flipW(),
                DOG_HEIGHT/2, null);
        }
    }
}

    private void drawTorret(Graphics g, int xlvlOffset) {
        for (Torret t : torrets) {
            if (t.isAlive()) {
                int state = t.getEnemyState();
                int animIdx = t.getAnimIndex();
                int maxFrames = torretArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;

                g.drawImage(torretArr[state][animIdx],
                    (int)t.getHitbox().x - xlvlOffset + t.flipX()- 20,
                    (int)t.getHitbox().y-10,
                    TORRET_WIDTH / 2 * t.flipW(), 
                    TORRET_HEIGHT / 2,
                    null);
            }
        }
    }

    private void drawBoss4(Graphics g, int xlvlOffset) {
        for (Boss4 b : boss4) {
            if(b.isAlive()){
                int state = b.getEnemyState();
                int animIdx = b.getAnimIndex();

                int maxFrames = boss4Arr[state].length; // 🔥
                if (animIdx >= maxFrames) animIdx = 0;
                
                g.drawImage(boss4Arr[state][animIdx], // 🔥
                (int)b.getHitbox().x - xlvlOffset + b.flipX(),
                (int)b.getHitbox().y - 60,            
                (BOSS4_WIDTH * 3 / 4) * b.flipW(),     
                BOSS4_HEIGHT * 3 / 4,                    
                null);
            }
        }
    }

    private void drawDron(Graphics g, int xlvlOffset) {
        for (Dron f : drons) {
            if(f.isAlive()){
                int state = f.getEnemyState();
                int animIdx = f.getAnimIndex();


                int maxFrames = dronArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;
                
                
                g.drawImage(dronArr[state][animIdx],
                    (int)f.getHitbox().x - xlvlOffset + f.flipX(),    
                    (int)f.getHitbox().y - 20,
                    DRON_WIDTH/2 * f.flipW(),
                    DRON_HEIGHT/2, null);
            //     g.setColor(java.awt.Color.BLUE);
            //      g.drawRect(
            //     (int)(f.getHitbox().x - xlvlOffset),
            //     (int)f.getHitbox().y,
            //     (int)f.getHitbox().width,
            //     (int)f.getHitbox().height
            // );

            }
        }

    }

    private void drawTrap(Graphics g, int xlvlOffset) {
        for (Trap t : traps) {
            Rectangle2D.Float box = t.getDamageBox();

            g.drawImage(trapArr[0][0],
                (int)(box.x - xlvlOffset),
                (int)box.y,
                32, 32,
                null);
        }
    }

    private void drawTrap2(Graphics g, int xlvlOffset) {
        for (Trap2 t : traps2) {
            int animIdx = t.getAnimIndex();

            g.drawImage(trap2Arr[0][animIdx],
                (int)(t.getHitbox().x - xlvlOffset),
                (int)t.getHitbox().y+16,
                32, 32,
                null);
        }
    }

    private void drawDoor(Graphics g, int xlvlOffset) {
        for (Door d : doors) {
            g.drawImage(doorArr[0][d.getAnimIndex()],
                (int)(d.getHitbox().x - xlvlOffset) +2, 
                (int)d.getHitbox().y+3 ,                 
                64, 64,
                null);

        }
    }

    private void drawButtons(Graphics g, int xlvlOffset) {
        for (ButtonFloor b : buttons) {
            int frame = b.getFrame();

            g.drawImage(buttonArr[0][frame],
                (int)(b.getHitbox().x - xlvlOffset),
                (int)b.getHitbox().y+14,
                32,
                32,
                null);
        }
    }

    private void drawNinja(Graphics g, int xlvlOffset) {
        for (Ninja n : ninjas) {
            if(n.isAlive()){
                int state = n.getEnemyState();
                int animIdx = n.getAnimIndex();

                int maxFrames = ninjaArr[state].length;
                if (animIdx >= maxFrames) animIdx = 0;

                g.drawImage(ninjaArr[state][animIdx],
                    (int)n.getHitbox().x - xlvlOffset + n.flipX(),
                    (int)n.getHitbox().y-30,
                    140 * n.flipW(),  
                    100,
                    null);
            }
        }
    }

    private void drawWeapons(Graphics g, int xlvlOffset) {
        for (Weapon w : weapons) {
            if (!w.isCollected()) {
                g.drawImage(
                    weaponImg,
                    (int)(w.getHitbox().x - xlvlOffset),
                    (int)(w.getHitbox().y),
                    32, 32,
                    null
                );
            }
        }
    }

    private void drawTorretBullets(Graphics g, int xlvlOffset) {
        for (Torret t : torrets) {
            if (!t.isAlive()) continue;
            for (TorretBullet b : t.getBullets()) {
                if (!b.isActive()) continue;
                int frame = b.getAnimIndex();
                int drawW = (b.dir == -1) ? -32 : 32;
                int drawX = (b.dir == -1)
                    ? (int)(b.x - xlvlOffset) + 32
                    : (int)(b.x - xlvlOffset);
                g.drawImage(torretBulletArr[frame], drawX, (int)b.y, drawW, 32, null);
            }
        }
    }    

    private void drawBullets(Graphics g, int xlvlOffset) {
        for (Dron d : drons) {
            if (!d.isAlive()) continue;
            for (Bullet b : d.getBullets()) {
                if (!b.active) continue;
                int frame = b.getAnimIndex();
                int drawW = (b.direction == utilz.Constantes.Direccion.LEFT)
                    ? -Bullet.WIDTH : Bullet.WIDTH;
                int drawX = (b.direction == utilz.Constantes.Direccion.LEFT)
                    ? (int)(b.x - xlvlOffset) + Bullet.WIDTH
                    : (int)(b.x - xlvlOffset);
                g.drawImage(bulletArr[frame],
                    drawX, (int)b.y,
                    drawW, Bullet.HEIGHT, null);
            }
        }
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }


    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for (Velociraptor r : raptors) {
            if(r.isAlive())
                if (attackBox.intersects(r.getHitbox())) {
                    r.hurt(10);
                    return;
                }
        }

        for (Planta p : plantas) {
            if(p.isAlive())
                if (attackBox.intersects(p.getHitbox())) {
                    p.hurt(10);
                    return;
                }
        }

        for (Indio i : indios) {
            if(i.isAlive())
                if (attackBox.intersects(i.getHitbox())) {
                    i.hurt(10);
                    return;
                }
        }

        for (Mosco m : moscos) {
            if(m.isAlive())
                if (attackBox.intersects(m.getHitbox())) {
                    m.hurt(10);
                    return;
                }
        }

        for (Flyer f : flyers) {
            if(f.isAlive())
                if (attackBox.intersects(f.getHitbox())) {
                    f.hurt(10);
                    return;
                }
        }

        for (Boss b : boss) {
            if (b.isAlive()) {
                Rectangle2D.Float ws = b.getWeakSpot();
                if (attackBox.intersects(b.getHitbox()) ||
                        (ws != null && attackBox.intersects(ws))) {
                    b.hurtIfWeakSpotHit(attackBox, 10);
                    return;
                }
            }
        }
        //Nivel 2
        for (Snake s : snake)
            if (s.isAlive() && attackBox.intersects(s.getHitbox())) { s.hurt(10); return; }

        for (Scorpio sc : scorpio)
            if (sc.isAlive() && attackBox.intersects(sc.getHitbox())) { sc.hurt(10); return; }

        for (Mummy m : mummy)
            if (m.isAlive() && attackBox.intersects(m.getHitbox())) { m.hurt(10); return; }

        for (Deceased d : deceased)
            if (d.isAlive() && attackBox.intersects(d.getHitbox())) { d.hurt(10); return; }

        for (EgyptianArcher archer : egyptianArcher)
            if (archer.isAlive() && attackBox.intersects(archer.getHitbox())){archer.hurt(10); return;}

        for (Anubis a : anubis)
            if (a.isAlive() && attackBox.intersects(a.getHitbox())) { a.hurt(10); return; }

        for (EgyptianFighter ef : egyptianFighter)
            if (ef.isAlive() && attackBox.intersects(ef.getHitbox())) { ef.hurt(35); return; }

        for (Faraon f : faraon)
            if (f.isAlive() && attackBox.intersects(f.getHitbox())) { f.hurt(35); return; }

        for (Manticora m : manticora)
            if (m.isAlive() && attackBox.intersects(m.getHitbox())) { m.hurt(35); return; }

        //Nivel 3
        for (Armado r : armados) {
            if(r.isAlive())
                if (attackBox.intersects(r.getHitbox())) {
                    r.hurt(10);
                    return;
                }
        }

        for (Rata p : rats) {
            if(p.isAlive())
                if (attackBox.intersects(p.getHitbox())) {
                    p.hurt(10);
                    return;
                }
        }

        for (Atcher i : atchers) {
            if(i.isAlive())
                if (attackBox.intersects(i.getHitbox())) {
                    i.hurt(10);
                    return;
                }
        }

        for (Hyena m : hyenas) {
            if(m.isAlive())
                if (attackBox.intersects(m.getHitbox())) {
                    m.hurt(10);
                    return;
                }
        }

        for (Bat f : bats) {
            if(f.isAlive())
                if (attackBox.intersects(f.getHitbox())) {
                    f.hurt(10);
                    return;
                }
        }

        for (Mago b : magos) {
            if(b.isAlive())
                if (attackBox.intersects(b.getHitbox())) {
                    b.hurt(10);
                    return;
                }
        }

        //Nivel 4
        for (Dog r : dogs) {
            if (r.isAlive() && attackBox.intersects(r.getHitbox())) {
                r.hurt(10);
                return;
            }
        }
        for (Torret t : torrets) {
            if (t.isAlive() && attackBox.intersects(t.getHitbox())) {
                t.hurt(10);
                return;
            }
        }
        for (Spider i : spiders) {
            if (i.isAlive() && attackBox.intersects(i.getHitbox())) {
                i.hurt(10);
                return;
            }
        }
        for (Dron f : drons) {
            if (f.isAlive() && attackBox.intersects(f.getHitbox())) {
                f.hurt(10);
                return;
            }
        }
        for (Boss4 b : boss4) {
            if (b.isAlive() && attackBox.intersects(b.getHitbox())) {
                b.hurt(10);
                return;
            }
        }
        for (Ninja n : ninjas) {
            if (n.isAlive() && attackBox.intersects(n.getHitbox())) {
                n.hurt(10);
                return;
            }
        }
    }

    private void loadEnemies() {
        veloArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VELO_SPRITES);
        for (int j = 0; j < veloArr.length; j++) {
            for (int i = 0; i < veloArr[j].length; i++) {
                veloArr[j][i] = temp.getSubimage(i * VELO_WIDTH_DEFAULT, j * VELO_HEIGHT_DEFAULT, VELO_WIDTH_DEFAULT, VELO_HEIGHT_DEFAULT);
            }
        }

        indioArr = new BufferedImage[5][6];
        BufferedImage tempIndio = LoadSave.GetSpriteAtlas(LoadSave.INDIO_SPRITES);
        for (int j = 0; j < indioArr.length; j++) {
            for (int i = 0; i < indioArr[j].length; i++) {
                indioArr[j][i] = tempIndio.getSubimage(i * INDIO_WIDTH_DEFAULT, j * INDIO_HEIGHT_DEFAULT, INDIO_WIDTH_DEFAULT, INDIO_HEIGHT_DEFAULT);
            }
        }

        plantaArr = new BufferedImage[5][16];
        BufferedImage tempPlanta = LoadSave.GetSpriteAtlas(LoadSave.PLANT_SPRITES);
        for (int j = 0; j < plantaArr.length; j++) {
            for (int i = 0; i < plantaArr[j].length; i++) {
                plantaArr[j][i] = tempPlanta.getSubimage(i * PLANT_WIDTH_DEFAULT, j * PLANT_HEIGHT_DEFAULT, PLANT_WIDTH_DEFAULT, PLANT_HEIGHT_DEFAULT);
            }
        }

        moscoArr = new BufferedImage[11][13];//
        BufferedImage tempMosco = LoadSave.GetSpriteAtlas(LoadSave.MOSCO_SPRITES);
        for (int j = 0; j < moscoArr.length; j++) {
            for (int i = 0; i < moscoArr[j].length; i++) {
                moscoArr[j][i] = tempMosco.getSubimage(i * MOSCO_WIDTH_DEFAULT, j * MOSCO_HEIGHT_DEFAULT, MOSCO_WIDTH_DEFAULT, MOSCO_HEIGHT_DEFAULT);
            }
        }
        
        flyerArr = new BufferedImage[4][4];//
        BufferedImage tempFlyer = LoadSave.GetSpriteAtlas(LoadSave.ALA_SPRITES);
        for (int j = 0; j < flyerArr.length; j++) {
            for (int i = 0; i < flyerArr[j].length; i++) {
                flyerArr[j][i] = tempFlyer.getSubimage(i * FLYER_WIDTH_DEFAULT, j * FLYER_HEIGHT_DEFAULT, FLYER_WIDTH_DEFAULT, FLYER_HEIGHT_DEFAULT);
            }
        }

        bossArr = new BufferedImage[7][12];
        BufferedImage tempBoss = LoadSave.GetSpriteAtlas(LoadSave.BOSS_SPRITES);
        for (int j = 0; j < bossArr.length; j++) {
            for (int i = 0; i < bossArr[j].length; i++) {
                bossArr[j][i] = tempBoss.getSubimage(i * BOSS_WIDTH_DEFAULT, j * BOSS_HEIGHT_DEFAULT, BOSS_WIDTH_DEFAULT, BOSS_HEIGHT_DEFAULT);
            }
        }
        //Nivel 2
        snakeArr = new BufferedImage[5][6];
        BufferedImage tempSnake = LoadSave.GetSpriteAtlas(LoadSave.SERPIENTE_SPRITES);
        for (int j = 0; j < snakeArr.length; j++)
            for (int i = 0; i < snakeArr[j].length; i++)
                snakeArr[j][i] = tempSnake.getSubimage(
                        i * SERPIENTE_WIDTH_DEFAULT, j * SERPIENTE_HEIGHT_DEFAULT,
                        SERPIENTE_WIDTH_DEFAULT, SERPIENTE_HEIGHT_DEFAULT);

        // Scorpio
        scorpioArr = new BufferedImage[5][6];
        BufferedImage tempScorpio = LoadSave.GetSpriteAtlas(LoadSave.SCORPIO_SPRITES);
        for (int j = 0; j < scorpioArr.length; j++)
            for (int i = 0; i < scorpioArr[j].length; i++)
                scorpioArr[j][i] = tempScorpio.getSubimage(
                        i * SCORPIO_WIDTH_DEFAULT, j * SCORPIO_HEIGHT_DEFAULT,
                        SCORPIO_WIDTH_DEFAULT, SCORPIO_HEIGHT_DEFAULT);

        // Mummy
        mummyArr = new BufferedImage[5][6];
        BufferedImage tempMummy = LoadSave.GetSpriteAtlas(LoadSave.MUMMY_SPRITES);
        for (int j = 0; j < mummyArr.length; j++)
            for (int i = 0; i < mummyArr[j].length; i++)
                mummyArr[j][i] = tempMummy.getSubimage(
                        i * MUMMY_WIDTH_DEFAULT, j * MUMMY_HEIGHT_DEFAULT,
                        MUMMY_WIDTH_DEFAULT, MUMMY_HEIGHT_DEFAULT);

        // Deceased
        deceasedArr = new BufferedImage[5][6];
        BufferedImage tempDeceased = LoadSave.GetSpriteAtlas(LoadSave.DECEASED_SPRITES);
        for (int j = 0; j < deceasedArr.length; j++)
            for (int i = 0; i < deceasedArr[j].length; i++)
                deceasedArr[j][i] = tempDeceased.getSubimage(
                        i * DECEASED_WIDTH_DEFAULT, j * DECEASED_HEIGHT_DEFAULT,
                        DECEASED_WIDTH_DEFAULT, DECEASED_HEIGHT_DEFAULT);

        ballImg = LoadSave.GetSpriteAtlas(LoadSave.DECEASED_BALL);

        // EgyptianArcher
        egyptianArcherArr = new BufferedImage[5][6];
        BufferedImage tempEgyptianArcher = LoadSave.GetSpriteAtlas(LoadSave.EGYPTIANARCHER_SPRITES);
        for (int j = 0; j < egyptianArcherArr.length; j++)
            for (int i = 0; i < egyptianArcherArr[j].length; i++)
                egyptianArcherArr[j][i] = tempEgyptianArcher.getSubimage(
                        i * EGYPTIANARCHER_WIDTH_DEFAULT, j * EGYPTIANARCHER_HEIGHT_DEFAULT,
                        EGYPTIANARCHER_WIDTH_DEFAULT, EGYPTIANARCHER_HEIGHT_DEFAULT);

        arrowImg = LoadSave.GetSpriteAtlas(LoadSave.EGYPTIANARCHER_ARROW);

        // Anubis
        // Mapeo: IDLE(6), WALK/RUN(8), ATTACK_1 corta(2), ATTACK_2 media(1), RANGED larga(3), HURT(5), DEAD(4), SNEER(7)
        int[] anubisStateToRow = { 6, 8, 2, 1, 3, 5, 4, 7 };
        anubisArr = new BufferedImage[8][6];
        BufferedImage tempAnubis = LoadSave.GetSpriteAtlas(LoadSave.ANUBIS_SPRITES);
        for (int state = 0; state < 8; state++) {
            int frames = GetSpriteAmount(ANUBIS, state);
            for (int i = 0; i < frames; i++)
                anubisArr[state][i] = tempAnubis.getSubimage(
                        i * ANUBIS_WIDTH_DEFAULT,
                        anubisStateToRow[state] * ANUBIS_HEIGHT_DEFAULT,
                        ANUBIS_WIDTH_DEFAULT, ANUBIS_HEIGHT_DEFAULT);
        }
        anubisBallImg = LoadSave.GetSpriteAtlas(LoadSave.ANUBIS_BALL);

        // EgyptianFighter — 48x48, 5 filas x 6 frames
        egyptianFighterArr = new BufferedImage[5][6];
        BufferedImage tempFighter = LoadSave.GetSpriteAtlas(LoadSave.WARRIOR_SPRITES);
        if (tempFighter != null)
            for (int j = 0; j < 5; j++)
                for (int i = 0; i < 6; i++)
                    egyptianFighterArr[j][i] = tempFighter.getSubimage(
                            i * EGYPTIANFIGHTER_WIDTH_DEFAULT, j * EGYPTIANFIGHTER_HEIGHT_DEFAULT,
                            EGYPTIANFIGHTER_WIDTH_DEFAULT, EGYPTIANFIGHTER_HEIGHT_DEFAULT);

        // Faraon — 48x48, 6 filas x 6 frames
        faraonArr = new BufferedImage[6][6];
        BufferedImage tempFaraon = LoadSave.GetSpriteAtlas(LoadSave.FARAON_SPRITES);
        if (tempFaraon != null)
            for (int j = 0; j < 6; j++)
                for (int i = 0; i < 6; i++)
                    faraonArr[j][i] = tempFaraon.getSubimage(
                            i * FARAON_WIDTH_DEFAULT, j * FARAON_HEIGHT_DEFAULT,
                            FARAON_WIDTH_DEFAULT, FARAON_HEIGHT_DEFAULT);
        faraonBallImg = LoadSave.GetSpriteAtlas(LoadSave.FARAON_BALL);

        // Manticora Boss — 72x72, 9 filas x 6 frames max
        int[] manticoraStateToRow = Manticora.STATE_TO_ROW;
        manticoraArr = new BufferedImage[9][6];
        BufferedImage tempManticora = LoadSave.GetSpriteAtlas(LoadSave.MANTICORA_SPRITES);
        if (tempManticora != null)
            for (int state = 0; state < 9; state++) {
                int frames = GetSpriteAmount(MANTICORA, state);
                int row    = manticoraStateToRow[state];
                for (int i = 0; i < frames; i++)
                    manticoraArr[state][i] = tempManticora.getSubimage(
                            i * MANTICORA_WIDTH_DEFAULT, row * MANTICORA_HEIGHT_DEFAULT,
                            MANTICORA_WIDTH_DEFAULT, MANTICORA_HEIGHT_DEFAULT);
            }
         manticoraBallImg = LoadSave.GetSpriteAtlas(LoadSave.MANTICORA_BALL);




        //Nivel 3
        cabArr = new BufferedImage[6][9];
        BufferedImage tempCab = LoadSave.GetSpriteAtlas(LoadSave.ARMADURA_SPRITES);
        for (int j = 0; j < cabArr.length; j++) {
            for (int i = 0; i < cabArr[j].length; i++) {
                cabArr[j][i] = tempCab.getSubimage(i * ARMAD_WIDTH_DEFAULT, j * ARMAD_HEIGHT_DEFAULT, ARMAD_WIDTH_DEFAULT, ARMAD_HEIGHT_DEFAULT);
            }
        }

        atcherArr = new BufferedImage[6][10];
        BufferedImage tempAtcher = LoadSave.GetSpriteAtlas(LoadSave.ATCHER_SPRITES);
        for (int j = 0; j < atcherArr.length; j++) {
            for (int i = 0; i < atcherArr[j].length; i++) {
                atcherArr[j][i] = tempAtcher.getSubimage(i * ATCHER_WIDTH_DEFAULT, j * ATCHER_HEIGHT_DEFAULT, ATCHER_WIDTH_DEFAULT, ATCHER_HEIGHT_DEFAULT);
            }
        }

        ratArr = new BufferedImage[5][8];
        BufferedImage tempRat = LoadSave.GetSpriteAtlas(LoadSave.RAT_SPRITES);
        for (int j = 0; j < ratArr.length; j++) {
            for (int i = 0; i < ratArr[j].length; i++) {
                ratArr[j][i] = tempRat.getSubimage(i * RAT_WIDTH_DEFAULT, j * RAT_HEIGHT_DEFAULT, RAT_WIDTH_DEFAULT, RAT_HEIGHT_DEFAULT); 
            }
        }

        hyenaArr = new BufferedImage[5][6];//
        BufferedImage tempHyena = LoadSave.GetSpriteAtlas(LoadSave.HYENA_SPRITES);
        for (int j = 0; j < hyenaArr.length; j++) {
            for (int i = 0; i < hyenaArr[j].length; i++) {
                hyenaArr[j][i] = tempHyena.getSubimage(i * HYENA_WIDTH_DEFAULT, j * HYENA_HEIGHT_DEFAULT, HYENA_WIDTH_DEFAULT, HYENA_HEIGHT_DEFAULT);
            }
        }
        
        batArr = new BufferedImage[5][10];//
        BufferedImage tempBat = LoadSave.GetSpriteAtlas(LoadSave.BAT_SPRITES);
        for (int j = 0; j < batArr.length; j++) {
            for (int i = 0; i < batArr[j].length; i++) {
                batArr[j][i] = tempBat.getSubimage(i * BAT_WIDTH_DEFAULT, j * BAT_HEIGHT_DEFAULT, BAT_WIDTH_DEFAULT, BAT_HEIGHT_DEFAULT);
            }
        }

        magoArr = new BufferedImage[5][8];
        BufferedImage tempMago = LoadSave.GetSpriteAtlas(LoadSave. MAGO_SPRITES);
        for (int j = 0; j < magoArr.length; j++) {
            for (int i = 0; i < magoArr[j].length; i++) {
                magoArr[j][i] = tempMago.getSubimage(i * MAGO_WIDTH_DEFAULT, j * MAGO_HEIGHT_DEFAULT, MAGO_WIDTH_DEFAULT, MAGO_HEIGHT_DEFAULT);
            }
        }

        //Nivel 4
        weaponImg = LoadSave.GetSpriteAtlas(LoadSave.WEAPON_SPRITES);

        dogArr = new BufferedImage[6][12];
        BufferedImage tempDog = LoadSave.GetSpriteAtlas(LoadSave.DOG_SPRITES);
        for (int j = 0; j < dogArr.length; j++) {
            for (int i = 0; i < dogArr[j].length; i++) {
                dogArr[j][i] = tempDog.getSubimage(i * DOG_WIDTH_DEFAULT, j * DOG_HEIGHT_DEFAULT, DOG_WIDTH_DEFAULT, DOG_HEIGHT_DEFAULT);
            }
        }

        spiderArr = new BufferedImage[6][11];
        BufferedImage tempSpider = LoadSave.GetSpriteAtlas(LoadSave.SPIDER_SPRITES);
        for (int j = 0; j < spiderArr.length; j++) {
            for (int i = 0; i < spiderArr[j].length; i++) {
                spiderArr[j][i] = tempSpider.getSubimage(i * SPIDER_WIDTH_DEFAULT, j * SPIDER_HEIGHT_DEFAULT, SPIDER_WIDTH_DEFAULT, SPIDER_HEIGHT_DEFAULT);
            }
        }

        torretArr = new BufferedImage[4][5]; 

        BufferedImage tempTorret = LoadSave.GetSpriteAtlas(LoadSave.TORRET_SPRITES);

        for (int j = 0; j < torretArr.length; j++) {
            for (int i = 0; i < torretArr[j].length; i++) {
                torretArr[j][i] = tempTorret.getSubimage(
                    i * 64,
                    j * 32,
                    64,
                    32
                );
            }
        }
        
        dronArr = new BufferedImage[5][12];
        BufferedImage tempDron = LoadSave.GetSpriteAtlas(LoadSave.DRON_SPRITES);
        for (int j = 0; j < dronArr.length; j++) {
            for (int i = 0; i < dronArr[j].length; i++) {
                dronArr[j][i] = tempDron.getSubimage(i * DRON_WIDTH_DEFAULT, j * DRON_HEIGHT_DEFAULT, DRON_WIDTH_DEFAULT, DRON_HEIGHT_DEFAULT);
            }
        }

        boss4Arr = new BufferedImage[6][14];
        BufferedImage tempBoss4 = LoadSave.GetSpriteAtlas(LoadSave.BOSS_SPRITES4);
        for (int j = 0; j < boss4Arr.length; j++) {
            for (int i = 0; i < boss4Arr[j].length; i++) {
                boss4Arr[j][i] = tempBoss4.getSubimage(i * BOSS4_WIDTH_DEFAULT, j * BOSS4_HEIGHT_DEFAULT, BOSS4_WIDTH_DEFAULT, BOSS4_HEIGHT_DEFAULT);
            }
        }

        ninjaArr = new BufferedImage[5][14];
        BufferedImage tempNinja = LoadSave.GetSpriteAtlas(LoadSave.NINJA_SPRITES);
        for (int j = 0; j < ninjaArr.length; j++) {
            for (int i = 0; i < ninjaArr[j].length; i++) {
                ninjaArr[j][i] = tempNinja.getSubimage(i * NINJA_WIDTH_DEFAULT, j * NINJA_HEIGHT_DEFAULT, NINJA_WIDTH_DEFAULT, NINJA_HEIGHT_DEFAULT);
            }
        }

        trapArr = new BufferedImage[1][1];
        BufferedImage tempTrap = LoadSave.GetSpriteAtlas(LoadSave.TRAP_SPRITES);
        for (int j = 0; j < trapArr.length; j++) {
            for (int i = 0; i < trapArr[j].length; i++) {
                trapArr[j][i] = tempTrap.getSubimage(0, 0, 32, 32);
            }
        }

        trap2Arr = new BufferedImage[1][10];
        BufferedImage tempTrap2 = LoadSave.GetSpriteAtlas(LoadSave.TRAP2_SPRITE);
        for (int i = 0; i < trap2Arr[0].length; i++) {
            trap2Arr[0][i] = tempTrap2.getSubimage(
                i * 32,
                0,
                32,
                32
            );
        }

        doorArr = new BufferedImage[1][12];
        BufferedImage tempDoor = LoadSave.GetSpriteAtlas(LoadSave.DOOR_SPRITES);
        for (int i = 0; i < 12; i++) {
            doorArr[0][i] = tempDoor.getSubimage(i * 32, 0, 32, 32);
        }

        buttonArr = new BufferedImage[1][3];
        BufferedImage tempButton = LoadSave.GetSpriteAtlas(LoadSave.BUTTON_SPRITES);

        for (int i = 0; i < 3; i++) {
            buttonArr[0][i] = tempButton.getSubimage(i * 32, 0, 32, 32);
        }

        bulletArr = new BufferedImage[4];
        BufferedImage tempBullet = LoadSave.GetSpriteAtlas(LoadSave.BULLET_SPRITES);
        for (int i = 0; i < 4; i++)
        bulletArr[i] = tempBullet.getSubimage(i * 32, 0, 32, 32);

        torretBulletArr = new BufferedImage[12]; 
        BufferedImage tempTorretBullet = LoadSave.GetSpriteAtlas(LoadSave.SHOOT_TORRET_SPRITES);
        for (int i = 0; i < 12; i++) {
            torretBulletArr[i] = tempTorretBullet.getSubimage(i * 32, 0, 32, 32);
        }


    }

   private void drawAnubis(Graphics g, int xlvlOffset) {
        for (Anubis a : anubis) {
            if (!a.isAlive())
                continue;
            int state = a.getEnemyState();
            int animIdx = a.getAnimIndex();
            if (animIdx >= anubisArr[state].length)
                animIdx = 0;

            
            int xDrawOffset = (int) (40 * Juego.SCALE);
            int yDrawOffset = (int) (45 * Juego.SCALE);

            g.drawImage(anubisArr[state][animIdx],
                    (int) a.getHitbox().x - xlvlOffset - xDrawOffset + a.flipX(),
                    (int) a.getHitbox().y - yDrawOffset,
                    ANUBIS_WIDTH * a.flipW(), ANUBIS_HEIGHT, null);

            if (a.isBallActive()) {
                Rectangle2D.Float bh = a.getBallHitbox();
                g.drawImage(anubisBallImg,
                        (int) bh.x - xlvlOffset,
                        (int) bh.y,
                        (int) (14 * Juego.SCALE),
                        (int) (14 * Juego.SCALE), null);
            }
            //a.drawHitbox(g, xlvlOffset);
            //a.drawAttackBox(g, xlvlOffset);
        }
    }

    public void resetAllEnemies() {
        for(Velociraptor r : raptors)
            r.resetEnemy();

        for(Indio i : indios)
            i.resetEnemy();

        for(Planta p : plantas)
            p.resetEnemy();

        for(Mosco m : moscos)
            m.resetEnemy();

        for(Flyer f : flyers)
            f.resetEnemy();

        for(Boss b : boss)
            b.resetEnemy();
        //Nivel 2
        for (Snake s : snake)
            s.resetEnemy();
        for (Scorpio sc : scorpio)
            sc.resetEnemy();
        for (Mummy m : mummy)
            m.resetEnemy();
        for (Deceased d : deceased)
            d.resetEnemy();
        for (EgyptianArcher archer : egyptianArcher)
            archer.resetEnemy();
        for (Anubis a : anubis)
            a.resetEnemy();
        for (EgyptianFighter ef : egyptianFighter)
            ef.resetEnemy();
        for (Faraon f : faraon)
            f.resetEnemy();
        for (Manticora m : manticora)
            m.resetEnemy();


        //Nivel 3
        for(Armado r : armados)
            r.resetEnemy();

        for(Atcher i : atchers)
            i.resetEnemy();

        for(Rata p : rats)
            p.resetEnemy();

        for(Hyena m : hyenas)
            m.resetEnemy();

        for(Bat f : bats)
            f.resetEnemy();

        for(Mago b : magos)
            b.resetEnemy();

        //Nivel 4
        for(Dog r : dogs)
            r.resetEnemy();

        for(Spider i : spiders)
            i.resetEnemy();

        for(Torret t : torrets)
            t.resetEnemy();

        for(Dron f : drons)
            f.resetEnemy();

        for(Boss b : boss)
            b.resetEnemy();

        for(Ninja n : ninjas)
        n.resetEnemy();

        if (playing.getLevelManager().getNivelActual() == 3)
            weapons = LoadSave.GetWeapons();
        else
            weapons = new ArrayList<>();
    }

    public boolean checkEnemyHitNivel0(Rectangle2D.Float attackBox) {
        for (Velociraptor r : raptors)
            if (r.isAlive() && attackBox.intersects(r.getHitbox())) { r.hurt(15); return true; }
        for (Planta p : plantas)
            if (p.isAlive() && attackBox.intersects(p.getHitbox())) { p.hurt(15); return true; }
        for (Indio i : indios)
            if (i.isAlive() && attackBox.intersects(i.getHitbox())) { i.hurt(15); return true; }
        for (Mosco m : moscos)
            if (m.isAlive() && attackBox.intersects(m.getHitbox())) { m.hurt(15); return true; }
        for (Flyer f : flyers)
            if (f.isAlive() && attackBox.intersects(f.getHitbox())) { f.hurt(15); return true; }
        for (Boss b : boss) {
            if (b.isAlive()) {
                Rectangle2D.Float ws = b.getWeakSpot();
                if (attackBox.intersects(b.getHitbox()) ||
                        (ws != null && attackBox.intersects(ws))) {
                    b.hurtIfWeakSpotHit(attackBox, 15);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkEnemyHitMundo3(Rectangle2D.Float attackBox) {
        for (Armado a : armados)
            if (a.isAlive() && attackBox.intersects(a.getHitbox())) { a.hurt(10); return true; }
        for (Atcher at : atchers)
            if (at.isAlive() && attackBox.intersects(at.getHitbox())) { at.hurt(10); return true; }
        for (Rata r : rats)
            if (r.isAlive() && attackBox.intersects(r.getHitbox())) { r.hurt(10); return true; }
        for (Hyena h : hyenas)
            if (h.isAlive() && attackBox.intersects(h.getHitbox())) { h.hurt(10); return true; }
        for (Bat b : bats)
            if (b.isAlive() && attackBox.intersects(b.getHitbox())) { b.hurt(10); return true; }
        for (Mago m : magos)
            if (m.isAlive() && attackBox.intersects(m.getHitbox())) { m.hurt(10); return true; }
        return false;
    }

    public boolean checkEnemyHitAndReturn(Rectangle2D.Float attackBox) {
        for (Dog r : dogs) {
            if (r.isAlive() && attackBox.intersects(r.getHitbox())) {
                r.hurt(5);
                return true; 
            }
        }
        for (Torret t : torrets) {
            if (t.isAlive() && attackBox.intersects(t.getHitbox())) {
                t.hurt(5);
                return true;
            }
        }
        for (Spider i : spiders) {
            if (i.isAlive() && attackBox.intersects(i.getHitbox())) {
                i.hurt(5);
                return true;
            }
        }
        for (Dron d : drons) {
        if (d.isAlive()) {
            boolean hit = attackBox.intersects(d.getHitbox());
            if (hit) {
                d.hurt(5); return true;
            }
        }
    }
        for (Boss4 b : boss4) {
            if (b.isAlive() && attackBox.intersects(b.getHitbox())) {
                b.hurt(3);
                return true;
            }
        }
        for (Ninja n : ninjas) {
            if (n.isAlive() && attackBox.intersects(n.getHitbox())) {
                n.hurt(5);
                return true;
            }
        }
        return false;
    }

}
