package puppy.code.entities;

import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;

/** Proyectil del jugador; las colisiones y su retiro del mundo se coordinan fuera de la entidad. */
public final class PlayerProjectile extends GameEntity {

    private final float speed;

    public PlayerProjectile(float x, float y) {
        super(x, y, GameConfig.PROJECTILE_WIDTH, GameConfig.PROJECTILE_HEIGHT);
        speed = GameConfig.PLAYER_PROJECTILE_SPEED;
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        setPosition(getX(), getY() + speed * delta);
    }

    public float getSpeed() {
        return speed;
    }
}
