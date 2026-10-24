package puppy.code.behaviors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

/**
 * Estrategia de movimiento horizontal para el bombardero (patrón Strategy).
 * Desciende suavemente hasta su altitud de crucero y luego patrulla
 * alternando continuamente entre el Este y el Oeste.
 */
public final class HorizontalMovementStrategy implements MovementStrategy {

    private final float cruiseY;
    private final float speed;
    private final float descentSpeed;
    private float directionX;

    public HorizontalMovementStrategy(
            float cruiseY, float speed, float descentSpeed, float initialDirectionX) {
        this.cruiseY = cruiseY;
        this.speed = Math.max(0f, speed);
        this.descentSpeed = Math.max(0f, descentSpeed);
        this.directionX = initialDirectionX >= 0f ? 1f : -1f;
    }

    public HorizontalMovementStrategy(float cruiseY, float speed) {
        this(cruiseY, speed, 120f, MathUtils.randomBoolean() ? 1f : -1f);
    }

    @Override
    public float speed() {
        return speed;
    }

    @Override
    public Vector2 nextPosition(float x, float y, float age, float delta) {
        float nextY = y;
        if (y > cruiseY) {
            nextY = Math.max(cruiseY, y - descentSpeed * delta);
        }

        float nextX = x + directionX * speed * delta;
        float minX = GameConfig.ALIEN_SPAWN_MARGIN;
        float maxX = GameConfig.WORLD_WIDTH - GameConfig.ALIEN_WIDTH - GameConfig.ALIEN_SPAWN_MARGIN;

        if (nextX >= maxX) {
            nextX = maxX;
            directionX = -1f; // Virar hacia el Oeste
        } else if (nextX <= minX) {
            nextX = minX;
            directionX = 1f; // Virar hacia el Este
        }

        return new Vector2(nextX, nextY);
    }
}