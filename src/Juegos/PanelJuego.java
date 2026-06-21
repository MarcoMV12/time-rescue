package Juegos;

import javax.swing.JPanel;
import Eventos.EntraTeclado;
import Eventos.EventoMouse;
import static Juegos.Juego.*;

import java.awt.*;

public class PanelJuego extends JPanel{
    private Juego  game;
    private EntraTeclado et;
    private EventoMouse em;

    public PanelJuego(Juego game) {
        em=new EventoMouse(this);
        et=new EntraTeclado(this);
        this.game=game;
        setFocusable(true);
        setPanelSize();        
        addKeyListener(et);  
        addMouseListener(em);        
        addMouseMotionListener(em);     
    }

    private void setPanelSize() {
        int w = (int)(GAME_WIDTH  * Juego.DISPLAY_SCALE);
        int h = (int)(GAME_HEIGHT * Juego.DISPLAY_SCALE);
        this.setPreferredSize(new Dimension(w, h));
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(Juego.DISPLAY_SCALE, Juego.DISPLAY_SCALE);
        game.render(g2d);
        g2d.scale(1.0 / Juego.DISPLAY_SCALE, 1.0 / Juego.DISPLAY_SCALE);
    }
    void updateGame(){}

    public Juego getGame() {
        return game;
    }   
}
