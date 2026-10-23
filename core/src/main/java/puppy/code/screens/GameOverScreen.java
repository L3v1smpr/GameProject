package puppy.code.screens;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;

/**
 * Pantalla de derrota.
 * Extiende de AbstractGameScreen reutilizando el flujo invariable de renderizado
 * y despliega el banner gráfico de fin de partida junto al audio dramático.
 */
public final class GameOverScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final NavigationInput input;
    private final int finalScore;
    private final int levelReached;

    public GameOverScreen(
            GameAssets assets,
            ScreenNavigator navigator,
            NavigationInput input,
            int finalScore,
            int levelReached) {
        super(assets);
        if (navigator == null || input == null) {
            throw new IllegalArgumentException("El navegador y el input no pueden ser null.");
        }
        this.navigator = navigator;
        this.input = input;
        this.finalScore = finalScore;
        this.levelReached = levelReached;
    }

    @Override
    public void show() {
        // Detiene la música de combate y reproduce la pista de derrota
        assets.stopMusic();
        assets.playMusic(GameAssets.GAMEOVER_MUSIC, false, 0.6f);
    }

    @Override
    public void hide() {
        assets.stopMusic();
    }

    @Override
    protected void handleInput(float delta) {
        // Al presionar la acción de confirmación (Enter / Espacio) reinicia la partida
        if (input.confirmJustPressed()) {
            navigator.showGame();
        } else if (input.backJustPressed()) {
            // Con ESC regresa al menú principal
            navigator.showMenu();
        }
    }

    @Override
    protected void update(float delta) {
        // Hook de actualización según el patrón Template Method
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        // 1. Dibujar el Banner central de Game Over
        Texture banner = assets.getTexture(GameAssets.BANNER_GAMEOVER_TEXTURE);
        if (banner != null) {
            float bannerWidth = 400f;
            float bannerHeight = 200f;
            float x = (camera.viewportWidth - bannerWidth) / 2f;
            float y = (camera.viewportHeight - bannerHeight) / 2f + 60f;
            assets.batch().draw(banner, x, y, bannerWidth, bannerHeight);
        } else {
            assets.font().draw(assets.batch(), "GAME OVER", 540f, 480f);
        }

        // 2. Métricas de la partida y guía de navegación
        assets.font().draw(assets.batch(), "Puntaje final: " + finalScore, 520f, 330f);
        assets.font().draw(assets.batch(), "Nivel alcanzado: " + levelReached, 520f, 290f);
        assets.font().draw(assets.batch(), "Presiona ENTER para reintentar", 480f, 230f);
        assets.font().draw(assets.batch(), "Presiona ESC para salir al menu", 485f, 190f);

        assets.batch().end();
    }
}
