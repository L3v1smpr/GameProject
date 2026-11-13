package puppy.code.factories;

import java.util.List;
import puppy.code.entities.Alien;

/** Contrato que el mundo usa para solicitar enemigos sin depender del director concreto. */
public interface AlienFactory {

    List<Alien> createWave(int level, int count);

    Alien createStaticAlien(int level, float x, float targetY);

    Alien createBomberAlien(int level, float x, float cruiseY);
}
