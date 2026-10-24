package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
    private final List<FireRing> fireRings = new ArrayList<>();
    private final List<PlayerRing> playerRings = new ArrayList<>();
    private final List<DebrisParticle> debrisParticles = new ArrayList<>();

    private WorldSnapshot snapshot;
    private float elapsedSeconds;
    private float dashEffectTimer;
    private float dashSpawnCooldown;
    private boolean returnToMenuRequested;
    private boolean paused;

    // Control de sacudida de pantalla (Screen Shake)
    private float shakeTimer;
    private float shakeDuration;
    private float shakeIntensity;

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
        shakeTimer = 0f;
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
        returnToMenuRequested = navigationInput.backJustPressed();

        if (playerInput.pauseJustPressed()
                && activeTransition == TransitionType.NONE
                && snapshot.getPhase() == GamePhase.ACTIVE) {
            paused = !paused;
            Music bgm = assets.getMusic(GameAssets.BACKGROUND_MUSIC);
            if (bgm != null) {
                if (paused && bgm.isPlaying()) {
                    bgm.pause();
                } else if (!paused && !bgm.isPlaying()) {
                    bgm.play();
                }
            }
        }
    }

    @Override
    protected void update(float delta) {
        if (returnToMenuRequested) {
            navigator.showMenu();
            return;
        }

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
        drawPhaseMessage();

        assets.batch().end();

        drawVisualEffects();
        drawFallbackEntities();
        drawTransitionOverlay();

        if (paused) {
            drawPauseOverlay();
        }
    }

    private void drawPauseOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        assets.shapes().setProjectionMatrix(camera.combined);
        assets.shapes().begin(ShapeType.Filled);
        assets.shapes().setColor(0f, 0f, 0f, 0.60f);
        assets.shapes().rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        assets.shapes().end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        assets.batch().setProjectionMatrix(camera.combined);
        assets.batch().begin();
        assets.font().setColor(Color.WHITE);
        assets.font().draw(assets.batch(), "JUEGO PAUSADO", 540f, 430f);
        assets.font().draw(assets.batch(), "Presiona P para continuar", 495f, 390f);
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
            TextureRegion region = assets.getAlienStaticRegion(alien.isFiring());
            if (region != null) {
                if (alien.isHurt()) {
                    assets.batch().setColor(1f, 0.25f, 0.25f, 0.85f);
                } else if (alien.isStatic()) {
                    // Tinte ámbar/dorado unificado para todas las torretas estáticas
                    assets.batch().setColor(1.0f, 0.75f, 0.25f, 1.0f);
                }

                assets.batch().draw(region, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());

                if (alien.isHurt() || alien.isStatic()) {
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
                "WASD: mover   I: disparar   O: dash   P: pausa   ESC: menu",
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
        // Sacudida enérgica de cámara al perder vida (5 px, 220 ms)
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
            // Micro-sacudida de impacto al detonar meteoritos pesados (3 px, 140 ms)
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