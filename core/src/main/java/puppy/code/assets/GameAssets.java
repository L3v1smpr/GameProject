package puppy.code.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Administra los recursos compartidos permitidos para el prototipo. */
public final class GameAssets {

    public static final String PLAYER_TEXTURE = "MainShip3.png";
    public static final String PLAYER_AMMO_TEXTURE = "Rocket2.png";
    public static final String SMALL_METEORITE_TEXTURE = "aGreySmall.png";
    public static final String MEDIUM_METEORITE_TEXTURE = "aGreyMedium4.png";
    public static final String LARGE_METEORITE_TEXTURE = "aGreyLarge.png";

    public static final String EXPLOSION_SOUND = "explosion.ogg";
    public static final String HURT_SOUND = "hurt.ogg";
    public static final String POP_SOUND = "pop-sound.mp3";
    public static final String BACKGROUND_MUSIC = "piano-loops.wav";

    private static GameAssets instance;

    private final AssetManager manager;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapes;
    private TextureRegion[][] playerFrames;
    private boolean disposed;
    private Music currentMusic;

    private GameAssets() {
        manager = new AssetManager();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
        loadAllowedResources();
    }

    public static synchronized GameAssets getInstance() {
        if (instance == null || instance.disposed) {
            instance = new GameAssets();
        }
        return instance;
    }

    private void loadAllowedResources() {
        loadIfPresent(SMALL_METEORITE_TEXTURE, Texture.class);
        loadIfPresent(MEDIUM_METEORITE_TEXTURE, Texture.class);
        loadIfPresent(LARGE_METEORITE_TEXTURE, Texture.class);
        loadIfPresent(PLAYER_TEXTURE, Texture.class);
        loadIfPresent(PLAYER_AMMO_TEXTURE, Texture.class);

        loadIfPresent(EXPLOSION_SOUND, Sound.class);
        loadIfPresent(HURT_SOUND, Sound.class);
        loadIfPresent(POP_SOUND, Sound.class);
        loadIfPresent(BACKGROUND_MUSIC, Music.class);

        manager.finishLoading();
        if (manager.isLoaded(PLAYER_TEXTURE, Texture.class)) {
            TextureRegion ship = new TextureRegion(manager.get(PLAYER_TEXTURE, Texture.class));
            playerFrames = new TextureRegion[][] {{ship}};
        }
    }

    private <T> void loadIfPresent(String path, Class<T> type) {
        if (assetExists(path)) {
            manager.load(path, type);
        }
    }

    private boolean assetExists(String path) {
        return Gdx.files != null && Gdx.files.internal(path).exists();
    }

    public SpriteBatch batch() {
        ensureOpen();
        return batch;
    }

    public ShapeRenderer shapes() {
        ensureOpen();
        return shapes;
    }

    public BitmapFont font() {
        ensureOpen();
        return font;
    }

    public TextureRegion[][] getPlayerFrames() {
        ensureOpen();
        return playerFrames;
    }

    /** No hay sprite de alien en la lista permitida; GameScreen dibuja su fallback geométrico. */
    public TextureRegion[][] getAlienFrames() {
        ensureOpen();
        return null;
    }

    public TextureRegion getAlienStaticRegion() {
        ensureOpen();
        return null;
    }

    public synchronized Texture texture(String path) {
        return load(path, Texture.class);
    }

    public synchronized Texture getTexture(String path) {
        return texture(path);
    }

    public synchronized Sound sound(String path) {
        return load(path, Sound.class);
    }

    public synchronized Sound getSound(String path) {
        return sound(path);
    }

    public synchronized Music music(String path) {
        return load(path, Music.class);
    }

    public synchronized Music getMusic(String path) {
        return music(path);
    }

    private <T> T load(String path, Class<T> type) {
        ensureOpen();
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del recurso no puede estar vacía.");
        }
        if (!assetExists(path)) {
            return null;
        }
        if (!manager.isLoaded(path, type)) {
            manager.load(path, type);
            manager.finishLoadingAsset(path);
        }
        return manager.get(path, type);
    }

    public synchronized void playSound(String path, float volume) {
        ensureOpen();
        Sound sound = sound(path);
        if (sound != null) {
            sound.play(volume);
        }
    }

    public synchronized void playMusic(String path, boolean loop, float volume) {
        ensureOpen();
        stopMusic();
        currentMusic = music(path);
        if (currentMusic != null) {
            currentMusic.setLooping(loop);
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }

    public synchronized void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public synchronized void dispose() {
        if (disposed) {
            return;
        }
        stopMusic();
        shapes.dispose();
        font.dispose();
        batch.dispose();
        manager.dispose();
        disposed = true;
        if (instance == this) {
            instance = null;
        }
    }

    private void ensureOpen() {
        if (disposed) {
            throw new IllegalStateException("GameAssets ya fue liberado.");
        }
    }
}
