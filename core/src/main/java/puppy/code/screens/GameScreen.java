package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import puppy.code.app.ScreenNavigator;
import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;
import puppy.code.input.NavigationInput;
import puppy.code.input.PlayerInput;
import puppy.code.world.GamePhase;
import puppy.code.world.GameWorld;
import puppy.code.world.WorldAudioListener;
import puppy.code.world.WorldSnapshot;

/** Presenta la partida y delega en GameWorld las reglas de combate y progresión. */
public final class GameScreen extends AbstractGameScreen implements WorldAudioListener {

    private static final float BLINK_STEPS_PER_SECOND = 10f;
    private static final float DASH_EFFECT_SECONDS = 0.20f;
    private static final float DASH_IMAGE_INTERVAL_SECONDS = 0.04f;
    private static final float TRANSITION_DURATION_SECONDS = 1.8f;
    private static final float BASE_MUSIC_VOLUME = 0.45f;
    private static final float SECTOR_COMPLETED_COOLDOWN_SECONDS = 3.0f;

    private enum TransitionType {
        NONE,
        GAME_OVER_FADE,
        VICTORY_FADE
    }

    private enum PauseMenuState {
        MAIN,
        OPTIONS,
        DEBUG
    }

    private enum KonamiKey {
        UP, DOWN, LEFT, RIGHT, FIRE, DASH
    }

    private static final KonamiKey[] KONAMI_SEQUENCE = {
        KonamiKey.UP, KonamiKey.UP,
        KonamiKey.DOWN, KonamiKey.DOWN,
        KonamiKey.LEFT, KonamiKey.RIGHT,
        KonamiKey.LEFT, KonamiKey.RIGHT,
        KonamiKey.FIRE,
        KonamiKey.DASH
    };

    private final ScreenNavigator navigator;
    private final GameWorld world;
    private final PlayerInput playerInput;
    private final NavigationInput navigationInput;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private final List<AfterImage> afterImages = new ArrayList<>();
    private final List<FireRing> fireRings = new ArrayList<>();
    private final List<PlayerRing> playerRings = new ArrayList<>();
    private final List<DebrisParticle> debrisParticles = new ArrayList<>();

    private WorldSnapshot snapshot;
    private float elapsedSeconds;
    private float dashEffectTimer;
    private float dashSpawnCooldown;

    // Estado del modal de pausa
    private boolean paused;
    private PauseMenuState pauseState = PauseMenuState.MAIN;
    private int pauseMenuIndex = 0;
    private int optionsMenuIndex = 0;
    private int debugMenuIndex = 0;

    // Easter Egg Konami
    private int konamiIndex = 0;
    private boolean debugUnlocked = false;

    // Control de sacudida de pantalla (Screen Shake)
    private float shakeTimer;
    private float shakeDuration;
    private float shakeIntensity;

    // Enfriamiento de 3s para apreciar la pantalla de sector asegurado
    private float sectorCompletedTimer;

    private TransitionType activeTransition = TransitionType.NONE;
    private float transitionTimer;
    private boolean transitionCompleted;

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
        this.world.setAudioListener(this);
        this.playerInput = playerInput;
        this.navigationInput = navigationInput;
        snapshot = world.snapshot();
    }

    @Override
    public void show() {
        activeTransition = TransitionType.NONE;
        transitionTimer = 0f;
        transitionCompleted = false;
        paused = false;
        pauseState = PauseMenuState.MAIN;
        pauseMenuIndex = 0;
        optionsMenuIndex = 0;
        debugMenuIndex = 0;
        konamiIndex = 0;
        shakeTimer = 0f;
        sectorCompletedTimer = 0f;
        playerRings.clear();
        fireRings.clear();
        debrisParticles.clear();
        afterImages.clear();
        resetCameraPosition();
        assets.playMusic(GameAssets.BACKGROUND_MUSIC, true, BASE_MUSIC_VOLUME);
    }

    @Override
    public void hide() {
        assets.stopMusic();
        paused = false;
        shakeTimer = 0f;
        sectorCompletedTimer = 0f;
        resetCameraPosition();
        afterImages.clear();
        fireRings.clear();
        playerRings.clear();
        debrisParticles.clear();
    }

    private void resetCameraPosition() {
        camera.position.set(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0f);
        camera.update();
    }

    private void triggerScreenShake(float duration, float intensity) {
        this.shakeDuration = duration;
        this.shakeTimer = duration;
        this.shakeIntensity = intensity;
    }

    private void updateScreenShake(float delta) {
        if (shakeTimer > 0f) {
            shakeTimer = Math.max(0f, shakeTimer - delta);
            float progress = shakeTimer / shakeDuration;
            float currentIntensity = shakeIntensity * progress;
            float offsetX = MathUtils.random(-currentIntensity, currentIntensity);
            float offsetY = MathUtils.random(-currentIntensity, currentIntensity);
            camera.position.set(
                    GameConfig.WORLD_WIDTH / 2f + offsetX,
                    GameConfig.WORLD_HEIGHT / 2f + offsetY,
                    0f);
        } else {
            camera.position.set(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0f);
        }
        camera.update();
    }

    @Override
    protected void handleInput(float delta) {
        if (activeTransition != TransitionType.NONE || snapshot.getPhase() != GamePhase.ACTIVE) {
            return;
        }

        if (paused) {
            handleKonamiCodeInput();

            if (navigationInput.backJustPressed()) {
                assets.playSound(GameAssets.MENU_SFX, 0.6f);
                if (pauseState == PauseMenuState.OPTIONS || pauseState == PauseMenuState.DEBUG) {
                    pauseState = PauseMenuState.MAIN;
                } else {
                    resumeGame();
                }
                return;
            }

            if (pauseState == PauseMenuState.MAIN) {
                int totalOptions = debugUnlocked ? 4 : 3;
                if (navigationInput.upJustPressed()) {
                    pauseMenuIndex = (pauseMenuIndex - 1 + totalOptions) % totalOptions;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                } else if (navigationInput.downJustPressed()) {
                    pauseMenuIndex = (pauseMenuIndex + 1) % totalOptions;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                }

                if (navigationInput.confirmJustPressed()) {
                    assets.playSound(GameAssets.MENU_SFX, 0.65f);
                    if (pauseMenuIndex == 0) {
                        resumeGame();
                    } else if (pauseMenuIndex == 1) {
                        pauseState = PauseMenuState.OPTIONS;
                        optionsMenuIndex = 0;
                    } else if (pauseMenuIndex == 2) {
                        paused = false;
                        navigator.showMenu();
                    } else if (pauseMenuIndex == 3 && debugUnlocked) {
                        pauseState = PauseMenuState.DEBUG;
                        debugMenuIndex = 0;
                    }
                }
            } else if (pauseState == PauseMenuState.OPTIONS) {
                if (navigationInput.upJustPressed()) {
                    optionsMenuIndex = (optionsMenuIndex - 1 + 3) % 3;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                } else if (navigationInput.downJustPressed()) {
                    optionsMenuIndex = (optionsMenuIndex + 1) % 3;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                }

                if (optionsMenuIndex == 0) {
                    if (navigationInput.leftJustPressed()) {
                        assets.setMusicVolume(assets.getMusicVolume() - 0.10f);
                        assets.playSound(GameAssets.MENU_SFX, 0.5f);
                    } else if (navigationInput.rightJustPressed()) {
                        assets.setMusicVolume(assets.getMusicVolume() + 0.10f);
                        assets.playSound(GameAssets.MENU_SFX, 0.5f);
                    }
                } else if (optionsMenuIndex == 1) {
                    if (navigationInput.leftJustPressed()) {
                        assets.setSoundVolume(assets.getSoundVolume() - 0.10f);
                        assets.playSound(GameAssets.PLAYER_SHOOT_SOUND, 0.45f);
                    } else if (navigationInput.rightJustPressed()) {
                        assets.setSoundVolume(assets.getSoundVolume() + 0.10f);
                        assets.playSound(GameAssets.PLAYER_SHOOT_SOUND, 0.45f);
                    }
                } else if (optionsMenuIndex == 2) {
                    if (navigationInput.confirmJustPressed()) {
                        assets.playSound(GameAssets.MENU_SFX, 0.6f);
                        pauseState = PauseMenuState.MAIN;
                    }
                }
            } else if (pauseState == PauseMenuState.DEBUG) {
                if (navigationInput.upJustPressed()) {
                    debugMenuIndex = (debugMenuIndex - 1 + 2) % 2;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                } else if (navigationInput.downJustPressed()) {
                    debugMenuIndex = (debugMenuIndex + 1) % 2;
                    assets.playSound(GameAssets.MENU_SFX, 0.5f);
                }

                if (debugMenuIndex == 0) {
                    if (navigationInput.leftJustPressed() || navigationInput.rightJustPressed() || navigationInput.confirmJustPressed()) {
                        boolean current = world.isGodMode();
                        world.setGodMode(!current);
                        assets.playSound(GameAssets.MENU_SFX, 0.65f);
                    }
                } else if (debugMenuIndex == 1) {
                    if (navigationInput.confirmJustPressed()) {
                        assets.playSound(GameAssets.MENU_SFX, 0.6f);
                        pauseState = PauseMenuState.MAIN;
                    }
                }
            }
            return;
        }

        if (navigationInput.backJustPressed()) {
            assets.playSound(GameAssets.MENU_SFX, 0.6f);
            pauseGame();
        }
    }

    private void handleKonamiCodeInput() {
        if (debugUnlocked) {
            return;
        }

        KonamiKey inputKey = null;
        if (navigationInput.upJustPressed()) inputKey = KonamiKey.UP;
        else if (navigationInput.downJustPressed()) inputKey = KonamiKey.DOWN;
        else if (navigationInput.leftJustPressed()) inputKey = KonamiKey.LEFT;
        else if (navigationInput.rightJustPressed()) inputKey = KonamiKey.RIGHT;
        else if (playerInput.fireJustPressed()) inputKey = KonamiKey.FIRE;
        else if (playerInput.dashJustPressed()) inputKey = KonamiKey.DASH;

        if (inputKey != null) {
            if (inputKey == KONAMI_SEQUENCE[konamiIndex]) {
                konamiIndex++;
                if (konamiIndex >= KONAMI_SEQUENCE.length) {
                    debugUnlocked = true;
                    konamiIndex = 0;
                    assets.playSound(GameAssets.EASTER_EGG_SOUND, 0.85f);
                }
            } else {
                konamiIndex = (inputKey == KONAMI_SEQUENCE[0]) ? 1 : 0;
            }
        }
    }

    private void pauseGame() {
        paused = true;
        pauseState = PauseMenuState.MAIN;
        pauseMenuIndex = 0;
    }

    private void resumeGame() {
        paused = false;
    }

    @Override
    protected void update(float delta) {
        if (paused) {
            return;
        }

        updateScreenShake(delta);
        updateVisualEffects(delta);

        if (activeTransition != TransitionType.NONE) {
            transitionTimer = Math.max(0f, transitionTimer - delta);
            float linearProgress = Math.min(1f, 1f - (transitionTimer / TRANSITION_DURATION_SECONDS));
            float soundProgress = Interpolation.pow2In.apply(linearProgress);

            Music bgm = assets.getMusic(GameAssets.BACKGROUND_MUSIC);
            if (bgm != null && bgm.isPlaying()) {
                bgm.setVolume(Math.max(0f, BASE_MUSIC_VOLUME * (1f - soundProgress) * assets.getMusicVolume()));
            }

            if (transitionTimer <= 0f && !transitionCompleted) {
                transitionCompleted = true;
                if (activeTransition == TransitionType.GAME_OVER_FADE) {
                    navigator.showGameOver(snapshot.getScore(), snapshot.getLevel());
                } else if (activeTransition == TransitionType.VICTORY_FADE) {
                    navigator.showVictory(snapshot.getScore());
                }
            }
            return;
        }

        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            activeTransition = TransitionType.GAME_OVER_FADE;
            transitionTimer = TRANSITION_DURATION_SECONDS;
            return;
        }
        if (snapshot.getPhase() == GamePhase.VICTORY) {
            activeTransition = TransitionType.VICTORY_FADE;
            transitionTimer = TRANSITION_DURATION_SECONDS;
            return;
        }
        if (snapshot.getPhase() == GamePhase.LEVEL_COMPLETED) {
            elapsedSeconds += delta;
            sectorCompletedTimer += delta;
            if (sectorCompletedTimer >= SECTOR_COMPLETED_COOLDOWN_SECONDS && navigationInput.anyActionJustPressed()) {
                world.advanceToNextLevel();
                sectorCompletedTimer = 0f;
                snapshot = world.snapshot();
                assets.playMusic(GameAssets.BACKGROUND_MUSIC, true, BASE_MUSIC_VOLUME);
            }
            return;
        }

        elapsedSeconds += delta;
        if (playerInput.dashJustPressed() && snapshot.isShipDashReady()) {
            dashEffectTimer = DASH_EFFECT_SECONDS;
            dashSpawnCooldown = 0f;
        }
        updateDashEffects(delta);

        world.update(delta, playerInput);
        snapshot = world.snapshot();
    }

    private void updateDashEffects(float delta) {
        if (dashEffectTimer > 0f) {
            dashEffectTimer = Math.max(0f, dashEffectTimer - delta);
            dashSpawnCooldown -= delta;
            if (dashSpawnCooldown <= 0f) {
                TextureRegion region = resolveShipRegion(assets.getPlayerFrames());
                if (region != null) {
                    WorldSnapshot.EntityView ship = snapshot.getShip();
                    afterImages.add(new AfterImage(
                            ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(), region, 0.22f));
                }
                dashSpawnCooldown = DASH_IMAGE_INTERVAL_SECONDS;
            }
        }

        Iterator<AfterImage> iterator = afterImages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().update(delta)) {
                iterator.remove();
            }
        }
    }

    private void updateVisualEffects(float delta) {
        Iterator<FireRing> ringIterator = fireRings.iterator();
        while (ringIterator.hasNext()) {
            if (ringIterator.next().update(delta)) {
                ringIterator.remove();
            }
        }

        Iterator<PlayerRing> playerRingIterator = playerRings.iterator();
        while (playerRingIterator.hasNext()) {
            if (playerRingIterator.next().update(delta)) {
                playerRingIterator.remove();
            }
        }

        Iterator<DebrisParticle> particleIterator = debrisParticles.iterator();
        while (particleIterator.hasNext()) {
            if (particleIterator.next().update(delta)) {
                particleIterator.remove();
            }
        }
    }

    @Override
    protected void draw(float delta) {
        if (transitionCompleted) {
            drawFinalCover();
            return;
        }

        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        for (AfterImage image : afterImages) {
            assets.batch().setColor(0.3f, 0.85f, 1f, image.getAlpha());
            assets.batch().draw(image.getRegion(), image.getX(), image.getY(), image.getWidth(), image.getHeight());
        }
        assets.batch().setColor(Color.WHITE);

        drawShip();
        drawAliens();
        drawProjectiles();
        drawMeteorites();
        drawHud();

        assets.batch().end();

        drawDashBar();
        drawVisualEffects();
        drawFallbackEntities();
        drawTransitionOverlay();

        if (paused) {
            drawPauseOverlay();
        }

        if (snapshot.getPhase() == GamePhase.LEVEL_COMPLETED) {
            drawLevelCompletedOverlay();
        }
    }

    private void drawDashBar() {
        float barX = 180f;
        float barY = 755f;
        float barWidth = 90f;
        float barHeight = 14f;
        float progress = snapshot.getDashCooldownProgress();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        assets.shapes().setProjectionMatrix(camera.combined);

        assets.shapes().begin(ShapeType.Filled);
        assets.shapes().setColor(0.12f, 0.15f, 0.20f, 0.85f);
        assets.shapes().rect(barX, barY, barWidth, barHeight);

        if (progress > 0f) {
            if (snapshot.isShipDashReady()) {
                assets.shapes().setColor(0.20f, 0.95f, 0.90f, 1.0f);
            } else {
                assets.shapes().setColor(0.15f, 0.55f, 0.80f, 0.90f);
            }
            assets.shapes().rect(barX, barY, barWidth * progress, barHeight);
        }
        assets.shapes().end();

        assets.shapes().begin(ShapeType.Line);
        if (snapshot.isShipDashReady()) {
            assets.shapes().setColor(0.40f, 1.0f, 0.95f, 1.0f);
        } else {
            assets.shapes().setColor(0.60f, 0.65f, 0.70f, 0.75f);
        }
        assets.shapes().rect(barX, barY, barWidth, barHeight);
        assets.shapes().end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawLevelCompletedOverlay() {
        float modalWidth = 560f;
        float modalHeight = 250f;
        float modalX = (GameConfig.WORLD_WIDTH - modalWidth) / 2f;
        float modalY = (GameConfig.WORLD_HEIGHT - modalHeight) / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        assets.shapes().setProjectionMatrix(camera.combined);

        assets.shapes().begin(ShapeType.Filled);
        assets.shapes().setColor(0f, 0f, 0f, 0.65f);
        assets.shapes().rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        assets.shapes().setColor(0.06f, 0.08f, 0.13f, 0.92f);
        assets.shapes().rect(modalX, modalY, modalWidth, modalHeight);
        assets.shapes().end();

        assets.shapes().begin(ShapeType.Line);
        assets.shapes().setColor(0.20f, 0.85f, 0.95f, 0.90f);
        assets.shapes().rect(modalX, modalY, modalWidth, modalHeight);
        assets.shapes().line(modalX + 30f, modalY + modalHeight - 55f, modalX + modalWidth - 30f, modalY + modalHeight - 55f);
        assets.shapes().end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        assets.font().setColor(0.30f, 1.0f, 0.90f, 1.0f);
        assets.font().draw(
                assets.batch(),
                "SECTOR " + snapshot.getLevel() + " ASEGURADO",
                modalX + 175f,
                modalY + modalHeight - 25f);

        assets.font().setColor(Color.WHITE);
        assets.font().draw(
                assets.batch(),
                "AMENAZAS ELIMINADAS: " + snapshot.getQuotaForLevel() + " NAVES",
                modalX + 130f,
                modalY + modalHeight - 85f);

        assets.font().draw(
                assets.batch(),
                "PUNTAJE ACUMULADO:   " + snapshot.getScore() + " PTS",
                modalX + 130f,
                modalY + modalHeight - 125f);

        if (sectorCompletedTimer < SECTOR_COMPLETED_COOLDOWN_SECONDS) {
            int remaining = (int) Math.ceil(SECTOR_COMPLETED_COOLDOWN_SECONDS - sectorCompletedTimer);
            assets.font().setColor(0.70f, 0.75f, 0.85f, 1.0f);
            assets.font().draw(
                    assets.batch(),
                    "SINCRONIZANDO SALTO HIPERESPACIAL... " + remaining + "S",
                    modalX + 85f,
                    modalY + 45f);
        } else {
            float blinkAlpha = 0.55f + 0.45f * MathUtils.sin(elapsedSeconds * 4f);
            assets.font().setColor(1.0f, 0.85f, 0.35f, blinkAlpha);
            assets.font().draw(
                    assets.batch(),
                    "PRESIONA CUALQUIER TECLA PARA INICIAR EL SALTO",
                    modalX + 65f,
                    modalY + 45f);
        }

        assets.font().setColor(Color.WHITE);
        assets.batch().end();
    }

    private void drawCenteredText(String text, float boxX, float boxY, float boxWidth, float boxHeight, Color color) {
        glyphLayout.setText(assets.font(), text);
        float textX = boxX + (boxWidth - glyphLayout.width) / 2f;
        float textY = boxY + (boxHeight + glyphLayout.height) / 2f;
        assets.font().setColor(color);
        assets.font().draw(assets.batch(), text, textX, textY);
    }

    private void drawPauseOverlay() {
        float modalWidth = 480f;
        float modalHeight;
        if (pauseState == PauseMenuState.MAIN) {
            modalHeight = debugUnlocked ? 285f : 240f;
        } else if (pauseState == PauseMenuState.OPTIONS) {
            modalHeight = 250f;
        } else {
            modalHeight = 220f;
        }

        float modalX = (GameConfig.WORLD_WIDTH - modalWidth) / 2f;
        float modalY = (GameConfig.WORLD_HEIGHT - modalHeight) / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        assets.shapes().setProjectionMatrix(camera.combined);

        // 1. RELLENOS Y FONDOS
        assets.shapes().begin(ShapeType.Filled);

        // Velo oscuro general
        assets.shapes().setColor(0f, 0f, 0f, 0.70f);
        assets.shapes().rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        // Ventana central
        assets.shapes().setColor(0.08f, 0.10f, 0.14f, 0.95f);
        assets.shapes().rect(modalX, modalY, modalWidth, modalHeight);

        // Resaltado de selección
        assets.shapes().setColor(0.18f, 0.45f, 0.65f, 0.50f);
        if (pauseState == PauseMenuState.MAIN) {
            float topOffset = debugUnlocked ? 22f : 16f;
            float gap = debugUnlocked ? 10f : 12f;
            float itemH = 34f;
            float topBoxY = modalY + modalHeight - 44f - topOffset - itemH;
            float selectY = topBoxY - pauseMenuIndex * (itemH + gap);
            assets.shapes().rect(modalX + 35f, selectY, modalWidth - 70f, itemH);
        } else if (pauseState == PauseMenuState.OPTIONS) {
            if (optionsMenuIndex == 0) {
                assets.shapes().rect(modalX + 30f, modalY + 148f, modalWidth - 60f, 36f);
            } else if (optionsMenuIndex == 1) {
                assets.shapes().rect(modalX + 30f, modalY + 100f, modalWidth - 60f, 36f);
            } else if (optionsMenuIndex == 2) {
                assets.shapes().rect(modalX + 35f, modalY + 50f, modalWidth - 70f, 32f);
            }
        } else if (pauseState == PauseMenuState.DEBUG) {
            if (debugMenuIndex == 0) {
                assets.shapes().rect(modalX + 30f, modalY + 110f, modalWidth - 60f, 38f);
            } else if (debugMenuIndex == 1) {
                assets.shapes().rect(modalX + 35f, modalY + 55f, modalWidth - 70f, 32f);
            }
        }

        // Barras de volumen en opciones
        if (pauseState == PauseMenuState.OPTIONS) {
            float barW = 180f;
            float barH = 14f;

            assets.shapes().setColor(0.15f, 0.20f, 0.28f, 1f);
            assets.shapes().rect(modalX + 155f, modalY + 159f, barW, barH);
            assets.shapes().setColor(0.20f, 0.90f, 0.85f, 1f);
            assets.shapes().rect(modalX + 155f, modalY + 159f, barW * assets.getMusicVolume(), barH);

            assets.shapes().setColor(0.15f, 0.20f, 0.28f, 1f);
            assets.shapes().rect(modalX + 155f, modalY + 111f, barW, barH);
            assets.shapes().setColor(0.20f, 0.90f, 0.85f, 1f);
            assets.shapes().rect(modalX + 155f, modalY + 111f, barW * assets.getSoundVolume(), barH);
        }
        assets.shapes().end();

        // 2. LÍNEAS Y BORDES
        assets.shapes().begin(ShapeType.Line);
        assets.shapes().setColor(0.25f, 0.85f, 0.95f, 0.85f);
        assets.shapes().rect(modalX, modalY, modalWidth, modalHeight);
        assets.shapes().line(modalX + 25f, modalY + modalHeight - 44f, modalX + modalWidth - 25f, modalY + modalHeight - 44f);
        assets.shapes().line(modalX + 25f, modalY + 36f, modalX + modalWidth - 25f, modalY + 36f);

        if (pauseState == PauseMenuState.OPTIONS) {
            float barW = 180f;
            float barH = 14f;
            assets.shapes().setColor(0.40f, 0.60f, 0.75f, 0.90f);
            assets.shapes().rect(modalX + 155f, modalY + 159f, barW, barH);
            assets.shapes().rect(modalX + 155f, modalY + 111f, barW, barH);
        }
        assets.shapes().end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 3. TEXTOS PERFECTAMENTE CENTRADOS
        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();

        if (pauseState == PauseMenuState.MAIN) {
            drawCenteredText("JUEGO PAUSADO", modalX, modalY + modalHeight - 44f, modalWidth, 44f, new Color(0.35f, 1.0f, 0.95f, 1f));

            float topOffset = debugUnlocked ? 22f : 16f;
            float gap = debugUnlocked ? 10f : 12f;
            float itemH = 34f;
            float topBoxY = modalY + modalHeight - 44f - topOffset - itemH;
            float itemW = modalWidth - 70f;
            float itemX = modalX + 35f;

            drawCenteredText("CONTINUAR", itemX, topBoxY, itemW, itemH, pauseMenuIndex == 0 ? Color.YELLOW : Color.WHITE);
            drawCenteredText("OPCIONES", itemX, topBoxY - 1 * (itemH + gap), itemW, itemH, pauseMenuIndex == 1 ? Color.YELLOW : Color.WHITE);
            drawCenteredText("SALIR AL MENU PRINCIPAL", itemX, topBoxY - 2 * (itemH + gap), itemW, itemH, pauseMenuIndex == 2 ? Color.YELLOW : Color.WHITE);

            if (debugUnlocked) {
                Color debugColor = pauseMenuIndex == 3 ? Color.YELLOW : new Color(0.9f, 0.35f, 1.0f, 1f);
                drawCenteredText("DEBUG", itemX, topBoxY - 3 * (itemH + gap), itemW, itemH, debugColor);
            }

            drawCenteredText("W/S: ELEGIR   ENTER: SELECCIONAR   ESC: REANUDAR", modalX, modalY, modalWidth, 36f, new Color(0.70f, 0.75f, 0.80f, 0.85f));
        } else if (pauseState == PauseMenuState.OPTIONS) {
            drawCenteredText("OPCIONES DE AUDIO", modalX, modalY + modalHeight - 44f, modalWidth, 44f, new Color(0.35f, 1.0f, 0.95f, 1f));

            int musicPct = Math.round(assets.getMusicVolume() * 100f);
            assets.font().setColor(optionsMenuIndex == 0 ? Color.YELLOW : Color.WHITE);
            assets.font().draw(assets.batch(), "MUSICA", modalX + 45f, modalY + 171f);
            assets.font().draw(assets.batch(), "< " + musicPct + "% >", modalX + 355f, modalY + 171f);

            int soundPct = Math.round(assets.getSoundVolume() * 100f);
            assets.font().setColor(optionsMenuIndex == 1 ? Color.YELLOW : Color.WHITE);
            assets.font().draw(assets.batch(), "EFECTOS", modalX + 45f, modalY + 123f);
            assets.font().draw(assets.batch(), "< " + soundPct + "% >", modalX + 355f, modalY + 123f);

            drawCenteredText("< VOLVER AL MENU DE PAUSA", modalX + 35f, modalY + 50f, modalWidth - 70f, 32f, optionsMenuIndex == 2 ? Color.YELLOW : Color.WHITE);
            drawCenteredText("A/D: AJUSTAR VOLUMEN   ENTER/ESC: VOLVER", modalX, modalY, modalWidth, 36f, new Color(0.70f, 0.75f, 0.80f, 0.85f));
        } else if (pauseState == PauseMenuState.DEBUG) {
            drawCenteredText("MENU DEBUG (SECRETO)", modalX, modalY + modalHeight - 44f, modalWidth, 44f, new Color(0.9f, 0.35f, 1.0f, 1f));

            boolean godMode = world.isGodMode();
            assets.font().setColor(debugMenuIndex == 0 ? Color.YELLOW : Color.WHITE);
            assets.font().draw(assets.batch(), "MODO DIOS (INVULNERABLE):", modalX + 45f, modalY + 134f);

            if (godMode) {
                assets.font().setColor(0.2f, 1.0f, 0.4f, 1.0f);
                assets.font().draw(assets.batch(), "< ACTIVADO >", modalX + 325f, modalY + 134f);
            } else {
                assets.font().setColor(0.85f, 0.3f, 0.3f, 1.0f);
                assets.font().draw(assets.batch(), "< DESACTIVADO >", modalX + 310f, modalY + 134f);
            }

            drawCenteredText("< VOLVER AL MENU DE PAUSA", modalX + 35f, modalY + 55f, modalWidth - 70f, 32f, debugMenuIndex == 1 ? Color.YELLOW : Color.WHITE);
            drawCenteredText("ENTER o A/D: ALTERNAR   ESC: VOLVER", modalX, modalY, modalWidth, 36f, new Color(0.70f, 0.75f, 0.80f, 0.85f));
        }

        assets.font().setColor(Color.WHITE);
        assets.batch().end();
    }

    private void drawVisualEffects() {
        if (fireRings.isEmpty() && debrisParticles.isEmpty() && playerRings.isEmpty()) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        assets.shapes().setProjectionMatrix(camera.combined);

        if (!debrisParticles.isEmpty()) {
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            assets.shapes().begin(ShapeType.Filled);
            for (DebrisParticle particle : debrisParticles) {
                float a = particle.getAlpha();
                float shade = particle.getShade();
                assets.shapes().setColor(shade, shade, shade, a);
                assets.shapes().rect(
                        particle.getX(), particle.getY(), particle.getSize(), particle.getSize());
            }
            assets.shapes().end();
        }

        if (!fireRings.isEmpty() || !playerRings.isEmpty()) {
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            assets.shapes().begin(ShapeType.Line);

            for (FireRing ring : fireRings) {
                float a = ring.getAlpha();
                assets.shapes().setColor(1.0f, 0.25f, 0.05f, a);
                assets.shapes().circle(ring.getX(), ring.getY(), ring.getRadius(), 32);

                if (ring.getRadius() > 4f) {
                    assets.shapes().setColor(1.0f, 0.85f, 0.40f, a * 0.9f);
                    assets.shapes().circle(ring.getX(), ring.getY(), ring.getRadius() - 3.5f, 32);
                }
            }

            for (PlayerRing ring : playerRings) {
                float a = ring.getAlpha();
                assets.shapes().setColor(63f / 255f, 0f, 1f, a);
                assets.shapes().circle(ring.getX(), ring.getY(), ring.getRadius(), 36);

                if (ring.getRadius() > 4f) {
                    assets.shapes().setColor(1.0f, 1.0f, 1.0f, a * 0.95f);
                    assets.shapes().circle(ring.getX(), ring.getY(), ring.getRadius() - 3.5f, 36);
                }
            }

            assets.shapes().end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawTransitionOverlay() {
        if (activeTransition == TransitionType.NONE) {
            return;
        }

        float linearProgress = Math.min(1f, 1f - (transitionTimer / TRANSITION_DURATION_SECONDS));
        float progress = Interpolation.fade.apply(linearProgress);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);

        if (activeTransition == TransitionType.GAME_OVER_FADE) {
            assets.shapes().setColor(
                    GameOverScreen.BG_RED_R,
                    GameOverScreen.BG_RED_G,
                    GameOverScreen.BG_RED_B,
                    progress);
        } else if (activeTransition == TransitionType.VICTORY_FADE) {
            assets.shapes().setColor(1f, 1f, 1f, progress);
        }

        assets.shapes().rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        assets.shapes().end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawFinalCover() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);

        if (activeTransition == TransitionType.GAME_OVER_FADE) {
            assets.shapes().setColor(
                    GameOverScreen.BG_RED_R,
                    GameOverScreen.BG_RED_G,
                    GameOverScreen.BG_RED_B,
                    1f);
        } else if (activeTransition == TransitionType.VICTORY_FADE) {
            assets.shapes().setColor(Color.WHITE);
        }

        assets.shapes().rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        assets.shapes().end();
    }

    private void drawShip() {
        if (snapshot.getLives() <= 0) {
            return;
        }

        WorldSnapshot.EntityView ship = snapshot.getShip();
        TextureRegion region = resolveShipRegion(assets.getPlayerFrames());
        boolean visible = !snapshot.isShipInvulnerable()
                || (((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0);
        if (region != null && visible) {
            assets.batch().draw(region, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight());
        }
    }

    private void drawAliens() {
        for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
            TextureRegion region;

            if (alien.isStatic()) {
                region = assets.getAlienSentryRegion(alien.isFiring());
            } else if (alien.isBomber()) {
                region = assets.getAlienBomberRegion(alien.isFiring());
            } else {
                region = assets.getAlienStaticRegion(alien.isFiring());
            }

            if (region != null) {
                if (alien.isHurt()) {
                    assets.batch().setColor(1f, 0.25f, 0.25f, 0.85f);
                }

                assets.batch().draw(
                        region,
                        alien.getX(),
                        alien.getY(),
                        alien.getWidth(),
                        alien.getHeight());

                if (alien.isHurt()) {
                    assets.batch().setColor(Color.WHITE);
                }
            }
        }
    }

    private void drawProjectiles() {
        Texture playerAmmo = assets.getTexture(GameAssets.PLAYER_AMMO_TEXTURE);
        TextureRegion alienAmmo = assets.getAlienAmmoRegion();

        for (WorldSnapshot.EntityView projectile : snapshot.getPlayerProjectiles()) {
            if (playerAmmo != null) {
                assets.batch().draw(
                        playerAmmo,
                        projectile.getX(),
                        projectile.getY(),
                        projectile.getWidth(),
                        projectile.getHeight());
            }
        }

        for (WorldSnapshot.EntityView projectile : snapshot.getAlienProjectiles()) {
            if (alienAmmo != null) {
                assets.batch().draw(
                        alienAmmo,
                        projectile.getX(),
                        projectile.getY(),
                        projectile.getWidth() / 2f,
                        projectile.getHeight() / 2f,
                        projectile.getWidth(),
                        projectile.getHeight(),
                        1f,
                        1f,
                        projectile.getRotation());
            }
        }
    }

    private void drawMeteorites() {
        for (WorldSnapshot.EntityView meteorite : snapshot.getMeteorites()) {
            Texture texture = assets.getTexture(meteorite.getAssetPath());
            if (texture != null) {
                if (meteorite.isHurt()) {
                    assets.batch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                    assets.batch().setColor(1f, 1f, 1f, 0.90f);
                }

                assets.batch().draw(
                        texture,
                        meteorite.getX(),
                        meteorite.getY(),
                        meteorite.getWidth(),
                        meteorite.getHeight());

                if (meteorite.isHurt()) {
                    assets.batch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    assets.batch().setColor(Color.WHITE);
                }
            }
        }
    }

    private void drawHud() {
        Texture lifeTexture = assets.getTexture(GameAssets.PLAYER_LIFE_TEXTURE);
        if (lifeTexture != null) {
            for (int index = 0; index < snapshot.getLives(); index++) {
                assets.batch().draw(lifeTexture, 24f + index * 28f, 750f, 24f, 24f);
            }
        } else {
            assets.font().draw(assets.batch(), "VIDAS: " + snapshot.getLives(), 24f, 770f);
        }

        assets.font().draw(assets.batch(), "DASH", 130f, 768f);
        if (snapshot.isShipDashReady()) {
            assets.font().setColor(0.3f, 1.0f, 0.9f, 1.0f);
            assets.font().draw(assets.batch(), "LISTO", 280f, 768f);
            assets.font().setColor(Color.WHITE);
        }

        if (snapshot.isGodMode()) {
            assets.font().setColor(1.0f, 0.85f, 0.20f, 1.0f);
            assets.font().draw(assets.batch(), "[MODO DIOS]", 340f, 768f);
            assets.font().setColor(Color.WHITE);
        }

        assets.font().draw(
                assets.batch(),
                "PUNTAJE: " + snapshot.getScore(),
                24f,
                718f);

        Texture quotaIcon = assets.getTexture(GameAssets.ICON_CUOTA_TEXTURE);
        float quotaTextX = 24f;
        if (quotaIcon != null) {
            assets.batch().draw(quotaIcon, 24f, 674f, 20f, 20f);
            quotaTextX = 50f;
        }

        assets.font().draw(
                assets.batch(),
                "SECTOR " + snapshot.getLevel() + "  ALIENS: "
                        + snapshot.getAliensDestroyedInLevel() + "/" + snapshot.getQuotaForLevel(),
                quotaTextX,
                690f);

        assets.font().draw(
                assets.batch(),
                "WASD: MOVER   I: DISPARAR   O: DASH   ESC: PAUSA/MENU",
                24f,
                662f);
    }

    private TextureRegion resolveShipRegion(TextureRegion[][] frames) {
        if (frames == null || frames.length == 0 || frames[0].length == 0) {
            return null;
        }

        int row = playerInput.firePressed() && frames.length > 1 ? 1 : 0;
        float dirX = playerInput.movementDirection().x;
        int column = dirX < -0.1f ? 0 : dirX > 0.1f ? 2 : 1;
        int safeRow = Math.min(row, frames.length - 1);
        int safeColumn = Math.max(0, Math.min(column, frames[safeRow].length - 1));
        return frames[safeRow][safeColumn];
    }

    private void drawFallbackEntities() {
        boolean shipNeedsFallback = snapshot.getLives() > 0
                && resolveShipRegion(assets.getPlayerFrames()) == null
                && (!snapshot.isShipInvulnerable()
                        || (((int) (elapsedSeconds * BLINK_STEPS_PER_SECOND)) % 2 == 0));
        boolean aliensNeedFallback = assets.getAlienStaticRegion() == null;
        boolean playerProjectilesNeedFallback = assets.getTexture(GameAssets.PLAYER_AMMO_TEXTURE) == null
                && !snapshot.getPlayerProjectiles().isEmpty();
        boolean alienProjectilesNeedFallback = assets.getAlienAmmoRegion() == null
                && !snapshot.getAlienProjectiles().isEmpty();
        if (!shipNeedsFallback && !aliensNeedFallback
                && !playerProjectilesNeedFallback && !alienProjectilesNeedFallback) {
            return;
        }

        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);
        if (shipNeedsFallback) {
            drawFallbackRect(snapshot.getShip(), Color.CYAN);
        }
        if (aliensNeedFallback) {
            for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
                drawFallbackRect(alien, Color.ORANGE);
            }
        }
        if (playerProjectilesNeedFallback) {
            for (WorldSnapshot.EntityView projectile : snapshot.getPlayerProjectiles()) {
                drawFallbackRect(projectile, Color.YELLOW);
            }
        }
        if (alienProjectilesNeedFallback) {
            for (WorldSnapshot.EntityView projectile : snapshot.getAlienProjectiles()) {
                drawFallbackRect(projectile, Color.RED);
            }
        }
        assets.shapes().end();
    }

    private void drawFallbackRect(WorldSnapshot.EntityView entity, Color color) {
        assets.shapes().setColor(color);
        assets.shapes().rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }

    @Override
    public void onPlayerShoot() {
        assets.playSound(GameAssets.PLAYER_SHOOT_SOUND, 0.45f);
    }

    @Override
    public void onPlayerDash() {
        assets.playSound(GameAssets.PLAYER_DASH_SOUND, 0.75f);
    }

    @Override
    public void onPlayerHurt(float centerX, float centerY) {
        assets.playSound(GameAssets.PLAYER_DEATH_SOUND, 0.9f);
        playerRings.add(new PlayerRing(centerX, centerY));
        triggerScreenShake(0.22f, 5f);
    }

    @Override
    public void onAlienHurt() {
        assets.playSound(GameAssets.ALIEN_HURT_SOUND, 0.4f);
    }

    @Override
    public void onAlienDestroyed(float centerX, float centerY) {
        assets.playSound(GameAssets.ALIEN_DEATH_SOUND, 0.7f);
        fireRings.add(new FireRing(centerX, centerY));
    }

    @Override
    public void onAlienShoot() {
        assets.playSound(GameAssets.ALIEN_SHOOT_SOUND, 0.35f);
    }

    @Override
    public void onMeteoriteHit() {
        assets.playSound(GameAssets.METEORITE_HIT_SOUND, 0.6f);
    }

    @Override
    public void onMeteoriteDestroyed(float centerX, float centerY, boolean isMajor) {
        if (isMajor) {
            if (assets.getSound(GameAssets.METEORITE_DESTROYED_SOUND) != null) {
                assets.playSound(GameAssets.METEORITE_DESTROYED_SOUND, 0.85f);
            } else {
                assets.playSound(GameAssets.METEORITE_HIT_SOUND, 0.85f);
            }
            triggerScreenShake(0.14f, 3f);
        } else {
            assets.playSound(GameAssets.METEORITE_HIT_SOUND, 0.6f);
        }

        int particleCount = isMajor ? 16 : 8;
        for (int i = 0; i < particleCount; i++) {
            debrisParticles.add(new DebrisParticle(centerX, centerY));
        }
    }

    @Override
    public void onLevelCompleted() {
        assets.stopMusic();
        assets.playSound(GameAssets.LEVEL_CLEAR_SOUND, 0.85f);
    }

    // --- EFECTOS VISUALES PROCEDURALES (VFX) ---

    private static final class PlayerRing {
        private final float x;
        private final float y;
        private float radius;
        private float timer;
        private static final float DURATION = 0.45f;
        private static final float MAX_RADIUS = 70f;

        public PlayerRing(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = 8f;
            this.timer = DURATION;
        }

        public boolean update(float delta) {
            timer = Math.max(0f, timer - delta);
            float progress = 1f - (timer / DURATION);
            radius = 8f + MAX_RADIUS * Interpolation.pow2Out.apply(progress);
            return timer <= 0f;
        }

        public float getX() { return x; }
        public float getY() { return y; }
        public float getRadius() { return radius; }
        public float getAlpha() { return Math.max(0f, timer / DURATION); }
    }

    private static final class FireRing {
        private final float x;
        private final float y;
        private float radius;
        private float timer;
        private static final float DURATION = 0.35f;
        private static final float MAX_RADIUS = 50f;

        public FireRing(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = 6f;
            this.timer = DURATION;
        }

        public boolean update(float delta) {
            timer = Math.max(0f, timer - delta);
            float progress = 1f - (timer / DURATION);
            radius = 6f + MAX_RADIUS * Interpolation.pow2Out.apply(progress);
            return timer <= 0f;
        }

        public float getX() { return x; }
        public float getY() { return y; }
        public float getRadius() { return radius; }
        public float getAlpha() { return Math.max(0f, timer / DURATION); }
    }

    private static final class DebrisParticle {
        private float x;
        private float y;
        private final float vx;
        private final float vy;
        private final float size;
        private final float shade;
        private float timer;
        private final float duration;

        public DebrisParticle(float x, float y) {
            this.x = x;
            this.y = y;
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(60f, 210f);
            this.vx = MathUtils.cosDeg(angle) * speed;
            this.vy = MathUtils.sinDeg(angle) * speed;
            this.size = MathUtils.random(3f, 6f);
            this.shade = MathUtils.random(0.50f, 0.85f);
            this.duration = MathUtils.random(0.30f, 0.50f);
            this.timer = this.duration;
        }

        public boolean update(float delta) {
            timer = Math.max(0f, timer - delta);
            x += vx * delta;
            y += vy * delta;
            return timer <= 0f;
        }

        public float getX() { return x; }
        public float getY() { return y; }
        public float getSize() { return size; }
        public float getShade() { return shade; }
        public float getAlpha() { return Math.max(0f, timer / duration); }
    }
}