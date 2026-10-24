package puppy.code.behaviors;

import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/** Dispara un proyectil en línea recta perpendicular hacia abajo (Nivel 1). */
public final class LinearAttackStrategy implements AlienAttackStrategy {

    private static final Vector2 STRAIGHT_DOWN = new Vector2(0f, -1f);

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        validate(alien, target);
        // Trayectoria vertical recta hacia el Sur
        AlienProjectile projectile = projectileFrom(alien, STRAIGHT_DOWN);
        return Collections.singletonList(projectile);
    }

    static void validate(Alien alien, Ship target) {
        if (alien == null || target == null) {
            throw new IllegalArgumentException("El alien y su objetivo no pueden ser null.");
        }
    }

    public static Vector2 directionTo(Alien alien, Ship target) {
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

    public static AlienProjectile projectileFrom(Alien alien, Vector2 direction) {
        // Centrar el proyectil horizontalmente respecto al ancho del alien y ubicarlo en su base
        float spawnX = alien.getX() + (alien.getWidth() - GameConfig.PROJECTILE_WIDTH) / 2f;
        float spawnY = alien.getY();
        return new AlienProjectile(spawnX, spawnY, direction, GameConfig.ALIEN_PROJECTILE_SPEED);
    }
}