package puppy.code.factories;

import puppy.code.behaviors.MovementStrategy;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;

/**
 * Construye un {@link Alien} paso a paso y valida la configuración antes de crearlo (patrón Builder).
 *
 * <p>La posición, la vida, el movimiento y el ataque son obligatorios; el tamaño usa el valor de
 * {@link GameConfig} si no se indica otro. El Builder también comprueba la velocidad que informa la
 * estrategia. Cada llamada a {@link #build()} entrega un alien nuevo.
 */
public final class AlienBuilder {

    private float x;
    private float y;
    private boolean positionDefined;
    private float width = GameConfig.ALIEN_WIDTH;
    private float height = GameConfig.ALIEN_HEIGHT;
    private int hitPoints;
    private MovementStrategy movement;
    private AlienAttackStrategy attackStrategy;

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

    public AlienBuilder hitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
        return this;
    }

    public AlienBuilder attack(AlienAttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
        return this;
    }

    /**
     * Crea el alien con la configuración acumulada.
     *
     * @throws IllegalStateException si falta alguna configuración obligatoria
     * @throws IllegalArgumentException si la posición, el tamaño, la vida o la velocidad no son válidos
     */
    public Alien build() {
        if (!positionDefined) {
            throw new IllegalStateException("Falta definir la posición del alien.");
        }
        if (movement == null) {
            throw new IllegalStateException("Falta definir la estrategia de movimiento del alien.");
        }
        if (!Float.isFinite(movement.speed()) || movement.speed() <= 0f) {
            throw new IllegalArgumentException("La velocidad del movimiento debe ser positiva y finita.");
        }
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("Los puntos de vida deben ser positivos.");
        }
        if (attackStrategy == null) {
            throw new IllegalStateException("Falta definir la estrategia de ataque del alien.");
        }
        return new Alien(x, y, width, height, hitPoints, movement, attackStrategy);
    }
}
