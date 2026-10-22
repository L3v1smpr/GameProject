package puppy.code.entities;

import puppy.code.assets.GameAssets;

/** Apariencia disponible para los meteoritos; la puntuación corresponde al servicio futuro. */
public enum MeteoriteSize {
    SMALL(GameAssets.SMALL_METEORITE_TEXTURE),
    MEDIUM(GameAssets.MEDIUM_METEORITE_TEXTURE),
    LARGE(GameAssets.LARGE_METEORITE_TEXTURE);

    private final String assetPath;

    MeteoriteSize(String assetPath) {
        this.assetPath = assetPath;
    }

    public String getAssetPath() {
        return assetPath;
    }
}
