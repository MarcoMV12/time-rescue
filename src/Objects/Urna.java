package Objects;

import Juegos.Juego;

public class Urna extends GameObject {
    private int salud = 1;
    private boolean rota = false;
    private int flashTick = 0;

    public Urna(int x, int y, int tipo) {
        super(x, y, tipo);
        int w = (int)(Juego.TILES_SIZE * 0.6f);
        int h = (int)(Juego.TILES_SIZE * 0.9f);
        initHitbox(w, h);
        hitbox.x = x + (Juego.TILES_SIZE - w) / 2;
        hitbox.y = y + (Juego.TILES_SIZE - h);
    }

    @Override
    protected int getSpriteAmount() { return 1; }

    public void update() {
        if (flashTick > 0) flashTick--;
    }

    public void hurt(int damage) {
        if (rota) return;
        salud -= damage;
        flashTick = 8;
        if (salud <= 0) {
            rota   = true;
            activo = false;
        }
    }

    public void resetUrna() {
        salud     = 1;
        rota      = false;
        flashTick = 0;
        activo    = true;
    }

    public boolean isRota()      { return rota; }
    public boolean isFlashing()  { return flashTick > 0; }

    public float getDropX() { return hitbox.x + hitbox.width / 2f; }
    public float getDropY() { return hitbox.y; }
}
