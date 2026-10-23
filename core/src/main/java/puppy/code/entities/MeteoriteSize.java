package puppy.code.entities;

import puppy.code.assets.GameAssets;

/** Apariencia disponible para los meteoritos; la puntuación corresponde al servicio futuro. */
public enum MeteoriteSize {
    SMALL(GameAssets.SMALL_METEORITE_TEXTURE, 28f, 28f, 25),
    MEDIUM(GameAssets.MEDIUM_METEORITE_TEXTURE, 42f, 42f, 50),
    LARGE(GameAssets.LARGE_METEORITE_TEXTURE, 64f, 64f, 75);

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
