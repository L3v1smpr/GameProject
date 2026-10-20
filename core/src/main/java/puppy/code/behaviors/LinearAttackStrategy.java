package puppy.code.behaviors;

import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/** Dispara un proyectil dirigido al centro de la nave. */
public final class LinearAttackStrategy implements AlienAttackStrategy {

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        validate(alien, target);
        Vector2 direction = directionTo(alien, target);
        AlienProjectile projectile = projectileFrom(alien, direction);
        return Collections.singletonList(projectile);
    }

    static void validate(Alien alien, Ship target) {
        if (alien == null || target == null) {
            throw new IllegalArgumentException("El alien y su objetivo no pueden ser null.");
        }
    }

    static Vector2 directionTo(Alien alien, Ship target) {
        float alienCenterX = alien.getX() + alien.getWidth() / 2f;
        float alienCenterY = alien.getY() + alien.getHeight() / 2f;
        float shipCenterX = target.getX() + target.getWidth() / 2f;
        float shipCenterY = target.getY() + target.getHeight() / 2f;
        Vector2 direction = new Vector2(shipCenterX - alienCenterX, shipCenterY - alienCenterY);
        if (direction.isZero()) {
            return new Vector2(0f, -1f);
        }
        return direction.nor();
    }

    static AlienProjectile projectileFrom(Alien alien, Vector2 direction) {
        float spawnX = alien.getX() + alien.getWidth() / 2f;
        float spawnY = alien.getY() + alien.getHeight() / 2f;
        return new AlienProjectile(spawnX, spawnY, direction, GameConfig.ALIEN_PROJECTILE_SPEED);
    }
}
