package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.PlayerInput;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldSnapshot;

/** Actualiza el modelo y dibuja su snapshot de solo lectura. */
public final class GameScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final GameWorld world;
    private final PlayerInput input;
    private WorldSnapshot snapshot;

    public GameScreen(GameAssets assets, ScreenNavigator navigator, GameWorld world, PlayerInput input) {
        super(assets);
        if (navigator == null || world == null || input == null) {
            throw new IllegalArgumentException("Los servicios de GameScreen no pueden ser null.");
        }
        this.navigator = navigator;
        this.world = world;
        this.input = input;
        snapshot = world.snapshot();
    }

    @Override
    protected void updateScreen(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.showMainMenu();
            return;
        }
        world.update(delta, input);
        snapshot = world.snapshot();
    }

    @Override
    protected void drawScreen() {
        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);

        WorldSnapshot.EntityView ship = snapshot.getShip();
        assets.shapes().setColor(Color.CYAN);
        assets.shapes().rect(ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());

        assets.shapes().setColor(Color.ORANGE);
        for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
            assets.shapes().rect(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
        }
        assets.shapes().end();

        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();
        assets.font().draw(assets.batch(), "Nave y alien: figuras temporales", 24f, 770f);
        assets.font().draw(assets.batch(), "W A S D: mover   |   ESC: volver al menu", 24f, 738f);
        assets.batch().end();
    }
}
