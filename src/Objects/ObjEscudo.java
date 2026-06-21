package Objects;

import static utilz.Constantes.ConstantesObjetos.SHIELD;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import utilz.LoadSave;

public class ObjEscudo extends objeto {

    private static BufferedImage img;
    private final int drawW = (int)(40 * Juego.SCALE);
    private final int drawH = (int)(46 * Juego.SCALE);

    public ObjEscudo(int x, int y) {
        super(x, y, SHIELD);
        initHitbox(20, 23);
        if (img == null) {
            BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.SHIELD_ATLAS);
            if (atlas != null)
                img = atlas.getSubimage(0, 0, 20, 23);
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!active || img == null) return;
        g.drawImage(img,
                (int)(hitbox.x - xLvlOffset),
                (int)(hitbox.y - yLvlOffset-10),
                drawW, drawH, null);
    }
}
