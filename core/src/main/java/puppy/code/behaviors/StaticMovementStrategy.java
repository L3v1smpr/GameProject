package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/**
 * Estrategia de movimiento estático con entrada suave (patrón Strategy).
 * Desciende desde fuera de la pantalla hasta su altitud objetivo y luego permanece fija como torreta.
 */
public final class StaticMovementStrategy implements MovementStrategy {

    private final float targetY;
    private final float descentSpeed;
    private final boolean hasTarget;

    public StaticMovementStrategy(float targetY, float descentSpeed) {
        this.targetY = targetY;
        this.descentSpeed = Math.max(0f, descentSpeed);
        this.hasTarget = true;
    }

    public StaticMovementStrategy() {
        this.targetY = 0f;
        this.descentSpeed = 0f;
        this.hasTarget = false;
    }

    @Override
    public float speed() {
        return 0f; // Permanece como entidad estática para las reglas de oleadas
    }

    @Override
    public Vector2 nextPosition(float x, float y, float age, float delta) {
        if (hasTarget && y > targetY && descentSpeed > 0f) {
            float nextY = Math.max(targetY, y - descentSpeed * delta);
            return new Vector2(x, nextY);
        }
        return new Vector2(x, hasTarget ? targetY : y);
    }
}