package Objects;

import static utilz.Constantes.ConstantesObjetos.*;

import java.awt.Graphics;

import Juegos.Juego;

public class contenedor extends objeto {

    public contenedor(int x, int y, int tipo) {
        super(x, y, tipo);
        createHitBox();
    }

    // public void drawHitbox(Graphics g, int xLvlOffset) {
    //     g.setColor(java.awt.Color.YELLOW);
    //     g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
    //             (int)hitbox.width, (int)hitbox.height);
    // }

    private void createHitBox() {
        switch (objType) {
            case BARRIL:
                initHitbox(22, 24);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            case CAJA:
                initHitbox(24, 26);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            case POT:
                initHitbox(20, 20);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            case LETRERO1:
                initHitbox(26, 23);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            case LETRERO2:
                initHitbox(26, 23);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            case COFRE:
                initHitbox(20, 18);
                xDrawOffSet = (int)(7 * Juego.SCALE);
                break;
            default:
                break;
        }
    }

    public void update() {
        if (doAnimation) {
            updateAnimationTick();
        }
    }

    public void recibirGolpe() {
        doAnimation = true;
    }

    public void quebrar() {
        setObjType(getObjType() + 1);
        animIndex = 0;
        animTick  = 0;
        doAnimation = true;
    }
}
