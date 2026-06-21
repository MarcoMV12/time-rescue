package Objects;

import static utilz.Constantes.ConstantesObjetos.RESORTERA;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import utilz.LoadSave;

public class ObjResortera extends objeto {

    private static BufferedImage[] frames;
    private final int drawW = (int)(32 * Juego.SCALE);
    private final int drawH = (int)(38 * Juego.SCALE);
    private int animTick = 0;
    private static final int ANIM_SPEED = 18; // velocidad de la animación (menor = más rápido)

    public ObjResortera(int x, int y) {
        super(x, y, RESORTERA);
        initHitbox(60, 90);
        if (frames == null) {
            BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.RESORTERA_ATLAS);
            if (atlas != null) {
                frames = new BufferedImage[3];
                for (int i = 0; i < 3; i++)
                    frames[i] = atlas.getSubimage(0, 0, 60, 90);
            }
        }
    }

    public void update() {
        if (!active) return;
        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            animIndex = (animIndex + 1) % 3;
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (!active) return;
        if (frames != null) {
            g.drawImage(frames[animIndex],
                    (int)(hitbox.x - xLvlOffset),
                    (int)(hitbox.y - 6),
                    drawW, drawH, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                       (int)hitbox.width, (int)hitbox.height);
        }
    }
}
