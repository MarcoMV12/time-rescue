package Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;
import static utilz.MetodosAyuda.CanMoveHere;

public class PiedraResortera {

    private float x, y;
    private int dir;
    private float speed = 5f;
    private int maxRange = 600;
    private float distanceTraveled = 0;

    private Rectangle2D.Float hitbox;
    private boolean active = true;
    private static final int SIZE = (int)(8 * Juego.SCALE);

    public PiedraResortera(float x, float y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        hitbox = new Rectangle2D.Float(x, y, SIZE, SIZE);
    }

    public void update(int[][] lvlData) {
        if (!active) return;

        float nextX = x + speed * dir;

        if (!CanMoveHere(nextX, y, hitbox.width, hitbox.height, lvlData)) {
            active = false;
            return;
        }

        x = nextX;
        hitbox.x = x;
        distanceTraveled += Math.abs(speed);

        if (distanceTraveled >= maxRange)
            active = false;
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (!active) return;
        g.setColor(Color.GRAY);
        g.fillOval((int)(hitbox.x - xLvlOffset), (int)hitbox.y, SIZE, SIZE);
    }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
