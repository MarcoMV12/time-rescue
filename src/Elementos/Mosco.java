package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.CanMoveHere;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Mosco extends Enemigo {
    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    // ── Patrullaje aéreo ──────────────────────────────────────────
    private float yOriginal;
    private static final float VEL_PATRULLA = 0.4f * Juego.SCALE;

    // ── Modo ataque ───────────────────────────────────────────────
    private boolean atacando = false;
    private boolean regresando = false;
    private static final float VEL_ATAQUE  = 1.25f * Juego.SCALE;
    private static final float VEL_REGRESO = 0.8f * Juego.SCALE;
    private float velAtaqueX = 0f, velAtaqueY = 0f;

    // ── Cooldown ──────────────────────────────────────────────────
    private long ultimoAtaque = 0;
    private static final long COOLDOWN_MS = 3000;

    // ── Distancias ────────────────────────────────────────────────
    private static final float RANGO_DETECCION = 200f * Juego.SCALE;
    private static final float DISTANCIA_FIN_EMBESTIDA = 30f * Juego.SCALE;

    private int ticksEnAtaque = 0;
    private static final int MAX_TICKS_ATAQUE = 200;

    public Mosco(float x, float y) {
        super(x, y, MOSCO_WIDTH, MOSCO_HEIGHT, MOSCO);
        initHitbox(x, y, (int)(55 * Juego.SCALE), (int)(23 * Juego.SCALE));
        initAttackBox();
        yOriginal = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
    }

    public void update(int [][] lvlData, Jugador jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

    public void updateBehavior(int [][] lvlData, Jugador jugador) {
        if (firstUpdate) {
            firstUpdate = false;
            yOriginal = hitbox.y;
        }

        switch (enemyState) {
            case MOSCO_RUN:
                comportamientoPatrulla(lvlData, jugador);
                break;
            case MOSCO_ATTACK:
                comportamientoAtaque(lvlData, jugador);
                break;
            case MOSCO_HURT:
                break;
            default:
                break;
        }
    }

    private void comportamientoPatrulla(int[][] lvlData, Jugador jugador) {
        moverHorizontal(lvlData, VEL_PATRULLA);

        long ahora = System.currentTimeMillis();
        if (ahora - ultimoAtaque < COOLDOWN_MS) return;

        if (puedeVerJugador(jugador)) {
            iniciarEmbestida(jugador);
        }
    }

    private void comportamientoAtaque(int[][] lvlData, Jugador jugador) {
        ticksEnAtaque++;

        if (ticksEnAtaque > MAX_TICKS_ATAQUE && !regresando) {
            regresando = true;
            attackChecked = true;
        }

        if (!regresando) {
            float sigX = hitbox.x + velAtaqueX;
            float sigY = hitbox.y + velAtaqueY;

            if (CanMoveHere(sigX, sigY, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x = sigX;
                hitbox.y = sigY;
            } else {
                regresando = true;
                attackChecked = true;
            }

            int maxX = lvlData[0].length * Juego.TILES_SIZE;
            if (hitbox.x < 0 || hitbox.x + hitbox.width > maxX) {
                regresando = true;
                attackChecked = true;
            }

            if (!attackChecked && attackBox.intersects(jugador.getHitbox())) {
                checkEnemyHit(attackBox, jugador);
            }

            float dx = jugador.getHitbox().x - hitbox.x;
            float dy = jugador.getHitbox().y - hitbox.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < DISTANCIA_FIN_EMBESTIDA) {
                regresando = true;
                attackChecked = true;
            }
        } else {
            float dy = yOriginal - hitbox.y;
            if (Math.abs(dy) < 2f) {
                hitbox.y = yOriginal;
                terminarAtaque();
            } else {
                float sigY = hitbox.y + Math.signum(dy) * VEL_REGRESO;
                if (CanMoveHere(hitbox.x, sigY, hitbox.width, hitbox.height, lvlData)) {
                    hitbox.y = sigY;
                } else {
                    terminarAtaque();
                    return;
                }

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
        newState(MOSCO_ATTACK);

        float dx = jugador.getHitbox().x - hitbox.x;
        float dy = jugador.getHitbox().y - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1f;

        velAtaqueX = (dx / dist) * VEL_ATAQUE;
        velAtaqueY = (dy / dist) * VEL_ATAQUE;

        walkDir = (dx > 0) ? RIGHT : LEFT;
    }

    private void terminarAtaque() {
        atacando = false;
        regresando = false;
        ticksEnAtaque = 0;
        ultimoAtaque = System.currentTimeMillis();
        newState(MOSCO_RUN);
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
            newState(MOSCO_DIE);
        } else {
            newState(MOSCO_HURT);
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
                    case MOSCO_HURT -> newState(MOSCO_RUN);
                    case MOSCO_DIE -> alive = false;
                }
            }
        }
    }

    public void resetEnemy(){
        hitbox.x = x;
        hitbox.y = y;
        yOriginal = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(MOSCO_RUN);
        alive = true;
        atacando = false;
        regresando = false;
        ultimoAtaque = 0;
        attackChecked = false;
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
            return MOSCO_WIDTH;
        else
            return 0;
    }

    public int flipW() {
        if(walkDir == RIGHT)
            return -1;
        else
            return 1;
    }

    protected void updateInAir(int [][] lvlData) {
        inAir = false;
    }

    protected void firstUpdateCheck(int [][] lvlData) {
        inAir = false;
        firstUpdate = false;
    }

    protected void move(int [][] lvlData) {
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            changeWalkDir();
        }
    }
}
