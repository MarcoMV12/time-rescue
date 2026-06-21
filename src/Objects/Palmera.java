package Objects;

import static utilz.Constantes.Objetos.PALMERA;

public class Palmera extends objeto {
    public Palmera(int x, int y) {
        super(x, y, PALMERA);
        initHitbox(32, 64); // ajusta alto a tu sprite
    }

    @Override
    protected int getSpriteAmount() { return 4; } 

    public void update() {
    }
}