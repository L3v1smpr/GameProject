package puppy.code.app;

import com.badlogic.gdx.Game;
import puppy.code.assets.GameAssets;
import puppy.code.input.KeyboardNavigationInput;
import puppy.code.input.KeyboardPlayerInput;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.screens.GameScreen;
import puppy.code.screens.MenuScreen;
import puppy.code.world.GameWorld;

/** Punto de inicio: conecta las dependencias compartidas y coordina las pantallas. */
public final class SpaceNavigation extends Game implements ScreenNavigator {

    private GameAssets assets;
    private NavigationInput navigationInput;
    private PlayerInput playerInput;

    @Override
    public void create() {
        assets = GameAssets.getInstance();
        navigationInput = new KeyboardNavigationInput();
        playerInput = new KeyboardPlayerInput();
        showMainMenu();
    }

    @Override
    public void showMainMenu() {
        setScreen(new MenuScreen(assets, this, navigationInput));
    }

    @Override
    public void startNewGame() {
        GameWorld world = new GameWorld();
        setScreen(new GameScreen(assets, this, world, playerInput, navigationInput));
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
