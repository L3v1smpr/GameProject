package puppy.code.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** Traduce Enter y Escape a las acciones de navegación; es el único lugar que lee esas teclas. */
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
}
