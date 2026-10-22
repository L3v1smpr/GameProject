package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.Ship;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;

/**
 * Es dueño de las entidades y coordina la partida (Controller de GRASP).
 *
 * <p>Recibe sus colaboradores por constructor: el {@link CollisionSystem} solo detecta contactos y
 * el {@link LevelDirector} solo construye oleadas; este mundo decide qué consecuencias aplicar.
 */
public final class GameWorld {

    private final CollisionSystem collisions;
    private final LevelDirector levelDirector;
    private final Ship ship;
    private final List<Alien> aliens = new ArrayList<>();
    private int level;
    private int lives;
    private GamePhase phase;

    public GameWorld(LevelDirector levelDirector, CollisionSystem collisions) {
        if (levelDirector == null || collisions == null) {
            throw new IllegalArgumentException("Los colaboradores de GameWorld no pueden ser null.");
        }
        this.levelDirector = levelDirector;
        this.collisions = collisions;
        ship = new Ship(
                GameConfig.SHIP_START_X,
                GameConfig.SHIP_START_Y,
                GameConfig.SHIP_WIDTH,
                GameConfig.SHIP_HEIGHT,
                GameConfig.SHIP_SPEED);
        restart();
    }

    /** Deja la partida en su estado inicial: tres vidas, primer nivel y una oleada nueva. */
    public void restart() {
        level = GameConfig.FIRST_LEVEL;
        lives = GameConfig.INITIAL_LIVES;
        phase = GamePhase.PLAYING;
        ship.respawn(GameConfig.SHIP_START_X, GameConfig.SHIP_START_Y, 0f);
        aliens.clear();
        spawnWave();
    }

    public void update(float delta, PlayerInput input) {
        if (input == null) {
            throw new IllegalArgumentException("PlayerInput no puede ser null.");
        }
        if (phase != GamePhase.PLAYING) {
            return;
        }

        ship.update(delta, input.movementDirection(), GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        for (Alien alien : aliens) {
            alien.update(delta, GameConfig.WORLD_WIDTH);
        }

        aliens.removeIf(Alien::hasLeftWorld);
        resolveShipContacts();
        aliens.removeIf(alien -> !alien.isActive());

        if (phase == GamePhase.PLAYING && aliens.isEmpty()) {
            spawnWave();
        }
    }

    public WorldSnapshot snapshot() {
        return new WorldSnapshot(ship, aliens, lives, phase);
    }

    /** Durante la invulnerabilidad los aliens atraviesan la nave sin hacerle daño ni desaparecer. */
    private void resolveShipContacts() {
        if (ship.isInvulnerable()) {
            return;
        }

        List<Alien> contacts = collisions.collidingWith(ship, aliens);
        if (contacts.isEmpty()) {
            return;
        }

        for (Alien alien : contacts) {
            alien.deactivate();
        }
        loseLife();
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            phase = GamePhase.GAME_OVER;
            return;
        }
        ship.respawn(
                GameConfig.SHIP_START_X,
                GameConfig.SHIP_START_Y,
                GameConfig.RESPAWN_INVULNERABILITY_SECONDS);
    }

    private void spawnWave() {
        aliens.addAll(levelDirector.createWave(level));
    }
}
