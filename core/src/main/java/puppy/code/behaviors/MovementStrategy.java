package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/**
 * Estrategia (patrón Strategy) que decide cómo se desplaza un alien.
 *
 * <p>Desacopla la cinemática respecto de la entidad {@code Alien}, permitiendo
 * intercambiar trayectorias en tiempo de ejecución sin conocer la clase concreta.
 */
public interface MovementStrategy {

    /** Velocidad de la estrategia, expresada en unidades por segundo. */
    float speed();

    /**
     * Calcula la siguiente posición.
     *
     * @param x posición horizontal actual
     * @param y posición vertical actual
     * @param age segundos transcurridos desde que apareció el alien
     * @param delta segundos transcurridos desde el cuadro anterior
     * @return una posición nueva; no modifica ningún objeto del mundo
     */
    Vector2 nextPosition(float x, float y, float age, float delta);
}