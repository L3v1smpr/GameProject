package puppy.code.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.AlternatingAttackStrategy;
import puppy.code.behaviors.BurstAttackStrategy;
import puppy.code.behaviors.HorizontalMovementStrategy;
import puppy.code.behaviors.LinearAttackStrategy;
import puppy.code.behaviors.LinearMovementStrategy;
import puppy.code.behaviors.MovementStrategy;
import puppy.code.behaviors.SpreadAttackStrategy;
import puppy.code.behaviors.StaticMovementStrategy;
import puppy.code.behaviors.VAttackStrategy;
import puppy.code.behaviors.ZigZagMovementStrategy;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;

/**
 * Dirige la construcción de las oleadas de cada nivel (Director del patrón Builder).
 */
public final class LevelDirector {

    private final AlienAttackStrategy linearAttack;
    private final AlienAttackStrategy vAttack;
    private final AlienAttackStrategy spreadAttack;
    private final AlienAttackStrategy alternatingAttack;

    public LevelDirector() {
        linearAttack = new LinearAttackStrategy();
        vAttack = new VAttackStrategy();
        spreadAttack = new SpreadAttackStrategy();
        alternatingAttack = new AlternatingAttackStrategy();
    }

    public List<Alien> createWave(int level) {
        return createWave(level, GameConfig.WAVE_SIZE);
    }

    public List<Alien> createWave(int level, int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        AlienAttackStrategy attack = attackFor(level);
        int hitPoints = hitPointsFor(level);

        float margin = GameConfig.ALIEN_SPAWN_MARGIN * 2f;
        float usableWidth = GameConfig.WORLD_WIDTH - margin * 2f - GameConfig.ALIEN_WIDTH;
        int numZones = Math.max(count, 5);
        float zoneWidth = usableWidth / numZones;

        List<Integer> zoneIndices = new ArrayList<>(numZones);
        for (int i = 0; i < numZones; i++) {
            zoneIndices.add(i);
        }
        Collections.shuffle(zoneIndices);

        List<Alien> wave = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int zone = zoneIndices.get(i);
            float zoneStart = margin + zone * zoneWidth;
            float jitter = MathUtils.random(0f, Math.max(0f, zoneWidth - GameConfig.ALIEN_WIDTH));
            float spawnX = zoneStart + jitter;

            float staggerY = MathUtils.random(15f, 75f);
            float spawnY = GameConfig.WORLD_HEIGHT + staggerY;

            MovementStrategy movement = createVariedMovement(level);

            wave.add(new AlienBuilder()
                    .position(spawnX, spawnY)
                    .hitPoints(hitPoints)
                    .movement(movement)
                    .attack(attack)
                    .build());
        }
        return Collections.unmodifiableList(wave);
    }

    /**
     * Construye un alien estático según el nivel:
     * - Nivel 2: Ataque alternado aleatorio (recto hacia abajo o apuntando a la nave).
     * - Nivel 3: Disparo en abanico de 3 perdigones dirigido al jugador.
     */
    public Alien createStaticAlien(int level, float x, float targetY) {
        AlienAttackStrategy attack = (level >= GameConfig.LAST_LEVEL) ? spreadAttack : alternatingAttack;
        float spawnY = GameConfig.WORLD_HEIGHT + 15f;
        return new AlienBuilder()
                .position(x, spawnY)
                .hitPoints(hitPointsFor(level))
                .movement(new StaticMovementStrategy(targetY, 120f))
                .attack(attack)
                .build();
    }

    /**
     * Construye un alien bombardero que desciende desde fuera de pantalla y
     * patrulla horizontalmente disparando ráfagas continuas hacia el jugador.
     */
    public Alien createBomberAlien(int level, float x, float cruiseY) {
        float speed = level >= GameConfig.LAST_LEVEL ? 140f : 115f;
        float spawnY = GameConfig.WORLD_HEIGHT + 15f;
        return new AlienBuilder()
                .position(x, spawnY)
                .hitPoints(hitPointsFor(level))
                .movement(new HorizontalMovementStrategy(cruiseY, speed))
                .attack(new BurstAttackStrategy())
                .build();
    }

    private MovementStrategy createVariedMovement(int level) {
        float speedMultiplier = MathUtils.random(0.85f, 1.20f);
        if (level == GameConfig.FIRST_LEVEL) {
            return new LinearMovementStrategy(GameConfig.LINEAR_ALIEN_SPEED * speedMultiplier);
        }
        if (level == GameConfig.SECOND_LEVEL || level == GameConfig.LAST_LEVEL) {
            return new ZigZagMovementStrategy(
                    GameConfig.ZIGZAG_ALIEN_SPEED * speedMultiplier,
                    GameConfig.ZIGZAG_HALF_PERIOD_SECONDS * MathUtils.random(0.9f, 1.15f));
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