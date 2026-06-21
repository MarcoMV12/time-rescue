package Elementos;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import utilz.LoadSave;

public class PlayerBullet {

    private float x, y;
    private int dir;
    private float speed = 6.0f;
    private float ySpeed = 0f; 
    private boolean active = true;

    private Rectangle2D.Float hitbox;

    private BufferedImage[] anim;
    private int animIndex = 0;
    private int animTick = 0;
    private int animSpeed = 5;

    // Constructor con ySpeed
    public PlayerBullet(float x, float y, int dir, float ySpeed) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.ySpeed = ySpeed;
        hitbox = new Rectangle2D.Float(x, y - 40, 32, 80);
        loadAnim();
    }

    public PlayerBullet(float x, float y, int dir) {
        this(x, y, dir, 0f);
    }

    private void loadAnim() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.CBULLET_SPRITES);

        if (img == null) {
            System.err.println("ERROR: no se encontró el sprite de la bala del jugador");
            anim = new BufferedImage[0];
            active = false;
            return;
        }

        int totalFrames = img.getWidth() / 32;
        anim = new BufferedImage[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            anim[i] = img.getSubimage(i * 32, 0, 32, 32);
        }
    }

    public void update() {
        x += speed * dir;
        hitbox.x = x;
        hitbox.y = y - 40;

        updateAnim();

        if (x < -500 || x > 50000) { 
            active = false;
        }
    }

    private void updateAnim() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= anim.length) {
                animIndex = 0;
            }
        }
    }

    public void render(Graphics g, int offset) {
        if (anim == null || anim.length == 0) return;
        g.drawImage(
            anim[animIndex],
            (int)(x - offset),
            (int)y,
            32, 32,
            null
        );

    //      g.setColor(java.awt.Color.GREEN);
    //     g.drawRect(
    //     (int)(hitbox.x - offset),
    //     (int)hitbox.y,
    //     (int)hitbox.width,
    //     (int)hitbox.height
    // );
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public void deactivate() {
        active = false;
    }
}