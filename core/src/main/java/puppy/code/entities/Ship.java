package puppy.code.entities;

import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

public final class Ship extends GameEntity {

    private final float speed;
    private float invulnerabilityTimer;
    private float dashCooldownTimer;
    private float dashDurationTimer;
    private Vector2 dashDirection = new Vector2(0, 1);

    public Ship(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.speed = speed;
    }

    public void update(float delta, Vector2 movementDirection, float worldWidth, float worldHeight) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (movementDirection == null) {
            throw new IllegalArgumentException("La dirección de movimiento no puede ser null.");
        }

        boolean dashing = dashDurationTimer > 0f;
        Vector2 direction = (dashing ? dashDirection : movementDirection).cpy();
        if (direction.len2() > 1f) {
            direction.nor();
        }

        float currentSpeed = dashing ? speed * GameConfig.DASH_SPEED_MULTIPLIER : speed;
        float newX = getX() + direction.x * currentSpeed * delta;
        float newY = getY() + direction.y * currentSpeed * delta;

        setPositionInside(newX, newY, worldWidth, worldHeight);

        invulnerabilityTimer = Math.max(0f, invulnerabilityTimer - delta);
        dashCooldownTimer = Math.max(0f, dashCooldownTimer - delta);
        if (dashing) {
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
        invulnerabilityTimer = Math.max(invulnerabilityTimer, GameConfig.DASH_DURATION_SECONDS);

        if (inputDirection.len2() > 0f) {
            dashDirection.set(inputDirection).nor();
        } else {
            dashDirection.set(0f, 1f); // Por defecto hacia arriba si no hay dirección
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
}
