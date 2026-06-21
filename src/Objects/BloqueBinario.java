package Objects;

import Juegos.Juego;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BloqueBinario {

    private float x;
    private float y;

    private int valor;

    private boolean activa = false;

    private boolean golpeado = false;

    private Rectangle2D.Float hitbox;

    public BloqueBinario(float x,
                         float y,
                         int valor) {

        this.x = x;
        this.y = y;

        this.valor = valor;

        hitbox = new Rectangle2D.Float(
                x,
                y,
                Juego.TILES_SIZE,
                Juego.TILES_SIZE
        );
    }

    public void draw(Graphics g,
                     int lvlOffset) {

        Graphics2D g2d =
                (Graphics2D) g;

        int drawX =
                (int)(x - lvlOffset);

        int drawY =
                (int)(activa ? y - 4 : y); // Ligero rebote visual al estar activo

        String texto =
                activa ? "1" : "0";

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm =
                g2d.getFontMetrics();

        int textX =
                drawX + (Juego.TILES_SIZE - fm.stringWidth(texto)) / 2;

        int textY =
                drawY + ((Juego.TILES_SIZE + fm.getAscent()) / 2) - 3;

        // Sombra para que destaque sobre la textura del mapa
        g2d.setColor(Color.BLACK);
        g2d.drawString(texto, textX + 2, textY + 2);

        // Color del texto
        g2d.setColor(activa ? Color.CYAN : Color.WHITE);
        g2d.drawString(texto, textX, textY);
    }

    public void golpear() {

        activa = !activa;
    }

    public Rectangle2D.Float getHitbox() {

        return hitbox;
    }

    public boolean isActiva() {

        return activa;
    }

    public void setActiva(boolean activa) {

        this.activa = activa;
    }

    public int getValor() {

        return valor;
    }

    public boolean isGolpeado() {

        return golpeado;
    }

    public void setGolpeado(
            boolean golpeado) {

        this.golpeado = golpeado;
    }
}