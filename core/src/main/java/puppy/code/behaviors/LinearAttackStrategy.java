package puppy.code.behaviors;

import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/**
 * Estrategia de ataque lineal estándar (patrón Strategy).
 */
public class LinearAttackStrategy implements AlienAttackStrategy {

    private static final Vector2 STRAIGHT_DOWN = new Vector2(0f, -1f);

    public static void validate(Alien alien, Ship target) {
        if (alien == null || target == null) {
            throw new IllegalArgumentException("El alien y el objetivo no pueden ser null.");
        }
    }

    public static Vector2 directionTo(Alien alien, Ship target) {
        float alienCenterX = alien.getX() + alien.getWidth() / 2f;
        float alienCenterY = alien.getY() + alien.getHeight() / 2f;
        float targetCenterX = target.getX() + target.getWidth() / 2f;
        float targetCenterY = target.getY() + target.getHeight() / 2f;

        Vector2 direction = new Vector2(targetCenterX - alienCenterX, targetCenterY - alienCenterY);
        if (direction.len2() <= 0.0001f) {
            return STRAIGHT_DOWN.cpy();
        }
        return direction.nor();
    }

    public static AlienProjectile projectileFrom(
            Alien alien, Vector2 direction, float offsetX, float offsetY) {
        float startX = alien.getX() + alien.getWidth() / 2f - GameConfig.PROJECTILE_WIDTH / 2f + offsetX;
        float startY = alien.getY() + offsetY;
        // Pasa las coordenadas, la dirección normalizada y la velocidad configurada
        return new AlienProjectile(startX, startY, direction, GameConfig.ALIEN_PROJECTILE_SPEED);
    }

    public static AlienProjectile projectileFrom(Alien alien, Vector2 direction) {
        return projectileFrom(alien, direction, 0f, 0f);
    }

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        validate(alien, target);
        return Collections.singletonList(projectileFrom(alien, STRAIGHT_DOWN));
    }
}