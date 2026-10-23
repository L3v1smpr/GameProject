package puppy.code.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.LinearAttackStrategy;
import puppy.code.behaviors.LinearMovementStrategy;
import puppy.code.behaviors.MovementStrategy;
import puppy.code.behaviors.VAttackStrategy;
import puppy.code.behaviors.ZigZagMovementStrategy;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;

/**
 * Dirige la construcción de las oleadas de cada nivel (Director del patrón Builder).
 *
 * <p>Decide la vida, el movimiento y el ataque de los aliens de cada nivel; delega la creación y la
 * validación de cada alien en {@link AlienBuilder}. La transición entre niveles pertenece al mundo.
 */
public final class LevelDirector {

    private final MovementStrategy linearMovement;
    private final MovementStrategy zigZagMovement;
    private final AlienAttackStrategy linearAttack;
    private final AlienAttackStrategy vAttack;

    public LevelDirector() {
        linearMovement = new LinearMovementStrategy(GameConfig.LINEAR_ALIEN_SPEED);
        zigZagMovement = new ZigZagMovementStrategy(
                GameConfig.ZIGZAG_ALIEN_SPEED, GameConfig.ZIGZAG_HALF_PERIOD_SECONDS);
        linearAttack = new LinearAttackStrategy();
        vAttack = new VAttackStrategy();
    }

    /** Crea una oleada nueva, de solo lectura, con los aliens repartidos a lo ancho del mundo. */
    public List<Alien> createWave(int level) {
        MovementStrategy movement = movementFor(level);
        AlienAttackStrategy attack = attackFor(level);
        int hitPoints = hitPointsFor(level);
        float spacing = GameConfig.WORLD_WIDTH / (GameConfig.WAVE_SIZE + 1);
        float spawnY = GameConfig.WORLD_HEIGHT - GameConfig.ALIEN_HEIGHT - GameConfig.ALIEN_SPAWN_MARGIN;

        List<Alien> wave = new ArrayList<>(GameConfig.WAVE_SIZE);
        for (int index = 1; index <= GameConfig.WAVE_SIZE; index++) {
            float spawnX = spacing * index - GameConfig.ALIEN_WIDTH / 2f;
            wave.add(new AlienBuilder()
                    .position(spawnX, spawnY)
                    .hitPoints(hitPoints)
                    .movement(movement)
                    .attack(attack)
                    .build());
        }
        return Collections.unmodifiableList(wave);
    }

    private MovementStrategy movementFor(int level) {
        if (level == GameConfig.FIRST_LEVEL) {
            return linearMovement;
        }
        if (level == GameConfig.SECOND_LEVEL || level == GameConfig.LAST_LEVEL) {
            return zigZagMovement;
        }
        throw new IllegalArgumentException("Solo se admiten los niveles 1, 2 y 3.");
    }

    private AlienAttackStrategy attackFor(int level) {
        return level == GameConfig.FIRST_LEVEL ? linearAttack : vAttack;
    }

    private int hitPointsFor(int level) {
        switch (level) {
            case GameConfig.FIRST_LEVEL:
                return GameConfig.LEVEL_ONE_ALIEN_HIT_POINTS;
            case GameConfig.SECOND_LEVEL:
                return GameConfig.LEVEL_TWO_ALIEN_HIT_POINTS;
            case GameConfig.LAST_LEVEL:
                return GameConfig.LEVEL_THREE_ALIEN_HIT_POINTS;
            default:
                throw new IllegalArgumentException("Solo se admiten los niveles 1, 2 y 3.");
        }
    }
}
