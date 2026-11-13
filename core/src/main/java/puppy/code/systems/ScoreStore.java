package puppy.code.systems;

import java.util.List;

/** Operaciones de puntaje que necesitan las pantallas, sin exponer el mecanismo de persistencia. */
public interface ScoreStore {

    List<HighScore> getTopScores();

    boolean isEligibleForTop3(int score);

    boolean addScore(String name, int score);
}
