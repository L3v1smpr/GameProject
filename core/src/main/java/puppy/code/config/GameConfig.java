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

    // Dimensiones de la hitbox reducida y centrada de la nave (26x48 px)
    public static final float SHIP_HITBOX_WIDTH = 26f;
    public static final float SHIP_HITBOX_HEIGHT = 48f;
    public static final float SHIP_HITBOX_OFFSET_X = (SHIP_WIDTH - SHIP_HITBOX_WIDTH) / 2f; // 11 px

    /** Velocidad aumentada de la nave a 1.5x (de 260 a 390 px/s). */
    public static final float SHIP_SPEED = 390f;

    /** Velocidad de los proyectiles del jugador, en px/s. */
    public static final float PLAYER_PROJECTILE_SPEED = 450f;

    /** Tiempo entre disparos mientras se mantiene presionada la tecla de fuego. */
    public static final float PLAYER_FIRE_COOLDOWN_SECONDS = 0.25f;

    /** Multiplicador provisional del movimiento mientras se ejecuta el dash. */
    public static final float DASH_SPEED_MULTIPLIER = 3f;

    public static final float DASH_DURATION_SECONDS = 0.15f;

    public static final float DASH_COOLDOWN_SECONDS = 3f;

    /** Punto fijo donde aparece la nave al iniciar y al perder una vida. */
    public static final float SHIP_START_X = WORLD_WIDTH / 2f - SHIP_WIDTH / 2f;

    public static final float SHIP_START_Y = 55f;

    public static final int INITIAL_LIVES = 3;

    /** Segundos de invulnerabilidad al reaparecer después de perder una vida. */
    public static final float RESPAWN_INVULNERABILITY_SECONDS = 1.5f;

    public static final int FIRST_LEVEL = 1;
    public static final int SECOND_LEVEL = 2;
    public static final int LAST_LEVEL = 3;

    public static final int LEVEL_ONE_ALIEN_QUOTA = 15;
    public static final int LEVEL_TWO_ALIEN_QUOTA = 30;
    public static final int LEVEL_THREE_ALIEN_QUOTA = 45;

    public static final int ALIEN_SCORE = 100;

    public static final float METEORITE_SPAWN_COOLDOWN_SECONDS = 4f;
    public static final float METEORITE_FALL_SPEED = 100f;

    // Escala calibrada de meteoritos
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

    /** Velocidad del alien con descenso lineal, en px/s. */
    public static final float LINEAR_ALIEN_SPEED = 90f;

    /** Velocidad total del alien con zigzag, en px/s. */
    public static final float ZIGZAG_ALIEN_SPEED = 110f;

    /** Velocidad de los proyectiles de los aliens, en px/s. */
    public static final float ALIEN_PROJECTILE_SPEED = 220f;

    /** Dimensiones de proyectiles (12x24 px). */
    public static final float PROJECTILE_WIDTH = 12f;
    public static final float PROJECTILE_HEIGHT = 24f;

    /** Apertura angular a cada lado del eje de puntería para el disparo en V. */
    public static final float V_ATTACK_SPREAD_ANGLE_DEGREES = 45f;

    /** Cadencia de disparo de los aliens, en segundos. */
    public static final float ALIEN_FIRE_COOLDOWN_SECONDS = 1.8f;

    public static final int LEVEL_ONE_ALIEN_HIT_POINTS = 1;
    public static final int LEVEL_TWO_ALIEN_HIT_POINTS = 2;
    public static final int LEVEL_THREE_ALIEN_HIT_POINTS = 3;

    /** Provisional: segundos que dura cada tramo del zigzag antes de cambiar de lado. */
    public static final float ZIGZAG_HALF_PERIOD_SECONDS = 1.2f;

    /** Cantidad de aliens que se generan a la vez dentro de una oleada. */
    public static final int WAVE_SIZE = 3;

    /** Separación entre el borde superior del mundo y los aliens recién creados. */
    public static final float ALIEN_SPAWN_MARGIN = 20f;

    private GameConfig() {
    }
}