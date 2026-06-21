package Elementos;

import java.awt.geom.Rectangle2D;

public class TorretBullet {

    public float x, y;
    public int dir;
    private float speed = 4.0f;
    private boolean active = true;

    private Rectangle2D.Float hitbox;

    private int animIndex = 0;
    private int animTick = 0;
    private int animSpeed = 5;
    private static final int TOTAL_FRAMES = 12; // 384 / 32 = 12 frames

    public TorretBullet(float x, float y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        hitbox = new Rectangle2D.Float(x, y - 20, 32, 40); // hitbox centrada
    }

    public void update4(Jugador4 jugador) {
        x += speed * dir;
        hitbox.x = x;
        hitbox.y = y - 20;

        // checar si golpea al jugador
        if (hitbox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-20); // daño al jugador
            active = false;
        }

        // desactivar si sale del mapa
        if (x < -500 || x > 50000)
            active = false;

        // animación
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= TOTAL_FRAMES)
                animIndex = 0;
        }
    }

    public boolean isActive() { return active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
    public int getAnimIndex() { return animIndex; }
}