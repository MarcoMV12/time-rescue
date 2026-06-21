package utilz;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Singleton que gestiona monedas, XP y la tabla de puntuaciones.
 * Se persiste en "scores.txt" junto al ejecutable del juego.
 */
public class ScoreManager {

    // ── Constantes de XP ────────────────────────────────────────────────────
    public static final int XP_MATAR_ENEMIGO   = 25;
    public static final int XP_RECOGER_MONEDA  =  5;
    public static final int XP_RECOGER_ITEM    =  8;
    public static final int XP_ACERTIJO        = 50;
    public static final int XP_NIVEL_COMPLETO  = 100;

    // ── Singleton ────────────────────────────────────────────────────────────
    private static ScoreManager instance;

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }

    // ── Estado de sesión ─────────────────────────────────────────────────────
    private int monedas;
    private int xp;
    private String nickname = "Jugador";

    // ── Tabla de records ─────────────────────────────────────────────────────
    private static final int MAX_SCORES = 10;
    private static final String FILE_NAME = "scores.txt";
    private List<ScoreEntry> highScores = new ArrayList<>();

    private ScoreManager() {
        cargarScores();
        // Guarda automáticamente si la JVM se cierra (cierre de ventana, Ctrl+C, etc.)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (xp > 0 || monedas > 0) {
                guardarPartida(nickname);
            }
        }, "score-shutdown-hook"));
    }

    // ── API de sesión ────────────────────────────────────────────────────────

    /** Suma una moneda y el XP correspondiente. */
    public void addMoneda() {
        monedas++;
        xp += XP_RECOGER_MONEDA;
    }

    /** Suma XP directamente (kills, ítems, acertijos…). */
    public void addXP(int amount) {
        xp += amount;
    }

    public int getMonedas()    { return monedas; }
    public int getXP()          { return xp; }
    public String getNickname() { return nickname; }
    public void setNickname(String n) {
        this.nickname = (n == null || n.isBlank()) ? "Jugador" : n.trim();
    }

    /** Reinicia los contadores para una nueva partida. */
    public void resetSesion() {
        monedas = 0;
        xp      = 0;
    }

    // ── Persistencia ─────────────────────────────────────────────────────────

    /**
     * Guarda la sesión actual en la tabla de records y escribe el archivo.
     *
     * @param personaje Nombre del personaje usado (para mostrar en la tabla).
     */
    public void guardarPartida(String personaje) {
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
        highScores.add(new ScoreEntry(personaje, monedas, xp, fecha));
        highScores.sort((a, b) -> Integer.compare(b.xp, a.xp));
        if (highScores.size() > MAX_SCORES)
            highScores = new ArrayList<>(highScores.subList(0, MAX_SCORES));
        escribirArchivo();
    }

    private void escribirArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry e : highScores)
                pw.println(e.personaje + "," + e.monedas + "," + e.xp + "," + e.fecha);
        } catch (IOException ex) {
            System.out.println("[ScoreManager] Error guardando scores: " + ex.getMessage());
        }
    }

    public void cargarScores() {
        highScores.clear();
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 4);
                if (p.length == 4) {
                    try {
                        highScores.add(new ScoreEntry(
                            p[0],
                            Integer.parseInt(p[1].trim()),
                            Integer.parseInt(p[2].trim()),
                            p[3]));
                    } catch (NumberFormatException ignore) {}
                }
            }
        } catch (IOException ex) {
            System.out.println("[ScoreManager] Error cargando scores: " + ex.getMessage());
        }
    }

    public List<ScoreEntry> getHighScores() {
        return Collections.unmodifiableList(highScores);
    }

    // ── Registro de puntuación ────────────────────────────────────────────────

    public static class ScoreEntry {
        public final String personaje;
        public final int    monedas;
        public final int    xp;
        public final String fecha;

        public ScoreEntry(String personaje, int monedas, int xp, String fecha) {
            this.personaje = personaje;
            this.monedas   = monedas;
            this.xp        = xp;
            this.fecha     = fecha;
        }
    }
}
