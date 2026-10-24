package puppy.code.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/**
 * Estrategia de ataque en perdigones / escopeta (patrón Strategy).
 * Dispara 3 proyectiles apuntando al jugador: uno central y dos en abanico a +-15 grados.
 */
public final class SpreadAttackStrategy implements AlienAttackStrategy {

    private static final float SPREAD_ANGLE_DEGREES = 15f;

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        LinearAttackStrategy.validate(alien, target);
        Vector2 aim = LinearAttackStrategy.directionTo(alien, target);

        List<AlienProjectile> volley = new ArrayList<>(3);
        // 1. Proyectil central hacia la posición de la nave
        volley.add(LinearAttackStrategy.projectileFrom(alien, aim));
        // 2. Proyectil lateral izquierdo (-15°)
        volley.add(LinearAttackStrategy.projectileFrom(
                alien, aim.cpy().rotateDeg(-SPREAD_ANGLE_DEGREES)));
        // 3. Proyectil lateral derecho (+15°)
        volley.add(LinearAttackStrategy.projectileFrom(
                alien, aim.cpy().rotateDeg(SPREAD_ANGLE_DEGREES)));

        return Collections.unmodifiableList(volley);
    }
}