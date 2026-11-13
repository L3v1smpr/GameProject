package puppy.code.entities;

import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;

public enum MeteoriteSize {
    SMALL(
            GameAssets.METEORITE_SMALL,
            GameConfig.SMALL_METEORITE_WIDTH,
            GameConfig.SMALL_METEORITE_HEIGHT,
            GameConfig.SMALL_METEORITE_HIT_POINTS,
            GameConfig.SMALL_METEORITE_SCORE),
    MEDIUM(
            GameAssets.METEORITE_MEDIUM,
            GameConfig.MEDIUM_METEORITE_WIDTH,
            GameConfig.MEDIUM_METEORITE_HEIGHT,
            GameConfig.MEDIUM_METEORITE_HIT_POINTS,
            GameConfig.MEDIUM_METEORITE_SCORE),
    LARGE(
            GameAssets.METEORITE_LARGE,
            GameConfig.LARGE_METEORITE_WIDTH,
            GameConfig.LARGE_METEORITE_HEIGHT,
            GameConfig.LARGE_METEORITE_HIT_POINTS,
            GameConfig.LARGE_METEORITE_SCORE);

    private final String assetPath;
    private final float width;
    private final float height;
    private final int hitPoints;
    private final int scoreValue;

    MeteoriteSize(String assetPath, float width, float height, int hitPoints, int scoreValue) {
        this.assetPath = assetPath;
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.scoreValue = scoreValue;
    }

    public String getAssetPath() { return assetPath; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getHitPoints() { return hitPoints; }
    public int getScoreValue() { return scoreValue; }
}