package puppy.code.behaviors;

import java.util.List;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/** Define cómo un alien prepara una descarga hacia la nave del jugador. */
public interface AlienAttackStrategy {

    List<AlienProjectile> createVolley(Alien alien, Ship target);
}
