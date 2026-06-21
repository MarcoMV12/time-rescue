package Objects;

import static utilz.Constantes.Objetos.COCO;
import Juegos.Juego;

public class Coco extends objeto {

    public enum Estado {
        CAYENDO, EXPLOTANDO, ESPERANDO
    }

    private Estado estado = Estado.ESPERANDO;
    private int esperaTick = 0;
    private int esperaMax = 180;
    private int explotaTick = 0;
    private int explotaMax = 40;
    private int delayInicial;

    private float velY = 0;
    private float gravedad = 0.15f;

    private int startX;
    private int startY;

    public Coco(int x, int y, int delayInicial) {
        super(x, y, COCO);
        this.startX = x;
        this.startY = y+25;
        this.delayInicial = delayInicial;
        this.esperaTick = -delayInicial;
        initHitbox(32, 32);
        hitbox.width  = 32;
        hitbox.height = 32;
        hitbox.x = x + 10;
        hitbox.y = y + 25;
        animVelocidad = 8;
    }

    @Override
    protected int getSpriteAmount() {
        return 7;
    }

    public void update(int[][] lvlData) {
        switch (estado) {

            case ESPERANDO -> {
                esperaTick++;
                if (esperaTick >= esperaMax) {
                    esperaTick = 0;
                    iniciarCaida();
                }
            }

           case CAYENDO -> {
    velY += gravedad;
    int pasos = Math.max(1, (int) velY);
    for (int i = 0; i < pasos; i++) {
        hitbox.y += 1;
        float tileX = (hitbox.x + hitbox.width / 2) / Juego.TILES_SIZE;
        float tileY = (hitbox.y + hitbox.height) / Juego.TILES_SIZE;
        if (tileY < lvlData.length && tileX >= 0 && tileX < lvlData[0].length) {
            int valor = lvlData[(int) tileY][(int) tileX];
            int[] transparentes = {11, 72, 84, 49, 52, 40};
            boolean solido = true;
            for (int t : transparentes)
                if (valor == t) { solido = false; break; }
            if (solido && valor != 0) {
                // posición exacta encima del tile
                hitbox.y  = tileY * Juego.TILES_SIZE - hitbox.height;
                estado    = Estado.EXPLOTANDO;
                animIndex = 3;
                animTick  = 0;
                velY      = 0;
                break;
            }
        }
    }
    animTick++;
    if (animTick >= animVelocidad) {
        animTick = 0;
        if (animIndex < 2) animIndex++;
    }
}

            case EXPLOTANDO -> {
                explotaTick++;
                if (explotaTick >= animVelocidad) {
                    explotaTick = 0;
                    animIndex++;
                    if (animIndex >= getSpriteAmount()) {
                        activo = false;
                        estado = Estado.ESPERANDO;
                        esperaTick = 0;
                        resetPosicion();
                    }
                }
            }
        }
    }

    private void iniciarCaida() {
        estado = Estado.CAYENDO;
        activo = true;
        velY = 0;
        animIndex = 0;
        animTick = 0;
        hitbox.x = startX + 6;
        hitbox.y = startY;
    }

    private void resetPosicion() {
        hitbox.x = startX + 6;
        hitbox.y = startY;
        animIndex = 0;
    }

   private boolean tocaSuelo(int[][] lvlData) {
    float tileX = (hitbox.x + hitbox.width / 2) / Juego.TILES_SIZE;
    float tileY = (hitbox.y + hitbox.height)     / Juego.TILES_SIZE;
    if (tileY >= lvlData.length || tileX < 0 || tileX >= lvlData[0].length) return true;
    int valor = lvlData[(int) tileY][(int) tileX];
    int[] transparentes = {11, 72, 84, 49, 52, 40};
    for (int t : transparentes) if (valor == t) return false;
    return valor != 0;
}

    public boolean isCayendo() {
        return estado == Estado.CAYENDO;
    }

    public boolean isExplotando() {
        return estado == Estado.EXPLOTANDO;
    }

    public Estado getEstado() {
        return estado;
    }
}