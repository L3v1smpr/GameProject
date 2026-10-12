package puppy.code.assets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Administra los recursos de dibujo compartidos; solo la aplicación crea esta instancia. */
public final class GameAssets {

    private static GameAssets instance;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapes;
    private boolean disposed;

    private GameAssets() {
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

    public synchronized void dispose() {
        if (disposed) {
            return;
        }

        shapes.dispose();
        font.dispose();
        batch.dispose();
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
