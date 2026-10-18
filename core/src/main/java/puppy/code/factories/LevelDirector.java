package puppy.code.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import puppy.code.behaviors.LinearMovement;
import puppy.code.behaviors.MovementStrategy;
import puppy.code.behaviors.ZigZagMovement;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;

/**
 * Dirige la construcción de las oleadas de cada nivel (Director del patrón Builder).
 *
 * <p>Decide cuántos aliens aparecen, dónde y con qué movimiento; delega la creación y la validación
 * de cada alien en {@link AlienBuilder}. Es el único lugar que elige la estrategia según el nivel.
 */
public final class LevelDirector {

    private final MovementStrategy linearMovement;
    private final MovementStrategy zigZagMovement;

    public LevelDirector() {
        linearMovement = new LinearMovement(GameConfig.LINEAR_ALIEN_SPEED);
        zigZagMovement = new ZigZagMovement(GameConfig.ZIGZAG_ALIEN_SPEED, GameConfig.ZIGZAG_HALF_PERIOD_SECONDS);
    }

    /** Crea una oleada nueva, de solo lectura, con los aliens repartidos a lo ancho del mundo. */
    public List<Alien> createWave(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("El nivel debe ser mayor o igual que 1.");
        }

        MovementStrategy movement = movementFor(level);
        float spacing = GameConfig.WORLD_WIDTH / (GameConfig.WAVE_SIZE + 1);
        float spawnY = GameConfig.WORLD_HEIGHT - GameConfig.ALIEN_HEIGHT - GameConfig.ALIEN_SPAWN_MARGIN;

        List<Alien> wave = new ArrayList<>(GameConfig.WAVE_SIZE);
        for (int index = 1; index <= GameConfig.WAVE_SIZE; index++) {
            float spawnX = spacing * index - GameConfig.ALIEN_WIDTH / 2f;
            wave.add(new AlienBuilder().position(spawnX, spawnY).movement(movement).build());
        }
        return Collections.unmodifiableList(wave);
    }

    /** Provisional: los niveles impares usan movimiento lineal y los pares zigzag, hasta que se acuerde. */
    private MovementStrategy movementFor(int level) {
        return level % 2 == 1 ? linearMovement : zigZagMovement;
    }
}
