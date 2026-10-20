package puppy.code.entities;

import puppy.code.config.GameConfig;

/** Obstáculo descendente de un impacto; el mundo decide su generación y resolución de choques. */
public final class Meteorite extends GameEntity {

    private final MeteoriteSize size;
    private final float descentSpeed;
    private final int hitPoints;

    public Meteorite(
            float x,
            float y,
            float width,
            float height,
            float descentSpeed,
            MeteoriteSize size) {
        super(x, y, width, height);
        if (!Float.isFinite(descentSpeed) || descentSpeed <= 0f) {
            throw new IllegalArgumentException("La velocidad debe ser positiva y finita.");
        }
        if (size == null) {
            throw new IllegalArgumentException("El tamaño del meteorito no puede ser null.");
        }
        this.size = size;
        this.descentSpeed = descentSpeed;
        hitPoints = GameConfig.METEORITE_HIT_POINTS;
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        setPosition(getX(), getY() - descentSpeed * delta);
    }

    public MeteoriteSize getSize() {
        return size;
    }

    public float getDescentSpeed() {
        return descentSpeed;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public String getAssetPath() {
        return size.getAssetPath();
    }
}
