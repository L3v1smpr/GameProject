package puppy.code.entities;

import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;

/** Apariencia disponible para los meteoritos con sus dimensiones, puntos y resistencia. */
public enum MeteoriteSize {
    SMALL(GameAssets.SMALL_METEORITE_TEXTURE,
            GameConfig.SMALL_METEORITE_WIDTH, GameConfig.SMALL_METEORITE_HEIGHT,
            GameConfig.SMALL_METEORITE_SCORE,
            GameConfig.SMALL_METEORITE_HIT_POINTS),
    MEDIUM(GameAssets.MEDIUM_METEORITE_TEXTURE,
            GameConfig.MEDIUM_METEORITE_WIDTH, GameConfig.MEDIUM_METEORITE_HEIGHT,
            GameConfig.MEDIUM_METEORITE_SCORE,
            GameConfig.MEDIUM_METEORITE_HIT_POINTS),
    LARGE(GameAssets.LARGE_METEORITE_TEXTURE,
            GameConfig.LARGE_METEORITE_WIDTH, GameConfig.LARGE_METEORITE_HEIGHT,
            GameConfig.LARGE_METEORITE_SCORE,
            GameConfig.LARGE_METEORITE_HIT_POINTS);

    private final String assetPath;
    private final float width;
    private final float height;
    private final int scoreValue;
    private final int hitPoints;

    MeteoriteSize(String assetPath, float width, float height, int scoreValue, int hitPoints) {
        this.assetPath = assetPath;
        this.width = width;
        this.height = height;
        this.scoreValue = scoreValue;
        this.hitPoints = hitPoints;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getHitPoints() {
        return hitPoints;
    }
}