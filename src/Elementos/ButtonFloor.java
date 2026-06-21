package Elementos;

import java.awt.geom.Rectangle2D;
import Juegos.Juego;
import static utilz.Constantes.ConstantesEnemy.*;

public class ButtonFloor extends Enemigo {

    private boolean pressed = false;
    private Rectangle2D.Float hitbox;

    public ButtonFloor(float x, float y) {
        super(x, y, 32, 32, BUTTON);

        initHitbox(x, y, 32, 32);
        this.hitbox = super.hitbox;
    }

    public void update4(Jugador4 jugador) {

        // Detecta si el jugador está encima
        if (hitbox.intersects(jugador.getHitbox())) {
            pressed = true;
        } else {
            pressed = false;
        }
    }

    public int getFrame() {
        if (pressed)
            return 2; // último frame
        else
            return 0; // frame inicial
    }

    public boolean isPressed() {
        return pressed;
    }
}