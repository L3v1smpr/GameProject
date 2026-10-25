package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Meteorite;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.PlayerWeapon;

/**
 * Coordina las entidades y aplica las reglas de una partida (Controller de GRASP).
 * Mantiene las listas privadas y entrega a las pantallas un snapshot inmutable.
 */
public final class GameWorld {

    private final CollisionSystem collisions;
    private final LevelDirector levelDirector;
    private final PlayerWeapon playerWeapon;
    private final Ship ship;
    private final List<Alien> aliens = new ArrayList<>();
    private final List<Meteorite> meteorites = new ArrayList<>();
    private final List<PlayerProjectile> playerProjectiles = new ArrayList<>();
    private final List<AlienProjectile> alienProjectiles = new ArrayList<>();

    private int level;
    private int lives;
    private int score;
    private int aliensDestroyedInLevel;
    private GamePhase phase;
    private float meteoriteSpawnTimer;
    private boolean playerFiredThisFrame;
    private boolean entityExplodedThisFrame;
    private boolean shipHurtThisFrame;

    public GameWorld(LevelDirector levelDirector, CollisionSystem collisions) {
        if (levelDirector == null || collisions == null) {
            throw new IllegalArgumentException("Los colaboradores de GameWorld no pueden ser null.");
        }
        this.levelDirector = levelDirector;
        this.collisions = collisions;
        playerWeapon = new PlayerWeapon();
        ship = new Ship(
                GameConfig.SHIP_START_X,
                GameConfig.SHIP_START_Y,
                GameConfig.SHIP_WIDTH,
                GameConfig.SHIP_HEIGHT,
                GameConfig.SHIP_SPEED);
        restart();
    }

    /** Inicia una partida desde el nivel uno con tres vidas y puntaje cero. */
    public void restart() {
        level = GameConfig.FIRST_LEVEL;
        lives = GameConfig.INITIAL_LIVES;
        score = 0;
        aliensDestroyedInLevel = 0;
        meteoriteSpawnTimer = 0f;
        phase = GamePhase.ACTIVE;
        ship.respawn(GameConfig.SHIP_START_X, GameConfig.SHIP_START_Y, 0f);
        aliens.clear();
        meteorites.clear();
        playerProjectiles.clear();
        alienProjectiles.clear();
        spawnWave();
    }

    public void update(float delta, PlayerInput input) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (input == null) {
            throw new IllegalArgumentException("PlayerInput no puede ser null.");
        }
        playerFiredThisFrame = false;
        entityExplodedThisFrame = false;
        shipHurtThisFrame = false;
        if (phase != GamePhase.ACTIVE) {
            return;
        }

        Vector2 movement = input.movementDirection();
        if (input.dashJustPressed()) {
            ship.triggerDash(movement);
        }
        ship.update(delta, movement, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        Optional<PlayerProjectile> newProjectile =
                playerWeapon.update(delta, input.firePressed(), ship);
        newProjectile.ifPresent(projectile -> {
            playerProjectiles.add(projectile);
            playerFiredThisFrame = true;
        });

        updatePlayerProjectiles(delta);
        updateAliens(delta);
        updateAlienProjectiles(delta);
        updateMeteorites(delta);

        resolveProjectileCollisions();
        resolveShipCollisions();
        removeInactiveEntities();
        updateQuotaProgression();
    }

    private void updatePlayerProjectiles(float delta) {
        for (PlayerProjectile projectile : playerProjectiles) {
            projectile.update(delta);
        }
        playerProjectiles.removeIf(projectile ->
                !projectile.isActive() || projectile.getY() > GameConfig.WORLD_HEIGHT);
    }

    private void updateAliens(float delta) {
        for (Alien alien : aliens) {
            if (!alien.isActive()) {
                continue;
            }
            alien.update(delta, GameConfig.WORLD_WIDTH);
            alienProjectiles.addAll(alien.fireDue(delta, ship));
        }
        aliens.removeIf(Alien::hasLeftWorld);
    }

    private void updateAlienProjectiles(float delta) {
        for (AlienProjectile projectile : alienProjectiles) {
            projectile.update(delta);
        }
        alienProjectiles.removeIf(projectile ->
                !projectile.isActive()
                        || projectile.getX() + projectile.getWidth() < 0f
                        || projectile.getX() > GameConfig.WORLD_WIDTH
                        || projectile.getY() + projectile.getHeight() < 0f
                        || projectile.getY() > GameConfig.WORLD_HEIGHT);
    }

    private void updateMeteorites(float delta) {
        meteoriteSpawnTimer += delta;
        if (meteoriteSpawnTimer >= GameConfig.METEORITE_SPAWN_COOLDOWN_SECONDS) {
            meteoriteSpawnTimer -= GameConfig.METEORITE_SPAWN_COOLDOWN_SECONDS;
            meteorites.add(Meteorite.random(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        }
        for (Meteorite meteorite : meteorites) {
            meteorite.update(delta);
        }
        meteorites.removeIf(meteorite ->
                !meteorite.isActive() || meteorite.getY() + meteorite.getHeight() < 0f);
    }

    private void resolveProjectileCollisions() {
        for (PlayerProjectile projectile : playerProjectiles) {
            if (!projectile.isActive()) {
                continue;
            }
            for (Alien alien : aliens) {
                if (alien.isActive() && collisions.collides(projectile, alien)) {
                    projectile.deactivate();
                    if (alien.takeDamage()) {
                        entityExplodedThisFrame = true;
                        score += GameConfig.ALIEN_SCORE;
                        aliensDestroyedInLevel++;
                    }
                    break;
                }
            }
            if (!projectile.isActive()) {
                continue;
            }
            for (Meteorite meteorite : meteorites) {
                if (meteorite.isActive() && collisions.collides(projectile, meteorite)) {
                    projectile.deactivate();
                    meteorite.deactivate();
                    entityExplodedThisFrame = true;
                    score += meteorite.getScoreValue();
                    break;
                }
            }
        }

        if (ship.isInvulnerable()) {
            return;
        }
        boolean shipWasHit = false;
        for (AlienProjectile projectile : alienProjectiles) {
            if (projectile.isActive() && collisions.collides(ship, projectile)) {
                projectile.deactivate();
                shipWasHit = true;
                break;
            }
        }
        if (shipWasHit) {
            loseLife();
        }
    }

    /** Un solo contacto físico puede quitar vida; los objetos atraviesan durante la invulnerabilidad. */
    private void resolveShipCollisions() {
        if (phase != GamePhase.ACTIVE || ship.isInvulnerable()) {
            return;
        }
        for (Alien alien : aliens) {
            if (alien.isActive() && collisions.collides(ship, alien)) {
                alien.deactivate();
                loseLife();
                return;
            }
        }
        if (ship.isInvulnerable() || phase != GamePhase.ACTIVE) {
            return;
        }
        for (Meteorite meteorite : meteorites) {
            if (meteorite.isActive() && collisions.collides(ship, meteorite)) {
                meteorite.deactivate();
                loseLife();
                return;
            }
        }
    }

    private void loseLife() {
        if (phase != GamePhase.ACTIVE) {
            return;
        }
        shipHurtThisFrame = true;
        lives = Math.max(0, lives - 1);
        if (lives == 0) {
            phase = GamePhase.GAME_OVER;
            return;
        }

        ship.respawn(
                GameConfig.SHIP_START_X,
                GameConfig.SHIP_START_Y,
                GameConfig.RESPAWN_INVULNERABILITY_SECONDS);
        playerProjectiles.clear();
        alienProjectiles.clear();
    }

    private void removeInactiveEntities() {
        aliens.removeIf(alien -> !alien.isActive());
        meteorites.removeIf(meteorite -> !meteorite.isActive());
        playerProjectiles.removeIf(projectile -> !projectile.isActive());
        alienProjectiles.removeIf(projectile -> !projectile.isActive());
    }

    private void updateQuotaProgression() {
        if (phase != GamePhase.ACTIVE) {
            return;
        }
        if (aliensDestroyedInLevel >= getQuotaForLevel(level)) {
            aliens.forEach(Alien::deactivate);
            aliens.clear();
            playerProjectiles.clear();
            alienProjectiles.clear();
            phase = level == GameConfig.LAST_LEVEL
                    ? GamePhase.VICTORY
                    : GamePhase.LEVEL_COMPLETED;
        } else if (aliens.isEmpty()) {
            spawnWave();
        }
    }

    /** Conserva vidas y puntaje; solo prepara la siguiente fase tras confirmación del jugador. */
    public void advanceToNextLevel() {
        if (phase != GamePhase.LEVEL_COMPLETED) {
            return;
        }
        level++;
        aliensDestroyedInLevel = 0;
        meteoriteSpawnTimer = 0f;
        aliens.clear();
        meteorites.clear();
        playerProjectiles.clear();
        alienProjectiles.clear();
        phase = GamePhase.ACTIVE;
        spawnWave();
    }

    private int getQuotaForLevel(int currentLevel) {
        switch (currentLevel) {
            case GameConfig.FIRST_LEVEL:
                return GameConfig.LEVEL_ONE_ALIEN_QUOTA;
            case GameConfig.SECOND_LEVEL:
                return GameConfig.LEVEL_TWO_ALIEN_QUOTA;
            case GameConfig.LAST_LEVEL:
                return GameConfig.LEVEL_THREE_ALIEN_QUOTA;
            default:
                throw new IllegalArgumentException("Solo se admiten los niveles 1, 2 y 3.");
        }
    }

    private void spawnWave() {
        aliens.addAll(levelDirector.createWave(level));
    }

    public WorldSnapshot snapshot() {
        return new WorldSnapshot(
                ship,
                aliens,
                playerProjectiles,
                alienProjectiles,
                meteorites,
                lives,
                score,
                level,
                aliensDestroyedInLevel,
                getQuotaForLevel(level),
                phase,
                playerFiredThisFrame,
                entityExplodedThisFrame,
                shipHurtThisFrame);
    }
}
