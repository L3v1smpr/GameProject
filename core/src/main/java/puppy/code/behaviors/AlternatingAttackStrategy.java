package puppy.code.behaviors;

import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/**
 * Estrategia de ataque alternada (patrón Strategy).
 * Selecciona aleatoriamente entre disparar recto hacia abajo o apuntar
 * directamente a la nave del jugador.
 */
public final class AlternatingAttackStrategy implements AlienAttackStrategy {

    private static final Vector2 STRAIGHT_DOWN = new Vector2(0f, -1f);

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        LinearAttackStrategy.validate(alien, target);

        // 50% probabilidad de disparo vertical hacia abajo, 50% dirigido a la nave
        Vector2 direction = MathUtils.randomBoolean()
                ? STRAIGHT_DOWN
                : LinearAttackStrategy.directionTo(alien, target);

        return Collections.singletonList(
                LinearAttackStrategy.projectileFrom(alien, direction));
    }
}