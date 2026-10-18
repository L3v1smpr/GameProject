package puppy.code.factories;

import puppy.code.behaviors.MovementStrategy;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;

/**
 * Construye un {@link Alien} paso a paso y valida la configuración antes de crearlo (patrón Builder).
 *
 * <p>La posición y el movimiento son obligatorios; el tamaño usa el valor de {@link GameConfig} si
 * no se indica otro. Cada llamada a {@link #build()} entrega un alien nuevo.
 */
public final class AlienBuilder {

    private float x;
    private float y;
    private boolean positionDefined;
    private float width = GameConfig.ALIEN_WIDTH;
    private float height = GameConfig.ALIEN_HEIGHT;
    private MovementStrategy movement;

    public AlienBuilder position(float x, float y) {
        this.x = x;
        this.y = y;
        this.positionDefined = true;
        return this;
    }

    public AlienBuilder size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public AlienBuilder movement(MovementStrategy movement) {
        this.movement = movement;
        return this;
    }

    /**
     * Crea el alien con la configuración acumulada.
     *
     * @throws IllegalStateException si falta la posición o el movimiento
     * @throws IllegalArgumentException si la posición o el tamaño no son válidos
     */
    public Alien build() {
        if (!positionDefined) {
            throw new IllegalStateException("Falta definir la posición del alien.");
        }
        if (movement == null) {
            throw new IllegalStateException("Falta definir la estrategia de movimiento del alien.");
        }
        return new Alien(x, y, width, height, movement);
    }
}
