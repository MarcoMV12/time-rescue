package minijuego;


import java.awt.*;

public class Pieza {
    public int x, y, w = 70, h = 70;
    public int valor;
    public String texto;
    public boolean colocada = false;

    public Pieza(int x, int y, int valor) {
        this.x = x;
        this.y = y;
        this.valor = valor;
        this.texto = (valor > 0 ? "+" : "") + valor + "V";
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (valor > 0) g2.setColor(new Color(70, 200, 120));
        else g2.setColor(new Color(220, 80, 80));

        g2.fillRoundRect(x, y, w, h, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, w, h, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(texto, x + 15, y + 40);
    }

    public boolean contains(int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
