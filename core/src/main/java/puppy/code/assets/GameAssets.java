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

/**
 * Gestor centralizado de recursos del juego.
 * Implementa el patrón creacional Singleton para garantizar una
 * unica instancia global encargada del ciclo de vida de texturas, fuentes y audio.
 */
public final class GameAssets {

    // CONSTANTES DE METEORITOS (Requeridas por MeteoriteSize)
    public static final String SMALL_METEORITE_TEXTURE = "aGreySmall.png";
    public static final String MEDIUM_METEORITE_TEXTURE = "aGreyMedium4.png";
    public static final String LARGE_METEORITE_TEXTURE = "aGreyLarge.png";

    // CONSTANTES DE ENTIDADES, HUD Y BANNERS
    public static final String PLAYER_SHEET_TEXTURE = "player_sheet.png";
    public static final String ALIEN_SHEET_TEXTURE = "alien_sheet.png";
    public static final String ALIEN_STATIC_TEXTURE = "alien_static.png";
    public static final String PLAYER_AMMO_TEXTURE = "player_ammo.png";
    public static final String ALIEN_AMMO_TEXTURE = "alien_ammo.png";
    public static final String PLAYER_LIFE_TEXTURE = "player_life.png";
    public static final String ICON_CUOTA_TEXTURE = "icon_cuota.png";
    public static final String BANNER_GAMEOVER_TEXTURE = "banner_gameover.png";
    public static final String BANNER_VICTORIA_TEXTURE = "banner_victoria.png";

    // CONSTANTES DE AUDIO
    public static final String PLAYER_SHOOT_SOUND = "player_ammo.wav";
    public static final String PLAYER_DASH_SOUND = "player_dash.wav";
    public static final String PLAYER_DEATH_SOUND = "player_death.wav";

    public static final String ALIEN_SHOOT_SOUND = "alien_ammo.wav";
    public static final String ALIEN_HURT_SOUND = "alien_hurt.wav";
    public static final String ALIEN_DEATH_SOUND = "alien_death.wav";

    public static final String METEORITE_HIT_SOUND = "meteorite_hit.wav";
    public static final String LEVEL_CLEAR_SOUND = "nivel_superado.MP3";
    public static final String VICTORY_MUSIC = "pantalla_victoria.mp3";
    public static final String GAMEOVER_MUSIC = "gameover.mp3";
    public static final String BACKGROUND_MUSIC = "background_music.MP3";

    private static GameAssets instance;

    private final AssetManager manager;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapes;
    private boolean disposed;

    // Sprite Sheets divididos en matrices [fila][columna] de 48x48 px
    private TextureRegion[][] playerFrames;
    private TextureRegion[][] alienFrames;
    private TextureRegion alienStaticRegion;

    private Music currentMusic;

    /**
     * Constructor privado para encapsular la instanciación (Singleton).
     */
    private GameAssets() {
        this.manager = new AssetManager();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.shapes = new ShapeRenderer();

        cargarRecursosBase();
    }

    /**
     * Punto de acceso global controlado a la instancia única.
     */
    public static synchronized GameAssets getInstance() {
        if (instance == null || instance.disposed) {
            instance = new GameAssets();
        }
        return instance;
    }

    /**
     * Precarga los recursos activos y orienta los sprites adecuadamente.
     */
    private void cargarRecursosBase() {
        // 1. Meteoritos
        cargarTexturaSiExiste(SMALL_METEORITE_TEXTURE);
        cargarTexturaSiExiste(MEDIUM_METEORITE_TEXTURE);
        cargarTexturaSiExiste(LARGE_METEORITE_TEXTURE);

        // 2. Entidades y proyectiles oficiales
        cargarTexturaSiExiste(PLAYER_SHEET_TEXTURE);
        cargarTexturaSiExiste(ALIEN_SHEET_TEXTURE);
        cargarTexturaSiExiste(ALIEN_STATIC_TEXTURE);
        cargarTexturaSiExiste(PLAYER_AMMO_TEXTURE);
        cargarTexturaSiExiste(ALIEN_AMMO_TEXTURE);

        // 3. Elementos de HUD y Banners de cierre
        cargarTexturaSiExiste(PLAYER_LIFE_TEXTURE);
        cargarTexturaSiExiste(ICON_CUOTA_TEXTURE);
        cargarTexturaSiExiste(BANNER_GAMEOVER_TEXTURE);
        cargarTexturaSiExiste(BANNER_VICTORIA_TEXTURE);

        // 4. Efectos de audio y pistas musicales
        cargarAudioSiExiste(PLAYER_SHOOT_SOUND, Sound.class);
        cargarAudioSiExiste(PLAYER_DASH_SOUND, Sound.class);
        cargarAudioSiExiste(PLAYER_DEATH_SOUND, Sound.class);

        cargarAudioSiExiste(ALIEN_SHOOT_SOUND, Sound.class);
        cargarAudioSiExiste(ALIEN_HURT_SOUND, Sound.class);
        cargarAudioSiExiste(ALIEN_DEATH_SOUND, Sound.class);

        cargarAudioSiExiste(METEORITE_HIT_SOUND, Sound.class);
        cargarAudioSiExiste(LEVEL_CLEAR_SOUND, Sound.class);
        cargarAudioSiExiste(VICTORY_MUSIC, Music.class);
        cargarAudioSiExiste(GAMEOVER_MUSIC, Music.class);
        cargarAudioSiExiste(BACKGROUND_MUSIC, Music.class);

        manager.finishLoading();

        // 5. Procesar Sprite Sheet del jugador (Mantiene vista al Norte)
        if (manager.isLoaded(PLAYER_SHEET_TEXTURE, Texture.class)) {
            Texture pSheet = manager.get(PLAYER_SHEET_TEXTURE, Texture.class);
            this.playerFrames = TextureRegion.split(pSheet, 48, 48);
        }

        // 6. Procesar Sprite Sheet del Alien (Invertido verticalmente para encarar al jugador)
        if (manager.isLoaded(ALIEN_SHEET_TEXTURE, Texture.class)) {
            Texture aSheet = manager.get(ALIEN_SHEET_TEXTURE, Texture.class);
            this.alienFrames = TextureRegion.split(aSheet, 48, 48);
            for (TextureRegion[] fila : this.alienFrames) {
                for (TextureRegion cuadro : fila) {
                    // Inset de 1 pixel para corregir el sangrado de textura (Texture Bleeding) con la fila adyacente
                    cuadro.setRegion(
                            cuadro.getRegionX() + 1,
                            cuadro.getRegionY() + 1,
                            cuadro.getRegionWidth() - 2,
                            cuadro.getRegionHeight() - 2);
                    cuadro.flip(false, true); // Inversión vertical: apunta al Sur
                }
            }
        }

        // 7. Variante estática del alien invertida
        if (manager.isLoaded(ALIEN_STATIC_TEXTURE, Texture.class)) {
            this.alienStaticRegion = new TextureRegion(manager.get(ALIEN_STATIC_TEXTURE, Texture.class));
            this.alienStaticRegion.flip(false, true);
        }
    }

    private void cargarTexturaSiExiste(String ruta) {
        if (Gdx.files != null && Gdx.files.internal(ruta).exists()) {
            manager.load(ruta, Texture.class);
        }
    }

    private <T> void cargarAudioSiExiste(String ruta, Class<T> tipo) {
        if (Gdx.files != null && Gdx.files.internal(ruta).exists()) {
            manager.load(ruta, tipo);
        }
    }

    // GETTERS PARA DIBUJO Y REGIONES

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

    public TextureRegion[][] getAlienFrames() {
        ensureOpen();
        return alienFrames;
    }

    public TextureRegion getAlienStaticRegion() {
        ensureOpen();
        return alienStaticRegion;
    }

    // ACCESO A RECURSOS (Texturas, Sonidos y Música)

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
        if (!manager.isLoaded(path, type)) {
            manager.load(path, type);
            manager.finishLoadingAsset(path);
        }
        return manager.get(path, type);
    }

    // REPRODUCCIÓN DE AUDIO CENTRALIZADA

    public synchronized void playSound(String fileName, float volume) {
        ensureOpen();
        Sound s = sound(fileName);
        if (s != null) {
            s.play(volume);
        }
    }

    public synchronized void playMusic(String fileName, boolean loop, float volume) {
        ensureOpen();
        stopMusic();
        currentMusic = music(fileName);
        if (currentMusic != null) {
            currentMusic.setLooping(loop);
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }

    public synchronized void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    // DESTRUCCIÓN CONTROLADA (Dispose)

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
