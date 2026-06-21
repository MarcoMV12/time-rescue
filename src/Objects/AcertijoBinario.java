package Objects;

import Elementos.Jugador2;
import Juegos.Juego;
import utilz.AudioPlayer;
import utilz.ScoreManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;

public class AcertijoBinario {

    private ArrayList<BloqueBinario> bloques = new ArrayList<>();
    private int valorObjetivo;
    private boolean resuelto = false;
    private boolean tutorialActivo = false;
    private boolean tutorialMostrado = false;
    private boolean enTumba = false;
    private boolean estabaEnTumba = false;
    private BufferedImage tutorialImg;
    private BufferedImage letreroImg;
    private Random random = new Random();

    private static final int[] VALORES = {16, 8, 4, 2, 1};
    private static final int BOTON_X = 116;
    private static final int BOTON_Y = 11;
    
    private static final int LETRERO_TILE_X = 124; // Coordenada X del mapa donde anclar el letrero
    private static final int LETRERO_TILE_Y = 0;   // Coordenada Y del mapa donde anclar el letrero
    private static final int LETRERO_OFFSET_Y = 45; // Aumenta este número para subir más el letrero
    private static final int TEXTO_OFFSET_Y = 45; // Aumenta este número para bajar el texto del código

    private Rectangle2D.Float botonConfirmar;
    private boolean puedeConfirmar = true;
    private String mensajeEstado = "";
    private int mensajeTimer = 0;

    public void cargarBloquesDesdeNivel(int[][] lvlData) {
        bloques.clear();
        int contador = 0;

        if (tutorialImg == null) {
            try {
                InputStream is1 = getClass().getResourceAsStream("/res/CodigoExplicacion.png");
                if (is1 != null) tutorialImg = ImageIO.read(is1);
                
                InputStream is2 = getClass().getResourceAsStream("/res/CodigoLetrero.png");
                if (is2 != null) letreroImg = ImageIO.read(is2);
                
                if (tutorialImg == null || letreroImg == null) System.out.println("ADVERTENCIA: No se encontraron las imagenes en /res/");
            } catch (Exception e) {
                System.out.println("Error cargando imagenes del acertijo: " + e.getMessage());
            }
        }

        // CREACIÓN FORZADA DEL BOTÓN
        // Instanciamos el botón directamente, sin importar qué ID de tile haya en tu mapa.
        botonConfirmar = new Rectangle2D.Float(BOTON_X * Juego.TILES_SIZE, BOTON_Y * Juego.TILES_SIZE, Juego.TILES_SIZE, Juego.TILES_SIZE);

        for (int j = 0; j < lvlData.length; j++) {
            for (int i = 0; i < lvlData[j].length; i++) {
                if (lvlData[j][i] == 56) {
                    float bx = i * Juego.TILES_SIZE;
                    float by = j * Juego.TILES_SIZE;

                    // Omitimos esta coordenada por si de casualidad pusiste un 56 ahí, para que no cuente como bloque sumable
                    if (i == BOTON_X && j == BOTON_Y) continue;

                    if (contador < 5) {
                        bloques.add(new BloqueBinario(bx, by, VALORES[contador]));
                        contador++;
                    }
                }
            }
        }
        generarNuevoReto();
    }

    public void generarNuevoReto() {
        valorObjetivo = random.nextInt(31) + 1;
        resuelto = false;
        for (BloqueBinario b : bloques) {
            b.setActiva(false);
            b.setGolpeado(false);
        }
    }

    public void update(Jugador2 jugador) {
        estabaEnTumba = enTumba;
        enTumba = (jugador.getHitbox().x >= (BOTON_X - 2) * Juego.TILES_SIZE);

        if (enTumba && !estabaEnTumba) {
            AudioPlayer.stopMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC);
            AudioPlayer.playMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC, AudioPlayer.MUSIC_VOLUME);
        } else if (!enTumba && estabaEnTumba) {
            AudioPlayer.stopMusic(AudioPlayer.Sound.TUMBA_BOSS_MUSIC);
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
        }

        // Comentamos esto para que el tutorial NO se resetee al volver a entrar a la tumba.
        // Así solo se mostrará una única vez.
        // if (enTumba && !estabaEnTumba) {
        //     tutorialMostrado = false;
        // }

        if (!tutorialMostrado && enTumba) {
            tutorialActivo = true;
            tutorialMostrado = true;
        }

        if (tutorialActivo) return;

        if (mensajeTimer > 0) {
            mensajeTimer--;
        }

        if (resuelto)
            return;

        Rectangle2D.Float ph = jugador.getHitbox();

        for (BloqueBinario b : bloques) {
            Rectangle2D.Float bh = b.getHitbox();

            // Evaluamos rango para saltarnos la corrección de la física del motor
            boolean enRangoX = (ph.x + ph.width > bh.x) && (ph.x < bh.x + bh.width);
            boolean golpeandoAbajo = (ph.y >= bh.y + bh.height - 15) && (ph.y <= bh.y + bh.height + 15);

            if (enRangoX && golpeandoAbajo) {
                if (!b.isGolpeado()) {
                    b.golpear();
                    b.setGolpeado(true);
                }
            } else if (!enRangoX || ph.y > bh.y + bh.height + 5) {
                b.setGolpeado(false);
            }
        }

        if (botonConfirmar != null) {
            Rectangle2D.Float sensorBoton = new Rectangle2D.Float(
                    botonConfirmar.x - 20, botonConfirmar.y - 20,
                    botonConfirmar.width + 40, botonConfirmar.height + 40
            );
            if (ph.intersects(sensorBoton)) {
                if (jugador.isInteract() && puedeConfirmar) {
                    comprobarRespuesta(jugador);
                    puedeConfirmar = false;
                }
            }
        }

        if (!jugador.isInteract()) {
            puedeConfirmar = true;
        }
    }

    private void comprobarRespuesta(Jugador2 jugador) {
        int suma = 0;
        for (BloqueBinario b : bloques) {
            if (b.isActiva()) suma += b.getValor();
        }

        if (suma == valorObjetivo) {
            resuelto = true;
            System.out.println("CODIGO CORRECTO");
            mensajeEstado = "¡CODIGO CORRECTO!";
            mensajeTimer = 180;
            ScoreManager.getInstance().addXP(ScoreManager.XP_ACERTIJO);
            // HOOK: abrir puerta
            // HOOK: dar recompensa
            // HOOK: activar animacion de exito
        } else {
            System.out.println("CODIGO INCORRECTO");
            mensajeEstado = "¡INCORRECTO!";
            mensajeTimer = 120;
            jugador.changeHealth(-20);
            generarNuevoReto();
        }
    }

    public void draw(Graphics g, int lvlOffset) {
        for (BloqueBinario b : bloques) {
            b.draw(g, lvlOffset);
        }

        if (botonConfirmar != null) {
            int bx = (int) (botonConfirmar.x - lvlOffset);
            int by = (int) botonConfirmar.y;

            Graphics2D g2d = (Graphics2D) g;
            
            // Dibujar un borde resaltado y fondo semitransparente para diferenciarlo
            g2d.setColor(new Color(0, 150, 255, 100)); 
            g2d.fillRect(bx, by, Juego.TILES_SIZE, Juego.TILES_SIZE);
            
            g2d.setStroke(new java.awt.BasicStroke(2));
            g2d.setColor(Color.CYAN);
            g2d.drawRect(bx, by, Juego.TILES_SIZE, Juego.TILES_SIZE);
            g2d.setStroke(new java.awt.BasicStroke(1));

            g.setFont(new Font("Arial", Font.BOLD, 12));
            
            g.setColor(Color.BLACK);
            g.drawString("(E)", bx + Juego.TILES_SIZE / 2 - 7, by + Juego.TILES_SIZE / 2 + 6);
            
            g.setColor(Color.WHITE);
            g.drawString("(E)", bx + Juego.TILES_SIZE / 2 - 8, by + Juego.TILES_SIZE / 2 + 5);
        }

        if (enTumba) {
            // Calculamos la posición en el mundo usando el tamaño del Tile y restando el lvlOffset
            int letX = (int)(LETRERO_TILE_X * Juego.TILES_SIZE) - lvlOffset;
            // Restamos LETRERO_OFFSET_Y para empujar el letrero hacia arriba
            int letY = (int)(LETRERO_TILE_Y * Juego.TILES_SIZE) - LETRERO_OFFSET_Y;

            if (letreroImg != null) {
                g.drawImage(letreroImg, letX, letY, null);

                int numY = letY + letreroImg.getHeight() + (int)(2 * Juego.SCALE) + TEXTO_OFFSET_Y;
                g.setColor(new Color(101, 55, 0)); // café
                g.setFont(new Font("Arial", Font.BOLD, 22));
                String textoObj = String.valueOf(valorObjetivo);
                FontMetrics fm = g.getFontMetrics();
                int textX = letX + (letreroImg.getWidth() - fm.stringWidth(textoObj)) / 2;
                g.drawString(textoObj, textX, numY + fm.getAscent());
            }
        }

    }

    public void drawOverlay(Graphics g) {
        if (mensajeTimer > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(mensajeEstado.equals("¡CODIGO CORRECTO!") ? Color.GREEN : Color.RED);
            FontMetrics fmMsg = g.getFontMetrics();
            int msgX = (Juego.GAME_WIDTH - fmMsg.stringWidth(mensajeEstado)) / 2;
            g.drawString(mensajeEstado, msgX, Juego.GAME_HEIGHT / 2);
        }

        if (tutorialActivo) {
            dibujarTutorial(g);
        }
    }

    private void dibujarTutorial(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

        if (tutorialImg != null) {
            int imgX = 116; // Fijo a 116 px de ancho en la pantalla
            int imgY = (Juego.GAME_HEIGHT - tutorialImg.getHeight()) / 2;
            g2d.drawImage(tutorialImg, imgX, imgY, null);
        }

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String linea = "Presiona [ENTER] para comenzar";
        int lx = (Juego.GAME_WIDTH - g2d.getFontMetrics().stringWidth(linea)) / 2;
        g2d.drawString(linea, lx, Juego.GAME_HEIGHT - 30);
    }

    public boolean isResuelto() { return resuelto; }
    public boolean isTutorialActivo() { return tutorialActivo; }
    public void cerrarTutorial() { tutorialActivo = false; }

    public void resetOnDeath() {
        generarNuevoReto();
        tutorialMostrado = false;
        tutorialActivo   = false;
        enTumba          = false;
        estabaEnTumba    = false;
    }
}