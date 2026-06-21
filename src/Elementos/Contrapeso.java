package Elementos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import Juegos.Juego;
import utilz.MetodosAyuda;

public class Contrapeso extends Cascaron {

    public enum Tipo {
        BOLSA_ARENA ("Bolsa arena",  5f,  new Color(210, 180, 140)),
        BARRIL      ("Barril",      15f,  new Color(120,  70,  30)),
        CAJA_PIEDRA ("Caja piedra", 30f,  new Color(130, 130, 130)),
        ROCA_GRANDE ("Roca grande", 50f,  new Color( 80,  75,  70));

        public final String nombre;
        public final float  peso;
        public final Color  color;

        Tipo(String nombre, float peso, Color color) {
            this.nombre = nombre;
            this.peso   = peso;
            this.color  = color;
        }
    }

    private Tipo tipo;
    private boolean enMano = false;
    private boolean colocado = false;

    private final float spawnX, spawnY;  // posición original para poder resetearlo

    // Física simple para cuando se cae si el jugador lo suelta sin colocarlo
    private float velY = 0f;
    private static final float GRAV = 0.04f * Juego.SCALE;
    private boolean inAir = false;

    private static final int W = (int)(24 * Juego.SCALE);
    private static final int H = (int)(24 * Juego.SCALE);

    public Contrapeso(float x, float y, Tipo tipo) {
        super(x, y, W, H);
        this.tipo   = tipo;
        this.spawnX = x;
        this.spawnY = y;
        initHitbox(x, y, W, H);
    }

    public void reset() {
        colocado = false;
        enMano   = false;
        velY     = 0f;
        inAir    = false;
        hitbox.x = spawnX;
        hitbox.y = spawnY;
    }

    // Aplica gravedad cuando no está en mano ni colocado
    public void update(int[][] lvlData) {
        if (enMano || colocado) return;

        if (!MetodosAyuda.IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
            float sigY = hitbox.y + velY;
            if (MetodosAyuda.CanMoveHere(hitbox.x, sigY, W, H, lvlData)) {
                hitbox.y = sigY;
                velY += GRAV;
            } else {
                hitbox.y = MetodosAyuda.GetEntityYPosUnderRoofOrAboveFloor(hitbox, velY, lvlData);
                velY = 0f;
                inAir = false;
            }
        } else {
            velY = 0f;
            inAir = false;
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (colocado || enMano) return;

        int dx = (int)(hitbox.x - xLvlOffset);
        int dy = (int) hitbox.y;
        drawConCodigo(g, dx, dy);

        // Etiqueta de peso para que el jugador identifique cuál es cuál
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(6 * Juego.SCALE)));
        g.drawString(tipo.nombre + " " + (int)tipo.peso + "kg", dx - 4, dy - 5);

        //drawHitbox(g, xLvlOffset);
    }

    public void drawEnMano(Graphics g, float jugX, float jugY, int xLvlOffset) {
        int dx = (int)(jugX - xLvlOffset) - W / 4;
        int dy = (int) jugY - H - 6;
        drawConCodigo(g, dx, dy);
    }

    private void drawConCodigo(Graphics g, int dx, int dy) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (tipo) {
            case BOLSA_ARENA:
                g2d.setColor(tipo.color);
                g2d.fillOval(dx, dy + H / 4, W, (int)(H * 0.75f));
                g2d.setColor(tipo.color.darker());
                g2d.drawOval(dx, dy + H / 4, W, (int)(H * 0.75f));
                g2d.setColor(new Color(160, 120, 60));
                g2d.fillRect(dx + W / 3, dy + H / 4 - 3, W / 3, 5);
                break;

            case BARRIL:
                g2d.setColor(tipo.color);
                g2d.fillRoundRect(dx + 2, dy, W - 4, H, 6, 6);
                g2d.setColor(new Color(80, 80, 80));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(dx + 2, dy + H / 4, W - 4, 3, 2, 2);
                g2d.drawRoundRect(dx + 2, dy + H * 2 / 3, W - 4, 3, 2, 2);
                g2d.setStroke(new BasicStroke(1f));
                break;

            case CAJA_PIEDRA:
                g2d.setColor(tipo.color);
                g2d.fillRect(dx, dy, W, H);
                g2d.setColor(tipo.color.darker());
                g2d.drawRect(dx, dy, W, H);
                g2d.setColor(new Color(100, 100, 100));
                g2d.drawLine(dx, dy + H / 2, dx + W, dy + H / 2);
                g2d.drawLine(dx + W / 2, dy, dx + W / 2, dy + H / 2);
                g2d.drawLine(dx + W / 3, dy + H / 2, dx + W / 3, dy + H);
                break;

            case ROCA_GRANDE:
                int[] px = { dx + 4, dx, dx + 3, dx + W / 2,
                             dx + W, dx + W - 2, dx + W - 5, dx + W / 2 };
                int[] py = { dy + H / 3, dy + H * 2 / 3, dy + H,
                             dy + H - 2, dy + H, dy + H / 2,
                             dy + H / 4, dy };
                g2d.setColor(tipo.color);
                g2d.fillPolygon(px, py, px.length);
                g2d.setColor(tipo.color.darker());
                g2d.drawPolygon(px, py, px.length);
                break;
        }
    }

    public Tipo    getTipo()              { return tipo;     }
    public float   getPeso()              { return tipo.peso; }
    public boolean isEnMano()             { return enMano;   }
    public void    setEnMano(boolean e)   { enMano = e;      }
    public boolean isColocado()           { return colocado; }
    public void    setColocado(boolean c) { colocado = c;    }
}
