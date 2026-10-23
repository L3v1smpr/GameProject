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

    public Ship(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    public void update(float delta, Vector2 movementDirection, float worldWidth, float worldHeight) {
        if (invulnerabilityTimer > 0f) {
            invulnerabilityTimer = Math.max(0f, invulnerabilityTimer - delta);
        }
        if (dashCooldownTimer > 0f) {
            dashCooldownTimer = Math.max(0f, dashCooldownTimer - delta);
        }

        float currentSpeed = speed;

        if (dashDurationTimer > 0f) {
            dashDurationTimer = Math.max(0f, dashDurationTimer - delta);
            currentSpeed = speed * GameConfig.DASH_SPEED_MULTIPLIER;
            movementDirection = dashDirection;
        }

        float newX = getX() + movementDirection.x * currentSpeed * delta;
        float newY = getY() + movementDirection.y * currentSpeed * delta;

        setX(Math.max(0f, Math.min(worldWidth - getWidth(), newX)));
        setY(Math.max(0f, Math.min(worldHeight - getHeight(), newY)));
    }

    public void triggerDash(Vector2 inputDirection) {
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
        setX(startX);
        setY(startY);
        this.invulnerabilityTimer = invulnerabilitySeconds;
        this.dashDurationTimer = 0f;
        this.dashCooldownTimer = 0f;
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0f || dashDurationTimer > 0f;
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}