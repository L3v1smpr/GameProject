package puppy.code.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;

/** Fija el ciclo de pantalla común (Template Method). */
public abstract class AbstractGameScreen extends ScreenAdapter {

    // Velocidad de la cinta: 10 píxeles por segundo (deriva espacial ultra lenta)
    private static final float BACKGROUND_SCROLL_SPEED = 10f;

    protected final GameAssets assets;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    protected final Starfield starfield;

    private float backgroundY;

    protected AbstractGameScreen(GameAssets assets) {
        if (assets == null) {
            throw new IllegalArgumentException("GameAssets no puede ser null.");
        }
        this.assets = assets;
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        starfield = new Starfield();
        backgroundY = 0f;
    }

    @Override
    public final void render(float delta) {
        float frameDelta = Math.max(0f, delta);
        clearScreen();
        renderBackground(frameDelta);
        handleInput(frameDelta);
        update(frameDelta);
        draw(frameDelta);
    }

    protected void clearScreen() {
        ScreenUtils.clear(0.02f, 0.02f, 0.05f, 1f);
        viewport.apply();
    }

    /** 
     * Paso hook del Template Method: desplaza y dibuja dos copias consecutivas de la 
     * textura de fondo en cinta continua antes de dibujar las estrellas y las entidades.
     */
    protected void renderBackground(float delta) {
    	Texture bg = assets.getTexture(GameAssets.BACKGROUND_TEXTURE);
        if (bg != null) {
            // Desplazar hacia abajo a velocidad constante independiente del framerate
            backgroundY -= BACKGROUND_SCROLL_SPEED * delta;
            while (backgroundY <= -GameConfig.WORLD_HEIGHT) {
                backgroundY += GameConfig.WORLD_HEIGHT;
            }

            assets.batch().setProjectionMatrix(camera.combined);
            assets.batch().begin();

            // Tinte atenuado al 35% de brillo
            assets.batch().setColor(0.35f, 0.35f, 0.35f, 1f);

            // Copia 1: Imagen base
            assets.batch().draw(
                    bg, 0f, backgroundY, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

            // Copia 2: Imagen continua ubicada inmediatamente arriba
            assets.batch().draw(
                    bg, 0f, backgroundY + GameConfig.WORLD_HEIGHT, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

            // Restaurar el color neutro para los demás elementos gráficos
            assets.batch().setColor(Color.WHITE);
            assets.batch().end();
        }

        starfield.updateAndRender(delta, assets.shapes(), camera);
    }

    protected abstract void handleInput(float delta);

    protected abstract void update(float delta);

    protected abstract void draw(float delta);

    @Override
    public final void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}