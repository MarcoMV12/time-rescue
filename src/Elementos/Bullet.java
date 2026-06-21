package Elementos;

import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.CanMoveHere;
import Juegos.Juego;
import java.awt.image.BufferedImage;

public class Bullet {

    public float x, y;
    public boolean active = true;
    public int direction;

    private float speedX = 3.5f * Juego.SCALE;
    private float speedY; 

    private int animIndex = 0;
    private int animTick = 0;
    private int animSpeed = 6;
    private static final int FRAMES = 4;

    public static final int WIDTH = (int)(32 * Juego.SCALE);
    public static final int HEIGHT = (int)(32 * Juego.SCALE);


    public Bullet(float x, float y, int direction, float targetY) {
        this.x = x;
        this.y = y;
        this.direction = direction;


        float diffY = targetY - y;
        float diffX = speedX;
        float length = (float) Math.sqrt(diffX * diffX + diffY * diffY);
        speedY = (diffY / length) * speedX;
    }

    public void update4(int[][] lvlData, Jugador4 jugador) {
        if (!active) return;

        float xSpeed = (direction == RIGHT) ? speedX : -speedX;

        if (!CanMoveHere(x + xSpeed, y + speedY, WIDTH, HEIGHT, lvlData)) {
            active = false;
            return;
        }

        x += xSpeed;
        y += speedY; 

        if (jugador.getHitbox().intersects(x, y, WIDTH, HEIGHT)) {
            jugador.changeHealth(-15);
            active = false;
            return;
        }

        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= FRAMES)
                animIndex = 0;
        }
    }

    public int getAnimIndex() {
        return animIndex;
    }
}