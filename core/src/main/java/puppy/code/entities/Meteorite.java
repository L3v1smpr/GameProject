package puppy.code.entities;

import com.badlogic.gdx.math.MathUtils;
import puppy.code.config.GameConfig;

/** Obstáculo descendente con resistencia configurable según tamaño. */
public final class Meteorite extends GameEntity {

    private final MeteoriteSize size;
    private final float descentSpeed;
    private int hitPoints;

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
        this.hitPoints = size.getHitPoints();
    }

    /** Crea un meteorito aleatorio por encima del área de juego. */
    public static Meteorite random(float worldWidth, float worldHeight) {
        if (!Float.isFinite(worldWidth) || worldWidth <= 0f
                || !Float.isFinite(worldHeight) || worldHeight <= 0f) {
            throw new IllegalArgumentException("Las dimensiones del mundo deben ser positivas y finitas.");
        }
        MeteoriteSize[] sizes = MeteoriteSize.values();
        MeteoriteSize size = sizes[MathUtils.random(sizes.length - 1)];
        float maxX = Math.max(0f, worldWidth - size.getWidth());
        float x = MathUtils.random(0f, maxX);
        return new Meteorite(
                x,
                worldHeight,
                size.getWidth(),
                size.getHeight(),
                GameConfig.METEORITE_FALL_SPEED,
                size);
    }

    /** Aplica un impacto y devuelve true únicamente cuando el impacto destruye al meteorito. */
    public boolean takeDamage() {
        if (!isActive()) {
            return false;
        }
        hitPoints--;
        if (hitPoints <= 0) {
            hitPoints = 0;
            deactivate();
            return true;
        }
        return false;
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

    public int getScoreValue() {
        return size.getScoreValue();
    }

    public String getAssetPath() {
        return size.getAssetPath();
    }
}