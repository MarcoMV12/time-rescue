package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
 
import Elementos.Contrapeso;
import Elementos.Proyectil;
import Juegos.Juego;
 
public class CatapultaOverlay {
 
    // Panel
    private static final int PANEL_W = (int)(280 * Juego.SCALE);
    private static final int PANEL_H = (int)(210 * Juego.SCALE);
    private static final int PANEL_X = Juego.GAME_WIDTH  - PANEL_W - 10;
    private static final int PANEL_Y = 10;
 
    // Física fija de la catapulta en el nivel
    public static final int   ANGULO_GRADOS   = 45;
    public static final float PESO_PROYECTIL  = 8f;   // kg
    public static final float GRAVEDAD_DISPLAY = 9.8f; // para mostrar en fórmula
 
    // Distancia al objetivo en píxeles — la asigna Playing al instanciar
    private float distanciaPixeles = 0f;
 
    // Contrapeso actualmente lanzado/cargado — para resaltar su trayectoria
    private Contrapeso contrapesoCargado = null;
 
    public void setDistanciaObjetivo(float distPixeles) {
        this.distanciaPixeles = distPixeles;
    }
 
    public void setContrapeso(Contrapeso c) {
        this.contrapesoCargado = c;
    }
 
    // ── Diálogo del científico ─────────────────────────────────────
    // Se muestra cuando jugador está cerca de la catapulta cargada con proyectil
    public void drawDialogoCientifico(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
        // Fondo semitransparente
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.88f));
        g2d.setColor(new Color(15, 20, 35));
        g2d.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 14, 14);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(new Color(180, 150, 70));
        g2d.drawRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 14, 14);
 
        int tx    = PANEL_X + 12;
        int ty    = PANEL_Y + 18;
        int lineH = (int)(13 * Juego.SCALE);
 
        // Nombre del personaje
        g2d.setFont(new Font("Monospaced", Font.BOLD, (int)(8 * Juego.SCALE)));
        g2d.setColor(new Color(100, 200, 255));
        g2d.drawString("Dr. Cavelius:", tx, ty);
        ty += lineH;
 
        // Observaciones con datos reales del nivel
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(7 * Juego.SCALE)));
        g2d.setColor(Color.WHITE);
 
        int distMetros = (int)(distanciaPixeles / Juego.TILES_SIZE);
        g2d.drawString("\"Ese objetivo está a ~" + distMetros + " m...", tx + 6, ty);
        ty += lineH;
        g2d.drawString("esta roca pesa ~" + (int)PESO_PROYECTIL + " kg...", tx + 6, ty);
        ty += lineH;
        g2d.drawString("ángulo de catapulta: " + ANGULO_GRADOS + "°...", tx + 6, ty);
        ty += lineH;
 
        // Separador
        g2d.setColor(new Color(80, 75, 50));
        g2d.drawLine(tx, ty + 3, PANEL_X + PANEL_W - 12, ty + 3);
        ty += lineH;
 
        // Fórmula
        g2d.setFont(new Font("Monospaced", Font.BOLD, (int)(7 * Juego.SCALE)));
        g2d.setColor(new Color(255, 220, 80));
        g2d.drawString("Si no mal recuerdo...", tx, ty);
        ty += lineH;
 
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(7 * Juego.SCALE)));
        g2d.setColor(new Color(180, 255, 180));
        g2d.drawString("Alcance = v₀²·sin(2θ) / g", tx + 6, ty);
        ty += lineH;
        g2d.drawString("g = " + GRAVEDAD_DISPLAY + " m/s²", tx + 6, ty);
        ty += lineH;
 
        // v0 que necesita para llegar al objetivo
        float v0necesaria = calcularV0Necesaria();
        g2d.setColor(new Color(255, 180, 100));
        g2d.drawString("Necesito v₀ ≈ " + String.format("%.1f", v0necesaria) + " m/s\"", tx + 6, ty);
        ty += lineH;
 
        // Separador
        g2d.setColor(new Color(80, 75, 50));
        g2d.drawLine(tx, ty + 2, PANEL_X + PANEL_W - 12, ty + 2);
        ty += lineH;
 
        // Pregunta
        g2d.setFont(new Font("Monospaced", Font.BOLD, (int)(7 * Juego.SCALE)));
        g2d.setColor(new Color(255, 120, 120));
        g2d.drawString("¿Con cuánta fuerza debo golpear", tx, ty);
        ty += lineH;
        g2d.drawString("el otro extremo?", tx, ty);
        ty += lineH;
 
        // Instrucción
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(6 * Juego.SCALE)));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("[ Acerca un contrapeso y lánzalo ]", tx, ty);
    }
 
    // ── Trayectorias de los 4 contrapesos posibles ─────────────────
    public void drawTrayectorias(Graphics g, float catX, float catY,
                                 int xLvlOffset, int[][] lvlData) {
        for (Contrapeso.Tipo tipo : Contrapeso.Tipo.values()) {
            float   v0  = calcularV0DesdePeso(tipo.peso);
            //float[] vel = calcularVelocidades(v0);
 
            boolean esCargado = contrapesoCargado != null
                             && contrapesoCargado.getTipo() == tipo;
 
            Color color = new Color(
                tipo.color.getRed(),
                tipo.color.getGreen(),
                tipo.color.getBlue(),
                esCargado ? 220 : 65);
 
            //Proyectil.drawTrayectoria(g, catX, catY, vel[0], vel[1],
            //                          xLvlOffset, lvlData, color,
            //                          esCargado ? 5 : 3);
        }
    }
 
    // ── Leyenda de trayectorias (esquina inferior izquierda) ───────
    public void drawLeyenda(Graphics g) {
        int lx    = 10;
        int ly    = Juego.GAME_HEIGHT - (int)(80 * Juego.SCALE);
        int lineH = (int)(13 * Juego.SCALE);
 
        Graphics2D g2d = (Graphics2D) g;
        int panH = Contrapeso.Tipo.values().length * lineH + 10;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        g2d.setColor(new Color(15, 20, 35));
        g2d.fillRoundRect(lx - 4, ly - 12, (int)(170 * Juego.SCALE), panH, 8, 8);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
 
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(6 * Juego.SCALE)));
 
        for (Contrapeso.Tipo tipo : Contrapeso.Tipo.values()) {
            float v0      = calcularV0DesdePeso(tipo.peso);
            float alcance = calcularAlcancePixeles(v0);
            int   metros  = (int)(alcance / Juego.TILES_SIZE);
 
            g2d.setColor(tipo.color);
            g2d.fillRect(lx, ly - 8, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawString(tipo.nombre + " (" + (int)tipo.peso + "kg) ~"
                         + metros + "m", lx + 12, ly);
            ly += lineH;
        }
    }
 
    // ── Física ────────────────────────────────────────────────────
 
    private float calcularV0Necesaria() {
        double rad = Math.toRadians(ANGULO_GRADOS);
        // Alcance = v0²·sin(2θ)/g → despejamos v0
        // Usamos gravedad en píxeles/tick² consistente con el juego
        float gravJuego = 0.04f * Juego.SCALE;
        return (float) Math.sqrt((distanciaPixeles * gravJuego)
                               / Math.sin(2 * rad));
    }
 
    public float calcularV0DesdePeso(float pesoContrapeso) {
        float BASE = 5f * Juego.SCALE;
        return BASE * (float) Math.sqrt(pesoContrapeso / PESO_PROYECTIL);
    }
 
    private float calcularAlcancePixeles(float v0) {
        double rad     = Math.toRadians(ANGULO_GRADOS);
        float gravJuego = 0.04f * Juego.SCALE;
        return (float)(v0 * v0 * Math.sin(2 * rad) / gravJuego);
    }
 
    public float[] calcularVelocidades(float v0, float dirX) {
        double rad = Math.toRadians(ANGULO_GRADOS);
        return new float[]{
            (float)( v0 * Math.cos(rad) * Math.signum(dirX)),
            (float)(-v0 * Math.sin(rad))
        };
    }
 
    public Proyectil crearProyectil(float catX, float catY, float pesoContrapeso) {
        float v0 = calcularV0DesdePeso(pesoContrapeso);
        // dirección por defecto: positiva (a la derecha). Se podría parametrizar
        // si guardas la posición del objetivo en el overlay.
        float dirX = +1f;
        float[] vel = calcularVelocidades(v0, dirX);
        System.out.println("Disparando: peso=" + pesoContrapeso + " v0=" + v0 
        + " vel=(" + vel[0] + "," + vel[1] + ")");

        return new Proyectil(catX, catY, vel[0], vel[1]);
    }

    // ── Panel de información central (se muestra con [I]) ──────────
    public void drawInfoCentral(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panW = (int)(500 * Juego.SCALE);
        int panH = (int)(340 * Juego.SCALE);
        int panX = (Juego.GAME_WIDTH  - panW) / 2;
        int panY = (Juego.GAME_HEIGHT - panH) / 2;

        // Fondo oscuro semitransparente
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.92f));
        g2d.setColor(new Color(12, 16, 30));
        g2d.fillRoundRect(panX, panY, panW, panH, 18, 18);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Borde dorado
        g2d.setColor(new Color(200, 160, 60));
        g2d.setStroke(new java.awt.BasicStroke(2f));
        g2d.drawRoundRect(panX, panY, panW, panH, 18, 18);
        g2d.setStroke(new java.awt.BasicStroke(1f));

        int tx    = panX + 22;
        int ty    = panY + 30;
        int lineH = 22;

        // Título
        g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2d.setColor(new Color(100, 200, 255));
        g2d.drawString("Dr. Cavelius — Notas de campo", tx, ty);
        ty += lineH + 4;

        // Separador
        g2d.setColor(new Color(80, 75, 50));
        g2d.drawLine(tx, ty, panX + panW - 22, ty);
        ty += 14;

        // Datos del objetivo
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g2d.setColor(Color.WHITE);
        int distMetros = (int)(distanciaPixeles / Juego.TILES_SIZE);
        g2d.drawString("\"Ese objetivo está a ~" + distMetros + " m de aquí...", tx + 8, ty);
        ty += lineH;
        g2d.drawString("esta roca pesa ~" + (int)PESO_PROYECTIL + " kg...", tx + 8, ty);
        ty += lineH;
        g2d.drawString("ángulo de lanzamiento fijo: " + ANGULO_GRADOS + "°...", tx + 8, ty);
        ty += lineH + 4;

        // Separador
        g2d.setColor(new Color(80, 75, 50));
        g2d.drawLine(tx, ty, panX + panW - 22, ty);
        ty += 14;

        // Fórmula
        g2d.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2d.setColor(new Color(255, 220, 80));
        g2d.drawString("Si no mal recuerdo, la fórmula de alcance es:", tx, ty);
        ty += lineH;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g2d.setColor(new Color(180, 255, 180));
        g2d.drawString("    Alcance = v₀²  ·  sin(2θ)  /  g", tx, ty);
        ty += lineH;
        g2d.drawString("    donde  g = " + GRAVEDAD_DISPLAY + " m/s²  y  θ = " + ANGULO_GRADOS + "°", tx, ty);
        ty += lineH + 4;

        // v0 necesaria
        float v0 = calcularV0Necesaria();
        g2d.setColor(new Color(255, 180, 100));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2d.drawString("    → Necesito v₀ ≈ " + String.format("%.2f", v0) + " m/s\"", tx, ty);
        ty += lineH + 8;

        // Separador
        g2d.setColor(new Color(80, 75, 50));
        g2d.drawLine(tx, ty, panX + panW - 22, ty);
        ty += 14;

        // Pregunta al jugador
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setColor(new Color(255, 110, 110));
        g2d.drawString("¿Con cuánta fuerza debo golpear el otro extremo?", tx, ty);
        ty += lineH;
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.setColor(new Color(255, 110, 110));
        g2d.drawString("(el peso del contrapeso determina la velocidad inicial)", tx, ty);
        ty += lineH + 4;

        // Contrapesos disponibles
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        for (Contrapeso.Tipo tipo : Contrapeso.Tipo.values()) {
            float v0t     = calcularV0DesdePeso(tipo.peso);
            float alcance = calcularAlcancePixeles(v0t);
            int   metros  = (int)(alcance / Juego.TILES_SIZE);
            g2d.setColor(tipo.color);
            g2d.fillRect(tx, ty - 10, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("  " + tipo.nombre + " (" + (int)tipo.peso + " kg)  →  alcance ~" + metros + " m", tx + 12, ty);
            ty += lineH;
        }

        // Instrucción cierre
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2d.setColor(new Color(130, 130, 130));
        g2d.drawString("[ I ] Cerrar", panX + panW - 90, panY + panH - 12);
    }
}
