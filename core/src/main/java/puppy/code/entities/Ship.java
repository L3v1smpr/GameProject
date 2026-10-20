package puppy.code.entities;

import com.badlogic.gdx.math.Vector2;

/** Entidad que controla el jugador; conoce su movimiento, pero no el teclado ni la pantalla. */
public final class Ship extends GameEntity {

    private final float speed;
    private float invulnerabilityRemaining;

    public Ship(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.speed = speed;
    }

    public void update(float delta, Vector2 direction, float worldWidth, float worldHeight) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (direction == null) {
            throw new IllegalArgumentException("La dirección de movimiento no puede ser null.");
        }

        invulnerabilityRemaining = Math.max(0f, invulnerabilityRemaining - delta);

        Vector2 normalizedDirection = direction.cpy();
        if (normalizedDirection.len2() > 1f) {
            normalizedDirection.nor();
        }

        float nextX = getX() + normalizedDirection.x * speed * delta;
        float nextY = getY() + normalizedDirection.y * speed * delta;
        setPositionInside(nextX, nextY, worldWidth, worldHeight);
    }

    /** Indica si la nave ignora el daño en este momento. */
    public boolean isInvulnerable() {
        return invulnerabilityRemaining > 0f;
    }

    /** Coloca la nave en un punto fijo y le da un periodo de invulnerabilidad (puede ser cero). */
    public void respawn(float x, float y, float invulnerabilitySeconds) {
        if (!Float.isFinite(invulnerabilitySeconds) || invulnerabilitySeconds < 0f) {
            throw new IllegalArgumentException("La invulnerabilidad debe ser finita y no negativa.");
        }
        setPosition(x, y);
        invulnerabilityRemaining = invulnerabilitySeconds;
    }
}
