package puppy.code.behaviors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

public final class HorizontalMovementStrategy implements MovementStrategy {

    private final float cruiseY;
    private final float speed;
    private final float descentSpeed;
    private final float alienWidth;
    private float directionX;

    public HorizontalMovementStrategy(
            float cruiseY, float speed, float descentSpeed, float alienWidth, float initialDirectionX) {
        this.cruiseY = cruiseY;
        this.speed = Math.max(0f, speed);
        this.descentSpeed = Math.max(0f, descentSpeed);
        this.alienWidth = alienWidth > 0f ? alienWidth : GameConfig.ALIEN_WIDTH;
        this.directionX = initialDirectionX >= 0f ? 1f : -1f;
    }

    public HorizontalMovementStrategy(float cruiseY, float speed, float alienWidth) {
        this(cruiseY, speed, 120f, alienWidth, MathUtils.randomBoolean() ? 1f : -1f);
    }

    public HorizontalMovementStrategy(float cruiseY, float speed) {
        this(cruiseY, speed, GameConfig.ALIEN_WIDTH);
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
        float maxX = GameConfig.WORLD_WIDTH - alienWidth - GameConfig.ALIEN_SPAWN_MARGIN;

        if (nextX >= maxX) {
            nextX = maxX;
            directionX = -1f;
        } else if (nextX <= minX) {
            nextX = minX;
            directionX = 1f;
        }

        return new Vector2(nextX, nextY);
    }
}