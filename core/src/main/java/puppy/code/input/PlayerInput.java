package puppy.code.input;

import com.badlogic.gdx.math.Vector2;

/** Expone la intención de movimiento sin revelar cómo leemos el teclado. */
public interface PlayerInput {

    Vector2 movementDirection();
}
