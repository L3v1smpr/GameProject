package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Meteorite;
import puppy.code.entities.MeteoriteSize;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;
import puppy.code.factories.LevelDirector;
import puppy.code.input.PlayerInput;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.PlayerWeapon;

/**
 * Coordina las entidades y aplica las reglas de una partida (Controller de GRASP).
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
    private float alienReinforcementTimer;
    private WorldAudioListener audioListener;

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

    public void setAudioListener(WorldAudioListener audioListener) {
        this.audioListener = audioListener;
    }

    private void notifyAudio(java.util.function.Consumer<WorldAudioListener> action) {
        if (audioListener != null) {
            action.accept(audioListener);
        }
    }

    public void restart() {
        level = GameConfig.FIRST_LEVEL;
        lives = GameConfig.INITIAL_LIVES;
        score = 0;
        aliensDestroyedInLevel = 0;
        meteoriteSpawnTimer = 0f;
        alienReinforcementTimer = 0f;
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
        if (phase != GamePhase.ACTIVE) {
            return;
        }

        Vector2 movement = input.movementDirection();
        if (input.dashJustPressed() && ship.canDash()) {
            ship.triggerDash(movement);
            notifyAudio(WorldAudioListener::onPlayerDash);
        }
        ship.update(delta, movement, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        Optional<PlayerProjectile> newProjectile =
                playerWeapon.update(delta, input.firePressed(), ship);
        newProjectile.ifPresent(projectile -> {
            playerProjectiles.add(projectile);
            notifyAudio(WorldAudioListener::onPlayerShoot);
        });

        updatePlayerProjectiles(delta);
        updateAliens(delta);
        updateAlienProjectiles(delta);
        updateMeteorites(delta);

        resolveProjectileCollisions();
        resolveShipCollisions();
        removeInactiveEntities();
        updateQuotaProgression(delta);
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
            List<AlienProjectile> shots = alien.fireDue(delta, ship);
            if (!shots.isEmpty()) {
                alienProjectiles.addAll(shots);
                notifyAudio(WorldAudioListener::onAlienShoot);
            }
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
        float currentCooldown = getMeteoriteCooldownForLevel(level);
        if (meteoriteSpawnTimer >= currentCooldown) {
            meteoriteSpawnTimer -= currentCooldown;
            meteorites.add(Meteorite.random(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        }
        for (Meteorite meteorite : meteorites) {
            meteorite.update(delta);
        }
        meteorites.removeIf(meteorite ->
                !meteorite.isActive() || meteorite.getY() + meteorite.getHeight() < 0f);
    }

    private float getMeteoriteCooldownForLevel(int currentLevel) {
        switch (currentLevel) {
            case GameConfig.FIRST_LEVEL:
                return GameConfig.LEVEL_ONE_METEORITE_COOLDOWN;
            case GameConfig.SECOND_LEVEL:
                return GameConfig.LEVEL_TWO_METEORITE_COOLDOWN;
            case GameConfig.LAST_LEVEL:
                return GameConfig.LEVEL_THREE_METEORITE_COOLDOWN;
            default:
                return GameConfig.METEORITE_SPAWN_COOLDOWN_SECONDS;
        }
    }

    private void resolveProjectileCollisions() {
        for (PlayerProjectile projectile : playerProjectiles) {
            if (!projectile.isActive()) {
                continue;
            }
            for (Alien alien : aliens) {
                if (alien.isActive() && collisions.collides(projectile, alien)) {
                    projectile.deactivate();
                    float centerX = alien.getX() + alien.getWidth() / 2f;
                    float centerY = alien.getY() + alien.getHeight() / 2f;
                    if (alien.takeDamage()) {
                        score += GameConfig.ALIEN_SCORE;
                        aliensDestroyedInLevel++;
                        notifyAudio(l -> l.onAlienDestroyed(centerX, centerY));
                    } else {
                        notifyAudio(WorldAudioListener::onAlienHurt);
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
                    float centerX = meteorite.getX() + meteorite.getWidth() / 2f;
                    float centerY = meteorite.getY() + meteorite.getHeight() / 2f;
                    if (meteorite.takeDamage()) {
                        score += meteorite.getScoreValue();
                        boolean isMajor = meteorite.getSize() == MeteoriteSize.MEDIUM
                                || meteorite.getSize() == MeteoriteSize.LARGE;
                        notifyAudio(l -> l.onMeteoriteDestroyed(centerX, centerY, isMajor));
                    } else {
                        notifyAudio(WorldAudioListener::onMeteoriteHit);
                    }
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

    private void resolveShipCollisions() {
        if (phase != GamePhase.ACTIVE || ship.isInvulnerable()) {
            return;
        }
        for (Alien alien : aliens) {
            if (alien.isActive() && collisions.collides(ship, alien)) {
                alien.deactivate();
                score += GameConfig.ALIEN_SCORE;
                aliensDestroyedInLevel++;
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
        lives = Math.max(0, lives - 1);
        float centerX = ship.getX() + ship.getWidth() / 2f;
        float centerY = ship.getY() + ship.getHeight() / 2f;
        notifyAudio(l -> l.onPlayerHurt(centerX, centerY));

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

    private void updateQuotaProgression(float delta) {
        if (phase != GamePhase.ACTIVE) {
            return;
        }
        int remainingQuota = getQuotaForLevel(level) - aliensDestroyedInLevel;

        if (remainingQuota <= 0) {
            aliens.forEach(Alien::deactivate);
            aliens.clear();
            playerProjectiles.clear();
            alienProjectiles.clear();
            phase = level == GameConfig.LAST_LEVEL
                    ? GamePhase.VICTORY
                    : GamePhase.LEVEL_COMPLETED;
            if (phase == GamePhase.LEVEL_COMPLETED) {
                notifyAudio(WorldAudioListener::onLevelCompleted);
            }
            return;
        }

        // Conteo exclusivo de aliens dinámicos móviles
        long dynamicAliensCount = 0;
        for (Alien alien : aliens) {
            if (!alien.isStatic()) {
                dynamicAliensCount++;
            }
        }

        // Si no quedan aliens móviles, spawnear nueva oleada de inmediato aunque haya torretas fijas
        if (dynamicAliensCount == 0) {
            alienReinforcementTimer = 0f;
            spawnWave();
            return;
        }

        // Si solo queda 1 alien móvil, enviar refuerzos tras un breve temporizador
        if (dynamicAliensCount < 2 && remainingQuota > aliens.size()) {
            alienReinforcementTimer += delta;
            if (alienReinforcementTimer >= GameConfig.ALIEN_REINFORCEMENT_COOLDOWN_SECONDS) {
                alienReinforcementTimer = 0f;
                int spaceAvailable = GameConfig.MAX_ALIENS_ON_SCREEN - aliens.size();
                int toSpawn = Math.min(spaceAvailable, Math.min(2, remainingQuota - aliens.size()));
                if (toSpawn > 0) {
                    aliens.addAll(levelDirector.createWave(level, toSpawn));
                }
            }
        } else {
            alienReinforcementTimer = 0f;
        }
    }

    public void advanceToNextLevel() {
        if (phase != GamePhase.LEVEL_COMPLETED) {
            return;
        }
        level++;
        aliensDestroyedInLevel = 0;
        meteoriteSpawnTimer = 0f;
        alienReinforcementTimer = 0f;
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
        int remainingQuota = getQuotaForLevel(level) - aliensDestroyedInLevel;
        if (remainingQuota <= 0) {
            return;
        }

        // En niveles 2 y 3, si no existe una torreta estática viva y queda cuota, se despliega 1 arriba
        boolean hasStatic = false;
        for (Alien alien : aliens) {
            if (alien.isStatic()) {
                hasStatic = true;
                break;
            }
        }

        if (level >= GameConfig.SECOND_LEVEL && !hasStatic && remainingQuota > 1) {
            float staticX = MathUtils.random(100f, GameConfig.WORLD_WIDTH - 150f);
            float staticY = MathUtils.random(640f, 700f);
            aliens.add(levelDirector.createStaticAlien(level, staticX, staticY));
            remainingQuota--;
        }

        int targetCount = level == GameConfig.FIRST_LEVEL ? MathUtils.random(2, 3) : 3;
        int capacity = GameConfig.MAX_ALIENS_ON_SCREEN - aliens.size();
        int toSpawn = Math.max(1, Math.min(targetCount, Math.min(capacity, remainingQuota)));
        aliens.addAll(levelDirector.createWave(level, toSpawn));
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
                phase);
    }
}