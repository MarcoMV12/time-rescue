package Objects;

import java.awt.Rectangle;
import Juegos.Juego;

public abstract class GameObject {
    protected int x, y;
    protected int tipo;
    protected Rectangle hitbox;
    protected boolean activo = true;
    protected int animIndex, animTick;
    protected int animVelocidad = 4;

    public GameObject(int x, int y, int tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }

    protected void initHitbox(int w, int h) {
        hitbox = new Rectangle(x, y, w, h);
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

    protected abstract int getSpriteAmount();

    public Rectangle getHitbox() { return hitbox; }
    public boolean isActivo()    { return activo; }
    public void setActivo(boolean a) { activo = a; }
    public int getTipo()         { return tipo; }
    public int getAnimIndex()    { return animIndex; }
}