package Elementos;

import java.awt.geom.Rectangle2D;

public class Weapon {

    private Rectangle2D.Float hitbox;
    private boolean collected = false;

    public Weapon(float x, float y) {
        hitbox = new Rectangle2D.Float(x, y, 32, 32);
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean value) {
        this.collected = value;
    }
}