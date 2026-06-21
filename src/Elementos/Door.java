package Elementos;

import java.awt.geom.Rectangle2D;
import Juegos.Juego;

public class Door {

    private Rectangle2D.Float hitbox;
    private int animIndex = 0;
    private int animTick = 0;
    private boolean opening = false;
    private boolean open = false;

    public Door(float x, float y) {
        hitbox = new Rectangle2D.Float(x -18,y-28, 64, 64);
    }

    public void update4() {
        if (opening && !open) {
            animTick++;
            if (animTick >= 5) {
                animTick = 0;
                animIndex++;

                if (animIndex >= 11) {
                    animIndex = 11;
                    open = true;
                }
            }
        }
    }

    public void openDoor() {
        opening = true;
    }

    public boolean isSolid() {
        return !open;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public int getAnimIndex() {
        return animIndex;
    }
}