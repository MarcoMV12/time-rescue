package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.CanMoveHere;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Dron extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;
    private int attackDir;
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY = 60;


    private java.util.ArrayList<Bullet> bullets = new java.util.ArrayList<>();
    

    public Dron(float x, float y) {
        super(x, y, DRON_WIDTH, DRON_HEIGHT, DRON);
        attackDistance = 200 * Juego.SCALE; 
        initHitbox(x, y, (int)(60 * Juego.SCALE), (int)(28 * Juego.SCALE));
        tileYPos = (int)(y / Juego.TILES_SIZE); 
        initAttackBox();
        newState(DRON_RUN); 
    }

        @Override
    protected void onDeath() {
        newState(DRON_DIE);
    }

    @Override
    protected void onHurt() {
        newState(DRON_HURT);
    }

    public void hurt(int damage) {
        if (!alive || currentHealth <= 0) return; 
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            onDeath();
        } else {
            onHurt();
        }
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, 
            (int)(20 * Juego.SCALE), (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    @Override
    protected void move(int[][] lvlData) {
        // 🔥 el dron solo verifica paredes, no suelo
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            changeWalkDir();
        }
    }

    public void update4(int[][] lvlData, Jugador4 jugador) {
        updateBehavior(lvlData, jugador);
        updateAttackBox();
        updateAnimationTick();
        updateBullets(lvlData, jugador); 
        if (shootCooldown > 0) shootCooldown--;
    }

    protected void updateAnimationTick() {
    animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case DRON_ATTACK, DRON_HURT -> newState(DRON_RUN);
                    case DRON_DIE -> alive = false;
                }
            }
        }
    }

    private void updateAttackBox() {
        if (attackDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width;
        else
            attackBox.x = hitbox.x + hitbox.width;
        attackBox.y = hitbox.y;
    }

    private void updateBullets(int[][] lvlData, Jugador4 jugador) {
    bullets.removeIf(b -> !b.active);
    for (Bullet b : bullets)
        b.update4(lvlData, jugador);
    }

    private void shoot(Jugador4 jugador) { 
        if (shootCooldown > 0) return;
        float bx = (walkDir == RIGHT)
            ? hitbox.x + hitbox.width
            : hitbox.x - Bullet.WIDTH;
        float by = hitbox.y + (hitbox.height / 2f) - (Bullet.HEIGHT / 2f);
        

        float targetY = jugador.getHitbox().y + jugador.getHitbox().height / 2f;
        bullets.add(new Bullet(bx, by, walkDir, targetY));
        shootCooldown = SHOOT_DELAY;
    }


    @Override
    protected boolean canSeePlayer(int[][] lvlData, Jugador4 jugador) {
        float playerCenterX = jugador.getHitbox().x + jugador.getHitbox().width / 2f;
        float dronCenterX   = hitbox.x + hitbox.width / 2f;
        float playerCenterY = jugador.getHitbox().y + jugador.getHitbox().height / 2f;
        float dronCenterY   = hitbox.y + hitbox.height / 2f;

        float diffX = Math.abs(playerCenterX - dronCenterX);
        float diffY = playerCenterY - dronCenterY;

        return diffX <= attackDistance
            && diffY >= 0
            && diffY <= 5 * Juego.TILES_SIZE;
    }

    public void updateBehavior(int[][] lvlData, Jugador4 jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case DRON_RUN:
                if (canSeePlayer(lvlData, jugador)) {
                    turnTowardsPlayer(jugador);
                    shoot(jugador); 
                    if (isPlayerCloseForAttack(jugador)) {
                        attackDir = walkDir;
                        attackChecked = false;
                        newState(DRON_ATTACK);
                        break;
                    }
                }
                move(lvlData);
                break;

                case DRON_ATTACK:
                    if (animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);
                    break;

                default:
                    break;
            }
        }
    }

    public java.util.ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        inAir = false;
        attackChecked = false;
        attackDir = RIGHT;
        tileYPos = (int)(y / Juego.TILES_SIZE);
        currentHealth = maxHealth;
        newState(DRON_RUN);
        alive = true;
        airSpeed = 0;
        bullets.clear();
        shootCooldown = 0;
    }

    public int flipX() {
        int dir = (enemyState == DRON_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return DRON_WIDTH / 2;
        else
            return 0;
    }

    public int flipW() {
        int dir = (enemyState == DRON_ATTACK) ? attackDir : walkDir;
        if (dir == LEFT)
            return -1;
        else
            return 1;
    }
}