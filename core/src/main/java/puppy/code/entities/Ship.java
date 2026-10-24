package puppy.code.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

public final class Ship extends GameEntity {

    private final float speed;
    private float invulnerabilityTimer;
    private float dashCooldownTimer;
    private float dashDurationTimer;
    private Vector2 dashDirection = new Vector2(0, 1);
    private boolean godMode;

    public Ship(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.speed = speed;
    }

    /**
     * Hitbox centrada de 26x48 px ajustada al cono triangular de la nave.
     * Evita colisiones indeseadas en las esquinas transparentes del sprite de 48x48 px.
     */
    @Override
    public Rectangle bounds() {
        return new Rectangle(
                getX() + GameConfig.SHIP_HITBOX_OFFSET_X,
                getY(),
                GameConfig.SHIP_HITBOX_WIDTH,
                GameConfig.SHIP_HITBOX_HEIGHT);
    }

    public void update(float delta, Vector2 movementDirection, float worldWidth, float worldHeight) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (movementDirection == null) {
            throw new IllegalArgumentException("La dirección de movimiento no puede ser null.");
        }

        Vector2 normalDirection = movementDirection.cpy();
        if (normalDirection.len2() > 1f) {
            normalDirection.nor();
        }
        float dashSeconds = Math.min(delta, dashDurationTimer);
        float regularSeconds = delta - dashSeconds;
        float newX = getX()
                + dashDirection.x * speed * GameConfig.DASH_SPEED_MULTIPLIER * dashSeconds
                + normalDirection.x * speed * regularSeconds;
        float newY = getY()
                + dashDirection.y * speed * GameConfig.DASH_SPEED_MULTIPLIER * dashSeconds
                + normalDirection.y * speed * regularSeconds;

        setPositionInside(newX, newY, worldWidth, worldHeight);

        invulnerabilityTimer = Math.max(0f, invulnerabilityTimer - delta);
        dashCooldownTimer = Math.max(0f, dashCooldownTimer - delta);
        if (dashDurationTimer > 0f) {
            dashDurationTimer = Math.max(0f, dashDurationTimer - delta);
        }
    }

    public void triggerDash(Vector2 inputDirection) {
        if (inputDirection == null) {
            throw new IllegalArgumentException("La dirección del dash no puede ser null.");
        }
        if (dashCooldownTimer > 0f || dashDurationTimer > 0f) {
            return;
        }
        dashDurationTimer = GameConfig.DASH_DURATION_SECONDS;
        dashCooldownTimer = GameConfig.DASH_COOLDOWN_SECONDS;

        // Se extiende la invulnerabilidad durante el dash y el búfer de gracia calibrado
        float totalInvulnerability = GameConfig.DASH_DURATION_SECONDS + GameConfig.DASH_INVULNERABILITY_POST_SECONDS;
        invulnerabilityTimer = Math.max(invulnerabilityTimer, totalInvulnerability);

        if (inputDirection.len2() > 0f) {
            dashDirection.set(inputDirection).nor();
        } else {
            dashDirection.set(0f, 1f);
        }
    }

    public void respawn(float startX, float startY, float invulnerabilitySeconds) {
        if (!Float.isFinite(invulnerabilitySeconds) || invulnerabilitySeconds < 0f) {
            throw new IllegalArgumentException("La invulnerabilidad debe ser finita y no negativa.");
        }
        setPosition(startX, startY);
        this.invulnerabilityTimer = invulnerabilitySeconds;
        this.dashDurationTimer = 0f;
        this.dashCooldownTimer = 0f;
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0f || dashDurationTimer > 0f;
    }

    public boolean canDash() {
        return dashCooldownTimer <= 0f && dashDurationTimer <= 0f;
    }

    /** Devuelve el porcentaje de recarga del dash en un rango normalizado de 0.0 a 1.0. */
    public float getDashCooldownProgress() {
        if (canDash()) {
            return 1.0f;
        }
        if (dashCooldownTimer <= 0f) {
            return 1.0f;
        }
        return Math.max(0f, Math.min(1f, 1f - (dashCooldownTimer / GameConfig.DASH_COOLDOWN_SECONDS)));
    }
    
    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }
}