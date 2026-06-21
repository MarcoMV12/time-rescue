package Elementos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

import Juegos.Juego;
import utilz.LoadSave;

public class ItemNivel extends Cascaron {

    public enum Tipo {
        LLAVE       ("Llave",    new Color(255, 215,  0)),
        CRISTAL     ("Cristal",  new Color(100, 200, 255)),
        PERGAMINO   ("Pergamino",new Color(220, 200, 150));

        public final String nombre;
        public final Color  color;

        Tipo(String nombre, Color color) {
            this.nombre = nombre;
            this.color  = color;
        }
    }

    private Tipo    tipo;
    private boolean recogido = false;

    // Sprites (cargados una sola vez; null = dibujo geométrico de respaldo)
    private static BufferedImage imgLlave     = null;
    private static BufferedImage imgCristal   = null;
    private static BufferedImage imgPergamino = null;
    private static boolean spritesLoaded      = false;

    private static void cargarSprites() {
        if (spritesLoaded) return;
        spritesLoaded = true;
        imgLlave     = tryLoadSprite(LoadSave.ITEM_LLAVE);
        imgCristal   = tryLoadSprite(LoadSave.ITEM_CRISTAL);
        imgPergamino = tryLoadSprite(LoadSave.ITEM_PERGAMINO);
    }

    private static BufferedImage tryLoadSprite(String path) {
        try {
            InputStream is = ItemNivel.class.getResourceAsStream("/res/" + path);
            if (is == null) return null;
            return ImageIO.read(is);
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage getSpriteParaTipo() {
        switch (tipo) {
            case LLAVE:     return imgLlave;
            case CRISTAL:   return imgCristal;
            case PERGAMINO: return imgPergamino;
            default:        return null;
        }
    }

    // Animación de flotación
    private float flotacionOffset = 0f;
    private float flotacionDir    = 1f;
    private static final float FLOTACION_MAX = 5f;
    private static final float FLOTACION_VEL = 0.15f;

    private static final int ITEM_SIZE = (int)(14 * Juego.SCALE);

    public ItemNivel(float x, float y, Tipo tipo) {
        super(x, y, ITEM_SIZE, ITEM_SIZE);
        this.tipo = tipo;
        initHitbox(x, y, ITEM_SIZE, ITEM_SIZE);
        cargarSprites();
    }

    public void update() {
        if (recogido) return;

        flotacionOffset += FLOTACION_VEL * flotacionDir;
        if (flotacionOffset >  FLOTACION_MAX) flotacionDir = -1f;
        if (flotacionOffset < -FLOTACION_MAX) flotacionDir =  1f;

        hitbox.y = y + flotacionOffset;
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (recogido) return;

        int drawX = (int)(hitbox.x - xLvlOffset);
        int drawY = (int) hitbox.y;

        // Brillo de fondo
        g.setColor(new Color(
            tipo.color.getRed(),
            tipo.color.getGreen(),
            tipo.color.getBlue(), 80));
        g.fillOval(drawX - 3, drawY - 3, ITEM_SIZE + 6, ITEM_SIZE + 6);

        BufferedImage sprite = getSpriteParaTipo();
        if (sprite != null) {
            // Usa el .png si existe en res/items/
            g.drawImage(sprite, drawX, drawY, ITEM_SIZE, ITEM_SIZE, null);
        } else {
            // Dibujo geométrico de respaldo hasta que existan los .png
            g.setColor(tipo.color);
            g.fillRect(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
            g.setColor(tipo.color.darker());
            g.drawRect(drawX, drawY, ITEM_SIZE, ITEM_SIZE);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, (int)(7 * Juego.SCALE)));
            switch (tipo) {
                case LLAVE:     g.drawString("🗝", drawX + 2, drawY + ITEM_SIZE - 2); break;
                case CRISTAL:   g.drawString("◆",  drawX + 3, drawY + ITEM_SIZE - 2); break;
                case PERGAMINO: g.drawString("📜", drawX + 2, drawY + ITEM_SIZE - 2); break;
            }
        }

        // Nombre flotante
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, (int)(6 * Juego.SCALE)));
        g.drawString(tipo.nombre, drawX - 2, drawY - 4);
    }

    public boolean isRecogido()           { return recogido; }
    public void    setRecogido(boolean r) { this.recogido = r; }
    public Tipo    getTipo()              { return tipo; }
}
