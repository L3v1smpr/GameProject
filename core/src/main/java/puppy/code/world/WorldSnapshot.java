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
    private final boolean shipDashReady;
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
        this.shipDashReady = ship.canDash();
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

    public boolean isShipDashReady() {
        return shipDashReady;
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

    public static final class EntityView {

        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final String assetPath;
        private final float rotation;
        private final boolean firing;
        private final boolean hurt;
        private final boolean isStatic;
        private final boolean isBomber;

        private EntityView(
                float x,
                float y,
                float width,
                float height,
                String assetPath,
                float rotation,
                boolean firing,
                boolean hurt,
                boolean isStatic,
                boolean isBomber) {
        	this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.assetPath = assetPath;
            this.rotation = rotation;
            this.firing = firing;
            this.hurt = hurt;
            this.isStatic = isStatic;
            this.isBomber = isBomber;
        }

        private static EntityView from(GameEntity entity) {
            String assetPath = entity instanceof Meteorite
                    ? ((Meteorite) entity).getAssetPath()
                    : null;
            float rotation = entity instanceof AlienProjectile
                    ? ((AlienProjectile) entity).getRotation()
                    : 0f;
            boolean firing = entity instanceof Alien
                    ? ((Alien) entity).isFiring()
                    : false;
            boolean isStatic = entity instanceof Alien
                    ? ((Alien) entity).isStatic()
                    : false;
            boolean isBomber = entity instanceof Alien
                    ? ((Alien) entity).isBomber()
                    : false;
            return new EntityView(
                    entity.getX(),
                    entity.getY(),
                    entity.getWidth(),
                    entity.getHeight(),
                    assetPath,
                    rotation,
                    firing,
                    entity.isHurt(),
                    isStatic,
                    isBomber);
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

        public String getAssetPath() {
            return assetPath;
        }

        public float getRotation() {
            return rotation;
        }

        public boolean isFiring() {
            return firing;
        }

        public boolean isHurt() {
            return hurt;
        }

        public boolean isStatic() {
            return isStatic;
        }
        
        public boolean isBomber() {
            return isBomber;
        }
    }
}