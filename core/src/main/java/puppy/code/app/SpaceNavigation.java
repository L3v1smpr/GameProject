package puppy.code.app;

import com.badlogic.gdx.Game;
import puppy.code.assets.GameAssets;
import puppy.code.input.KeyboardPlayerInput;
import puppy.code.screens.GameScreen;
import puppy.code.screens.MenuScreen;
import puppy.code.world.GameWorld;

/** Punto de inicio: conecta las dependencias compartidas y coordina las pantallas. */
public final class SpaceNavigation extends Game implements ScreenNavigator {

    private GameAssets assets;

    @Override
    public void create() {
        assets = GameAssets.getInstance();
        showMainMenu();
    }

    @Override
    public void showMainMenu() {
        setScreen(new MenuScreen(assets, this));
    }

    @Override
    public void startNewGame() {
        GameWorld world = new GameWorld();
        setScreen(new GameScreen(assets, this, world, new KeyboardPlayerInput()));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (assets != null) {
            assets.dispose();
            assets = null;
        }
    }
}
