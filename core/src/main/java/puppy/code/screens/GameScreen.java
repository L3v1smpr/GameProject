package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
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

    private enum TransitionType {
        NONE,
        GAME_OVER_FADE,
        VICTORY_FADE
    }

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
        assets.playMusic(GameAssets.BACKGROUND_MUSIC, true, BASE_MUSIC_VOLUME);
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
            navigator.showMenu();
            return;
        }

        if (activeTransition != TransitionType.NONE) {
            transitionTimer = Math.max(0f, transitionTimer - delta);
            float linearProgress = Math.min(1f, 1f - (transitionTimer / TRANSITION_DURATION_SECONDS));
            float soundProgress = Interpolation.pow2In.apply(linearProgress);

            Music bgm = assets.getMusic(GameAssets.BACKGROUND_MUSIC);
            if (bgm != null && bgm.isPlaying()) {
                bgm.setVolume(Math.max(0f, BASE_MUSIC_VOLUME * (1f - soundProgress)));
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
            if (navigationInput.anyActionJustPressed()) {
                world.advanceToNextLevel();
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
        drawPhaseMessage();

        assets.batch().end();
        drawFallbackEntities();
        drawTransitionOverlay();
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
            TextureRegion region = assets.getAlienStaticRegion(alien.isFiring());
            if (region != null) {
                assets.batch().draw(region, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
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
                assets.batch().draw(
                        texture,
                        meteorite.getX(),
                        meteorite.getY(),
                        meteorite.getWidth(),
                        meteorite.getHeight());
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
            assets.font().draw(assets.batch(), "Vidas: " + snapshot.getLives(), 24f, 770f);
        }

        assets.font().draw(
                assets.batch(),
                "Puntaje: " + snapshot.getScore(),
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
                "Nivel " + snapshot.getLevel() + "  Aliens: "
                        + snapshot.getAliensDestroyedInLevel() + "/" + snapshot.getQuotaForLevel(),
                quotaTextX,
                690f);
        assets.font().draw(
                assets.batch(),
                "WASD: mover   I: disparar   O: dash   ESC: menu",
                24f,
                662f);
    }

    private void drawPhaseMessage() {
        if (snapshot.getPhase() == GamePhase.LEVEL_COMPLETED) {
            assets.font().draw(assets.batch(), "Nivel superado", 515f, 430f);
            assets.font().draw(
                    assets.batch(),
                    "Presiona cualquier tecla o haz clic para continuar",
                    365f,
                    390f);
        }
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
        boolean shipNeedsFallback = resolveShipRegion(assets.getPlayerFrames()) == null
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
    public void onPlayerHurt() {
        assets.playSound(GameAssets.PLAYER_DEATH_SOUND, 0.9f);
    }

    @Override
    public void onAlienHurt() {
        assets.playSound(GameAssets.ALIEN_HURT_SOUND, 0.4f);
    }

    @Override
    public void onAlienDestroyed() {
        assets.playSound(GameAssets.ALIEN_DEATH_SOUND, 0.7f);
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
    public void onMeteoriteDestroyed() {
        if (assets.getSound(GameAssets.METEORITE_DESTROYED_SOUND) != null) {
            assets.playSound(GameAssets.METEORITE_DESTROYED_SOUND, 0.85f);
        } else {
            assets.playSound(GameAssets.METEORITE_HIT_SOUND, 0.85f);
        }
    }

    @Override
    public void onLevelCompleted() {
        assets.stopMusic();
        assets.playSound(GameAssets.LEVEL_CLEAR_SOUND, 0.85f);
    }
}