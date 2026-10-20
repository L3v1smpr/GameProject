package puppy.code.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.world.GamePhase;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldSnapshot;

/**
 * Pantalla principal de juego.
 * Actualiza la simulación y dibuja el snapshot de solo lectura utilizando
 * sprites reales, ciclo de vida de audio y elementos gráficos del HUD.
 */
public final class GameScreen extends AbstractGameScreen {

    private static final float BLINK_STEPS_PER_SECOND = 10f;

    private final ScreenNavigator navigator;
    private final GameWorld world;
    private final PlayerInput playerInput;
    private final NavigationInput navigationInput;
    private WorldSnapshot snapshot;
    private float elapsedSeconds;
    private boolean returnToMenuRequested;

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
        this.snapshot = world.snapshot();
    }

    @Override
    public void show() {
        // Inicia la música de combate centralizada en bucle
        assets.playMusic(GameAssets.BACKGROUND_MUSIC, true, 0.45f);
    }

    @Override
    public void hide() {
        // Corta el audio de inmediato al salir de la pantalla
        assets.stopMusic();
    }

    @Override
    protected void handleInput(float delta) {
        returnToMenuRequested = navigationInput.backJustPressed();
    }

    @Override
    protected void update(float delta) {
        if (returnToMenuRequested) {
            navigator.showMainMenu();
            return;
        }
        elapsedSeconds += delta;
        world.update(delta, playerInput);
        snapshot = world.snapshot();
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        // 1. Dibujo de la Nave del Jugador (Sprite real con parpadeo en invulnerabilidad)
        WorldSnapshot.EntityView ship = snapshot.getShip();
        TextureRegion[][] playerFrames = assets.getPlayerFrames();
        TextureRegion shipRegion = (playerFrames != null) ? playerFrames[0][1] : null;

        boolean blinkVisible = !snapshot.isShipInvulnerable()
                || (((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0);

        if (shipRegion != null && blinkVisible) {
            assets.batch().draw(shipRegion, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());
        }

        // 2. Dibujo de Aliens (Sprite real invertido verticalmente mirando al jugador)
        TextureRegion[][] alienFrames = assets.getAlienFrames();
        TextureRegion alienRegion = (alienFrames != null) ? alienFrames[0][1] : null;

        if (alienRegion != null) {
            for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
                assets.batch().draw(alienRegion, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
            }
        }

        // 3. HUD: Vidas renderizadas con iconos gráficos
        Texture lifeTex = assets.getTexture(GameAssets.PLAYER_LIFE_TEXTURE);
        if (lifeTex != null) {
            for (int i = 0; i < snapshot.getLives(); i++) {
                assets.batch().draw(lifeTex, 24f + (i * 28f), 750f, 24f, 24f);
            }
        } else {
            assets.font().draw(assets.batch(), "Vidas: " + snapshot.getLives(), 24f, 770f);
        }

        assets.font().draw(assets.batch(), "W A S D: mover   |   ESC: volver al menu", 24f, 738f);

        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            assets.font().draw(assets.batch(), "GAME OVER", 540f, 420f);
        }

        assets.batch().end();
    }
}