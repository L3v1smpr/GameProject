package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/** Descenso en línea recta hacia la parte inferior de la pantalla. */
public final class LinearMovement implements MovementStrategy {

    private final float speed;

    public LinearMovement(float speed) {
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.speed = speed;
    }

    @Override
    public Vector2 nextPosition(float x, float y, float age, float delta) {
        return new Vector2(x, y - speed * delta);
    }
}
