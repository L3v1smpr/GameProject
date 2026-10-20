package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/**
 * Descenso diagonal que cambia de lado cada medio periodo.
 *
 * <p>La velocidad indicada es la total: se reparte en partes iguales entre el avance horizontal y
 * el descenso para que el zigzag no sea más rápido de lo que indica la cifra.
 */
public final class ZigZagMovementStrategy implements MovementStrategy {

    private static final float DIAGONAL_FACTOR = (float) Math.sqrt(0.5);

    private final float speed;
    private final float halfPeriodSeconds;

    public ZigZagMovementStrategy(float speed, float halfPeriodSeconds) {
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        if (!Float.isFinite(halfPeriodSeconds) || halfPeriodSeconds <= 0f) {
            throw new IllegalArgumentException("El medio periodo debe ser positivo y finito.");
        }
        this.speed = speed;
        this.halfPeriodSeconds = halfPeriodSeconds;
    }

    @Override
    public float speed() {
        return speed;
    }

    @Override
    public Vector2 nextPosition(float x, float y, float age, float delta) {
        int step = (int) (age / halfPeriodSeconds);
        float direction = step % 2 == 0 ? 1f : -1f;
        float component = speed * DIAGONAL_FACTOR * delta;
        return new Vector2(x + direction * component, y - component);
    }
}
