package puppy.code.systems;

import java.util.Optional;
import puppy.code.config.GameConfig;
import puppy.code.entities.PlayerProjectile;
import puppy.code.entities.Ship;

/** Aplica la cadencia del disparo mantenido y crea como máximo un proyectil por actualización. */
public final class PlayerWeapon {

    private float cooldownRemaining;

    public Optional<PlayerProjectile> update(float delta, boolean firePressed, Ship ship) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (ship == null) {
            throw new IllegalArgumentException("La nave no puede ser null.");
        }

        cooldownRemaining = Math.max(0f, cooldownRemaining - delta);
        if (!firePressed || cooldownRemaining > 0f) {
            return Optional.empty();
        }

        cooldownRemaining = GameConfig.PLAYER_FIRE_COOLDOWN_SECONDS;
        float spawnX = ship.getX() + ship.getWidth() / 2f - GameConfig.PROJECTILE_WIDTH / 2f;
        float spawnY = ship.getY() + ship.getHeight();
        return Optional.of(new PlayerProjectile(spawnX, spawnY));
    }
}
