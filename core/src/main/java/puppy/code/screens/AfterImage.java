package puppy.code.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Representa una silueta temporal o "after-image" para el efecto visual del Dash.
 * Encapsula la posición, dimensiones, recorte de textura y el decaimiento
 * de opacidad (alpha) a lo largo del tiempo de vida.
 */
public final class AfterImage {

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final TextureRegion region;
    private final float maxLifeTime;
    private float lifeTime;
    private float alpha;

    public AfterImage(float x, float y, float width, float height, TextureRegion region, float lifeTime) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.region = region;
        this.maxLifeTime = lifeTime;
        this.lifeTime = lifeTime;
        this.alpha = 0.8f;
    }

    /**
     * Actualiza el temporizador de la estela y reduce progresivamente su opacidad.
     * @param delta Tiempo en segundos transcurrido desde el último fotograma.
     * @return true si la silueta ha expirado y debe eliminarse de la lista activa.
     */
    public boolean update(float delta) {
        lifeTime -= delta;
        alpha = Math.max(0f, (lifeTime / maxLifeTime) * 0.8f);
        return lifeTime <= 0f;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public float getAlpha() {
        return alpha;
    }
}