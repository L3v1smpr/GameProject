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
 * reproduce la banda sonora, renderiza el HUD y anima los estados de inclinación/disparo de la nave.
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

        // Transición a GameOverScreen al agotarse las vidas
        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            navigator.showGameOver();
            return;
        }

        elapsedSeconds += delta;

        // Detección de Dash e instanciación de siluetas AfterImage
        if (playerInput.dashJustPressed()) {
            dashEffectTimer = 0.20f;
            dashSpawnCooldown = 0f;
        }

        if (dashEffectTimer > 0f) {
            dashEffectTimer -= delta;
            dashSpawnCooldown -= delta;
            if (dashSpawnCooldown <= 0f) {
                TextureRegion currentShipRegion = resolveShipRegion(assets.getPlayerFrames());
                if (currentShipRegion != null) {
                    WorldSnapshot.EntityView ship = snapshot.getShip();
                    afterImages.add(new AfterImage(
                            ship.getX(),
                            ship.getY(),
                            ship.getWidth(),
                            ship.getHeight(),
                            currentShipRegion,
                            0.22f
                    ));
                }
                dashSpawnCooldown = 0.04f;
            }
        }

        // Limpieza de estelas expiradas
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

        // 1. Renderizado de estelas de Dash (Cian traslúcido con la inclinación correspondiente)
        for (AfterImage ai : afterImages) {
            assets.batch().setColor(0.3f, 0.85f, 1f, ai.getAlpha());
            assets.batch().draw(ai.getRegion(), ai.getX(), ai.getY(), ai.getWidth(), ai.getHeight());
        }
        assets.batch().setColor(Color.WHITE);

        // 2. Nave del Jugador (Sprite reactivo a inclinación y disparo, con parpadeo de invulnerabilidad)
        WorldSnapshot.EntityView ship = snapshot.getShip();
        TextureRegion shipRegion = resolveShipRegion(assets.getPlayerFrames());

        boolean blinkVisible = !snapshot.isShipInvulnerable()
                || (((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0);

        if (shipRegion != null && blinkVisible) {
            assets.batch().draw(shipRegion, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());
        }

        // 3. Aliens (Sprite real invertido hacia el Sur)
        TextureRegion[][] alienFrames = assets.getAlienFrames();
        TextureRegion alienRegion = (alienFrames != null && alienFrames.length > 0 && alienFrames[0].length > 1)
                ? alienFrames[0][1]
                : null;

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

        // 5. Guía de navegación y controles
        assets.font().draw(assets.batch(), "W A S D: mover   |   I: fuego   |   O: dash   |   ESC: menu", 24f, 738f);

        assets.batch().end();
    }

    /**
     * Resuelve el cuadro adecuado del sprite sheet de la nave considerando:
     * - Inclinación lateral (Columna 0 = izquierda, 1 = centro, 2 = derecha).
     * - Acción de disparo (Fila 1 si está disponible en la textura).
     */
    private TextureRegion resolveShipRegion(TextureRegion[][] frames) {
        if (frames == null || frames.length == 0 || frames[0].length == 0) {
            return null;
        }

        // Determinar fila: fila 1 para disparo si la textura cuenta con más de una fila
        int targetRow = 0;
        if (playerInput.firePressed() && frames.length > 1) {
            targetRow = 1;
        }

        // Determinar columna según la dirección en el eje X
        float dirX = playerInput.movementDirection().x;
        int targetCol = 1; // Centro / reposo

        if (dirX < -0.1f) {
            targetCol = 0; // Giro a la izquierda
        } else if (dirX > 0.1f) {
            targetCol = 2; // Giro a la derecha
        }

        // Validación defensiva para evitar índices fuera de rango
        int safeRow = Math.min(targetRow, frames.length - 1);
        int safeCol = Math.max(0, Math.min(targetCol, frames[safeRow].length - 1));

        return frames[safeRow][safeCol];
    }
}