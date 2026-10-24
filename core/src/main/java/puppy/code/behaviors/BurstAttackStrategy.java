package puppy.code.behaviors;

import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/**
 * Estrategia de ataque en ráfaga continua / metralleta (patrón Strategy).
 * Dispara una cadena de 5 proyectiles consecutivos a 0.10s de intervalo
 * dirigidos en tiempo real al jugador.
 */
public final class BurstAttackStrategy implements AlienAttackStrategy {

    private static final int BURST_SHOTS = 6;
    private static final float SHOT_INTERVAL_SECONDS = 0.2f;
    private static final float BURST_REST_SECONDS = 1.2f;

    private int shotsFiredInBurst = 0;

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        LinearAttackStrategy.validate(alien, target);
        Vector2 aim = LinearAttackStrategy.directionTo(alien, target);

        shotsFiredInBurst++;

        // Dispara un proyectil unitario en la posición actual del bombardero
        return Collections.singletonList(
                LinearAttackStrategy.projectileFrom(alien, aim));
    }

    @Override
    public float nextCooldownSeconds() {
        if (shotsFiredInBurst >= BURST_SHOTS) {
            shotsFiredInBurst = 0;
            return BURST_REST_SECONDS; // Pausa al completar la ráfaga
        }
        return SHOT_INTERVAL_SECONDS; // Intervalo rápido de metralleta
    }
}