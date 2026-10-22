package puppy.code.world;

/**
 * Fases de la partida. El menú queda fuera: pertenece a la aplicación, no al mundo.
 */
public enum GamePhase {
    ACTIVE,
    LEVEL_COMPLETED,
    GAME_OVER,
    VICTORY
}