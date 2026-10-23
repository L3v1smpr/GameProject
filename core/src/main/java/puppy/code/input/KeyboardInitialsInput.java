package puppy.code.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** Traduce las teclas A-Z y Retroceso al contrato de ingreso de iniciales. */
public final class KeyboardInitialsInput implements InitialsInput {

    @Override
    public char letterJustPressed() {
        for (int keyCode = Input.Keys.A; keyCode <= Input.Keys.Z; keyCode++) {
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                return (char) ('A' + keyCode - Input.Keys.A);
            }
        }
        return ' ';
    }

    @Override
    public boolean deleteJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE);
    }
}
