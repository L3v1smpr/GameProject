package puppy.code.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;

/** Fija el ciclo de pantalla común (Template Method). */
public abstract class AbstractGameScreen extends ScreenAdapter {

    protected final GameAssets assets;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;

    protected AbstractGameScreen(GameAssets assets) {
        if (assets == null) {
            throw new IllegalArgumentException("GameAssets no puede ser null.");
        }
        this.assets = assets;
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
    }

    @Override
    public final void render(float delta) {
        float frameDelta = Math.max(0f, delta);
        clearScreen();
        handleInput(frameDelta);
        update(frameDelta);
        draw(frameDelta);
    }

    protected void clearScreen() {
        ScreenUtils.clear(0.035f, 0.045f, 0.09f, 1f);
        viewport.apply();
    }

    protected abstract void handleInput(float delta);

    protected abstract void update(float delta);

    protected abstract void draw(float delta);

    @Override
    public final void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
