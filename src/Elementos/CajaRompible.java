package Elementos;

import java.awt.Color;
import java.awt.Graphics;
 
import Juegos.Juego;
 
public class CajaRompible extends Cascaron {
 
    public enum Estado {
        INTACTA,    // sin daño
        DAÑADA,     // recibió un impacto, visible en sprite
        DESTRUIDA   // suelta el item
    }
 
    private Estado estado   = Estado.INTACTA;
    private int maxHealth   = 2;   // cuántos impactos aguanta
    private int salud       = maxHealth;
 
    // Item que cae al destruirse
    private ItemNivel itemInterno;
    private boolean   itemSoltado = false;
 
    // Tamaño de la caja en pantalla
    private static final int CAJA_W = (int)(32 * Juego.SCALE);
    private static final int CAJA_H = (int)(32 * Juego.SCALE);
 
    // Animación de destrucción
    private int animTick     = 0;
    private int animIndex    = 0;
    private static final int ANIM_SPEED  = 6;
    private static final int ANIM_FRAMES = 4; // frames de explosión/rotura
 
    // Partículas simples al romperse
    private boolean mostrarParticulas = false;
    private int     particulasTick    = 0;
 
    public CajaRompible(float x, float y, ItemNivel.Tipo tipoItem) {
        super(x, y, CAJA_W, CAJA_H);
        initHitbox(x, y, CAJA_W, CAJA_H);
        // El item cae justo encima de donde estaba la caja
        this.itemInterno = new ItemNivel(x, y - CAJA_H, tipoItem);
    }
 
    public void update() {
        if (estado == Estado.DESTRUIDA) {
            // Animar rotura y soltar item
            animTick++;
            if (animTick >= ANIM_SPEED) {
                animTick = 0;
                animIndex++;
                if (animIndex >= ANIM_FRAMES && !itemSoltado)
                    itemSoltado = true;
            }
            if (mostrarParticulas) {
                particulasTick++;
                if (particulasTick > 30)
                    mostrarParticulas = false;
            }
        }
    }
 
    public void draw(Graphics g, int xLvlOffset) {
        int drawX = (int)(hitbox.x - xLvlOffset);
        int drawY = (int) hitbox.y;
 
        if (estado != Estado.DESTRUIDA) {
            dibujarCaja(g, drawX, drawY);
        } else if (mostrarParticulas) {
            dibujarParticulas(g, drawX, drawY);
        }
 
        // Item flotando esperando ser recogido
        if (itemSoltado)
            itemInterno.draw(g, xLvlOffset);

        drawHitbox(g, xLvlOffset);
    }
 
    private void dibujarCaja(Graphics g, int drawX, int drawY) {
        // Color según estado de salud
        Color colorBase = (estado == Estado.INTACTA)
                ? new Color(160, 110, 60)    // madera sana
                : new Color(110, 70,  30);   // madera dañada
 
        g.setColor(colorBase);
        g.fillRect(drawX, drawY, CAJA_W, CAJA_H);
 
        // Bordes y refuerzo de caja
        g.setColor(new Color(80, 50, 20));
        g.drawRect(drawX, drawY, CAJA_W, CAJA_H);
        // Cruz de refuerzo
        g.drawLine(drawX, drawY, drawX + CAJA_W, drawY + CAJA_H);
        g.drawLine(drawX + CAJA_W, drawY, drawX, drawY + CAJA_H);
 
        // Grietas si está dañada
        if (estado == Estado.DAÑADA) {
            g.setColor(new Color(60, 35, 10));
            g.drawLine(drawX + 8, drawY + 5, drawX + 14, drawY + 18);
            g.drawLine(drawX + 20, drawY + 10, drawX + 25, drawY + 28);
        }
 
        // Indicador de vida (puntos)
        dibujarVida(g, drawX, drawY);
    }
 
    private void dibujarVida(Graphics g, int drawX, int drawY) {
        int radio = 4;
        int margen = 3;
        int startX = drawX + (CAJA_W - (maxHealth * (radio * 2 + margen))) / 2;
        int iy = drawY - 10;
 
        for (int i = 0; i < maxHealth; i++) {
            int cx = startX + i * (radio * 2 + margen);
            g.setColor(i < salud ? new Color(220, 80, 80) : new Color(80, 80, 80));
            g.fillOval(cx, iy, radio * 2, radio * 2);
        }
    }
 
    private void dibujarParticulas(Graphics g, int drawX, int drawY) {
        g.setColor(new Color(160, 110, 60, 
                   Math.max(0, 255 - particulasTick * 8)));
        int d = particulasTick;
        g.fillRect(drawX - d,     drawY - d,     8, 8);
        g.fillRect(drawX + d + 20, drawY - d,    8, 8);
        g.fillRect(drawX - d,     drawY + d + 20, 8, 8);
        g.fillRect(drawX + d + 20, drawY + d + 20, 8, 8);
    }
 
    // Llamado desde Playing cuando el proyectil impacta
    public void recibirImpacto(int daño) {
        if (estado == Estado.DESTRUIDA) return;
 
        salud -= daño;
 
        if (salud <= 0) {
            salud              = 0;
            estado             = Estado.DESTRUIDA;
            mostrarParticulas  = true;
            particulasTick     = 0;
            animIndex          = 0;
            animTick           = 0;
        } else {
            estado = Estado.DAÑADA;
        }
    }
 
    public boolean isDestruida()   { return estado == Estado.DESTRUIDA; }
    public boolean isItemSoltado() { return itemSoltado; }
    public ItemNivel getItem()     { return itemInterno; }
 
    // Para detectar colisión del proyectil
    public boolean colisionaCon(java.awt.geom.Rectangle2D.Float otraHitbox) {
        return estado != Estado.DESTRUIDA && hitbox.intersects(otraHitbox);
    }
}