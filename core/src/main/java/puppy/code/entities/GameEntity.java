package puppy.code.entities;

import com.badlogic.gdx.math.Rectangle;

/** Mantiene el estado privado común de los objetos que ocupan espacio en el mundo. */
public abstract class GameEntity implements Collidable {

    private static final float HURT_DURATION_SECONDS = 0.12f;

    private float x;
    private float y;
    private final float width;
    private final float height;
    private boolean active = true;
    private float hurtTimer;

    protected GameEntity(float x, float y, float width, float height) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            throw new IllegalArgumentException("La posición debe ser finita.");
        }
        if (!Float.isFinite(width) || !Float.isFinite(height) || width <= 0f || height <= 0f) {
            throw new IllegalArgumentException("El ancho y el alto deben ser positivos y finitos.");
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final float getX() {
        return x;
    }

    public final float getY() {
        return y;
    }

    public final float getWidth() {
        return width;
    }

    public final float getHeight() {
        return height;
    }

    public final boolean isActive() {
        return active;
    }

    public final void deactivate() {
        active = false;
    }

    /** Indica si la entidad se encuentra en el fotograma de retroalimentación de daño. */
    public final boolean isHurt() {
        return hurtTimer > 0f;
    }

    /** Activa el destello visual de impacto durante una fracción de segundo. */
    protected final void triggerHurt() {
        hurtTimer = HURT_DURATION_SECONDS;
    }

    /** Actualiza la caducidad del destello de daño en cada cuadro de simulación. */
    protected final void updateHurtTimer(float delta) {
        if (hurtTimer > 0f) {
            hurtTimer = Math.max(0f, hurtTimer - delta);
        }
    }

    /** 
     * Método hook polimórfico: procesa un impacto y devuelve true si destruye a la entidad.
     * Las subclases destructibles deben sobrescribirlo.
     */
    public boolean takeDamage() {
        return false;
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle(x, y, width, height);
    }

    protected final void setPosition(float x, float y) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            throw new IllegalArgumentException("La posición debe ser finita.");
        }
        this.x = x;
        this.y = y;
    }

    protected final void setPositionInside(float x, float y, float worldWidth, float worldHeight) {
        float maxX = Math.max(0f, worldWidth - width);
        float maxY = Math.max(0f, worldHeight - height);
        setPosition(clamp(x, 0f, maxX), clamp(y, 0f, maxY));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}