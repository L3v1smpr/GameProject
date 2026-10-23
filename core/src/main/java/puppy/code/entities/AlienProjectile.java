package puppy.code.entities;

import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

/** Proyectil enemigo móvil. La resolución de impactos queda a cargo del sistema de colisiones. */
public final class AlienProjectile extends GameEntity {

    private final Vector2 direction;
    private final float speed;

    public AlienProjectile(float x, float y, Vector2 direction, float speed) {
        super(x, y, GameConfig.PROJECTILE_WIDTH, GameConfig.PROJECTILE_HEIGHT);
        if (direction == null || direction.isZero()) {
            throw new IllegalArgumentException("La dirección del proyectil debe tener magnitud.");
        }
        if (!Float.isFinite(speed) || speed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        this.direction = direction.cpy().nor();
        this.speed = speed;
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        setPosition(getX() + direction.x * speed * delta, getY() + direction.y * speed * delta);
    }

    public float getSpeed() {
        return speed;
    }

    /**
     * Devuelve la rotación en grados para que el sprite apunte hacia su vector de viaje.
     * Como alien_ammo.png apunta al Sur (270 grados en LibGDX), se resta 270 grados.
     */
    public float getRotation() {
        return direction.angleDeg() - 270f;
    }
}