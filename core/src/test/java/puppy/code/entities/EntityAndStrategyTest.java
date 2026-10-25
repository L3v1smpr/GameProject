package puppy.code.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.LinearAttackStrategy;
import puppy.code.behaviors.LinearMovementStrategy;
import puppy.code.behaviors.MovementStrategy;
import puppy.code.behaviors.VAttackStrategy;
import puppy.code.behaviors.ZigZagMovementStrategy;
import puppy.code.config.GameConfig;
import puppy.code.factories.AlienBuilder;
import puppy.code.factories.LevelDirector;
import puppy.code.systems.CollisionSystem;

public class EntityAndStrategyTest {

    @Test
    public void levelDirectorAssignsExpectedAlienHitPoints() {
        LevelDirector director = new LevelDirector();

        assertEquals(GameConfig.LEVEL_ONE_ALIEN_HIT_POINTS, director.createWave(1).get(0).getHitPoints());
        assertEquals(GameConfig.LEVEL_TWO_ALIEN_HIT_POINTS, director.createWave(2).get(0).getHitPoints());
        assertEquals(GameConfig.LEVEL_THREE_ALIEN_HIT_POINTS, director.createWave(3).get(0).getHitPoints());
    }

    @Test
    public void builderCreatesConfiguredAlienAndRejectsMissingParts() {
        Alien alien = new AlienBuilder()
                .position(20f, 30f)
                .hitPoints(2)
                .movement(new LinearMovementStrategy(90f))
                .attack(new LinearAttackStrategy())
                .build();

        assertEquals(2, alien.getHitPoints());
        assertEquals(20f, alien.getX(), 0f);
        assertEquals(30f, alien.getY(), 0f);
        assertEquals(GameConfig.ALIEN_WIDTH, alien.getWidth(), 0.001f);

        try {
            new AlienBuilder()
                    .position(0f, 0f)
                    .hitPoints(1)
                    .attack(new LinearAttackStrategy())
                    .build();
            fail("El builder debe rechazar una estrategia de movimiento ausente.");
        } catch (IllegalStateException expected) {
            // La configuración incompleta no produce entidades inválidas.
        }
    }

    @Test
    public void movementStrategiesProduceTheirConfiguredTrajectories() {
        MovementStrategy linear = new LinearMovementStrategy(90f);
        Vector2 linearPosition = linear.nextPosition(10f, 100f, 0f, 0.5f);
        assertEquals(10f, linearPosition.x, 0.001f);
        assertEquals(55f, linearPosition.y, 0.001f);

        MovementStrategy zigzag = new ZigZagMovementStrategy(110f, 1.2f);
        Vector2 rightStep = zigzag.nextPosition(10f, 100f, 0.5f, 0.1f);
        Vector2 leftStep = zigzag.nextPosition(10f, 100f, 1.2f, 0.1f);
        assertTrue(rightStep.x > 10f);
        assertTrue(leftStep.x < 10f);
        assertTrue(rightStep.y < 100f);
        assertTrue(leftStep.y < 100f);
    }

    @Test
    public void attackStrategiesCreateOneAndTwoProjectiles() {
        Alien alien = alienAt(200f, 300f, 1);
        Ship ship = new Ship(200f, 50f, GameConfig.SHIP_WIDTH, GameConfig.SHIP_HEIGHT, GameConfig.SHIP_SPEED);
        AlienAttackStrategy linear = new LinearAttackStrategy();
        AlienAttackStrategy spread = new VAttackStrategy();

        assertEquals(1, linear.createVolley(alien, ship).size());
        assertEquals(2, spread.createVolley(alien, ship).size());
    }

    @Test
    public void shipDashUsesConfiguredDurationCooldownAndInvulnerability() {
        Ship ship = new Ship(100f, 100f, 40f, 36f, GameConfig.SHIP_SPEED);
        ship.triggerDash(new Vector2(1f, 0f));

        assertTrue(ship.isInvulnerable());
        assertFalse(ship.canDash());
        ship.update(GameConfig.DASH_DURATION_SECONDS, new Vector2(), 1200f, 800f);
        assertEquals(100f + GameConfig.SHIP_SPEED * GameConfig.DASH_SPEED_MULTIPLIER
                * GameConfig.DASH_DURATION_SECONDS, ship.getX(), 0.01f);
        assertFalse(ship.isInvulnerable());

        ship.update(2.84f, new Vector2(), 1200f, 800f);
        assertFalse(ship.canDash());
        ship.update(0.02f, new Vector2(), 1200f, 800f);
        assertTrue(ship.canDash());
        assertEquals(3f, GameConfig.DASH_COOLDOWN_SECONDS, 0f);
    }

    @Test
    public void respawnKeepsShipInvulnerableForConfiguredTime() {
        Ship ship = new Ship(0f, 0f, 40f, 36f, GameConfig.SHIP_SPEED);
        ship.respawn(100f, 80f, GameConfig.RESPAWN_INVULNERABILITY_SECONDS);

        assertEquals(100f, ship.getX(), 0f);
        assertEquals(80f, ship.getY(), 0f);
        assertTrue(ship.isInvulnerable());
        ship.update(1.49f, new Vector2(), 1200f, 800f);
        assertTrue(ship.isInvulnerable());
        ship.update(0.02f, new Vector2(), 1200f, 800f);
        assertFalse(ship.isInvulnerable());
    }

    @Test
    public void collisionSystemUsesCollidableBounds() {
        CollisionSystem collisions = new CollisionSystem();
        Ship first = new Ship(10f, 10f, 20f, 20f, 100f);
        Ship overlapping = new Ship(25f, 25f, 20f, 20f, 100f);
        Ship separate = new Ship(40f, 40f, 20f, 20f, 100f);

        assertTrue(collisions.collides(first, overlapping));
        assertFalse(collisions.collides(first, separate));
    }

    static Alien alienAt(float x, float y, int hitPoints) {
        return new AlienBuilder()
                .position(x, y)
                .hitPoints(hitPoints)
                .movement(new LinearMovementStrategy(GameConfig.LINEAR_ALIEN_SPEED))
                .attack(new LinearAttackStrategy())
                .build();
    }
}
