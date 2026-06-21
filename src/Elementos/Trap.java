package Elementos;

import java.awt.geom.Rectangle2D;
import Juegos.Juego;
import static utilz.Constantes.ConstantesEnemy.*;

public class Trap extends Enemigo {

    private Rectangle2D.Float damageBox;

    public Trap(float x, float y) {
        super(x, y, 32, 32, 999); // tipo 999 o el que definas

        // Hitbox pequeña (zona de daño)
        initHitbox(x, y + 10, 32, 20);

        damageBox = new Rectangle2D.Float(x, y + 10, 32, 20);

        inAir = false; // no cae
    }

    public void update4(int[][] lvlData, Jugador4 jugador) {
        // Siempre hace daño si el jugador lo toca
        if (damageBox.intersects(jugador.getHitbox())) {
            jugador.changeHealth(-10);
        }
    }

    public Rectangle2D.Float getDamageBox() {
        return damageBox;
    }
}
