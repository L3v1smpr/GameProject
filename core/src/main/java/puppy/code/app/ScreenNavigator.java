package puppy.code.app;

/**
 * Define las transiciones de pantalla permitidas desde la aplicación (Indirección / DIP).
 */
public interface ScreenNavigator {

    void showMainMenu();

    void startNewGame();

    void showGameOver();

    void showGameOver(int finalScore, int levelReached);

    void showVictory();

    void showVictory(int finalScore);
}