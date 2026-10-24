package puppy.code.screens;

import java.util.List;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;
import puppy.code.systems.ScoreRepository;

/** Presenta el menú, el Top 3 persistente y delega las transiciones a la aplicación. */
public final class MenuScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final NavigationInput navigationInput;
    private final ScoreRepository scoreRepository;
    private boolean startRequested;

    public MenuScreen(
            GameAssets assets,
            ScreenNavigator navigator,
            NavigationInput navigationInput,
            ScoreRepository scoreRepository) {
        super(assets);
        if (navigator == null || navigationInput == null || scoreRepository == null) {
            throw new IllegalArgumentException("Los servicios de MenuScreen no pueden ser null.");
        }
        this.navigator = navigator;
        this.navigationInput = navigationInput;
        this.scoreRepository = scoreRepository;
    }

    @Override
    protected void handleInput(float delta) {
        startRequested = navigationInput.confirmJustPressed();
    }

    @Override
    protected void update(float delta) {
        if (startRequested) {
            navigator.showGame();
        }
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();
        assets.font().draw(assets.batch(), "SPACE NAVIGATION", 470f, 455f);
        assets.font().draw(assets.batch(), "Presiona ENTER para comenzar", 455f, 390f);
        assets.font().draw(assets.batch(), "WASD: mover   I: disparar   O: dash   ESC: pausa/menu", 360f, 345f);
        drawTopScores();
        assets.batch().end();
    }

    private void drawTopScores() {
        assets.font().draw(assets.batch(), "TOP 3 LOCAL", 530f, 275f);
        List<ScoreRepository.HighScore> scores = scoreRepository.getTopScores();
        for (int i = 0; i < scores.size(); i++) {
            ScoreRepository.HighScore score = scores.get(i);
            assets.font().draw(
                    assets.batch(),
                    (i + 1) + ". " + score.getName() + "   " + score.getScore(),
                    520f,
                    245f - i * 28f);
        }
    }
}