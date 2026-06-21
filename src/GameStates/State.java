package GameStates;

import java.awt.event.MouseEvent;

import Juegos.Juego;
import ui.startButton;

public class State {
    protected Juego game;

    public State(Juego game){
        this.game = game;
    }

    public boolean isIn(MouseEvent e, startButton pb) {
        return pb.getBounds().contains(e.getX(), e.getY());
    }

    public Juego getGame(){
        return game;
    }

}
