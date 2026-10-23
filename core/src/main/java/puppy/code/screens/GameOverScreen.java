package puppy.code.screens;

import com.badlogic.gdx.graphics.Texture;
import java.util.List;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.InitialsInput;
import puppy.code.input.NavigationInput;
import puppy.code.systems.ScoreRepository;

/** Presenta la derrota y permite guardar iniciales si el resultado entra al Top 3. */
public final class GameOverScreen extends AbstractGameScreen {

    private final ScreenNavigator navigator;
    private final NavigationInput input;
    private final InitialsInput initialsInput;
    private final ScoreRepository scoreRepository;
    private final int finalScore;
    private final int levelReached;
    private final boolean eligibleForTop3;
    private final StringBuilder initials = new StringBuilder(3);
    private boolean scoreSaved;

    public GameOverScreen(
            GameAssets assets,
            ScreenNavigator navigator,
            NavigationInput input,
            InitialsInput initialsInput,
            ScoreRepository scoreRepository,
            int finalScore,
            int levelReached) {
        super(assets);
        if (navigator == null || input == null || initialsInput == null || scoreRepository == null) {
            throw new IllegalArgumentException("Los servicios de GameOverScreen no pueden ser null.");
        }
        this.navigator = navigator;
        this.input = input;
        this.initialsInput = initialsInput;
        this.scoreRepository = scoreRepository;
        this.finalScore = finalScore;
        this.levelReached = levelReached;
        this.eligibleForTop3 = scoreRepository.isEligibleForTop3(finalScore);
    }

    @Override
    public void show() {
        assets.stopMusic();
        assets.playMusic(GameAssets.GAMEOVER_MUSIC, false, 0.6f);
    }

    @Override
    public void hide() {
        assets.stopMusic();
    }

    @Override
    protected void handleInput(float delta) {
        if (input.backJustPressed()) {
            navigator.showMenu();
            return;
        }

        if (eligibleForTop3 && !scoreSaved) {
            captureInitials();
            if (input.confirmJustPressed() && initials.length() == 3) {
                scoreSaved = scoreRepository.addScore(initials.toString(), finalScore);
            }
            return;
        }

        if (input.confirmJustPressed()) {
            navigator.showGame();
        }
    }

    private void captureInitials() {
        if (initialsInput.deleteJustPressed() && initials.length() > 0) {
            initials.deleteCharAt(initials.length() - 1);
        }
        char letter = initialsInput.letterJustPressed();
        if (letter >= 'A' && letter <= 'Z' && initials.length() < 3) {
            initials.append(letter);
        }
    }

    @Override
    protected void update(float delta) {
        // La pantalla final no actualiza el mundo de la partida.
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

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

        assets.font().draw(assets.batch(), "Puntaje final: " + finalScore, 520f, 330f);
        assets.font().draw(assets.batch(), "Nivel alcanzado: " + levelReached, 520f, 300f);
        drawScoreEntry();
        drawTopScores();
        if (eligibleForTop3 && !scoreSaved) {
            assets.font().draw(assets.batch(), "ESC: volver al menu", 505f, 58f);
        } else {
            assets.font().draw(assets.batch(), "ENTER: jugar de nuevo   ESC: menu", 445f, 58f);
        }

        assets.batch().end();
    }

    private void drawScoreEntry() {
        if (eligibleForTop3 && !scoreSaved) {
            assets.font().draw(assets.batch(), "Nuevo record: ingresa 3 letras A-Z", 440f, 265f);
            assets.font().draw(assets.batch(), initialsLine(), 570f, 235f);
            assets.font().draw(assets.batch(), "Letras: escribir   Retroceso: borrar   ENTER: guardar", 390f, 205f);
        } else if (scoreSaved) {
            assets.font().draw(assets.batch(), "Puntaje guardado como " + initials, 475f, 265f);
        } else {
            assets.font().draw(assets.batch(), "Este puntaje no entra al Top 3", 470f, 265f);
        }
    }

    private String initialsLine() {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            line.append(i < initials.length() ? initials.charAt(i) : '_');
            if (i < 2) {
                line.append(' ');
            }
        }
        return line.toString();
    }

    private void drawTopScores() {
        assets.font().draw(assets.batch(), "TOP 3 LOCAL", 535f, 165f);
        List<ScoreRepository.HighScore> scores = scoreRepository.getTopScores();
        for (int i = 0; i < scores.size(); i++) {
            ScoreRepository.HighScore score = scores.get(i);
            assets.font().draw(
                    assets.batch(),
                    (i + 1) + ". " + score.getName() + "   " + score.getScore(),
                    535f,
                    140f - i * 23f);
        }
    }
}
