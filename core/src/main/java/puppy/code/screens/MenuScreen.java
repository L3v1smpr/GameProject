package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;

/** Presenta el menú y delega las transiciones a la aplicación. */
public final class MenuScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;

    public MenuScreen(GameAssets assets, ScreenNavigator navigator) {
        super(assets);
        if (navigator == null) {
            throw new IllegalArgumentException("ScreenNavigator no puede ser null.");
        }
        this.navigator = navigator;
    }

    @Override
    protected void updateScreen(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.justTouched()) {
            navigator.startNewGame();
        }
    }

    @Override
    protected void drawScreen() {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();
        assets.font().draw(assets.batch(), "SPACE NAVIGATION", 470f, 455f);
        assets.font().draw(assets.batch(), "Presiona ENTER para comenzar", 455f, 390f);
        assets.font().draw(assets.batch(), "Movimiento: W A S D", 505f, 345f);
        assets.batch().end();
    }
}
