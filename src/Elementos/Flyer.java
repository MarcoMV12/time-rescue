package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.CanMoveHere;
import static utilz.MetodosAyuda.IsFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Flyer extends Enemigo{
    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    // ── Patrullaje aéreo ──────────────────────────────────────────
    private float yOriginal;                 // altura de vuelo "normal" (patrulla)
    private static final float VEL_PATRULLA = 0.4f * Juego.SCALE;
    
    // ── Modo ataque ───────────────────────────────────────────────
    private boolean atacando = false;
    private boolean regresando = false;       // volviendo a yOriginal después de embestir
    private static final float VEL_ATAQUE   = 1.25f * Juego.SCALE;
    private static final float VEL_REGRESO  = 0.8f * Juego.SCALE;
    private float velAtaqueX = 0f, velAtaqueY = 0f;
    
    // ── Cooldown ──────────────────────────────────────────────────
    private long ultimoAtaque = 0;
    private static final long COOLDOWN_MS = 3000;
    
    // ── Distancias ────────────────────────────────────────────────
    private static final float RANGO_DETECCION = 200f * Juego.SCALE;
    private static final float DISTANCIA_FIN_EMBESTIDA = 30f * Juego.SCALE;

    private int ticksEnAtaque = 0;
    private static final int MAX_TICKS_ATAQUE = 200;

    public Flyer(float x, float y) {
        super(x, y, FLYER_WIDTH, FLYER_HEIGHT, FLYER);
                              //FLYER_WIDTH/3            //FLYER_HEIGHT/3 arreglao
        initHitbox(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        initAttackBox();
        yOriginal = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        //attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update(int [][] lvlData, Jugador jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        //updateAttackBox();
    }

    private void updateAttackBox() {

        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width ;
        else
            attackBox.x = hitbox.x + hitbox.width ;
        attackBox.y = hitbox.y;
        // if (walkDir == LEFT)
        //     attackBox.x = hitbox.x - attackBox.width +10;
        // else
        //     attackBox.x = hitbox.x + hitbox.width -10; // el attackbox se dibuja bien falta que funcione bien
        
        // attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador jugador) {
        if (firstUpdate){
            //firstUpdateCheck(lvlData);
            firstUpdate = false;
            yOriginal = hitbox.y;
        }

        switch (enemyState) {
            case FLYER_RUN:
                comportamientoPatrulla(lvlData, jugador);
                break;
            case FLYER_ATTACK:
                comportamientoAtaque(lvlData, jugador);
                break;
            case FLYER_HURT:
                // Animación bloqueante; no se mueve
                break;
            default:
                break;
        }
    }

    private void comportamientoPatrulla(int[][] lvlData, Jugador jugador) {
        // Vuela horizontalmente
        moverHorizontal(lvlData, VEL_PATRULLA);
        
        // ¿Detecta al jugador y cooldown listo?
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoAtaque < COOLDOWN_MS) return;
        
        if (puedeVerJugador(jugador)) {
            iniciarEmbestida(jugador);
        }
    }

    private void comportamientoAtaque(int[][] lvlData, Jugador jugador) {
        ticksEnAtaque++;
        
        // Timeout de seguridad: si el ataque se alarga demasiado, regresar
        if (ticksEnAtaque > MAX_TICKS_ATAQUE && !regresando) {
            regresando = true;
            attackChecked = true;
        }
        
        if (!regresando) {
            // FASE 1: embestida hacia el jugador, validando colisiones
            float sigX = hitbox.x + velAtaqueX;
            float sigY = hitbox.y + velAtaqueY;
            
            // Verifica si puede moverse a la posición siguiente
            if (CanMoveHere(sigX, sigY, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x = sigX;
                hitbox.y = sigY;
            } else {
                // Chocó con una superficie → regresa inmediatamente
                regresando = true;
                attackChecked = true;
            }
            
            // Verifica que no se salga del mapa horizontalmente
            int maxX = lvlData[0].length * Juego.TILES_SIZE;
            if (hitbox.x < 0 || hitbox.x + hitbox.width > maxX) {
                regresando = true;
                attackChecked = true;
            }
            
            // Daño si toca al jugador
            if (!attackChecked && attackBox.intersects(jugador.getHitbox())) {
                checkEnemyHit(attackBox, jugador);
            }
            
            // ¿Llegó cerca del jugador? — empezar a regresar
            float dx = jugador.getHitbox().x - hitbox.x;
            float dy = jugador.getHitbox().y - hitbox.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (dist < DISTANCIA_FIN_EMBESTIDA) {
                regresando = true;
                attackChecked = true;
            }
        } else {
            // FASE 2: regreso a la altura original
            float dy = yOriginal - hitbox.y;
            if (Math.abs(dy) < 2f) {
                hitbox.y = yOriginal;
                terminarAtaque();
            } else {
                // Solo sube si puede
                float sigY = hitbox.y + Math.signum(dy) * VEL_REGRESO;
                if (CanMoveHere(hitbox.x, sigY, hitbox.width, hitbox.height, lvlData)) {
                    hitbox.y = sigY;
                } else {
                    // No puede subir, terminar el ataque aquí
                    terminarAtaque();
                    return;
                }
                
                // Avanza lateral solo si puede
                float sigX = hitbox.x + (walkDir == LEFT ? -VEL_PATRULLA : VEL_PATRULLA);
                if (CanMoveHere(sigX, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                    hitbox.x = sigX;
                }
            }
        }
    }

    private void iniciarEmbestida(Jugador jugador) {
        atacando = true;
        regresando = false;
        attackChecked = false;
        ticksEnAtaque = 0;
        newState(FLYER_ATTACK);
        
        // Calcula vector velocidad hacia el jugador
        float dx = jugador.getHitbox().x - hitbox.x;
        float dy = jugador.getHitbox().y - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1f;
        
        velAtaqueX = (dx / dist) * VEL_ATAQUE;
        velAtaqueY = (dy / dist) * VEL_ATAQUE;
        
        // Actualiza la dirección de vuelo para flip del sprite
        walkDir = (dx > 0) ? RIGHT : LEFT;
    }

    private void terminarAtaque() {
        atacando = false;
        regresando = false;
        ticksEnAtaque = 0;
        ultimoAtaque = System.currentTimeMillis();
        newState(FLYER_RUN);
    }

    private boolean puedeVerJugador(Jugador jugador) {
        float dx = jugador.getHitbox().x - hitbox.x;
        float dy = jugador.getHitbox().y - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        return dist < RANGO_DETECCION;
    }

    private void moverHorizontal(int[][] lvlData, float vel) {
        float xSpeed = (walkDir == LEFT) ? -vel : vel;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, 
                        hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            changeWalkDir();
        }
    }

    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(FLYER_DIE);
        } else {
            newState(FLYER_HURT);
            // Si lo golpean en plena embestida, cancela
            if (atacando) {
                atacando = false;
                regresando = false;
                ultimoAtaque = System.currentTimeMillis();
            }
        }
    }

    protected void updateAnimationTick(){
        animTick++;
        if(animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if(animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case FLYER_HURT -> {
                        // Después de doler, vuelve a volar normal
                        newState(FLYER_RUN);
                    }
                    case FLYER_DIE -> alive = false;
                    // Ojo: NO terminamos FLYER_ATTACK aquí — se termina por distancia
                }

                // switch (enemyState) {
                //     case FLYER_ATTACK, FLYER_HURT -> enemyState = FLYER_RUN;
                //     case FLYER_DIE -> alive = false;
                // }
            }
        }
    }

    public void resetEnemy(){
        hitbox.x =x;
        hitbox.y = y;
        yOriginal = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(FLYER_RUN);//FLYER_IDLE/2
        alive = true;
        atacando = false;
        regresando = false;
        ultimoAtaque = 0;
        attackChecked = false;
        //airSpeed = 0;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return FLYER_WIDTH/2;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == RIGHT)//return walkDir == LEFT ? -1 : 1;
            return -1;
        else
            return 1;
    }

    protected void updateInAir(int [][] lvlData) {
        inAir = false;
    }

    protected void firstUpdateCheck(int [][] lvlData) {
        //if (!IsEntityOnFloor(hitbox, lvlData)) 
            inAir = false;
            firstUpdate = false;
    }

    protected void move(int [][] lvlData) {
        
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            
                hitbox.x += xSpeed;
            //changeWalkDir();
            //return;
        }
        else
        //if (!isChasingPlayer)
            changeWalkDir();

    }

}
