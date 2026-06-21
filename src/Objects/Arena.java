package Objects;

import static utilz.Constantes.Objetos.ARENA;

public class Arena extends objeto {

    public enum Estado { OCULTO, VISIBLE, CAYENDO }

    private Estado estado = Estado.OCULTO;
    private int animVelocidadCaida = 6;

    public Arena(int x, int y, int ordenEnGrupo) {
        super(x, y, ARENA);
        initHitbox(64, 58);
        this.animVelocidad = animVelocidadCaida;
    }

    @Override
    protected int getSpriteAmount() { return 5; }

    public void update() {
        if (estado == Estado.CAYENDO) {
            animTick++;
            if (animTick >= animVelocidad) {
                animTick = 0;
                animIndex++;
                if (animIndex >= getSpriteAmount()) {
                    animIndex = getSpriteAmount() - 1;
                    estado = Estado.OCULTO; // desaparece al terminar
                    activo = false;
                }
            }
        }
    }

    public void aparecer() {
        estado = Estado.VISIBLE;
        animIndex = 0;
        animTick = 0;
        activo = true;
    }

    public void activarCaida() {
        if (estado == Estado.VISIBLE) {
            estado = Estado.CAYENDO;
            animIndex = 0;
            animTick = 0;
        }
    }

    public void resetParaFase() {
        estado    = Estado.OCULTO;
        animIndex = 0;
        animTick  = 0;
        activo    = false;
    }

    public boolean isCayendo()  { return estado == Estado.CAYENDO; }
    public boolean isVisible()  { return estado == Estado.VISIBLE; }
    public boolean isOculto()   { return estado == Estado.OCULTO; }
    public Estado getEstado()   { return estado; }
}