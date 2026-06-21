package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import GameStates.State;
import GameStates.StateMethods;
import GameStates.gameState;
import Juegos.Juego;
import ui.startButton;
import utilz.LoadSave;
import utilz.ScoreManager;

public class pauseOverlay extends State implements StateMethods{

    private startButton[] buttons = new startButton[3];
    private BufferedImage backgroundImg;
    private int pauseX, pauseY, pauseWidth, pauseHeight;

    public pauseOverlay(Juego game) {
        super(game);
        loadButtons();
        loadBackground();
    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        pauseWidth = (int)(backgroundImg.getWidth() * Juego.SCALE);
        pauseHeight = (int)(backgroundImg.getHeight() * Juego.SCALE);
        pauseX = Juego.GAME_WIDTH / 2 - pauseWidth / 2;
        pauseY = (int)(85 * Juego.SCALE);
    }

    private void loadButtons() {
        buttons[0] = new startButton((Juego.GAME_WIDTH/2)-130, (int)(350 * Juego.SCALE), 0, gameState.PLAYING);
        buttons[1] = new startButton((Juego.GAME_WIDTH/2)-130, (int)(450 * Juego.SCALE), 1, gameState.START);
        buttons[2] = new startButton((Juego.GAME_WIDTH/2)-130, (int)(550 * Juego.SCALE), 2, gameState.QUIT);
    }

    @Override
    public void update() {
        for (startButton pb : buttons) {
            pb.update();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, pauseX, pauseY - 80, pauseWidth, pauseHeight, null);

        for (startButton pb : buttons) {
            pb.draw(g);
        }

        dibujarTablaScores((Graphics2D) g);
    }

    /** Tabla de puntuaciones en el overlay de pausa. */
    private void dibujarTablaScores(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        java.util.List<ScoreManager.ScoreEntry> scores =
                ScoreManager.getInstance().getHighScores();

        // Sesión actual (siempre visible)
        ScoreManager sm = ScoreManager.getInstance();
        int w = Juego.GAME_WIDTH;

        int rowH   = 16;
        int rows   = Math.min(scores.size(), 8);
        int tableW = 390;
        int tableH = (rows + 1) * rowH + 14;
        int tx     = w - tableW - 70;
        int ty     = (int)(Juego.GAME_HEIGHT * 0.12f) + rowH + 300;

        // ── Sesión actual ────────────────────────────────────────────────
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(tx - 6, ty - rowH - 4, tableW + 12, rowH + 14, 8, 8);
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("Monedas: " + sm.getMonedas() + "   XP: " + sm.getXP(),
                       tx + 4, ty + 4);
        ty += rowH + 14;

        if (scores.isEmpty()) return;

        // ── Tabla de records ─────────────────────────────────────────────
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(tx - 6, ty - rowH - 4, tableW + 12, tableH, 10, 10);

        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("MEJORES PUNTUACIONES", tx + tableW / 2 - 80, ty - 4);

        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        g2d.setColor(new Color(180, 180, 255));
        int[] colX = { tx, tx + 110, tx + 200, tx + 275 };
        String[] headers = { "Personaje", "Monedas", "XP", "Fecha" };
        for (int c = 0; c < headers.length; c++)
            g2d.drawString(headers[c], colX[c], ty + rowH / 2);

        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        for (int i = 0; i < rows; i++) {
            ScoreManager.ScoreEntry e = scores.get(i);
            int ry = ty + (i + 1) * rowH + rowH / 2 + 4;
            if (i % 2 == 0) {
                g2d.setColor(new Color(255, 255, 255, 18));
                g2d.fillRect(tx - 4, ry - rowH + 2, tableW + 8, rowH);
            }
            g2d.setColor(Color.WHITE);
            g2d.drawString((i + 1) + ". " + e.personaje, colX[0], ry);
            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString(String.valueOf(e.monedas),     colX[1], ry);
            g2d.setColor(new Color(100, 220, 100));
            g2d.drawString(String.valueOf(e.xp),          colX[2], ry);
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawString(e.fecha,                       colX[3], ry);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (startButton pb : buttons) {
            if (isIn(e, pb)) {
                pb.setMousePressed(true);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            startButton pb = buttons[i];
            if (isIn(e, pb) && pb.isMousePressed()) {
                switch (i) {
                    case 0 -> {
                        // Solo reanudar la partida donde se quedó, sin cambiar nada
                        gameState.state = gameState.PLAYING;
                    }
                    case 1 -> {
                        // Reiniciar el nivel actual y mostrar selector de personaje
                        int nivelActual = game.getPlaying().getLevelManager().getNivelActual();
                        game.getCharacterSelect().activarModoReinicio(nivelActual);
                        gameState.state = gameState.CHARACTER_SELECT;
                    }
                    case 2 -> {
                        // Reiniciar todo y volver a la pantalla de inicio; guardar score
                        utilz.ScoreManager sm = utilz.ScoreManager.getInstance();
                        sm.guardarPartida(sm.getNickname());
                        sm.resetSesion();
                        game.getCharacterSelect().desactivarModoReinicio();
                        game.getPlaying().reiniciarCompleto();
                        game.getStart().resetar();
                        gameState.state = gameState.START;
                    }
                }
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for (startButton pb : buttons) {
            pb.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (startButton pb : buttons) {
            pb.setMouseOver(false);
        }

        for (startButton pb : buttons) {
            if (isIn(e, pb)) {
                pb.setMouseOver(true);
                System.out.println("entra");
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameState.state = gameState.PLAYING;
            //System.out.println("entra");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
    }

}
