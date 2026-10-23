package puppy.code.app;

/**
 * Define las transiciones de pantalla permitidas desde la aplicación.
 */
public interface ScreenNavigator {

    void showMenu();

    void showGame();

    void showGameOver();

    void showVictory();

    void showGameOver(int finalScore, int levelReached);

    void showVictory(int finalScore);
}
