package puppy.code.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor centralizado de activos del juego (Aplica el patrón Singleton).
 */
public final class GameAssets {

    private static GameAssets instance;

    // Fondo (en la raíz de assets/)
    public static final String BACKGROUND_TEXTURE = "background.png";

    // Jugador (assets/player/)
    public static final String PLAYER_SHEET_TEXTURE = "player/player_sheet.png";
    public static final String PLAYER_AMMO_TEXTURE = "player/player_ammo.png";
    public static final String PLAYER_LIFE_TEXTURE = "player/player_life.png";

    // Aliens (assets/aliens/)
    public static final String ALIEN_STATIC_TEXTURE = "aliens/alien_static.png";
    public static final String ALIEN_SENTRY_TEXTURE = "aliens/alien_sentry.png";
    public static final String ALIEN_BOMBER_TEXTURE = "aliens/alien_bomber.png";
    public static final String ALIEN_AMMO_TEXTURE = "aliens/alien_ammo.png";

    // Obstáculos / Meteoritos (assets/obstacles/)
    public static final String METEORITE_LARGE = "obstacles/aGreyLarge.png";
    public static final String METEORITE_MEDIUM = "obstacles/aGreyMedium4.png";
    public static final String METEORITE_SMALL = "obstacles/aGreySmall.png";

    // Interfaz (assets/ui/)
    public static final String ICON_CUOTA_TEXTURE = "ui/icon_cuota.png";
    public static final String BANNER_GAMEOVER_TEXTURE = "ui/banner_gameover.png";
    public static final String BANNER_VICTORIA_TEXTURE = "ui/banner_victoria.png";

    // Música (assets/audio/music/)
    public static final String BACKGROUND_MUSIC = "audio/music/background_music.MP3";
    public static final String GAMEOVER_MUSIC = "audio/music/gameover.MP3";
    public static final String VICTORY_MUSIC = "audio/music/pantalla_victoria.mp3";

    // Efectos de sonido (assets/audio/sfx/)
    public static final String PLAYER_SHOOT_SOUND = "audio/sfx/player_ammo.wav";
    public static final String PLAYER_DASH_SOUND = "audio/sfx/player_dash.wav";
    public static final String PLAYER_DEATH_SOUND = "audio/sfx/player_death.wav";
    public static final String ALIEN_SHOOT_SOUND = "audio/sfx/alien_ammo.wav";
    public static final String ALIEN_HURT_SOUND = "audio/sfx/alien_hurt.wav";
    public static final String ALIEN_DEATH_SOUND = "audio/sfx/alien_death.wav";
    public static final String METEORITE_HIT_SOUND = "audio/sfx/meteorite_hit.wav";
    public static final String METEORITE_DESTROYED_SOUND = "audio/sfx/meteorite_destroyed.MP3";
    public static final String LEVEL_CLEAR_SOUND = "audio/sfx/nivel_superado.MP3";

    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final BitmapFont font = new BitmapFont();

    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, Sound> sounds = new HashMap<>();
    private final Map<String, Music> music = new HashMap<>();

    private TextureRegion[][] playerFrames;
    private TextureRegion[][] alienStaticFrames;
    private TextureRegion[][] alienSentryFrames;
    private TextureRegion[][] alienBomberFrames;
    private TextureRegion alienAmmoRegion;

    public GameAssets() {
        instance = this;
        loadAll(); // Carga inmediata al instanciar
    }

    public static synchronized GameAssets getInstance() {
        if (instance == null) {
            instance = new GameAssets();
        }
        return instance;
    }

    public void loadAll() {
        // Texturas
        loadTexture(BACKGROUND_TEXTURE);
        loadTexture(PLAYER_SHEET_TEXTURE);
        loadTexture(PLAYER_AMMO_TEXTURE);
        loadTexture(PLAYER_LIFE_TEXTURE);
        loadTexture(ALIEN_STATIC_TEXTURE);
        loadTexture(ALIEN_SENTRY_TEXTURE);
        loadTexture(ALIEN_BOMBER_TEXTURE);
        loadTexture(ALIEN_AMMO_TEXTURE);
        loadTexture(METEORITE_LARGE);
        loadTexture(METEORITE_MEDIUM);
        loadTexture(METEORITE_SMALL);
        loadTexture(ICON_CUOTA_TEXTURE);
        loadTexture(BANNER_GAMEOVER_TEXTURE);
        loadTexture(BANNER_VICTORIA_TEXTURE);

        // Subdivisión de Spritesheets (2 filas: reposo arriba, disparo abajo)
        Texture pSheet = textures.get(PLAYER_SHEET_TEXTURE);
        if (pSheet != null) {
            playerFrames = TextureRegion.split(pSheet, pSheet.getWidth() / 3, pSheet.getHeight() / 2);
        }

     // Subdivisión y orientación de Spritesheets alienígenas (apuntando al Sur)
        Texture aStatic = textures.get(ALIEN_STATIC_TEXTURE);
        if (aStatic != null) {
            alienStaticFrames = TextureRegion.split(aStatic, aStatic.getWidth(), aStatic.getHeight() / 2);
            flipFrames(alienStaticFrames);
        }

        Texture aSentry = textures.get(ALIEN_SENTRY_TEXTURE);
        if (aSentry != null) {
            alienSentryFrames = TextureRegion.split(aSentry, aSentry.getWidth(), aSentry.getHeight() / 2);
            flipFrames(alienSentryFrames);
        }

        Texture aBomber = textures.get(ALIEN_BOMBER_TEXTURE);
        if (aBomber != null) {
            alienBomberFrames = TextureRegion.split(aBomber, aBomber.getWidth(), aBomber.getHeight() / 2);
            flipFrames(alienBomberFrames);
        }

        Texture aAmmo = textures.get(ALIEN_AMMO_TEXTURE);
        if (aAmmo != null) {
            alienAmmoRegion = new TextureRegion(aAmmo);
        }

        // Audio
        loadMusic(BACKGROUND_MUSIC);
        loadMusic(GAMEOVER_MUSIC);
        loadMusic(VICTORY_MUSIC);

        loadSound(PLAYER_SHOOT_SOUND);
        loadSound(PLAYER_DASH_SOUND);
        loadSound(PLAYER_DEATH_SOUND);
        loadSound(ALIEN_SHOOT_SOUND);
        loadSound(ALIEN_HURT_SOUND);
        loadSound(ALIEN_DEATH_SOUND);
        loadSound(METEORITE_HIT_SOUND);
        loadSound(METEORITE_DESTROYED_SOUND);
        loadSound(LEVEL_CLEAR_SOUND);
    }

    private void loadTexture(String path) {
        if (Gdx.files.internal(path).exists()) {
            textures.put(path, new Texture(Gdx.files.internal(path)));
        } else {
            Gdx.app.error("GameAssets", "No se encontro la textura en: " + path);
        }
    }

    private void loadSound(String path) {
        if (Gdx.files.internal(path).exists()) {
            sounds.put(path, Gdx.audio.newSound(Gdx.files.internal(path)));
        } else {
            Gdx.app.error("GameAssets", "No se encontro el sonido en: " + path);
        }
    }

    private void loadMusic(String path) {
        if (Gdx.files.internal(path).exists()) {
            music.put(path, Gdx.audio.newMusic(Gdx.files.internal(path)));
        } else {
            Gdx.app.error("GameAssets", "No se encontro la musica en: " + path);
        }
    }

    public TextureRegion getAlienStaticRegion(boolean firing) {
        return (alienStaticFrames != null) ? alienStaticFrames[firing ? 1 : 0][0] : null;
    }

    public TextureRegion getAlienStaticRegion() {
        return getAlienStaticRegion(false);
    }

    public TextureRegion getAlienSentryRegion(boolean firing) {
        return (alienSentryFrames != null) ? alienSentryFrames[firing ? 1 : 0][0] : null;
    }

    public TextureRegion getAlienBomberRegion(boolean firing) {
        return (alienBomberFrames != null) ? alienBomberFrames[firing ? 1 : 0][0] : null;
    }

    public TextureRegion[][] getPlayerFrames() { return playerFrames; }
    public TextureRegion getAlienAmmoRegion() { return alienAmmoRegion; }
    public Texture getTexture(String path) { return textures.get(path); }
    public Sound getSound(String path) { return sounds.get(path); }
    public Music getMusic(String path) { return music.get(path); }
    public SpriteBatch batch() { return batch; }
    public ShapeRenderer shapes() { return shapes; }
    public BitmapFont font() { return font; }

    public void playSound(String path, float volume) {
        Sound s = sounds.get(path);
        if (s != null) s.play(volume);
    }

    public void playMusic(String path, boolean loop, float volume) {
        Music m = music.get(path);
        if (m != null) {
            m.setLooping(loop);
            m.setVolume(volume);
            m.play();
        }
    }

    public void stopMusic() {
        for (Music m : music.values()) {
            if (m.isPlaying()) m.stop();
        }
    }

    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        for (Texture t : textures.values()) t.dispose();
        for (Sound s : sounds.values()) s.dispose();
        for (Music m : music.values()) m.dispose();
        instance = null;
    }
    
    private void flipFrames(TextureRegion[][] frames) {
        if (frames == null) {
            return;
        }
        for (TextureRegion[] row : frames) {
            for (TextureRegion frame : row) {
                frame.flip(false, true); // Invierte la orientación para que apunten hacia la nave del jugador
            }
        }
    }
}