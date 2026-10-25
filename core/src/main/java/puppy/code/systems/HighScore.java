package puppy.code.systems;

/** Resultado inmutable de una partida. */
public final class HighScore implements Comparable<HighScore> {

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
        return Integer.compare(other.score, score);
    }
}
