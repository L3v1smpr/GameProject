package puppy.code.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Encapsula la persistencia local del Top 3 usando LibGDX Preferences. */
public final class ScoreRepository implements ScoreStore {

    private static final String PREFS_NAME = "space_navigation_scores";
    private final Preferences prefs;

    public ScoreRepository() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    /** Obtiene la lista ordenada del Top 3 de puntajes. */
    @Override
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

    /** Verifica si el puntaje supera al tercer resultado guardado. */
    @Override
    public boolean isEligibleForTop3(int score) {
        if (score < 0) {
            return false;
        }
        List<HighScore> current = getTopScores();
        return score > current.get(current.size() - 1).getScore();
    }

    /**
     * Guarda el resultado solo si entra al Top 3 y normaliza el nombre a tres letras A-Z.
     *
     * @return true si el puntaje quedó guardado
     */
    @Override
    public boolean addScore(String name, int score) {
        if (!isEligibleForTop3(score)) {
            return false;
        }

        List<HighScore> current = getTopScores();
        current.add(new HighScore(normalizeName(name), score));
        Collections.sort(current);

        for (int i = 0; i < 3; i++) {
            prefs.putString("name_" + i, current.get(i).getName());
            prefs.putInteger("score_" + i, current.get(i).getScore());
        }
        prefs.flush();
        return true;
    }

    private String normalizeName(String name) {
        StringBuilder normalized = new StringBuilder(3);
        if (name != null) {
            for (int i = 0; i < name.length() && normalized.length() < 3; i++) {
                char letter = Character.toUpperCase(name.charAt(i));
                if (letter >= 'A' && letter <= 'Z') {
                    normalized.append(letter);
                }
            }
        }
        while (normalized.length() < 3) {
            normalized.append('A');
        }
        return normalized.toString();
    }
}
