package puppy.code.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** Traduce el teclado a las acciones de navegación de menús. */
public final class KeyboardNavigationInput implements NavigationInput {

    @Override
    public boolean confirmJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
    }

    @Override
    public boolean backJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    @Override
    public boolean anyActionJustPressed() {
        if (Gdx.input.justTouched()) {
            return true;
        }
        for (int keyCode = 1; keyCode < 256; keyCode++) {
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean upJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W);
    }

    @Override
    public boolean downJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S);
    }

    @Override
    public boolean leftJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A);
    }

    @Override
    public boolean rightJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D);
    }
}