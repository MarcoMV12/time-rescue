package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import GameStates.Playing;
import GameStates.gameState;
import Juegos.Juego;
import utilz.LoadSave;

public class GameOverOverlay {

    private Playing playing;
    public GameOverOverlay(Playing playing) {
        this.playing = playing;
    }

    public void draw(Graphics g) {
        g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.GAME_OVER_BG), 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);

        // g.setColor(new Color(0,0,0,200));
        // g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

        // g.setColor(Color.white);
        // g.drawString("Game Over", Juego.GAME_WIDTH/2-50, Juego.GAME_HEIGHT/2);
        // g.drawString("Press Esc to Restart", Juego.GAME_WIDTH/2-70, Juego.GAME_HEIGHT/2 + 30);

    } 

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playing.recargarNivelActual();
            gameState.state = gameState.PLAYING;
        }
    }

}
