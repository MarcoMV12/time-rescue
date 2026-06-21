package Eventos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GameStates.gameState;
import Juegos.PanelJuego;

public class EntraTeclado implements KeyListener {
  private PanelJuego pan;

  public EntraTeclado(PanelJuego pan) {
    this.pan = pan;
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (gameState.state) {
      case PAUSE: pan.getGame().getPause().keyPressed(e);
        break;
      case PLAYING: pan.getGame().getPlaying().keyPressed(e);
        break;
      case START: pan.getGame().getStart().keyPressed(e);
        break;
      case LEVEL_COMPLETED: pan.getGame().getLevelCompleted().keyPressed(e);
        break;
      case CHARACTER_SELECT: pan.getGame().getCharacterSelect().keyPressed(e);
        break;
      default:
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    switch (gameState.state) {
      case PAUSE: pan.getGame().getPause().keyReleased(e);
        break;
      case PLAYING: pan.getGame().getPlaying().keyReleased(e);
        break;
      case START: pan.getGame().getStart().keyReleased(e);
        break;
      case LEVEL_COMPLETED: pan.getGame().getLevelCompleted().keyReleased(e);
        break;
      case CHARACTER_SELECT: pan.getGame().getCharacterSelect().keyReleased(e);
        break;
      default:
        break;
    }
  }

}
