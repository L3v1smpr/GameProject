package puppy.code.entities;

import puppy.code.assets.GameAssets;
import puppy.code.config.GameConfig;

/** Apariencia disponible para los meteoritos; la puntuación corresponde al servicio futuro. */
public enum MeteoriteSize {
    SMALL(GameAssets.SMALL_METEORITE_TEXTURE,
            GameConfig.SMALL_METEORITE_WIDTH, GameConfig.SMALL_METEORITE_HEIGHT,
            GameConfig.SMALL_METEORITE_SCORE),
    MEDIUM(GameAssets.MEDIUM_METEORITE_TEXTURE,
            GameConfig.MEDIUM_METEORITE_WIDTH, GameConfig.MEDIUM_METEORITE_HEIGHT,
            GameConfig.MEDIUM_METEORITE_SCORE),
    LARGE(GameAssets.LARGE_METEORITE_TEXTURE,
            GameConfig.LARGE_METEORITE_WIDTH, GameConfig.LARGE_METEORITE_HEIGHT,
            GameConfig.LARGE_METEORITE_SCORE);

    private final String assetPath;
    private final float width;
    private final float height;
    private final int scoreValue;

    MeteoriteSize(String assetPath, float width, float height, int scoreValue) {
        this.assetPath = assetPath;
        this.width = width;
        this.height = height;
        this.scoreValue = scoreValue;
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
}
