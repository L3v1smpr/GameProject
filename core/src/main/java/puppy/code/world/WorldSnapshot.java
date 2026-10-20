package puppy.code.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import puppy.code.entities.Alien;
import puppy.code.entities.GameEntity;
import puppy.code.entities.Ship;

/** Vista inmutable que las pantallas usan para dibujar sin apropiarse de las entidades. */
public final class WorldSnapshot {

    private final EntityView ship;
    private final boolean shipInvulnerable;
    private final List<EntityView> aliens;
    private final int lives;
    private final GamePhase phase;

    WorldSnapshot(Ship ship, List<Alien> aliens, int lives, GamePhase phase) {
        this.ship = EntityView.from(ship);
        this.shipInvulnerable = ship.isInvulnerable();

        List<EntityView> alienViews = new ArrayList<>(aliens.size());
        for (Alien alien : aliens) {
            alienViews.add(EntityView.from(alien));
        }
        this.aliens = Collections.unmodifiableList(alienViews);
        this.lives = lives;
        this.phase = phase;
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

    public int getLives() {
        return lives;
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
