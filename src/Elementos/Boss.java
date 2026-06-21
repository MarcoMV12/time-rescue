package Elementos;
import static utilz.Constantes.ConstantesEnemy.*;
import static utilz.Constantes.Direccion.*;
import static utilz.MetodosAyuda.IsFloor;
import utilz.MetodosAyuda;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import Juegos.Juego;

public class Boss extends Enemigo {

    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    private static final int   WALK_DURATION = 60;
    private static final float JUMP_FORCE = 3.6f;

    private static final int LASER_FIRE_FRAME = 5;
    private static final int LASER_DURATION = 45;

    private static final int CHARGE_HP_THRESHOLD = 40;
    private static final int CHARGE_DURATION = 90;

    private static final int WEAK_SPOT_INTERVAL = 600;
    private static final int WEAK_SPOT_DURATION = 300;
    private static final int WEAK_SPOT_R = 13;
    private static final int WEAK_HEAD_Y = 53;  // px arriba de hitbox.y (zona cabeza)
    private static final int WEAK_TORSO_Y = 18;  // px arriba de hitbox.y (zona torso)

    private int weakSpotTimer  = 0;
    private boolean weakSpotActive = false;
    private boolean weakSpotOnHead = true;
    private Rectangle2D.Float weakSpot = null;

    private int patternTick = 0;
    private int cooldownTick = 0;
    private boolean jumpInitiated = false;
    private int chargeTick = 0;
    private static final int LASER_FIRE_COOLDOWN = 600; // ticks entre disparos (~4 s)
    private int laserFireCooldown  = LASER_FIRE_COOLDOWN;

    // láser
    private float laserStartX, laserStartY, laserEndX, laserEndY;
    private int laserTick = 0;
    private boolean laserDamageDealt = false;

    public Boss(float x, float y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS);

        initHitbox(x, y, (int)(32 * Juego.SCALE), (int)(32 * Juego.SCALE));

        attackDistance = 55 * Juego.SCALE;
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y,
            (int)(20 * Juego.SCALE),
            (int)(28 * Juego.SCALE));
        attackBoxOffsetX = (int)(10 * Juego.SCALE);
    }

    public void update(int[][] lvlData, Jugador jugador) {
        updateAttackBox();
        updateBehavior(lvlData, jugador);
        updateAnimationTick();
        updateWeakSpot();
        if (laserTick > 0) laserTick--;
    }

    private void updateAttackBox() {
        if (walkDir == LEFT)
            attackBox.x = hitbox.x - attackBox.width + 10;
        else
            attackBox.x = hitbox.x + hitbox.width - 10;
        attackBox.y = hitbox.y;
    }

    private void updateWeakSpot() {
        if (!alive) return;

        weakSpotTimer++;

        // Activar el punto al inicio de cada ciclo
        if (weakSpotTimer == 1) {
            weakSpotOnHead = Math.random() < 0.5;
            weakSpotActive = true;
        }

        // Desactivar después de WEAK_SPOT_DURATION
        if (weakSpotTimer == WEAK_SPOT_DURATION + 1) {
            weakSpotActive = false;
        }

        // Reiniciar ciclo
        if (weakSpotTimer >= WEAK_SPOT_INTERVAL) {
            weakSpotTimer = 0;
        }

        // Actualizar posición cada tick (el boss se mueve)
        if (weakSpotActive) {
            float cx = hitbox.x + hitbox.width / 2f - WEAK_SPOT_R;
            float cy = weakSpotOnHead
                ? hitbox.y - WEAK_HEAD_Y - WEAK_SPOT_R
                : hitbox.y - WEAK_TORSO_Y - WEAK_SPOT_R;
            if (weakSpot == null)
                weakSpot = new Rectangle2D.Float(cx, cy, WEAK_SPOT_R * 2, WEAK_SPOT_R * 2);
            else { weakSpot.x = cx; weakSpot.y = cy; }
        }
    }

    public void hurtIfWeakSpotHit(Rectangle2D.Float attackBox, int damage) {
        if (weakSpotActive && weakSpot != null && weakSpot.intersects(attackBox)) {
            hurt(damage);
        }
    }

    /** Devuelve el rectángulo del punto débil activo, o null si no está activo. */
    public Rectangle2D.Float getWeakSpot() {
        return (weakSpotActive) ? weakSpot : null;
    }

    public void drawWeakSpot(Graphics g, int xLvlOffset) {
        if (!weakSpotActive || weakSpot == null) return;

        // Pulso visual: alterna entre opaco y semi-transparente
        int pulse = (weakSpotTimer % 20 < 10) ? 220 : 140;
        Graphics2D g2d = (Graphics2D) g;

        int drawX = (int)(weakSpot.x - xLvlOffset);
        int drawY = (int) weakSpot.y;
        int diameter = WEAK_SPOT_R * 2;

        // Relleno rojo pulsante
        g2d.setColor(new Color(220, 20, 20, pulse));
        g2d.fillOval(drawX, drawY, diameter, diameter);

        // Borde blanco para que se vea sobre cualquier fondo
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(drawX, drawY, diameter, diameter);
        g2d.setStroke(new BasicStroke(1f));
    }

    public void updateBehavior(int[][] lvlData, Jugador jugador) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (!inAir) {
            if (laserFireCooldown > 0) laserFireCooldown--;
            if (laserFireCooldown <= 0 && enemyState == BOSS_RUN && isPlayerInLaserRange(jugador)) {
                laserFireCooldown = LASER_FIRE_COOLDOWN;
                turnTowardsPlayer(jugador);
                newState(BOSS_ATTACK2);
            }
        }

        if (inAir) {
            updateInAir(lvlData);
            if (enemyState == BOSS_JUMP)
                jumpMove(lvlData);

            if (!inAir && enemyState == BOSS_JUMP) {
                jumpInitiated = false;
                if (isPlayerCloseForAttack(jugador))
                    newState(BOSS_ATTACK);
                else {
                    newState(BOSS_RUN);
                    patternTick = WALK_DURATION;
                }
            }
        } else {
            switch (enemyState) {

                case BOSS_RUN:
                    if (IsFloor(hitbox, airSpeed, lvlData)) {
                        turnTowardsPlayer(jugador);
                        if (isPlayerCloseForAttack(jugador)) {
                            newState(BOSS_ATTACK);
                            break;
                        }
                    }
                    if (--patternTick <= 0) {
                        if (currentHealth <= CHARGE_HP_THRESHOLD && isPlayerinRange(jugador)) {
                            newState(BOSS_CHARGE);
                        } else {
                            patternTick = WALK_DURATION;
                        }
                        break;
                    }
                    move(lvlData);
                    break;

                case BOSS_ATTACK:
                    if (animIndex == 0) attackChecked = false;
                    if (animIndex == 3 && !attackChecked)
                        checkEnemyHit(attackBox, jugador);
                    break;

                case BOSS_CHARGE:
                    turnTowardsPlayer(jugador);
                    if (chargeTick < CHARGE_DURATION) {
                        chargeTick++;
                    } else {
                        chargeTick = 0;
                        newState(BOSS_JUMP);
                    }
                    break;

                case BOSS_JUMP:
                    if (!jumpInitiated) {
                        jumpInitiated = true;
                        inAir    = true;
                        airSpeed = -JUMP_FORCE;
                        turnTowardsPlayer(jugador);
                    }
                    break;

                case BOSS_ATTACK2:
                    if (animIndex == 0) laserDamageDealt = false;
                    if (animIndex == LASER_FIRE_FRAME && !laserDamageDealt)
                        fireLaser(jugador);
                    break;

                default:
                    break;
            }
        }
    }

    private void jumpMove(int[][] lvlData) {
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (MetodosAyuda.CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            hitbox.x += xSpeed;
        else
            changeWalkDir();
    }

    @Override
    protected void updateInAir(int[][] lvlData) {
        if (enemyState == BOSS_JUMP) {
            breakTilesInPath(lvlData);
        }
        super.updateInAir(lvlData);
    }

    private void breakTilesInPath(int[][] lvlData) {
        float nextY = hitbox.y + airSpeed;
        // Borde frontal: top al subir, bottom al bajar
        int checkTileY = (airSpeed < 0)
            ? (int)(nextY / Juego.TILES_SIZE)
            : (int)((nextY + hitbox.height) / Juego.TILES_SIZE);

        // No romper las filas del borde del nivel (fila 0 y 1)
        if (checkTileY <= 1 || checkTileY >= lvlData.length) return;

        int tileX1 = (int)(hitbox.x / Juego.TILES_SIZE);
        int tileX2 = (int)((hitbox.x + hitbox.width - 1) / Juego.TILES_SIZE);

        for (int tx = tileX1; tx <= tileX2; tx++) {
            if (tx >= 0 && tx < lvlData[0].length
                    && MetodosAyuda.IsTileSolid(tx, checkTileY, lvlData)) {
                lvlData[checkTileY][tx] = MetodosAyuda.EMPTY_TILE;
            }
        }
    }

    private static final float LASER_X_OFF = 0f;   // <-- cambiá este número
    private static final float LASER_Y_OFF = -35f;  // <-- cambiá este número

    private boolean isPlayerInLaserRange(Jugador jugador) {
        float dx = Math.abs(jugador.getHitbox().x - hitbox.x);
        float dy = Math.abs(jugador.getHitbox().y - hitbox.y);
        float maxDist = 9 * Juego.TILES_SIZE;
        return dx <= maxDist && dy <= maxDist;
    }

    private void fireLaser(Jugador jugador) {
        laserStartX = hitbox.x + hitbox.width / 2f + LASER_X_OFF;
        laserStartY = hitbox.y + LASER_Y_OFF;
        laserEndX   = jugador.getHitbox().x + jugador.getHitbox().width / 2f;
        laserEndY   = jugador.getHitbox().y + jugador.getHitbox().height / 2f;
        laserTick   = LASER_DURATION;
        laserDamageDealt = true;
        jugador.changeHealth(-10);
    }

    public void drawLaser(Graphics g, int lvlOffsetX) {
        if (laserTick <= 0) return;
        Graphics2D g2d = (Graphics2D) g;

        int sx = (int)(laserStartX - lvlOffsetX);
        int sy = (int) laserStartY;
        int ex = (int)(laserEndX   - lvlOffsetX);
        int ey = (int) laserEndY;

        g2d.setColor(new Color(35, 160, 40, 80));
        g2d.setStroke(new BasicStroke(14));
        g2d.drawLine(sx, sy, ex, ey);

        g2d.setColor(new Color(115, 233, 7, 220));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine(sx, sy, ex, ey);

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(sx, sy, ex, ey);

        g2d.setStroke(new BasicStroke(1));
    }

    // ----------------------------------------------------------------
    // DAÑO / MUERTE
    // ----------------------------------------------------------------
    public void hurt(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            newState(BOSS_DIE);
        } else {
            newState(BOSS_HURT);
        }
    }

    protected void updateAnimationTick() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpriteAmount(enemyType, enemyState)) {
                animIndex = 0;
                switch (enemyState) {
                    case BOSS_ATTACK  -> { newState(BOSS_RUN); patternTick = WALK_DURATION; }
                    case BOSS_ATTACK2 -> { newState(BOSS_RUN); patternTick = WALK_DURATION; }
                    case BOSS_HURT    -> { newState(BOSS_RUN); patternTick = WALK_DURATION; }
                    case BOSS_DIE     -> alive = false;
                }
            }
        }
    }

    public void resetEnemy() {
        hitbox.x      = x;
        hitbox.y      = y;
        firstUpdate   = true;
        currentHealth = maxHealth;
        newState(BOSS_RUN);
        alive         = true;
        airSpeed      = 0;
        patternTick   = WALK_DURATION;
        cooldownTick  = 0;
        jumpInitiated = false;
        chargeTick    = 0;
        laserTick     = 0;
        weakSpotTimer  = 0;
        weakSpotActive = false;
        weakSpot       = null;
    }

    public void drawHitbox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)(hitbox.x - xLvlOffset), (int)hitbox.y,
                   (int)hitbox.width, (int)hitbox.height);
    }

    public void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.orange);
        g.drawRect((int)(attackBox.x - lvlOffsetX), (int)attackBox.y,
                   (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if (walkDir == RIGHT) return BOSS_WIDTH + 65;
        else return 0;
    }

    public int flipW() {
        if (walkDir == RIGHT) return -1;
        else return 1;
    }
}
