package Elementos;

public enum Personaje {
    //Cambiar valores
    CAVERNICOLA("Personajes/caveman3.png",  100, 1.5f, 20, 75,  65,  null),
    EGIPCIO    ("Personajes/ninja.png",      80, 2.0f, 15, 75,  65,  null),
    CABALLERO  ("Personajes/Caballero.png", 150, 1.2f, 30, 128, 112, null),
    CIENTIFICO ("Personajes/CientificoSprite8.png", 90, 1.4f, 25, 64, 128, null);

    public final String spriteAtlas;
    public final int maxHp;
    public final float velocidad;
    public final int dano;
    public final int frameW, frameH;
    /** Base path para animaciones cuadro a cuadro (Jugador2). null = usa la animación por defecto. */
    public final String frameSeqBase;

    Personaje(String spriteAtlas, int maxHp, float velocidad, int dano,
              int frameW, int frameH, String frameSeqBase) {
        this.spriteAtlas  = spriteAtlas;
        this.maxHp        = maxHp;
        this.velocidad    = velocidad;
        this.dano         = dano;
        this.frameW       = frameW;
        this.frameH       = frameH;
        this.frameSeqBase = frameSeqBase;
    }

    public static Personaje seleccionado = CAVERNICOLA;
}
