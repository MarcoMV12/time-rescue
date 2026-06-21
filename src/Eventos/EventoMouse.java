package Eventos;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionListener;

//import GameStates.Start;
import GameStates.gameState;
import Juegos.PanelJuego;

public class EventoMouse extends MouseAdapter {

   private PanelJuego pan;
    
   public EventoMouse(PanelJuego pan) {
        this.pan = pan;
   }

   /** Convierte coordenadas del panel (escalado) al espacio lógico del juego. */
   private MouseEvent escalar(MouseEvent e) {
       double s = Juegos.Juego.DISPLAY_SCALE;
       return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(),
           e.getModifiersEx(),
           (int)(e.getX() / s), (int)(e.getY() / s),
           e.getClickCount(), e.isPopupTrigger(), e.getButton());
   }

    public void mouseClicked(MouseEvent e){
       e = escalar(e);
       switch (gameState.state) {
         case PLAYING: pan.getGame().getPlaying().mouseClicked(e);
            break;
         case LEVEL_COMPLETED: pan.getGame().getLevelCompleted().mouseClicked(e);
            break;
         case CHARACTER_SELECT: pan.getGame().getCharacterSelect().mouseClicked(e);
            break;
         default:
            break;
       }

    }
    public void mousePressed(MouseEvent e){
         e = escalar(e);
         switch (gameState.state) {
            case START:
               pan.getGame().getStart().mousePressed(e);
               break;
            case PLAYING:
               pan.getGame().getPlaying().mousePressed(e);
               break;
            case PAUSE:
               pan.getGame().getPause().mousePressed(e);
               break;
            case LEVEL_COMPLETED:
               pan.getGame().getLevelCompleted().mousePressed(e);
               break;
            case CHARACTER_SELECT:
               pan.getGame().getCharacterSelect().mousePressed(e);
               break;
            default:
               break;
         }
    }
    public void mouseReleased(MouseEvent e){
         e = escalar(e);
         switch (gameState.state) {
            case START:
               pan.getGame().getStart().mouseReleased(e);
               break;
            case PLAYING:
               pan.getGame().getPlaying().mouseReleased(e);
               break;
            case PAUSE:
               pan.getGame().getPause().mouseReleased(e);
               break;
            case LEVEL_COMPLETED:
               pan.getGame().getLevelCompleted().mouseReleased(e);
               break;
            case CHARACTER_SELECT:
               pan.getGame().getCharacterSelect().mouseReleased(e);
               break;
            default:
               break;
         }
    }


   public void mouseMoved(MouseEvent e){
      e = escalar(e);
      switch (gameState.state) {
         case START:
            pan.getGame().getStart().mouseMoved(e);
            break;
         case PLAYING:
            pan.getGame().getPlaying().mouseMoved(e);
            break;
         case LEVEL_COMPLETED:
             pan.getGame().getLevelCompleted().mouseMoved(e);
             break;
          case CHARACTER_SELECT:
             pan.getGame().getCharacterSelect().mouseMoved(e);
             break;
         default:
            break;
      }
   }

    

}
