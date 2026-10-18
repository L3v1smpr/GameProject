package puppy.code.entities;

import com.badlogic.gdx.math.Vector2;
import puppy.code.behaviors.MovementStrategy;

/** Alien de un único arquetipo; delega su trayectoria en una {@link MovementStrategy}. */
public final class Alien extends GameEntity {

    private final MovementStrategy movement;
    private float age;

    public Alien(float x, float y, float width, float height, MovementStrategy movement) {
        super(x, y, width, height);
        if (movement == null) {
            throw new IllegalArgumentException("La estrategia de movimiento no puede ser null.");
        }
        this.movement = movement;
    }

    public void update(float delta, float worldWidth) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (!Float.isFinite(worldWidth) || worldWidth <= 0f) {
            throw new IllegalArgumentException("El ancho del mundo debe ser positivo y finito.");
        }

        age += delta;
        Vector2 next = movement.nextPosition(getX(), getY(), age, delta);
        float maxX = Math.max(0f, worldWidth - getWidth());
        setPosition(Math.max(0f, Math.min(next.x, maxX)), next.y);
    }

    /** Indica si el alien ya salió por completo por el borde inferior del mundo. */
    public boolean hasLeftWorld() {
        return getY() + getHeight() < 0f;
    }
}
