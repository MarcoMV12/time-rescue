package minijuego;


import java.util.ArrayList;
import java.util.Random;

public class NivelManager {

    public int objetivo;
    public int voltajeBase;
    public ArrayList<Pieza> piezas = new ArrayList<>();

    public void generarNivel() {
        Random rand = new Random();

        voltajeBase = 5 + rand.nextInt(11);
        piezas.clear();

        ArrayList<Integer> valores = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            int val = rand.nextBoolean() ? (2 + rand.nextInt(9)) : -(2 + rand.nextInt(5));
            valores.add(val);
        }

        int xPos = 50;
        for (int val : valores) {
            piezas.add(new Pieza(xPos, 320, val));
            xPos += 90;
        }

        boolean encontrado = false;

        while (!encontrado) {
            int i = rand.nextInt(valores.size());
            int j = rand.nextInt(valores.size());
            int k = rand.nextInt(valores.size());

            if (i != j && j != k && i != k) {
                int suma = valores.get(i) + valores.get(j) + valores.get(k);
                int posibleObjetivo = voltajeBase + suma;

                if (posibleObjetivo >= 5 && posibleObjetivo <= 30) {
                    objetivo = posibleObjetivo;
                    encontrado = true;
                }
            }
        }
    }
}
