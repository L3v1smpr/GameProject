package puppy.code.screens;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;

/**
 * Pantalla de victoria.
 * Reutiliza el algoritmo base de renderizado de AbstractGameScreen
 * y despliega el banner de victoria junto a las opciones de navegación.
 */
public final class VictoryScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final NavigationInput input;
    private final int finalScore;

    public VictoryScreen(
            GameAssets assets,
            ScreenNavigator navigator,
            NavigationInput input,
            int finalScore) {
        super(assets);
        if (navigator == null || input == null) {
            throw new IllegalArgumentException("El navegador y el input no pueden ser null.");
        }
        this.navigator = navigator;
        this.input = input;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        // Detiene la música de combate activa
        assets.stopMusic();
    }

    @Override
    public void hide() {
        assets.stopMusic();
    }

    @Override
    protected void handleInput(float delta) {
        // Enter / Espacio: iniciar nueva partida
        if (input.confirmJustPressed()) {
            navigator.showGame();
        } else if (input.backJustPressed()) {
            // ESC: volver al menú principal
            navigator.showMenu();
        }
    }

    @Override
    protected void update(float delta) {
        // Hook de actualización para efectos o animaciones
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        // 1. Banner central de Victoria
        Texture banner = assets.getTexture(GameAssets.BANNER_VICTORIA_TEXTURE);
        if (banner != null) {
            float bannerWidth = 400f;
            float bannerHeight = 200f;
            float x = (camera.viewportWidth - bannerWidth) / 2f;
            float y = (camera.viewportHeight - bannerHeight) / 2f + 60f;
            assets.batch().draw(banner, x, y, bannerWidth, bannerHeight);
        } else {
            assets.font().draw(assets.batch(), "¡VICTORIA!", 540f, 480f);
        }

        // 2. Puntaje y controles de navegación
        assets.font().draw(assets.batch(), "Puntaje final: " + finalScore, 520f, 320f);
        assets.font().draw(assets.batch(), "Presiona ENTER para jugar de nuevo", 465f, 240f);
        assets.font().draw(assets.batch(), "Presiona ESC para salir al menu", 485f, 200f);

        assets.batch().end();
    }
}
