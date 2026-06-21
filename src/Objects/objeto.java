package Objects;

import static utilz.Constantes.ANI_SPEED;
import static utilz.Constantes.ConstantesObjetos.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import Juegos.Juego;;

public class objeto {

    protected int x, y, objType;
    protected Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true;
    protected int animTick, animIndex;
    protected int xDrawOffSet, yDrawOffSet;
    // ---Nivel 2
    protected boolean activo = true;
    protected int animVelocidad = 4;

    public objeto(int x, int y, int tipo){
        this.x = x;
        this.y = y;
        this.objType =tipo;
    }

    protected void updateAnimationTick(){
        animTick++;
        if(animTick >= ANI_SPEED) {
            animTick = 0;
            animIndex++;
            if(animIndex >= GetSpriteAmount(objType)) {
                animIndex = 0;           
                switch (objType) {
                    case BARRIL_B:
                    case CAJA_B:
                    case LETRERO1_B:
                    case LETRERO2_B:
                    case POT_B:
                    case COFRE_B:
                        doAnimation = false;
                        active = false;
                        break;
                }     
                
            }
        }
    }

    public void  reset(){
        animIndex = 0;
        animTick = 0;
        active = true;

        switch (objType) {
            case BARRIL:
            case CAJA:
            case LETRERO1:
            case LETRERO2:
            case POT:
            case COFRE:
                doAnimation = false;
                break;
            case COIN:
            case HP:
                doAnimation = true;
                break;
        }

    }

    protected void initHitbox(int w, int h){
        hitbox=new Rectangle2D.Float(x,y,(int)(w * Juego.SCALE), (int)(h * Juego.SCALE));
    }

    protected void initHitbox(int offsetX, int offsetY, int w, int h) {
        hitbox = new Rectangle2D.Float(
            x + offsetX,
            y + offsetY,
            (int)(w * Juego.SCALE),
            (int)(h * Juego.SCALE)
        );
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                  (int)hitbox.width, (int)hitbox.height);
    }

    public int getObjType() {
        return objType;
    }

    public void setObjType(int objType) {
        this.objType = objType;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle2D.Float hitbox) {
        this.hitbox = hitbox;
    }

    public int getxDrawOffSet() {
        return xDrawOffSet;
    }

    public void setxDrawOffSet(int xDrawOffSet) {
        this.xDrawOffSet = xDrawOffSet;
    }

    public int getyDrawOffSet() {
        return yDrawOffSet;
    }

    public void setyDrawOffSet(int yDragOffSet) {
        this.yDrawOffSet = yDragOffSet;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getAnimIndex() {
        return animIndex;
    }

    public void setAnimation(boolean doAnimation){
        this.doAnimation = doAnimation;
    }

    protected int getSpriteAmount(){
        return 0;
    }

}
