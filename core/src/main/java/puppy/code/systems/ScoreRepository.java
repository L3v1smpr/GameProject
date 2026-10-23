package puppy.code.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsula la persistencia local de los mejores puntajes (Top 3) usando LibGDX Preferences.
 */
public final class ScoreRepository {

    private static final String PREFS_NAME = "space_navigation_scores";
    private final Preferences prefs;

    public static final class HighScore implements Comparable<HighScore> {
        private final String name;
        private final int score;

        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(HighScore other) {
            return Integer.compare(other.score, this.score); // Orden descendente (mayor puntaje primero)
        }
    }

    public ScoreRepository() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    /** Obtiene la lista ordenada del Top 3 de puntajes. */
    public List<HighScore> getTopScores() {
        List<HighScore> scores = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String name = prefs.getString("name_" + i, "---");
            int score = prefs.getInteger("score_" + i, 0);
            scores.add(new HighScore(name, score));
        }
        Collections.sort(scores);
        return scores;
    }

    /** Verifica si un puntaje clasifica dentro del Top 3. */
    public boolean isEligibleForTop3(int score) {
        List<HighScore> current = getTopScores();
        if (current.size() < 3) {
            return true;
        }
        return score > current.get(current.size() - 1).getScore();
    }

    /** Agrega un nuevo puntaje asegurando que el nombre tenga exactamente 3 caracteres. */
    public void addScore(String name, int score) {
        List<HighScore> current = getTopScores();
        
        if (name == null || name.trim().isEmpty()) {
            name = "AAA";
        } else if (name.length() > 3) {
            name = name.substring(0, 3);
        } else if (name.length() < 3) {
            name = String.format("%-3s", name).replace(' ', 'A');
        }

        current.add(new HighScore(name.toUpperCase(), score));
        Collections.sort(current);

        // Guarda solo los 3 mejores
        for (int i = 0; i < Math.min(3, current.size()); i++) {
            prefs.putString("name_" + i, current.get(i).getName());
            prefs.putInteger("score_" + i, current.get(i).getScore());
        }
        prefs.flush();
    }
}