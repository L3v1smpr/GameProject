package puppy.code.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import puppy.code.behaviors.LinearAttackStrategy;
import puppy.code.behaviors.LinearMovementStrategy;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Meteorite;
import puppy.code.entities.MeteoriteSize;
import puppy.code.entities.PlayerProjectile;
import puppy.code.factories.AlienBuilder;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;

public class GameWorldTest {

    @Test
    public void snapshotReportsQuotasForAllThreeLevels() throws Exception {
        GameWorld world = newWorld();
        assertEquals(15, world.snapshot().getQuotaForLevel());

        setField(world, "phase", GamePhase.LEVEL_COMPLETED);
        world.advanceToNextLevel();
        assertEquals(30, world.snapshot().getQuotaForLevel());

        setField(world, "phase", GamePhase.LEVEL_COMPLETED);
        world.advanceToNextLevel();
        assertEquals(45, world.snapshot().getQuotaForLevel());
    }

    @Test
    public void snapshotCarriesFrameSoundEventsWithoutAudioDependencies() {
        GameWorld world = newWorld();
        world.update(GameConfig.PLAYER_FIRE_COOLDOWN_SECONDS, new TestInput(true));

        WorldSnapshot fired = world.snapshot();
        assertTrue(fired.didPlayerFireThisFrame());
        assertFalse(fired.didEntityExplodeThisFrame());
        assertFalse(fired.didShipGetHurtThisFrame());

        world.update(0f, new TestInput(false));
        assertFalse(world.snapshot().didPlayerFireThisFrame());
    }

    @Test
    public void projectileDestroyingAlienRaisesExplosionEvent() throws Exception {
        GameWorld world = newWorld();
        List<Alien> aliens = getList(world, "aliens");
        aliens.clear();
        aliens.add(alienAt(0f, 100f, 1));
        getList(world, "playerProjectiles").add(new PlayerProjectile(0f, 100f));

        world.update(0f, new TestInput(false));

        assertTrue(world.snapshot().didEntityExplodeThisFrame());
        assertEquals(GameConfig.ALIEN_SCORE, world.snapshot().getScore());
        assertEquals(1, world.snapshot().getAliensDestroyedInLevel());
    }

    @Test
    public void physicalShipAlienCollisionCostsLifeWithoutAwardingScoreOrQuota() throws Exception {
        GameWorld world = newWorld();
        List<Alien> aliens = getList(world, "aliens");
        aliens.clear();
        aliens.add(alienAt(GameConfig.SHIP_START_X, GameConfig.SHIP_START_Y, 1));
        aliens.add(alienAt(0f, 700f, 1));

        world.update(0f, new TestInput(false));

        WorldSnapshot snapshot = world.snapshot();
        assertEquals(GameConfig.INITIAL_LIVES - 1, snapshot.getLives());
        assertEquals(0, snapshot.getScore());
        assertEquals(0, snapshot.getAliensDestroyedInLevel());
        assertTrue(snapshot.didShipGetHurtThisFrame());
        assertFalse(snapshot.didEntityExplodeThisFrame());
    }

    @Test
    public void losingLifeClearsProjectilesAndPreservesOtherAliensAndMeteorites() throws Exception {
        GameWorld world = newWorld();
        int alienCount = world.snapshot().getAliens().size();
        List<Meteorite> meteorites = getList(world, "meteorites");
        meteorites.add(new Meteorite(
                GameConfig.SHIP_START_X + 4f,
                GameConfig.SHIP_START_Y + 4f,
                GameConfig.SMALL_METEORITE_WIDTH,
                GameConfig.SMALL_METEORITE_HEIGHT,
                1f,
                MeteoriteSize.SMALL));
        meteorites.add(new Meteorite(100f, 600f, 28f, 28f, 1f, MeteoriteSize.SMALL));
        getList(world, "playerProjectiles").add(new PlayerProjectile(0f, 0f));
        getList(world, "alienProjectiles").add(new AlienProjectile(0f, 0f, new Vector2(0f, -1f), 220f));

        world.update(0f, new TestInput(false));

        WorldSnapshot snapshot = world.snapshot();
        assertEquals(GameConfig.INITIAL_LIVES - 1, snapshot.getLives());
        assertEquals(alienCount, snapshot.getAliens().size());
        assertEquals(1, snapshot.getMeteorites().size());
        assertTrue(snapshot.getPlayerProjectiles().isEmpty());
        assertTrue(snapshot.getAlienProjectiles().isEmpty());
        assertTrue(snapshot.isShipInvulnerable());
    }

    private static GameWorld newWorld() {
        return new GameWorld(new LevelDirector(), new CollisionSystem());
    }

    private static Alien alienAt(float x, float y, int hitPoints) {
        return new AlienBuilder()
                .position(x, y)
                .hitPoints(hitPoints)
                .movement(new LinearMovementStrategy(GameConfig.LINEAR_ALIEN_SPEED))
                .attack(new LinearAttackStrategy())
                .build();
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getList(GameWorld world, String name) throws Exception {
        Field field = GameWorld.class.getDeclaredField(name);
        field.setAccessible(true);
        return (List<T>) field.get(world);
    }

    private static void setField(GameWorld world, String name, Object value) throws Exception {
        Field field = GameWorld.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(world, value);
    }

    private static final class TestInput implements PlayerInput {
        private final boolean firePressed;

        private TestInput(boolean firePressed) {
            this.firePressed = firePressed;
        }

        @Override
        public Vector2 movementDirection() {
            return new Vector2();
        }

        @Override
        public boolean firePressed() {
            return firePressed;
        }

        @Override
        public boolean dashJustPressed() {
            return false;
        }
    }
}
