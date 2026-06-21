package Juegos;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
//import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class VtaJuego {

    public VtaJuego(PanelJuego  n){
        JFrame vta=new JFrame();
        vta.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vta.add(n);
        vta.pack();
        vta.setLocationRelativeTo(null);
        vta.setResizable(false);
        vta.setVisible(true);
        vta.addWindowFocusListener(new WindowFocusListener() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                n.getGame().windowFocusLost();
             }            
        });
       
    }
}
