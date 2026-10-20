package puppy.code.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.world.GamePhase;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldSnapshot;

/**
 * Pantalla principal de juego.
 * Actualiza la simulación, gestiona el efecto visual de estela (AfterImage) del Dash,
 * reproduce la banda sonora y renderiza el HUD y las entidades con sprites reales.
 */
public final class GameScreen extends AbstractGameScreen {

    private static final float BLINK_STEPS_PER_SECOND = 10f;

    private final ScreenNavigator navigator;
    private final GameWorld world;
    private final PlayerInput playerInput;
    private final NavigationInput navigationInput;
    private final List<AfterImage> afterImages = new ArrayList<>();

    private WorldSnapshot snapshot;
    private float elapsedSeconds;
    private float dashEffectTimer;
    private float dashSpawnCooldown;
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
        assets.playMusic(GameAssets.BACKGROUND_MUSIC, true, 0.45f);
    }

    @Override
    public void hide() {
        assets.stopMusic();
        afterImages.clear();
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

        // 1. Transición real a GameOverScreen al perder todas las vidas
        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            navigator.showGameOver();
            return;
        }

        elapsedSeconds += delta;

        // 2. Detección de Dash e instanciación de siluetas fantasma (AfterImage)
        if (playerInput.dashJustPressed()) {
            dashEffectTimer = 0.20f;
            dashSpawnCooldown = 0f;
        }

        if (dashEffectTimer > 0f) {
            dashEffectTimer -= delta;
            dashSpawnCooldown -= delta;
            if (dashSpawnCooldown <= 0f) {
                TextureRegion[][] playerFrames = assets.getPlayerFrames();
                TextureRegion shipRegion = (playerFrames != null) ? playerFrames[0][1] : null;
                if (shipRegion != null) {
                    WorldSnapshot.EntityView ship = snapshot.getShip();
                    afterImages.add(new AfterImage(
                            ship.getX(),
                            ship.getY(),
                            ship.getWidth(),
                            ship.getHeight(),
                            shipRegion,
                            0.22f
                    ));
                }
                dashSpawnCooldown = 0.04f;
            }
        }

        // 3. Actualización y remoción de estelas que hayan expirado
        Iterator<AfterImage> it = afterImages.iterator();
        while (it.hasNext()) {
            if (it.next().update(delta)) {
                it.remove();
            }
        }

        world.update(delta, playerInput);
        snapshot = world.snapshot();
    }

    @Override
    protected void draw(float delta) {
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        // 1. Renderizado de estelas de Dash (Cian traslúcido)
        for (AfterImage ai : afterImages) {
            assets.batch().setColor(0.3f, 0.85f, 1f, ai.getAlpha());
            assets.batch().draw(ai.getRegion(), ai.getX(), ai.getY(), ai.getWidth(), ai.getHeight());
        }
        assets.batch().setColor(Color.WHITE);

        // 2. Nave del Jugador (Sprite real con parpadeo en invulnerabilidad)
        WorldSnapshot.EntityView ship = snapshot.getShip();
        TextureRegion[][] playerFrames = assets.getPlayerFrames();
        TextureRegion shipRegion = (playerFrames != null) ? playerFrames[0][1] : null;

        boolean blinkVisible = !snapshot.isShipInvulnerable()
                || (((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0);

        if (shipRegion != null && blinkVisible) {
            assets.batch().draw(shipRegion, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());
        }

        // 3. Aliens (Sprite real invertido verticalmente hacia el Sur)
        TextureRegion[][] alienFrames = assets.getAlienFrames();
        TextureRegion alienRegion = (alienFrames != null) ? alienFrames[0][1] : null;

        if (alienRegion != null) {
            for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
                assets.batch().draw(alienRegion, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
            }
        }

        // 4. HUD: Vidas renderizadas con player_life.png
        Texture lifeTex = assets.getTexture(GameAssets.PLAYER_LIFE_TEXTURE);
        if (lifeTex != null) {
            for (int i = 0; i < snapshot.getLives(); i++) {
                assets.batch().draw(lifeTex, 24f + (i * 28f), 750f, 24f, 24f);
            }
        } else {
            assets.font().draw(assets.batch(), "Vidas: " + snapshot.getLives(), 24f, 770f);
        }

        // 5. Guía de controles acorde al mapeo del proyecto
        assets.font().draw(assets.batch(), "W A S D: mover   |   I: fuego   |   O: dash   |   ESC: menu", 24f, 738f);

        assets.batch().end();
    }
}