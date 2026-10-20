package puppy.code.assets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;

/** Administra los recursos de dibujo compartidos; solo la aplicación crea esta instancia. */
public final class GameAssets {

    public static final String PLAYER_SHIP_TEXTURE = "MainShip3.png";
    public static final String PLAYER_PROJECTILE_TEXTURE = "Rocket2.png";
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
    private boolean disposed;

    private GameAssets() {
        manager = new AssetManager();
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapes = new ShapeRenderer();
    }

    public static synchronized GameAssets getInstance() {
        if (instance == null || instance.disposed) {
            instance = new GameAssets();
        }
        return instance;
    }

    public SpriteBatch batch() {
        ensureOpen();
        return batch;
    }

    public BitmapFont font() {
        ensureOpen();
        return font;
    }

    public ShapeRenderer shapes() {
        ensureOpen();
        return shapes;
    }

    public synchronized Texture texture(String path) {
        return load(path, Texture.class);
    }

    public synchronized Sound sound(String path) {
        return load(path, Sound.class);
    }

    public synchronized Music music(String path) {
        return load(path, Music.class);
    }

    public synchronized void dispose() {
        if (disposed) {
            return;
        }

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

    private <T> T load(String path, Class<T> type) {
        ensureOpen();
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del recurso no puede estar vacía.");
        }
        if (!manager.isLoaded(path, type)) {
            manager.load(path, type);
            manager.finishLoadingAsset(path);
        }
        return manager.get(path, type);
    }
}
