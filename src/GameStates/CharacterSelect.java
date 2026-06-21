package GameStates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Elementos.Personaje;
import Juegos.Juego;
import utilz.LoadSave;

public class CharacterSelect extends State implements StateMethods {
    private final Personaje[] opciones = Personaje.values();
    private int seleccion = 0;
    private int hoverIndex = -1;
    private int personajesDisponibles = 2; // se actualiza al completar cada nivel

    private final int cardW = 180;
    private final int cardH = 240;
    private final int spacing = 45;
    private int startX, cardY;

    private final BufferedImage[] charImgs    = new BufferedImage[4];
    private final BufferedImage[] lockedImgs  = new BufferedImage[4]; // index 0 unused
    private BufferedImage bgImg;

    // Tamaño de la imagen de cada personaje dentro de su carta (ancho, alto)
    // Modificar estos valores para ajustar cada personaje por separado
    private final int[] imgW = { cardW - 10, cardW - 10, cardW - 10, cardW - 10 };
    private final int[] imgH = {        145,        160,        145,        145 };

    public CharacterSelect(Juego game) {
        super(game);
        int totalW = opciones.length * cardW + (opciones.length - 1) * spacing;
        startX = (Juego.GAME_WIDTH - totalW) / 2;
        cardY = (Juego.GAME_HEIGHT - cardH) / 2;

        bgImg = LoadSave.GetSpriteAtlas(LoadSave.CHAR_SELECT_BG);

        charImgs[0] = LoadSave.GetSpriteAtlas(LoadSave.CHAR_IMG_1);
        charImgs[1] = LoadSave.GetSpriteAtlas(LoadSave.CHAR_IMG_2);
        charImgs[2] = LoadSave.GetSpriteAtlas(LoadSave.CHAR_IMG_3);
        charImgs[3] = LoadSave.GetSpriteAtlas(LoadSave.CHAR_IMG_4);

        lockedImgs[1] = LoadSave.GetSpriteAtlas(LoadSave.NOT_CHAR_IMG_2);
        lockedImgs[2] = LoadSave.GetSpriteAtlas(LoadSave.NOT_CHAR_IMG_3);
        lockedImgs[3] = LoadSave.GetSpriteAtlas(LoadSave.NOT_CHAR_IMG_4);
    }

    // Llamado desde LevelCompleted antes de mostrar esta pantalla
    public void setNivelCompletado(int nivelCompletado) {
        // nivel 0 completado → 2 personajes, 1 → 3, 2 → 4
        personajesDisponibles = Math.min(nivelCompletado + 2, opciones.length);
        seleccion = Math.min(seleccion, personajesDisponibles - 1);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics g) {
        if (bgImg != null)
            g.drawImage(bgImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        else {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        }

        // g.setColor(Color.WHITE);
        // g.setFont(new Font("Arial", Font.BOLD, 32));
        // String titulo = "Elige tu personaje";
        // int tw = g.getFontMetrics().stringWidth(titulo);
        // g.drawString(titulo, (Juego.GAME_WIDTH - tw) / 2, 80);

        for (int i = 0; i < opciones.length; i++) {
            int x = startX + i * (cardW + spacing);
            if (i < personajesDisponibles)
                dibujarCarta(g, x+10, cardY + 130, opciones[i], i, i == hoverIndex);
            else
                dibujarCartaBloqueada(g, x, cardY + 90, i);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String hint = "Click para seleccionar — ENTER para confirmar";
        int hw = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (Juego.GAME_WIDTH - hw) / 2, Juego.GAME_HEIGHT - 40);
    }

    private void dibujarCarta(Graphics g, int x, int y, Personaje p, int idx, boolean hover) {
        // g.setColor(hover ? new Color(255, 220, 80, 80) : new Color(255, 255, 255, 30));
        // g.fillRect(x, y, cardW, cardH);
        // g.setColor(hover ? Color.YELLOW : Color.WHITE);
        // g.drawRect(x, y, cardW, cardH);

        // Imagen del personaje (parte superior de la carta)
        if (charImgs[idx] != null)
            g.drawImage(charImgs[idx], x + 5, y + 5, imgW[idx], imgH[idx], null);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString(p.name(), x + 20, y );

        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString("HP: "   + p.maxHp,     x + 10, y + 185);
        g.drawString("Vel: "  + p.velocidad, x + 10, y + 205);
        g.drawString("Daño: " + p.dano,      x + 10, y + 225);
    }

    private void dibujarCartaBloqueada(Graphics g, int x, int y, int idx) {
        // g.setColor(new Color(30, 30, 30, 220));
        // g.fillRect(x, y, cardW, cardH);
        // g.setColor(new Color(70, 70, 70));
        // g.drawRect(x, y, cardW, cardH);

        // Imagen bloqueada del personaje
        if (idx < lockedImgs.length && lockedImgs[idx] != null) {
            g.drawImage(lockedImgs[idx], x + 5, y + 5, cardW - 10, cardH - 10, null);
        } else {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(new Color(100, 100, 100));
            String q = "?";
            int qw = g.getFontMetrics().stringWidth(q);
            g.drawString(q, x + (cardW - qw) / 2, y + cardH / 2 - 10);

            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.setColor(new Color(100, 100, 100));
            String lock = "Bloqueado";
            int lw = g.getFontMetrics().stringWidth(lock);
            g.drawString(lock, x + (cardW - lw) / 2, y + cardH / 2 + 18);
        }
    }

    private boolean modoReinicio = false; // true = recargar nivel actual, false = avanzar al siguiente

    /** Llama a este método antes de cambiar a CHARACTER_SELECT para recargar el nivel actual. */
    public void activarModoReinicio(int nivelActual) {
        modoReinicio = true;
        // Personajes disponibles equivalentes al estado en que empezó ese nivel
        setNivelCompletado(Math.max(0, nivelActual - 1));
    }

    public void desactivarModoReinicio() {
        modoReinicio = false;
    }

    private void confirmar() {
        if (seleccion >= personajesDisponibles) return;
        Personaje.seleccionado = opciones[seleccion];
        if (modoReinicio) {
            modoReinicio = false;
            game.getPlaying().recargarNivelActual();
        } else {
            game.getPlaying().cargarSiguienteNivel();
        }
        gameState.state = gameState.PLAYING;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (hoverIndex >= 0 && hoverIndex < personajesDisponibles) {
            seleccion = hoverIndex;
            confirmar();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        hoverIndex = -1;
        for (int i = 0; i < personajesDisponibles; i++) {
            int x = startX + i * (cardW + spacing);
            if (e.getX() >= x && e.getX() <= x + cardW
             && e.getY() >= cardY + 130 && e.getY() <= cardY + 130 + cardH) {
                hoverIndex = i;
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                seleccion = Math.max(0, seleccion - 1);
                hoverIndex = seleccion;
                break;
            case KeyEvent.VK_RIGHT:
                seleccion = Math.min(personajesDisponibles - 1, seleccion + 1);
                hoverIndex = seleccion;
                break;
            case KeyEvent.VK_ENTER:
                confirmar();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
