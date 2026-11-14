package puppy.code.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.AlienPhaseTransition;
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
import puppy.code.entities.AlienType;

/**
 * Dirige la construcción de las oleadas de cada nivel (Director del patrón Builder).
 */
public final class LevelDirector implements AlienFactory {

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

    @Override
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
                    .type(AlienType.STANDARD)
                    .hitPoints(hitPoints)
                    .movement(movement)
                    .attack(attack)
                    .phaseTransition(createStandardTransition())
                    .build());
        }
        return Collections.unmodifiableList(wave);
    }

    /**
     * Construye un alien estático (Centinela) según el nivel:
     * - Nivel 2: Ataque alternado aleatorio (recto hacia abajo o apuntando a la nave).
     * - Nivel 3: Disparo en abanico de 3 perdigones dirigido al jugador.
     * Al entrar en fase 2, patrulla horizontalmente en la zona superior y cambia de ataque.
     */
    @Override
    public Alien createStaticAlien(int level, float x, float targetY) {
        AlienAttackStrategy attack = (level >= GameConfig.LAST_LEVEL) ? spreadAttack : alternatingAttack;
        float spawnY = GameConfig.WORLD_HEIGHT + 15f;
        return new AlienBuilder()
                .position(x, spawnY)
                .type(AlienType.STATIC)
                .hitPoints(hitPointsFor(level))
                .movement(new StaticMovementStrategy(targetY, 120f))
                .attack(attack)
                .phaseTransition(createStaticTransition(attack))
                .build();
    }

    /**
     * Construye un alien bombardero que desciende desde fuera de pantalla y
     * patrulla horizontalmente disparando ráfagas continuas hacia el jugador.
     * Al entrar en fase 2, cambia su patrón de vuelo a zigzag o descenso vertical.
     */
    @Override
    public Alien createBomberAlien(int level, float x, float cruiseY) {
        float speed = level >= GameConfig.LAST_LEVEL ? 140f : 115f;
        float spawnY = GameConfig.WORLD_HEIGHT + 15f;
        return new AlienBuilder()
                .position(x, spawnY)
                .size(GameConfig.BOMBER_ALIEN_WIDTH, GameConfig.BOMBER_ALIEN_HEIGHT)
                .type(AlienType.BOMBER)
                .hitPoints(hitPointsFor(level))
                .movement(new HorizontalMovementStrategy(cruiseY, speed, GameConfig.BOMBER_ALIEN_WIDTH))
                .attack(new BurstAttackStrategy())
                .phaseTransition(createBomberTransition())
                .build();
    }

    private AlienPhaseTransition createStandardTransition() {
        return new AlienPhaseTransition() {
            @Override
            public MovementStrategy nextMovement(float currentY, float currentSpeed, float alienWidth) {
                float baseSpeed = Math.max(115f, currentSpeed);
                int choice = MathUtils.random(2);
                switch (choice) {
                    case 0:
                        return new HorizontalMovementStrategy(currentY, Math.max(175f, baseSpeed * 1.25f), alienWidth);
                    case 1:
                        return new ZigZagMovementStrategy(
                                baseSpeed * 1.15f,
                                GameConfig.ZIGZAG_HALF_PERIOD_SECONDS * 0.85f);
                    default:
                        return new LinearMovementStrategy(baseSpeed * 1.25f);
                }
            }

            @Override
            public AlienAttackStrategy nextAttack() {
                int choice = MathUtils.random(4);
                switch (choice) {
                    case 0:
                        return linearAttack;
                    case 1:
                        return vAttack;
                    case 2:
                        return spreadAttack;
                    case 3:
                        return alternatingAttack;
                    default:
                        return new BurstAttackStrategy();
                }
            }
        };
    }

    private AlienPhaseTransition createStaticTransition(final AlienAttackStrategy initialAttack) {
        return new AlienPhaseTransition() {
            @Override
            public MovementStrategy nextMovement(float currentY, float currentSpeed, float alienWidth) {
                float upperCruiseY = Math.min(currentY, MathUtils.random(640f, 700f));
                return new HorizontalMovementStrategy(upperCruiseY, 135f, alienWidth);
            }

            @Override
            public AlienAttackStrategy nextAttack() {
                if (initialAttack == spreadAttack) {
                    return MathUtils.randomBoolean() ? vAttack : new BurstAttackStrategy();
                }
                return MathUtils.randomBoolean() ? spreadAttack : vAttack;
            }
        };
    }

    private AlienPhaseTransition createBomberTransition() {
        return new AlienPhaseTransition() {
            @Override
            public MovementStrategy nextMovement(float currentY, float currentSpeed, float alienWidth) {
                if (MathUtils.randomBoolean()) {
                    float zigzagSpeed = Math.max(125f, currentSpeed * 1.10f);
                    return new ZigZagMovementStrategy(
                            zigzagSpeed,
                            GameConfig.ZIGZAG_HALF_PERIOD_SECONDS * 0.85f);
                }
                float verticalSpeed = Math.max(120f, currentSpeed);
                return new LinearMovementStrategy(verticalSpeed);
            }

            @Override
            public AlienAttackStrategy nextAttack() {
                return null;
            }
        };
    }

    private MovementStrategy createVariedMovement(int level) {
        float speedMultiplier = MathUtils.random(0.85f, 1.20f);
        if (level == GameConfig.FIRST_LEVEL || level == GameConfig.SECOND_LEVEL) {
            if (MathUtils.randomBoolean()) {
                return new LinearMovementStrategy(GameConfig.LINEAR_ALIEN_SPEED * speedMultiplier);
            }
            return new ZigZagMovementStrategy(
                    GameConfig.ZIGZAG_ALIEN_SPEED * speedMultiplier,
                    GameConfig.ZIGZAG_HALF_PERIOD_SECONDS * MathUtils.random(0.9f, 1.15f));
        }
        if (level == GameConfig.LAST_LEVEL) {
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