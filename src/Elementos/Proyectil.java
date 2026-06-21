package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
 
import Juegos.Juego;
import utilz.LoadSave;
import utilz.MetodosAyuda;
 
public class Proyectil extends Cascaron {
 
    private float vx, vy;
    private static final float GRAVEDAD = 0.04f * Juego.SCALE;
    
    // Coeficientes de rebote (entre 0 y 1)
    private static final float REBOTE_VERTICAL   = 0.45f;  // pierde 55% de velY al rebotar
    private static final float REBOTE_HORIZONTAL = 0.55f;  // pierde 45% de velX al rebotar contra pared
    private static final float FRICCION_SUELO    = 0.92f;  // pierde 8% de velX en cada tick rodando
    
    // Umbral mínimo para considerar que se detuvo
    private static final float UMBRAL_PARADO_X = 0.05f;
    private static final float UMBRAL_PARADO_Y = 0.5f;
    
    // Cuántos rebotes máximos antes de "asentarse" definitivamente
    private static final int MAX_REBOTES = 6;
    private int contadorRebotes = 0;
 
    private boolean activo  = false;
    private boolean enSuelo = false;
    private boolean perdido = false;
 
    private static BufferedImage sprite;
    private static final int RADIO = (int)(8 * Juego.SCALE);
 
    public Proyectil(float x, float y, float vx, float vy) {
        super(x, y, RADIO * 2, RADIO * 2);
        this.vx = vx;
        this.vy = vy;
        initHitbox(x, y, RADIO * 2, RADIO * 2);
        activo = true;
        cargarSprite();
    }
 
    private static void cargarSprite() {
        if (sprite != null) return;
        sprite = LoadSave.GetSpriteAtlas("proyectil.png");
        // Ajusta el path según donde esté tu archivo dentro de /res/
    }
 
    public void update(int[][] lvlData) {
        if (!activo || enSuelo) return;

        // System.out.println("Proyectil update: x=" + hitbox.x + " y=" + hitbox.y 
        // + " vx=" + vx + " vy=" + vy);
        
        // Cayó al vacío
        if (hitbox.y > Juego.GAME_HEIGHT + 100) {
            activo  = false;
            enSuelo = false;
            perdido = true;
            //System.out.println("Proyectil perdido por caer al vacío");
            return;
        }
 
        vy += GRAVEDAD;
        
        // Movimiento en X con rebote contra paredes
        float sigX = hitbox.x + vx;
        if (MetodosAyuda.CanMoveHere(sigX, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x = sigX;
        } else {
            // Choque contra pared lateral
            System.out.println("Proyectil rebotó horizontal en x=" + hitbox.x);

            vx = -vx * REBOTE_HORIZONTAL;
            contadorRebotes++;
        }
        
        // Movimiento en Y con rebote contra suelo/techo
        float sigY = hitbox.y + vy;
        if (MetodosAyuda.CanMoveHere(hitbox.x, sigY, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y = sigY;
        } else {
            // Choque vertical
            if (vy > 0) {
                System.out.println("Proyectil rebotó suelo en y=" + hitbox.y);
                // Rebote contra suelo
                vy = -vy * REBOTE_VERTICAL;
                vx *= FRICCION_SUELO;
                contadorRebotes++;
            } else {
                // Choque contra techo
                vy = 0;
            }
        }
        
        // Asentarse: si la velocidad es muy baja o ya rebotó demasiado, parar
        boolean enContactoConSuelo = !MetodosAyuda.CanMoveHere(
            hitbox.x, hitbox.y + 1, hitbox.width, hitbox.height, lvlData);
        
        if (enContactoConSuelo && Math.abs(vy) < UMBRAL_PARADO_Y) {
            // Aplica fricción rodando en el suelo
            vx *= FRICCION_SUELO;
            vy = 0;
            
            if (Math.abs(vx) < UMBRAL_PARADO_X || contadorRebotes >= MAX_REBOTES) {
                vx = 0;
                vy = 0;
                enSuelo = true;
                activo  = false;
                //System.out.println("Proyectil se asentó en x=" + hitbox.x + " y=" + hitbox.y);
            }
        }
    }
 
    // public void draw(Graphics g, int xLvlOffset) {
    //     if (!activo && !enSuelo) return;
 
    //     int drawX = (int)(hitbox.x - xLvlOffset);
    //     int drawY = (int) hitbox.y;
    //     int size  = RADIO * 3;
 
    //     if (sprite != null)
    //         g.drawImage(sprite, drawX, drawY, size, size, null);
    //     else {
    //         g.setColor(new Color(120, 100, 60));
    //         g.fillOval(drawX, drawY, size, size);
    //         g.setColor(Color.DARK_GRAY);
    //         g.drawOval(drawX, drawY, size, size);
    //     }
    // }

    public void draw(Graphics g, int xLvlOffset) {
        if (!activo && !enSuelo) return;
        int drawX = (int)(hitbox.x - xLvlOffset);
        int drawY = (int) hitbox.y;
        dibujar(g, drawX, drawY, RADIO * 2, RADIO * 2);
    }
 
    /** Método de dibujo compartido: cualquier clase puede llamarlo para pintar
     *  la roca con el mismo aspecto que cuando está en vuelo. */
    public static void dibujar(Graphics g, int drawX, int drawY, int w, int h) {
        BufferedImage img = getSprite();
        if (img != null) {
            g.drawImage(img, drawX, drawY, w, h, null);
        } else {
            // fallback si proyectil.png no cargó
            g.setColor(new Color(120, 100, 60));
            g.fillOval(drawX, drawY, w, h);
            g.setColor(Color.DARK_GRAY);
            g.drawOval(drawX, drawY, w, h);
        }
    }

    public static BufferedImage getSprite() {
        if (sprite == null) cargarSprite();
        return sprite;
    }
 
    public static void drawTrayectoria(Graphics g, float origenX, float origenY,
        float vx, float vy, int xLvlOffset,
        int[][] lvlData, Color color, int tamPunto) {
        float simX  = origenX;
        float simY  = origenY;
        float simVy = vy;
 
        g.setColor(color);
 
        for (int t = 0; t < 150; t++) {
            float sigX = simX + vx;
            float sigY = simY + simVy;
            simVy += GRAVEDAD;
 
            if (!MetodosAyuda.CanMoveHere(sigX, sigY, RADIO * 2, RADIO * 2, lvlData))
                break;
 
            if (t % 3 == 0)
                g.fillOval((int)(sigX - xLvlOffset), (int)sigY, tamPunto, tamPunto);
 
            simX = sigX;
            simY = sigY;
            if (sigY > Juego.GAME_HEIGHT) break;
        }
    }
 
    public void detener()      { activo = false; enSuelo = true; vx = 0; vy = 0; }
    public boolean isActivo()  { return activo;  }
    public boolean isEnSuelo() { return enSuelo; }
    public boolean isPerdido() { return perdido; }
}
