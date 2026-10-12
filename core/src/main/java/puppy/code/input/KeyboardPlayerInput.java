package puppy.code.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/** Traduce las teclas acordadas a una dirección de movimiento normalizada. */
public final class KeyboardPlayerInput implements PlayerInput {

    @Override
    public Vector2 movementDirection() {
        float horizontal = 0f;
        float vertical = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vertical -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            vertical += 1f;
        }

        Vector2 direction = new Vector2(horizontal, vertical);
        if (direction.len2() > 1f) {
            direction.nor();
        }
        return direction;
    }
}
