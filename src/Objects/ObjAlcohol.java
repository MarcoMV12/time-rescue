package Objects;

import static utilz.Constantes.ConstantesObjetos.ALCOHOL;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import utilz.LoadSave;

public class ObjAlcohol extends objeto {

    private static BufferedImage img;
    private final int drawW = (int)(18 * Juego.SCALE);
    private final int drawH = (int)(35 * Juego.SCALE);

    public ObjAlcohol(int x, int y) {
        super(x, y, ALCOHOL);
        initHitbox(18, 35);
        if (img == null)
            img = LoadSave.GetSpriteAtlas(LoadSave.ALCOHOL_ATLAS);
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!active || img == null) return;
        g.drawImage(img,
                (int)(hitbox.x - xLvlOffset),
                (int)(hitbox.y - yLvlOffset),
                drawW, drawH, null);
    }
}
