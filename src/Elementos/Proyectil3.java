package Elementos;

import java.awt.geom.Rectangle2D;
import Juegos.Juego;

public class Proyectil3 {
    private float x, y;
    private float speed = 2.5f;
    private int dir; // -1 izquierda, 1 derecha
    private boolean active = true;
    private Rectangle2D.Float hitbox;
    private int width  = (int)(8  * Juego.SCALE);
    private int height = (int)(4  * Juego.SCALE);

    public Proyectil3(float x, float y, int dir) {
        this.x   = x;
        this.y   = y;
        this.dir = dir;
        hitbox   = new Rectangle2D.Float(x, y, width, height);
    }

    public void update3(Rectangle2D.Float atcherAttackBox, Jugador3 jugador) {
        // Mueve el proyectil horizontalmente
        x += speed * dir;
        hitbox.x = x;
        hitbox.y = y;

        // Si sale del attackBox del atcher se desactiva
        if (!atcherAttackBox.intersects(hitbox))
            active = false;

        if (active && hitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-10);
            active = false;
        }
    }

    public boolean isActive() { return active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth()  { return width;  }
    public int getHeight() { return height; }
}
