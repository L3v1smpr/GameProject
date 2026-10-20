package puppy.code.entities;

import com.badlogic.gdx.math.Rectangle;

/** Contrato mínimo de los objetos que pueden participar en una colisión. */
public interface Collidable {

    /** Devuelve una copia de los límites actuales para no exponer el estado interno. */
    Rectangle bounds();
}
