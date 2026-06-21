package minijuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JuegoPanel extends JPanel implements MouseListener, MouseMotionListener {

    private boolean gano = false;

    Rectangle[] slots = new Rectangle[3];
    Pieza[] piezasEnSlots = new Pieza[3];

    NivelManager nivel = new NivelManager();

    Pieza seleccionada = null;
    int offsetX, offsetY;

    JButton btnNuevoNivel;

    boolean temporizadorIniciado = false; // 🔥 NUEVO

    public JuegoPanel() {
        setLayout(null);

        addMouseListener(this);
        addMouseMotionListener(this);

        for (int i = 0; i < 3; i++) {
            slots[i] = new Rectangle(140 + i * 110, 150, 80, 80);
        }

        // 🎨 BOTÓN MEJORADO
        btnNuevoNivel = new JButton("Nuevo nivel 🔄");
        btnNuevoNivel.setBounds(200, 400, 200, 40);
        btnNuevoNivel.setFocusPainted(false);
        btnNuevoNivel.setBorderPainted(false);
        btnNuevoNivel.setFont(new Font("Arial", Font.BOLD, 14));
        btnNuevoNivel.setForeground(Color.WHITE);
        btnNuevoNivel.setBackground(new Color(70, 130, 180));
        btnNuevoNivel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover
        btnNuevoNivel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnNuevoNivel.setBackground(new Color(90, 150, 200));
            }

            public void mouseExited(MouseEvent evt) {
                btnNuevoNivel.setBackground(new Color(70, 130, 180));
            }
        });

        btnNuevoNivel.addActionListener(e -> {
            nivel.generarNivel();
            limpiarSlots();
            temporizadorIniciado = false; // 🔥 reset
            repaint();
        });

        add(btnNuevoNivel);

        nivel.generarNivel();
    }

    private void limpiarSlots() {
        for (int i = 0; i < piezasEnSlots.length; i++) {
            piezasEnSlots[i] = null;
        }
    }

    private int calcularVoltaje() {
        int total = nivel.voltajeBase;
        for (Pieza p : piezasEnSlots) {
            if (p != null) total += p.valor;
        }
        return total;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(30, 30, 40));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("⚡ Distribuye la Energía", 160, 30);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("Objetivo: " + nivel.objetivo + "V", 20, 70);
        g2.drawString("Fuente: " + nivel.voltajeBase + "V", 20, 90);

        for (Rectangle r : slots) {
            g2.setColor(new Color(80, 80, 100));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 15, 15);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        }

        for (int i = 0; i < piezasEnSlots.length; i++) {
            if (piezasEnSlots[i] != null) {
                Pieza p = piezasEnSlots[i];
                p.x = slots[i].x + 5;
                p.y = slots[i].y + 5;
                p.draw(g);
            }
        }

        for (Pieza p : nivel.piezas) {
            if (!p.colocada) p.draw(g);
        }

        int total = calcularVoltaje();

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Voltaje actual: " + total + "V", 20, 120);

        // 🔥 LÓGICA DE VICTORIA + TIMER
        if (total == nivel.objetivo) {
            gano = true; // 🔥 IMPORTANTE

            g2.setColor(new Color(100, 255, 120));
            g2.drawString("🚪 PUERTA ABIERTA", 300, 80);

            if (!temporizadorIniciado) {
                temporizadorIniciado = true;

                Timer timer = new Timer(1000, e -> {
                    Window ventana = SwingUtilities.getWindowAncestor(this);
                    ventana.dispose();
                });
                timer.setRepeats(false);
                timer.start();
            }
        } else {
            g2.setColor(new Color(255, 100, 100));
            g2.drawString("🚪 PUERTA CERRADA", 300, 80);
        }
    }

    // ===== MOUSE =====
    @Override
    public void mousePressed(MouseEvent e) {

        for (int i = 0; i < piezasEnSlots.length; i++) {
            Pieza p = piezasEnSlots[i];
            if (p != null && p.contains(e.getX(), e.getY())) {
                piezasEnSlots[i] = null;
                p.colocada = false;

                seleccionada = p;
                offsetX = e.getX() - p.x;
                offsetY = e.getY() - p.y;
                return;
            }
        }

        for (Pieza p : nivel.piezas) {
            if (!p.colocada && p.contains(e.getX(), e.getY())) {
                seleccionada = p;
                offsetX = e.getX() - p.x;
                offsetY = e.getY() - p.y;
                break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (seleccionada != null) {
            seleccionada.x = e.getX() - offsetX;
            seleccionada.y = e.getY() - offsetY;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (seleccionada != null) {

            boolean colocadaEnSlot = false;

            for (int i = 0; i < slots.length; i++) {
                if (slots[i].contains(e.getPoint()) && piezasEnSlots[i] == null) {
                    piezasEnSlots[i] = seleccionada;
                    seleccionada.colocada = true;
                    colocadaEnSlot = true;
                    break;
                }
            }

            if (!colocadaEnSlot) {
                seleccionada.colocada = false;
                seleccionada.y = 320;
            }

            seleccionada = null;
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public boolean gano() {
        return gano;
    }
}