package Objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import utilz.LoadSave;
import static utilz.MetodosAyuda.CanMoveHere;

public class Posion {

    private float x, y;
    private int dir; 
    private float speed = 4f;
    private int maxRange = 500;
    private float distanceTraveled = 0;

    private Rectangle2D.Float hitbox;
    private BufferedImage[] frames;
    private int animIndex = 0;
    private int animTick = 0;
    private int animSpeed = 8;
    private boolean active = true;

    public Posion(float x, float y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        hitbox = new Rectangle2D.Float(x, y, (int)(9 * Juego.SCALE), (int)(17 * Juego.SCALE));
        loadAnimation();
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.POSION_ATLAS);
        frames = new BufferedImage[7]; // 126 / 18 = 7 frames
        for (int i = 0; i < frames.length; i++)
            frames[i] = img.getSubimage(i * 18, 0, 18, 35);
    }

    public void update(int[][] lvlData) {
        if (!active) return;

        float nextX = x + speed * dir;

        // Desactivar si choca con un bloque sólido
        if (!CanMoveHere(nextX, y, hitbox.width, hitbox.height, lvlData)) {
            active = false;
            return;
        }

        // Mover en la dirección lanzada
        x = nextX;
        hitbox.x = x;
        distanceTraveled += Math.abs(speed);

        // Desactivar si viajó demasiado lejos
        if (distanceTraveled >= maxRange)
            active = false;

        // Animar
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= frames.length)
                animIndex = 0;
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!active) return;
        g.drawImage(frames[animIndex],
                (int)(hitbox.x - xLvlOffset),
                (int)(hitbox.y - yLvlOffset),
                (int)(9 * Juego.SCALE),
                (int)(17 * Juego.SCALE), null);
    }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
