package puppy.code.app;

import com.badlogic.gdx.Game;
import puppy.code.assets.GameAssets;
import puppy.code.factories.LevelDirector;
import puppy.code.input.InitialsInput;
import puppy.code.input.KeyboardInitialsInput;
import puppy.code.input.KeyboardNavigationInput;
import puppy.code.input.KeyboardPlayerInput;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.screens.GameOverScreen;
import puppy.code.screens.GameScreen;
import puppy.code.screens.MenuScreen;
import puppy.code.screens.VictoryScreen;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.ScoreRepository;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldSnapshot;

/** Punto de inicio: conecta las dependencias compartidas y coordina las pantallas. */
public final class SpaceNavigation extends Game implements ScreenNavigator {

    private GameAssets assets;
    private NavigationInput navigationInput;
    private InitialsInput initialsInput;
    private PlayerInput playerInput;
    private LevelDirector levelDirector;
    private CollisionSystem collisionSystem;
    private ScoreRepository scoreRepository;
    private GameWorld currentWorld;

    @Override
    public void create() {
        assets = GameAssets.getInstance();
        navigationInput = new KeyboardNavigationInput();
        initialsInput = new KeyboardInitialsInput();
        playerInput = new KeyboardPlayerInput();
        levelDirector = new LevelDirector();
        collisionSystem = new CollisionSystem();
        scoreRepository = new ScoreRepository();
        showMenu();
    }

    @Override
    public void showMenu() {
        currentWorld = null;
        setScreen(new MenuScreen(assets, this, navigationInput, scoreRepository));
    }

    @Override
    public void showGame() {
        currentWorld = new GameWorld(levelDirector, collisionSystem);
        setScreen(new GameScreen(assets, this, currentWorld, playerInput, navigationInput));
    }

    @Override
    public void showGameOver() {
        if (currentWorld == null) {
            showMenu();
            return;
        }
        WorldSnapshot snapshot = currentWorld.snapshot();
        showGameOver(snapshot.getScore(), snapshot.getLevel());
    }

    @Override
    public void showGameOver(int finalScore, int levelReached) {
        setScreen(new GameOverScreen(
                assets, this, navigationInput, initialsInput, scoreRepository, finalScore, levelReached));
    }

    @Override
    public void showVictory() {
        if (currentWorld == null) {
            showMenu();
            return;
        }
        showVictory(currentWorld.snapshot().getScore());
    }

    @Override
    public void showVictory(int finalScore) {
        setScreen(new VictoryScreen(assets, this, navigationInput, initialsInput, scoreRepository, finalScore));
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
