package puppy.code.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import puppy.code.world.WorldSnapshot;

/** Presenta la partida y delega en GameWorld las reglas de combate y progresión. */
public final class GameScreen extends AbstractGameScreen {

    private static final float BLINK_STEPS_PER_SECOND = 10f;
    private static final float DASH_EFFECT_SECONDS = 0.20f;
    private static final float DASH_IMAGE_INTERVAL_SECONDS = 0.04f;

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
        snapshot = world.snapshot();
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
            navigator.showMenu();
            return;
        }

        if (snapshot.getPhase() == GamePhase.GAME_OVER) {
            navigator.showGameOver(snapshot.getScore(), snapshot.getLevel());
            return;
        }
        if (snapshot.getPhase() == GamePhase.VICTORY) {
            navigator.showVictory(snapshot.getScore());
            return;
        }
        if (snapshot.getPhase() == GamePhase.LEVEL_COMPLETED) {
            if (navigationInput.anyActionJustPressed()) {
                world.advanceToNextLevel();
                snapshot = world.snapshot();
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
        TextureRegion region = resolveAlienRegion();
        for (WorldSnapshot.EntityView alien : snapshot.getAliens()) {
            if (region != null) {
                assets.batch().draw(region, alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight());
            }
        }
    }

    private void drawProjectiles() {
        Texture playerAmmo = assets.getTexture(GameAssets.PLAYER_AMMO_TEXTURE);
        Texture alienAmmo = assets.getTexture(GameAssets.ALIEN_AMMO_TEXTURE);
        for (WorldSnapshot.EntityView projectile : snapshot.getPlayerProjectiles()) {
            if (playerAmmo != null) {
                assets.batch().draw(
                        playerAmmo, projectile.getX(), projectile.getY(),
                        projectile.getWidth(), projectile.getHeight());
            }
        }
        for (WorldSnapshot.EntityView projectile : snapshot.getAlienProjectiles()) {
            if (alienAmmo != null) {
                assets.batch().draw(
                        alienAmmo, projectile.getX(), projectile.getY(),
                        projectile.getWidth(), projectile.getHeight());
            }
        }
    }

    private void drawMeteorites() {
        for (WorldSnapshot.EntityView meteorite : snapshot.getMeteorites()) {
            Texture texture = assets.getTexture(meteorite.getAssetPath());
            if (texture != null) {
                assets.batch().draw(
                        texture, meteorite.getX(), meteorite.getY(),
                        meteorite.getWidth(), meteorite.getHeight());
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
        assets.font().draw(
                assets.batch(),
                "Nivel " + snapshot.getLevel() + "  Aliens: "
                        + snapshot.getAliensDestroyedInLevel() + "/" + snapshot.getQuotaForLevel(),
                24f,
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

    private TextureRegion resolveAlienRegion() {
        TextureRegion[][] frames = assets.getAlienFrames();
        if (frames != null && frames.length > 0 && frames[0].length > 1) {
            return frames[0][1];
        }
        return assets.getAlienStaticRegion();
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
        boolean aliensNeedFallback = resolveAlienRegion() == null;
        boolean playerProjectilesNeedFallback = assets.getTexture(GameAssets.PLAYER_AMMO_TEXTURE) == null
                && !snapshot.getPlayerProjectiles().isEmpty();
        boolean alienProjectilesNeedFallback = assets.getTexture(GameAssets.ALIEN_AMMO_TEXTURE) == null
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
}
