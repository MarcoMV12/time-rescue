package Objects;

import Juegos.Juego;

public class ArmaItem extends GameObject {
    private boolean recogido = false;
    private int bobTick = 0;

    public ArmaItem(float dropX, float dropY, int tipo) {
        super((int) dropX, (int) dropY, tipo);
        int size = (int)(Juego.TILES_SIZE * 0.5f);
        initHitbox(size, size * 2);
        hitbox.x = (int)(dropX - size / 2f);
        hitbox.y = (int) dropY;
        activo   = true;
    }

    @Override
    protected int getSpriteAmount() { return 1; }

    public void update() {
        bobTick++;
    }

    public void recoger() {
        recogido = true;
        activo   = false;
    }

    public boolean isRecogido() { return recogido; }

    public int getBobOffset() {
        return (int)(Math.sin(bobTick * 0.08) * 5 * Juego.SCALE);
    }
}
