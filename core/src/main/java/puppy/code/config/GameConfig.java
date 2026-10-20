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

    public static final float SHIP_WIDTH = 40f;
    public static final float SHIP_HEIGHT = 36f;

    /** Velocidad de la nave, en px/s. */
    public static final float SHIP_SPEED = 260f;

    /** Velocidad de los proyectiles del jugador, en px/s. */
    public static final float PLAYER_PROJECTILE_SPEED = 450f;

    /** Tiempo entre disparos mientras se mantiene presionada la tecla de fuego. */
    public static final float PLAYER_FIRE_COOLDOWN_SECONDS = 0.25f;

    /** Punto fijo donde aparece la nave al iniciar y al perder una vida. */
    public static final float SHIP_START_X = WORLD_WIDTH / 2f - SHIP_WIDTH / 2f;

    public static final float SHIP_START_Y = 55f;

    public static final int INITIAL_LIVES = 3;

    /** Segundos de invulnerabilidad al reaparecer después de perder una vida. */
    public static final float RESPAWN_INVULNERABILITY_SECONDS = 1.5f;

    public static final int FIRST_LEVEL = 1;

    public static final float ALIEN_WIDTH = 40f;
    public static final float ALIEN_HEIGHT = 32f;

    /** Velocidad del alien con descenso lineal, en px/s. */
    public static final float LINEAR_ALIEN_SPEED = 90f;

    /** Velocidad total del alien con zigzag, en px/s. */
    public static final float ZIGZAG_ALIEN_SPEED = 110f;

    /** Velocidad de los proyectiles de los aliens, en px/s. */
    public static final float ALIEN_PROJECTILE_SPEED = 220f;

    /** Cadencia de disparo de los aliens, en segundos. */
    public static final float ALIEN_FIRE_COOLDOWN_SECONDS = 1.8f;

    public static final int LEVEL_ONE_ALIEN_HIT_POINTS = 1;
    public static final int LEVEL_TWO_ALIEN_HIT_POINTS = 2;
    public static final int LEVEL_THREE_ALIEN_HIT_POINTS = 3;

    public static final int METEORITE_HIT_POINTS = 1;

    /** Provisional: segundos que dura cada tramo del zigzag antes de cambiar de lado. */
    public static final float ZIGZAG_HALF_PERIOD_SECONDS = 1.2f;

    /** Provisional: cantidad de aliens por oleada hasta definir las cuotas por nivel. */
    public static final int WAVE_SIZE = 3;

    /** Separación entre el borde superior del mundo y los aliens recién creados. */
    public static final float ALIEN_SPAWN_MARGIN = 20f;

    private GameConfig() {
    }
}
