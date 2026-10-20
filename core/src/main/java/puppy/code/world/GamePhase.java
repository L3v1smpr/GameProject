package puppy.code.world;

/**
 * Fases de la partida. El menú queda fuera: pertenece a la aplicación, no al mundo.
 *
 * <p>Se agregarán {@code LEVEL_COMPLETE} y {@code VICTORY} cuando existan los niveles por cuotas.
 */
public enum GamePhase {
    PLAYING,
    GAME_OVER
}
