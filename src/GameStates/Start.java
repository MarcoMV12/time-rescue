package GameStates;

import utilz.AudioPlayer;
import utilz.ScoreManager;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import Juegos.Juego;
//import ui.startButton;
import utilz.LoadSave;

public class Start extends State implements StateMethods{
    //Botones
    //private startButton[] buttons = new startButton[3];

    //Fondo 
    private ImageIcon gifFondo;
    private final float OVERLAY_ALPHA = 0.55f;

    //Imágenes
    private BufferedImage imgTitulo;
    private BufferedImage imgTextoInicio;
    private float alphaTitulo = 0f;
    private long ultimoFade   = 0;

    //Parpadeo
    private boolean textoVisible   = true;
    private long ultimoParpadeo    = 0;
    private static final int INTERVALO_PARPADEO = 520;

    //Entrada de nombre
    private boolean inputtingName    = false;
    private final StringBuilder nicknameBuffer = new StringBuilder();


    public Start(Juego game) {
        super(game);
        //loadButtons();
        loadGif();
        loadImagenes();
    }

    //Carga de recursos 
    private void loadGif() {
        gifFondo = new ImageIcon(LoadSave.class.getResource("/res/" + LoadSave.GIF));
    }

    private void loadImagenes() {
        try { imgTitulo      = LoadSave.GetSpriteAtlas(LoadSave.TITLE); } 
        catch (Exception e) { imgTitulo = null; }

        try { imgTextoInicio = LoadSave.GetSpriteAtlas(LoadSave.START_TEXT); } 
        catch (Exception e) { imgTextoInicio = null; }
    }

    // private void loadButtons() {
    //     buttons[0] = new startButton((Juego.GAME_WIDTH / 2) - 80,  (int)(200 * Juego.SCALE), 0, gameState.PLAYING);
    //     buttons[1] = new startButton((Juego.GAME_WIDTH / 2) + 190, (int)(200 * Juego.SCALE), 1, gameState.START);
    //     buttons[2] = new startButton((Juego.GAME_WIDTH / 2) + 70,  (int)(300 * Juego.SCALE), 2, gameState.QUIT);
    // }

    // Update
    @Override
    public void update() {
        long ahora = System.currentTimeMillis();

        if (alphaTitulo < 1f && ahora - ultimoFade >= 30) {
            alphaTitulo = Math.min(1f, alphaTitulo + 0.03f);
            ultimoFade  = ahora;
        }

        if (ahora - ultimoParpadeo >= INTERVALO_PARPADEO) {
            textoVisible   = !textoVisible;
            ultimoParpadeo = ahora;
        }

        // for (startButton pb : buttons)
        //     pb.update();
    }

    // Draw
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = Juego.GAME_WIDTH;
        int h = Juego.GAME_HEIGHT;

        // GIF de fondo
        if (gifFondo != null)
            g2d.drawImage(gifFondo.getImage(), 0, 0, w, h, null);

        // Overlay oscuro
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OVERLAY_ALPHA));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, w, h);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Título con fade-in
        dibujarTitulo(g2d, w, h);

        // Texto parpadeante
        if (textoVisible)
            dibujarTextoInicio(g2d, w, h);

        // Tabla de puntuaciones
        dibujarTablaScores(g2d, w, h);

        // Overlay de entrada de nombre
        if (inputtingName)
            dibujarInputNombre(g2d, w, h);

        // Botones
        // for (startButton pb : buttons)
        //     pb.draw(g);
    }

    /** Dibuja la tabla de los mejores scores en la parte inferior de la pantalla. */
    private void dibujarTablaScores(Graphics2D g2d, int w, int h) {
        java.util.List<ScoreManager.ScoreEntry> scores = ScoreManager.getInstance().getHighScores();
        if (scores.isEmpty()) return;

        int rowH  = 18;
        int cols  = 4;
        int rows  = Math.min(scores.size(), 8);
        int tableW = 420;
        int tableH = (rows + 1) * rowH + 14;
        int tx    = w - tableW - 270;
        int ty    = (int)(h * 0.34f) + 90;

        // Fondo
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRoundRect(tx - 6, ty - rowH - 4, tableW + 12, tableH, 10, 10);

        // Título de la tabla
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("MEJORES PUNTUACIONES", tx + tableW / 2 - 90, ty - 4);

        // Cabecera
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        g2d.setColor(new Color(180, 180, 255));
        int[] colX = { tx, tx + 120, tx + 210, tx + 295 };
        String[] headers = { "Jugador", "Monedas", "XP", "Fecha" };
        for (int c = 0; c < cols; c++)
            g2d.drawString(headers[c], colX[c], ty + rowH / 2);

        // Filas
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        for (int i = 0; i < rows; i++) {
            ScoreManager.ScoreEntry e = scores.get(i);
            int ry = ty + (i + 1) * rowH + rowH / 2 + 4;
            // Fila alternada
            if (i % 2 == 0) {
                g2d.setColor(new Color(255, 255, 255, 18));
                g2d.fillRect(tx - 4, ry - rowH + 2, tableW + 8, rowH);
            }
            g2d.setColor(Color.WHITE);
            g2d.drawString((i + 1) + ". " + e.personaje,  colX[0], ry);
            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString(String.valueOf(e.monedas),      colX[1], ry);
            g2d.setColor(new Color(100, 220, 100));
            g2d.drawString(String.valueOf(e.xp),           colX[2], ry);
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawString(e.fecha,                        colX[3], ry);
        }
    }

    //Helpers de dibujo
    private void dibujarTitulo(Graphics2D g2d, int w, int h) {
        if (imgTitulo == null) return;

        int imgW = (int)(w * 0.55f);
        int imgH = (int)(imgTitulo.getHeight() * ((float) imgW / imgTitulo.getWidth()));
        int tx   = (w - imgW) / 2;
        int ty   = (int)(h * 0.12f) - 30;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTitulo));
        g2d.drawImage(imgTitulo, tx, ty, imgW, imgH, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void dibujarTextoInicio(Graphics2D g2d, int w, int h) {
        if (imgTextoInicio == null) return;

        int imgW = (int)(w * 0.40f);
        int imgH = (int)(imgTextoInicio.getHeight() * ((float) imgW / imgTextoInicio.getWidth()));
        int tx   = (w - imgW) / 2;
        int ty   = (int)(h * 0.80f);

        g2d.drawImage(imgTextoInicio, tx, ty, imgW, imgH, null);
    }

    //Eventos de mouse
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    //Eventos de teclado 
    @Override
    public void keyPressed(KeyEvent e) {
        if (!inputtingName) {
            // Primera tecla: activar modo ingreso de nombre
            inputtingName = true;
            return;
        }

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
            // Confirmar nombre y arrancar juego
            String nombre = nicknameBuffer.toString().trim();
            utilz.ScoreManager.getInstance().setNickname(nombre.isEmpty() ? "Jugador" : nombre);
            arrancarJuego();
        } else if (code == KeyEvent.VK_ESCAPE) {
            // Cancelar: usar nombre por defecto
            utilz.ScoreManager.getInstance().setNickname("Jugador");
            arrancarJuego();
        } else if (code == KeyEvent.VK_BACK_SPACE) {
            if (nicknameBuffer.length() > 0)
                nicknameBuffer.deleteCharAt(nicknameBuffer.length() - 1);
        } else {
            char c = e.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(c)
                    && nicknameBuffer.length() < 16) {
                nicknameBuffer.append(c);
            }
        }
    }

    private void arrancarJuego() {
        int nivel = game.getPlaying().getLevelManager().getNivelActual();
        if (nivel == 1) {
            AudioPlayer.playMusic(AudioPlayer.Sound.EGYPTIAN_MUSIC, AudioPlayer.MUSIC_VOLUME);
        } else {
            AudioPlayer.playMusic(AudioPlayer.Sound.AMBIENT, AudioPlayer.MUSIC_VOLUME);
        }
        gameState.state = gameState.PLAYING;
    }

    /** Reinicia el estado para que la pantalla de inicio vuelva al principio. */
    public void resetar() {
        inputtingName = false;
        nicknameBuffer.setLength(0);
        alphaTitulo    = 0f;
        ultimoFade     = 0;
        textoVisible   = true;
        ultimoParpadeo = 0;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    /** Dibuja el panel de ingreso de nombre del jugador. */
    private void dibujarInputNombre(Graphics2D g2d, int w, int h) {
        int boxW = 420, boxH = 130;
        int bx = (w - boxW) / 2;
        int by = (int)(h * 0.46f);

        // Fondo semitransparente
        g2d.setColor(new java.awt.Color(0, 0, 0, 190));
        g2d.fillRoundRect(bx, by, boxW, boxH, 16, 16);
        g2d.setColor(new java.awt.Color(255, 215, 0, 180));
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawRoundRect(bx, by, boxW, boxH, 16, 16);

        // Título
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        g2d.setColor(new java.awt.Color(255, 215, 0));
        String titulo = "Ingresa tu nombre:";
        int tw = g2d.getFontMetrics().stringWidth(titulo);
        g2d.drawString(titulo, bx + (boxW - tw) / 2, by + 32);

        // Campo de texto con cursor parpadeante
        String texto = nicknameBuffer.toString() + (textoVisible ? "|" : " ");
        g2d.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 20));
        g2d.setColor(java.awt.Color.WHITE);
        int textW = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, bx + (boxW - textW) / 2, by + 72);

        // Hint
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        g2d.setColor(new java.awt.Color(160, 160, 160));
        String hint = "[ Enter ] Confirmar   [ Esc ] Omitir";
        int hw = g2d.getFontMetrics().stringWidth(hint);
        g2d.drawString(hint, bx + (boxW - hw) / 2, by + 110);
    }
    //     for (startButton pb : buttons)
    //         pb.resetBools();
    // }

}
