package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
public abstract class Cascaron {
    protected float x,y;
    protected int h,w;
    protected Rectangle2D.Float hitbox;

    public Cascaron(float x, float y,int w,int h) {
        this.x = x;
        this.y = y;
        this.h=h;
        this.w=w;
    }
    protected void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                  (int)hitbox.width, (int)hitbox.height);
    }

    protected void drawHitbox(Graphics g, int xLvlOffset,int yLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)(hitbox.y - yLvlOffset),
                  (int)hitbox.width, (int)hitbox.height);
    }

    protected void initHitbox(float x, float y, int w, int h){
        hitbox=new Rectangle2D.Float(x,y,w,h);
    }
   protected void initHitbox(float x,float y,float w,float h){
        hitbox=new Rectangle2D.Float(x,y,w,h);
    }
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
    
}
