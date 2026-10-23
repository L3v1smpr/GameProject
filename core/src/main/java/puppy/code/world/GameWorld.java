package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.Meteorite;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.PlayerWeapon;

public final class GameWorld {

    private final CollisionSystem collisions;
    private final LevelDirector levelDirector;
    private final PlayerWeapon playerWeapon;
    private final Ship ship;
    private final List<Alien> aliens = new ArrayList<>();
    private final List<Meteorite> meteorites = new ArrayList<>();
    private final List<PlayerProjectile> playerProjectiles = new ArrayList<>();
    private int level;
    private int lives;
    private int score;
    private int aliensDestroyedInLevel;
    private GamePhase phase;
    private float meteoriteSpawnTimer;

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
        meteorites.clear();
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

        meteoriteSpawnTimer += delta;
        if (meteoriteSpawnTimer >= 4f) { // Genera meteoritos periódicamente fuera de la cuota
            meteoriteSpawnTimer = 0f;
            meteorites.add(Meteorite.random(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        }

        for (Meteorite met : meteorites) {
            met.update(delta);
        }
        meteorites.removeIf(m -> !m.isActive() || m.getY() < -50f);

        aliens.removeIf(Alien::hasLeftWorld);
        resolveCollisions();
        aliens.removeIf(alien -> !alien.isActive());
        meteorites.removeIf(m -> !m.isActive());

        checkQuotaProgression();
    }

    private void checkQuotaProgression() {
        int quotaNeeded = getQuotaForLevel(level);
        if (aliensDestroyedInLevel >= quotaNeeded) {
            if (level >= 3) {
                phase = GamePhase.VICTORY;
            } else {
                phase = GamePhase.LEVEL_COMPLETED;
            }
        } else if (aliens.isEmpty()) {
            spawnWave();
        }
    }

    private int getQuotaForLevel(int currentLevel) {
        if (currentLevel == 1) return 15;
        if (currentLevel == 2) return 30;
        return 45;
    }

    public void advanceToNextLevel() {
        if (phase == GamePhase.LEVEL_COMPLETED) {
            level++;
            aliensDestroyedInLevel = 0;
            aliens.clear();
            meteorites.clear();
            playerProjectiles.clear();
            phase = GamePhase.ACTIVE;
            spawnWave();
        }
    }

    private void resolveCollisions() {
        // Proyectiles con Aliens
        for (PlayerProjectile projectile : playerProjectiles) {
            if (!projectile.isActive()) continue;
            for (Alien alien : aliens) {
                if (alien.isActive() && collisions.collides(projectile, alien)) {
                    projectile.deactivate();
                    alien.deactivate();
                    score += 100;
                    aliensDestroyedInLevel++;
                    break;
                }
            }
        }

        // Proyectiles con Meteoritos
        for (PlayerProjectile projectile : playerProjectiles) {
            if (!projectile.isActive()) continue;
            for (Meteorite met : meteorites) {
                if (met.isActive() && collisions.collides(projectile, met)) {
                    projectile.deactivate();
                    met.deactivate();
                    score += met.getScoreValue(); // 25, 50 o 75 puntos según tamaño
                    break;
                }
            }
        }

        // Colisión nave con aliens o meteoritos
        if (!ship.isInvulnerable()) {
            List<Alien> alienContacts = collisions.collidingWith(ship, aliens);
            for (Alien alien : alienContacts) {
                alien.deactivate();
                loseLife();
            }

            List<Meteorite> metContacts = collisions.collidingWith(ship, meteorites);
            for (Meteorite met : metContacts) {
                met.deactivate();
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
        playerProjectiles.clear();
    }

    private void spawnWave() {
        aliens.addAll(levelDirector.createWave(level));
    }

    public WorldSnapshot snapshot() {
        return new WorldSnapshot(ship, aliens, playerProjectiles, lives, score, level, phase);
    }
}