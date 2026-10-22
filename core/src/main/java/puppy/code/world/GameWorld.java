package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.PlayerWeapon;

/**
 * Es dueño de las entidades y coordina la partida (Controller de GRASP).
 */
public final class GameWorld {

    private final CollisionSystem collisions;
    private final LevelDirector levelDirector;
    private final PlayerWeapon playerWeapon;
    private final Ship ship;
    private final List<Alien> aliens = new ArrayList<>();
    private final List<PlayerProjectile> playerProjectiles = new ArrayList<>();
    private int level;
    private int lives;
    private int score;
    private int aliensDestroyedInLevel;
    private GamePhase phase;

    public GameWorld(LevelDirector levelDirector, CollisionSystem collisions) {
        if (levelDirector == null || collisions == null) {
            throw new IllegalArgumentException("Los colaboradores de GameWorld no pueden ser null.");
        }
        this.levelDirector = levelDirector;
        this.collisions = collisions;
        this.playerWeapon = new PlayerWeapon();
        this.ship = new Ship(
                GameConfig.SHIP_START_X,
                GameConfig.SHIP_START_Y,
                GameConfig.SHIP_WIDTH,
                GameConfig.SHIP_HEIGHT,
                GameConfig.SHIP_SPEED);
        restart();
    }

    public void restart() {
        level = GameConfig.FIRST_LEVEL;
        lives = GameConfig.INITIAL_LIVES;
        score = 0;
        aliensDestroyedInLevel = 0;
        phase = GamePhase.ACTIVE;
        ship.respawn(GameConfig.SHIP_START_X, GameConfig.SHIP_START_Y, 0f);
        aliens.clear();
        playerProjectiles.clear();
        spawnWave();
    }

    public void update(float delta, PlayerInput input) {
        if (input == null) {
            throw new IllegalArgumentException("PlayerInput no puede ser null.");
        }
        if (phase != GamePhase.ACTIVE) {
            return;
        }

        ship.update(delta, input.movementDirection(), GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        
        Optional<PlayerProjectile> newProjectile = playerWeapon.update(delta, input.firePressed(), ship);
        newProjectile.ifPresent(playerProjectiles::add);

        for (PlayerProjectile projectile : playerProjectiles) {
            projectile.update(delta);
        }
        playerProjectiles.removeIf(p -> !p.isActive() || p.getY() > GameConfig.WORLD_HEIGHT);

        for (Alien alien : aliens) {
            alien.update(delta, GameConfig.WORLD_WIDTH);
        }

        aliens.removeIf(Alien::hasLeftWorld);
        resolveCollisions();
        aliens.removeIf(alien -> !alien.isActive());

        if (phase == GamePhase.ACTIVE && aliens.isEmpty()) {
            spawnWave();
        }
    }

    private void resolveCollisions() {
        if (!ship.isInvulnerable()) {
            List<Alien> contacts = collisions.collidingWith(ship, aliens);
            for (Alien alien : contacts) {
                alien.deactivate();
                loseLife();
            }
        }
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

    public WorldSnapshot snapshot() {
        return new WorldSnapshot(ship, aliens, playerProjectiles, lives, score, level, phase);
    }
}