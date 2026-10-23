package puppy.code.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.GameEntity;
import puppy.code.entities.Meteorite;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;

/** Vista inmutable que las pantallas usan para dibujar sin apropiarse de las entidades. */
public final class WorldSnapshot {

    private final EntityView ship;
    private final boolean shipInvulnerable;
    private final List<EntityView> aliens;
    private final List<EntityView> playerProjectiles;
    private final List<EntityView> alienProjectiles;
    private final List<EntityView> meteorites;
    private final int lives;
    private final int score;
    private final int level;
    private final int aliensDestroyedInLevel;
    private final int quotaForLevel;
    private final GamePhase phase;

    WorldSnapshot(
            Ship ship,
            List<Alien> aliens,
            List<PlayerProjectile> playerProjectiles,
            List<AlienProjectile> alienProjectiles,
            List<Meteorite> meteorites,
            int lives,
            int score,
            int level,
            int aliensDestroyedInLevel,
            int quotaForLevel,
            GamePhase phase) {
        this.ship = EntityView.from(ship);
        this.shipInvulnerable = ship.isInvulnerable();
        this.aliens = viewsOf(aliens);
        this.playerProjectiles = viewsOf(playerProjectiles);
        this.alienProjectiles = viewsOf(alienProjectiles);
        this.meteorites = viewsOf(meteorites);
        this.lives = lives;
        this.score = score;
        this.level = level;
        this.aliensDestroyedInLevel = aliensDestroyedInLevel;
        this.quotaForLevel = quotaForLevel;
        this.phase = phase;
    }

    private static <T extends GameEntity> List<EntityView> viewsOf(List<T> entities) {
        List<EntityView> views = new ArrayList<>(entities.size());
        for (T entity : entities) {
            views.add(EntityView.from(entity));
        }
        return Collections.unmodifiableList(views);
    }

    public EntityView getShip() {
        return ship;
    }

    public boolean isShipInvulnerable() {
        return shipInvulnerable;
    }

    public List<EntityView> getAliens() {
        return aliens;
    }

    public List<EntityView> getPlayerProjectiles() {
        return playerProjectiles;
    }

    public List<EntityView> getAlienProjectiles() {
        return alienProjectiles;
    }

    public List<EntityView> getMeteorites() {
        return meteorites;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getAliensDestroyedInLevel() {
        return aliensDestroyedInLevel;
    }

    public int getQuotaForLevel() {
        return quotaForLevel;
    }

    public GamePhase getPhase() {
        return phase;
    }

    /** Valor inmutable; el mundo no expone sus entidades ni colecciones mutables. */
    public static final class EntityView {

        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private EntityView(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private static EntityView from(GameEntity entity) {
            return new EntityView(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }
    }
}
