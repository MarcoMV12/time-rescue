package Elementos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import Juegos.Juego;
 
public class ItemPeso extends Cascaron {
 
    public enum Tipo {
        PIEDRA  ("Piedra",   2f,  "Liviana"),
        ROCA    ("Roca",     5f,  "Mediana"),
        PEDRUSCO("Pedrusco", 10f, "Pesada");
 
        public final String nombre;
        public final float  peso;       // kg (afecta velocidad inicial)
        public final String descripcion;
 
        Tipo(String nombre, float peso, String descripcion) {
            this.nombre      = nombre;
            this.peso        = peso;
            this.descripcion = descripcion;
        }
    }
 
    private Tipo    tipo;
    private boolean recogido = false;
 
    // Tamaño visual del item en el mundo
    private static final int ITEM_SIZE = (int)(32 * Juego.SCALE);
 
    public ItemPeso(float x, float y, Tipo tipo) {
        super(x, y, ITEM_SIZE, ITEM_SIZE);
        this.tipo = tipo;
        initHitbox(x, y, ITEM_SIZE, ITEM_SIZE);
    }
 
    public void draw(Graphics g, int xLvlOffset) {
        if (recogido) return;
 
        int drawX = (int)(hitbox.x - xLvlOffset);
        int drawY = (int)(hitbox.y);
 
        // Color según tipo
        switch (tipo) {
            case PIEDRA:   g.setColor(new Color(180, 160, 140)); break;
            case ROCA:     g.setColor(new Color(120, 110, 100)); break;
            case PEDRUSCO: g.setColor(new Color(70,  65,  60 )); break;
        }
        Proyectil.dibujar(g, drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        // g.fillOval(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        // g.setColor(Color.DARK_GRAY);
        // g.drawOval(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
 
        // Etiqueta de peso encima del item
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(7 * Juego.SCALE)));
        g.drawString(tipo.nombre, drawX - 2, drawY - 4);
    }
 
    // Dibuja el item sobre la cabeza del jugador cuando lo carga
    public void drawEnMano(Graphics g, float jugadorX, float jugadorY, int xLvlOffset) {
        int drawX = (int)(jugadorX - xLvlOffset) + (ITEM_SIZE / 4);
        int drawY = (int)(jugadorY) - ITEM_SIZE - 4;
 
        switch (tipo) {
            case PIEDRA:   g.setColor(new Color(180, 160, 140)); break;
            case ROCA:     g.setColor(new Color(120, 110, 100)); break;
            case PEDRUSCO: g.setColor(new Color(70,  65,  60 )); break;
        }
        Proyectil.dibujar(g, drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        // g.fillOval(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        // g.setColor(Color.DARK_GRAY);
        // g.drawOval(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
    }
 
    public boolean isRecogido()           { return recogido; }
    public void    setRecogido(boolean r) { this.recogido = r; }
    public Tipo    getTipo()              { return tipo; }
    public float   getPeso()             { return tipo.peso; }
}