package puppy.code.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/** Traduce las teclas WASD, I (disparo), O (dash) y P (pausa) a comandos del jugador. */
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

    @Override
    public boolean firePressed() {
        return Gdx.input.isKeyPressed(Input.Keys.I);
    }

    @Override
    public boolean dashJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.O);
    }

    @Override
    public boolean pauseJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.P);
    }
}