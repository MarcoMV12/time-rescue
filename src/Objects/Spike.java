package Objects;

import static utilz.Constantes.Objetos.SPIKE;

public class Spike extends objeto {
    public Spike(int x, int y) {
        super(x, y, SPIKE);
        initHitbox(32, 32);
        animVelocidad = 20;
    }

    @Override
    protected int getSpriteAmount() { return 11; }

    public void update() {
        updateAnimacion();
    }

    public void updateAnimacion() {
        animTick++;
        if (animTick >= animVelocidad) {
            animTick = 0;
            animIndex++;
            if (animIndex >= getSpriteAmount())
                animIndex = 0;
        }
    }

}