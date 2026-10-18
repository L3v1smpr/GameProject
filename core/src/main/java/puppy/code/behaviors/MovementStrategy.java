package puppy.code.behaviors;

import com.badlogic.gdx.math.Vector2;

/**
 * Estrategia (patrón Strategy) que decide cómo se desplaza un alien.
 *
 * <p>Las implementaciones no guardan estado: reciben la posición y el tiempo de vida del alien y
 * devuelven la siguiente posición. Así se pueden compartir entre aliens y el alien no necesita
 * conocer qué trayectoria sigue.
 */
public interface MovementStrategy {

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
