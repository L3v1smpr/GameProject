package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/** Descenso en línea recta hacia la parte inferior de la pantalla. */
public final class LinearMovementStrategy implements MovementStrategy {

    private final float speed;

    public LinearMovementStrategy(float speed) {
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.speed = speed;
    }

    @Override
    public float speed() {
        return speed;
    }

    @Override
    public Vector2 nextPosition(float x, float y, float age, float delta) {
        return new Vector2(x, y - speed * delta);
    }
}
