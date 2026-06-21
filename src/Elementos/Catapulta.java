package Elementos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
 
import GameStates.Playing;
import Juegos.Juego;
import ui.CatapultaOverlay;
import utilz.LoadSave;
import static utilz.Constantes.ConstantesCatapulta.*;
 
public class Catapulta extends Cascaron {
 
    public enum Estado {
        SIN_PROYECTIL,   // esperando que el jugador coloque la roca
        CON_PROYECTIL,   // roca colocada, esperando contrapeso
        DISPARANDO,      // animación de lanzamiento en curso
        DISPARADA        // lanzamiento terminado, catapulta vacía
    }
 
    private BufferedImage[][] catFrames;
    private int accionActual = CAT_IDLE_SIN_PROY;
    private int animIndex    = 0;
    private int animTick     = 0;
 
    private static final int DRAW_W = (int)(120 * Juego.SCALE);
    private static final int DRAW_H = (int)(80  * Juego.SCALE);
 
    private Estado estado = Estado.SIN_PROYECTIL;
 
    private static final float RANGO_INTERACCION = 80f * Juego.SCALE;
    private static final float OFFSET_EXTREMO_X = -(int)(20 * Juego.SCALE);
    private static final float OFFSET_EXTREMO_Y = -(int)(10 * Juego.SCALE);
    private static final float RANGO_CONTRAPESO = 50f * Juego.SCALE;
 
    private boolean jugadorCerca = false;
    private boolean mostrarInfo = false;
 
    private Proyectil proyectil = null;
    private CatapultaOverlay overlay;
 
    private static final int CANON_OFFSET_X =  (int)(60 * Juego.SCALE);
    private static final int CANON_OFFSET_Y = -(int)(40 * Juego.SCALE);
 
    public Catapulta(float x, float y, float distanciaObjetivo) {
        super(x, y, DRAW_W, DRAW_H);
        initHitbox(x, y + DRAW_H / 2, DRAW_W, DRAW_H / 2);
        loadSprite();
        overlay = new CatapultaOverlay();
        overlay.setDistanciaObjetivo(distanciaObjetivo);
    }

    private void loadSprite() {
        BufferedImage sheet = LoadSave.GetSpriteAtlas("ass/catapulta.png");
        if (sheet == null) return;
 
        catFrames = new BufferedImage[3][];
        catFrames[CAT_IDLE_SIN_PROY] = new BufferedImage[2];
        catFrames[CAT_IDLE_CON_PROY] = new BufferedImage[2];
        catFrames[CAT_LANZAR]        = new BufferedImage[16];
 
        for (int accion = 0; accion < catFrames.length; accion++)
            for (int i = 0; i < catFrames[accion].length; i++)
                catFrames[accion][i] = sheet.getSubimage(
                    i      * CAT_FRAME_W,
                    accion * CAT_FRAME_H,
                    CAT_FRAME_W, CAT_FRAME_H);
    }
 
    public void update(Jugador jugador, int[][] lvlData) {
        boolean estabaLejos = !jugadorCerca;
        jugadorCerca = distancia(jugador) < RANGO_INTERACCION;
        // Si el jugador se aleja, cerrar el info panel automáticamente
        if (estabaLejos == false && !jugadorCerca) mostrarInfo = false;
 
        if (proyectil != null && proyectil.isActivo())
            proyectil.update(lvlData);
 
        if (estado == Estado.DISPARANDO)
            updateAnimacion();
    }
 
    private void updateAnimacion() {
        animTick++;
        if (animTick >= GetAnimSpeed(accionActual)) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetFrameCount(accionActual)) {
                animIndex = 0;
                if (accionActual == CAT_LANZAR) {
                    nuevaAccion(CAT_IDLE_SIN_PROY);
                    estado = Estado.DISPARADA;
                }
            }
        }
    }

    public void resetParaReintentar() {
        proyectil = null;
        proyectilConsumido = false;  
        estado = Estado.SIN_PROYECTIL;
        nuevaAccion(CAT_IDLE_SIN_PROY);
    }
 
    private void nuevaAccion(int accion) {
        if (accionActual == accion) return;
        accionActual = accion;
        animIndex    = 0;
        animTick     = 0;
    }
 
    public void draw(Graphics g, int xLvlOffset, int[][] lvlData) {
        int drawX = (int)(hitbox.x - xLvlOffset);
        int drawY = (int)(hitbox.y - DRAW_H / 2);
 
        // Sprite o fallback
        if (catFrames != null)
            g.drawImage(catFrames[accionActual][animIndex],
                        drawX, drawY, DRAW_W, DRAW_H, null);
        else {
            g.setColor(new Color(120, 80, 40));
            g.fillRect(drawX, drawY, DRAW_W, DRAW_H);
        }
 
        // Proyectil en vuelo
        if (proyectil != null)
            proyectil.draw(g, xLvlOffset);
 
        // Indicador del extremo donde cae el contrapeso
        if (estado == Estado.CON_PROYECTIL)
            drawIndicadorExtemo(g, xLvlOffset);
 
        // Mensaje contextual
        drawMensaje(g, drawX, drawY);
 
        // Info central solo si el jugador la activó con [I]
        if (mostrarInfo && jugadorCerca) {
            overlay.drawInfoCentral(g);
        }

    }
 
    // Flecha/círculo que indica dónde debe caer el contrapeso
    private void drawIndicadorExtemo(Graphics g, int xLvlOffset) {
        int ex = (int)(getExtremoX() - xLvlOffset);
        int ey = (int) getExtremoY();
 
        Graphics2D g2d = (Graphics2D) g;
        boolean parpadeo = (System.currentTimeMillis() / 400) % 2 == 0;
        if (parpadeo) {
            g2d.setColor(new Color(255, 220, 50, 180));
            g2d.drawOval(ex - 12, ey - 12, 24, 24);
            g2d.setColor(new Color(255, 220, 50, 80));
            g2d.fillOval(ex - 12, ey - 12, 24, 24);
        }
    }
 
    private void drawMensaje(Graphics g, int drawX, int drawY) {
        if (!jugadorCerca) return;
 
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, (int)(8 * Juego.SCALE)));
        boolean parpadeo = (System.currentTimeMillis() / 500) % 2 == 0;
 
        switch (estado) {
            case SIN_PROYECTIL:
                if (parpadeo) {
                    g2d.setColor(new Color(255, 220, 80));
                    g2d.drawString("[ E ] Colocar proyectil", drawX, drawY - 10);
                }
                break;
            case CON_PROYECTIL:
                if (parpadeo) {
                    g2d.setColor(new Color(100, 220, 255));
                    g2d.drawString("[ I ] Leer info  |  [ E ] Lanzar", drawX, drawY - 10);
                }
                break;
            case DISPARADA:
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawString("La catapulta está vacía", drawX, drawY - 10);
                break;
            default:
                break;
        }
    }
 
    public void toggleInfo() { mostrarInfo = !mostrarInfo; }
    public boolean isMostrarInfo() { return mostrarInfo; }
 
    // ── Interacciones ────────────────────────────────────────────────
 
    // Jugador presiona E cerca con el proyectil en mano
    public void colocarProyectil(Jugador jugador) {
        if (estado != Estado.SIN_PROYECTIL) return;
        if (!jugador.tieneItem()) return;
 
        jugador.soltarItem(); // quita la roca de la mano
        estado = Estado.CON_PROYECTIL;
        nuevaAccion(CAT_IDLE_CON_PROY);
    }
 
    // Playing llama esto cuando detecta que un contrapeso aterrizó cerca del extremo
    public void recibirContrapeso(Contrapeso c) {
        if (estado != Estado.CON_PROYECTIL) return;
        overlay.setContrapeso(c);
        disparar(c.getPeso());
    }
 
    private void disparar(float pesoContrapeso) {
        proyectil = overlay.crearProyectil(getCanonX(), getCanonY(), pesoContrapeso);
        System.out.println("Proyectil creado en (" + getCanonX() + ", " + getCanonY() 
        + ") con vel=(" + proyectil + ")");
        estado    = Estado.DISPARANDO;
        nuevaAccion(CAT_LANZAR);
    }
 
    // ── Helpers ──────────────────────────────────────────────────────
 
    private float distancia(Jugador jugador) {
        float dx = jugador.getHitbox().x - hitbox.x;
        float dy = jugador.getHitbox().y - hitbox.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private boolean proyectilConsumido = false;

    public void marcarProyectilConsumido() {
        proyectilConsumido = true;
    }

    public boolean isProyectilConsumido() {
        return proyectilConsumido;
    }
 
    public float getCanonX()   { return hitbox.x + CANON_OFFSET_X; }
    public float getCanonY()   { return hitbox.y + CANON_OFFSET_Y; }
    public float getExtremoX() { return hitbox.x + OFFSET_EXTREMO_X; }
    public float getExtremoY() { return hitbox.y + OFFSET_EXTREMO_Y; }
    public float getRangoContrapeso() { return RANGO_CONTRAPESO; }
 
    public boolean isJugadorCerca()  { return jugadorCerca;              }
    public Estado  getEstado()       { return estado;                    }
    public Proyectil getProyectil()  { return proyectil;                 }
    public boolean proyectilActivo() { return proyectil != null && proyectil.isActivo(); }
}
