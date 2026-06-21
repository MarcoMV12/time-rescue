package GameStates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import Juegos.Juego;
import utilz.LoadSave;

public class LevelCompleted extends State implements StateMethods{

    private ImageIcon gifTransicion;
    private long inicioTransicion;
    private static final long DURACION_GIF_MS = 6000;
    private int nivelCompletado = 0;

    // Botón omitir
    private int btnX, btnY, btnW = 100, btnH = 40;
    private boolean omitirHover = false;

    public LevelCompleted(Juego game) {
        super(game);
        btnX = Juego.GAME_WIDTH - btnW - 20;
        btnY = Juego.GAME_HEIGHT - btnH - 20;
    }

    public void iniciar(int nivelCompletado) {
        this.nivelCompletado = nivelCompletado;
        String gifPath;
        switch (nivelCompletado) {
            case 0: gifPath = LoadSave.TRANSICION_GIF_1; break;
            case 1: gifPath = LoadSave.TRANSICION_GIF_2; break;
            case 2: gifPath = LoadSave.TRANSICION_GIF_3; break;
            default: gifPath = LoadSave.TRANSICION_GIF_1; break;
        }
        gifTransicion = new ImageIcon(LoadSave.class.getResource("/res/" + gifPath));
        inicioTransicion = System.currentTimeMillis();
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - inicioTransicion >= DURACION_GIF_MS) {
            avanzarASelector();
        }
    }

    private void avanzarASelector() {
        game.getCharacterSelect().setNivelCompletado(nivelCompletado);
        gameState.state = gameState.CHARACTER_SELECT;
    }

    @Override
    public void draw(Graphics g) {
        // Fondo negro
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

        // GIF
        if (gifTransicion != null)
            g.drawImage(gifTransicion.getImage(), 0, 0,
                Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);

        // Botón "Omitir"
        g.setColor(omitirHover ? new Color(200, 200, 80, 220)
                               : new Color(0, 0, 0, 180));
        g.fillRect(btnX, btnY, btnW, btnH);
        g.setColor(Color.WHITE);
        g.drawRect(btnX, btnY, btnW, btnH);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Omitir »", btnX + 18, btnY + 25);
    }

    private boolean estaSobreOmitir(MouseEvent e) {
        return e.getX() >= btnX && e.getX() <= btnX + btnW
            && e.getY() >= btnY && e.getY() <= btnY + btnH;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (estaSobreOmitir(e))
            avanzarASelector();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        omitirHover = estaSobreOmitir(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            avanzarASelector();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    

}
