package puppy.code.app;

/**
 * Define las transiciones de pantalla permitidas desde la aplicación.
 */
public interface ScreenNavigator {

    void showMainMenu();

    void startNewGame();

    /** Alias de dominio que coincide con las transiciones descritas en las bases. */
    default void showMenu() {
        showMainMenu();
    }

    /** Alias de dominio que coincide con las transiciones descritas en las bases. */
    default void showGame() {
        startNewGame();
    }

    void showGameOver();

    void showVictory();

    void showGameOver(int finalScore, int levelReached);

    void showVictory(int finalScore);
}
