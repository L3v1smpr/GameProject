package puppy.code.config;

/**
 * Reúne los valores de balance y de tamaño del juego para ajustarlos sin tocar la lógica.
 *
 * <p>Las velocidades, las vidas y la invulnerabilidad salen de lo acordado por el equipo. Los
 * valores marcados como provisionales siguen pendientes de decisión y se deben probar jugando.
 */
public final class GameConfig {

    public static final float WORLD_WIDTH = 1200f;
    public static final float WORLD_HEIGHT = 800f;

    public static final float SHIP_WIDTH = 48f;
    public static final float SHIP_HEIGHT = 48f;

    // Hitbox reducida y centrada de la nave (26x48 px)
    public static final float SHIP_HITBOX_WIDTH = 26f;
    public static final float SHIP_HITBOX_HEIGHT = 48f;
    public static final float SHIP_HITBOX_OFFSET_X = (SHIP_WIDTH - SHIP_HITBOX_WIDTH) / 2f;

    /** Velocidad aumentada de la nave a 1.5x (390 px/s). */
    public static final float SHIP_SPEED = 390f;

    /** Velocidad de los proyectiles del jugador, en px/s. */
    public static final float PLAYER_PROJECTILE_SPEED = 450f;

    /** Tiempo entre disparos mientras se mantiene presionada la tecla de fuego. */
    public static final float PLAYER_FIRE_COOLDOWN_SECONDS = 0.25f;

    public static final float DASH_SPEED_MULTIPLIER = 3f;
    public static final float DASH_DURATION_SECONDS = 0.15f;
    public static final float DASH_COOLDOWN_SECONDS = 3f;

    /** Segundos de invulnerabilidad adicionales al finalizar el impulso del dash. */
    public static final float DASH_INVULNERABILITY_POST_SECONDS = 0.5f;

    public static final float SHIP_START_X = WORLD_WIDTH / 2f - SHIP_WIDTH / 2f;
    public static final float SHIP_START_Y = 55f;

    public static final int INITIAL_LIVES = 3;
    public static final float RESPAWN_INVULNERABILITY_SECONDS = 1.5f;

    public static final int FIRST_LEVEL = 1;
    public static final int SECOND_LEVEL = 2;
    public static final int LAST_LEVEL = 3;

    public static final int LEVEL_ONE_ALIEN_QUOTA = 15;
    public static final int LEVEL_TWO_ALIEN_QUOTA = 30;
    public static final int LEVEL_THREE_ALIEN_QUOTA = 45;

    public static final int ALIEN_SCORE = 100;

    // Frecuencia progresiva de generación de meteoritos según nivel
    public static final float LEVEL_ONE_METEORITE_COOLDOWN = 3.5f;
    public static final float LEVEL_TWO_METEORITE_COOLDOWN = 2.4f;
    public static final float LEVEL_THREE_METEORITE_COOLDOWN = 1.5f;
    public static final float METEORITE_SPAWN_COOLDOWN_SECONDS = 3.5f;

    public static final float METEORITE_FALL_SPEED = 160f;

    // Escala y resistencia calibrada de meteoritos
    public static final float SMALL_METEORITE_WIDTH = 36f;
    public static final float SMALL_METEORITE_HEIGHT = 36f;
    public static final float MEDIUM_METEORITE_WIDTH = 68f;
    public static final float MEDIUM_METEORITE_HEIGHT = 68f;
    public static final float LARGE_METEORITE_WIDTH = 136f;
    public static final float LARGE_METEORITE_HEIGHT = 136f;

    public static final int SMALL_METEORITE_SCORE = 25;
    public static final int MEDIUM_METEORITE_SCORE = 50;
    public static final int LARGE_METEORITE_SCORE = 100;

    public static final int SMALL_METEORITE_HIT_POINTS = 1;
    public static final int MEDIUM_METEORITE_HIT_POINTS = 2;
    public static final int LARGE_METEORITE_HIT_POINTS = 3;
    public static final int METEORITE_HIT_POINTS = 1;

    public static final float ALIEN_WIDTH = 48f;
    public static final float ALIEN_HEIGHT = 48f;

    public static final float LINEAR_ALIEN_SPEED = 90f;
    public static final float ZIGZAG_ALIEN_SPEED = 110f;
    public static final float ALIEN_PROJECTILE_SPEED = 220f;

    public static final float PROJECTILE_WIDTH = 12f;
    public static final float PROJECTILE_HEIGHT = 24f;

    public static final float V_ATTACK_SPREAD_ANGLE_DEGREES = 45f;
    public static final float ALIEN_FIRE_COOLDOWN_SECONDS = 1.8f;

    public static final int LEVEL_ONE_ALIEN_HIT_POINTS = 1;
    public static final int LEVEL_TWO_ALIEN_HIT_POINTS = 2;
    public static final int LEVEL_THREE_ALIEN_HIT_POINTS = 3;

    public static final float ZIGZAG_HALF_PERIOD_SECONDS = 1.2f;

    // Control de población alienígena en pantalla
    public static final int MIN_ALIENS_ON_SCREEN = 1;
    public static final int MAX_ALIENS_ON_SCREEN = 4;
    public static final float ALIEN_REINFORCEMENT_COOLDOWN_SECONDS = 3.5f;

    public static final int WAVE_SIZE = 3;
    public static final float ALIEN_SPAWN_MARGIN = 20f;

    private GameConfig() {
    }
}