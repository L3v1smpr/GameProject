package puppy.code.screens;

import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;

/** Presenta el menú y delega las transiciones a la aplicación. */
public final class MenuScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final NavigationInput navigationInput;

    public MenuScreen(GameAssets assets, ScreenNavigator navigator, NavigationInput navigationInput) {
        super(assets);
        if (navigator == null || navigationInput == null) {
            throw new IllegalArgumentException("Los servicios de MenuScreen no pueden ser null.");
        }
        this.navigator = navigator;
        this.navigationInput = navigationInput;
    }

    @Override
    protected void updateScreen(float delta) {
        if (navigationInput.confirmJustPressed()) {
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
