package Objects;

import Juegos.Juego;

public class Coin extends objeto{

    public Coin(int x, int y, int tipo) {
        super(x, y, tipo);
        doAnimation = true;
        initHitbox(8, 14,20, 20);
        xDrawOffSet = (int)(3 * Juego.SCALE);
        yDrawOffSet = (int)(2 * Juego.SCALE);
    }

    public void update(){
        updateAnimationTick();

    }




}
