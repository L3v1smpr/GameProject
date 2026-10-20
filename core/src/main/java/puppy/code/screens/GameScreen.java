package puppy.code.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.world.GamePhase;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldSnapshot;

/** Actualiza el modelo y dibuja su snapshot de solo lectura. */
public final class GameScreen extends AbstractGameScreen {

    private static final float BLINK_STEPS_PER_SECOND = 10f;

    private final ScreenNavigator navigator;
    private final GameWorld world;
    private final PlayerInput playerInput;
    private final NavigationInput navigationInput;
    private WorldSnapshot snapshot;
    private float elapsedSeconds;

    public GameScreen(
            GameAssets assets,
            ScreenNavigator navigator,
            GameWorld world,
            PlayerInput playerInput,
            NavigationInput navigationInput) {
        super(assets);
        if (navigator == null || world == null || playerInput == null || navigationInput == null) {
            throw new IllegalArgumentException("Los servicios de GameScreen no pueden ser null.");
        }
        this.navigator = navigator;
        this.world = world;
        this.playerInput = playerInput;
        this.navigationInput = navigationInput;
        snapshot = world.snapshot();
    }

    @Override
    protected void updateScreen(float delta) {
        if (navigationInput.backJustPressed()) {
            navigator.showMainMenu();
            return;
        }
        elapsedSeconds += delta;
        world.update(delta, playerInput);
        snapshot = world.snapshot();
    }

    @Override
    protected void drawScreen() {
        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);

        WorldSnapshot.EntityView ship = snapshot.getShip();
        assets.shapes().setColor(shipColor());
        assets.shapes().rect(ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());

        assets.shapes().setColor(Color.ORANGE);
        for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
            assets.shapes().rect(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
        }
        assets.shapes().end();

        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();
        assets.font().draw(assets.batch(), "Vidas: " + snapshot.getLives(), 24f, 770f);
        assets.font().draw(assets.batch(), "W A S D: mover   |   ESC: volver al menu", 24f, 738f);
        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            assets.font().draw(assets.batch(), "GAME OVER", 540f, 420f);
        }
        assets.batch().end();
    }

    /** Parpadea entre cian y blanco mientras la nave es invulnerable. */
    private Color shipColor() {
        if (!snapshot.isShipInvulnerable()) {
            return Color.CYAN;
        }
        boolean blinkOn = ((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0;
        return blinkOn ? Color.WHITE : Color.CYAN;
    }
}
