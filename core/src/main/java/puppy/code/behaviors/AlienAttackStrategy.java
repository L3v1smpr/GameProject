package puppy.code.behaviors;

import java.util.List;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/** Contrato desacoplado para las estrategias de ataque enemigo (patrón Strategy). */
public interface AlienAttackStrategy {

    List<AlienProjectile> createVolley(Alien alien, Ship target);

    /** Tiempo en segundos hasta el próximo disparo (por defecto 1.8s). */
    default float nextCooldownSeconds() {
        return GameConfig.ALIEN_FIRE_COOLDOWN_SECONDS;
    }
}