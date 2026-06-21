package Elementos;

import java.awt.geom.Rectangle2D;

public class Trap2 {

    private Rectangle2D.Float hitbox;
    private int animIndex, animTick;
    private int damageCooldown = 0;


    public Trap2(float x, float y) {
        hitbox = new Rectangle2D.Float(x, y, 32, 32);
    }

    public void update4(Jugador4 jugador) {
    updateAnimationTick();

    if (damageCooldown > 0)
        damageCooldown--;

    if (hitbox.intersects(jugador.getHitbox()) && damageCooldown == 0) {
        jugador.changeHealth(-10);
        damageCooldown = 30; // frames de espera
    }
}

    private void updateAnimationTick() {
        animTick++;

        if (animTick >= 10) {
            animTick = 0;
            animIndex++;

            if (animIndex >= 10) // tus 10 frames
                animIndex = 0;
        }
    }

    public int getAnimIndex() {
        return animIndex;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}