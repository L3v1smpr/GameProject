package puppy.code.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.MovementStrategy;
import puppy.code.config.GameConfig;

/** Alien configurable que delega movimiento y ataque en estrategias separadas. */
public final class Alien extends GameEntity {

    private static final float FIRING_FRAME_DURATION_SECONDS = 0.20f;

    private int hitPoints;
    private final MovementStrategy movement;
    private final AlienAttackStrategy attackStrategy;
    private float age;
    private float fireCooldownRemaining = GameConfig.ALIEN_FIRE_COOLDOWN_SECONDS;
    private float firingTimer;

    public Alien(
            float x,
            float y,
            float width,
            float height,
            int hitPoints,
            MovementStrategy movement,
            AlienAttackStrategy attackStrategy) {
        super(x, y, width, height);
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("La vida del alien debe ser positiva.");
        }
        if (movement == null) {
            throw new IllegalArgumentException("La estrategia de movimiento no puede ser null.");
        }
        if (!Float.isFinite(movement.speed()) || movement.speed() <= 0f) {
            throw new IllegalArgumentException("La velocidad del alien debe ser positiva y finita.");
        }
        if (attackStrategy == null) {
            throw new IllegalArgumentException("La estrategia de ataque no puede ser null.");
        }
        this.hitPoints = hitPoints;
        this.movement = movement;
        this.attackStrategy = attackStrategy;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public boolean isFiring() {
        return firingTimer > 0f;
    }

    /** Aplica un impacto y devuelve true solo cuando este impacto destruye al alien. */
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

    public void update(float delta, float worldWidth) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (!Float.isFinite(worldWidth) || worldWidth <= 0f) {
            throw new IllegalArgumentException("El ancho del mundo debe ser positivo y finito.");
        }

        age += delta;
        firingTimer = Math.max(0f, firingTimer - delta);

        Vector2 next = movement.nextPosition(getX(), getY(), age, delta);
        float maxX = Math.max(0f, worldWidth - getWidth());
        setPosition(Math.max(0f, Math.min(next.x, maxX)), next.y);
    }

    /**
     * Actualiza la cadencia del alien y devuelve las descargas que vencieron en este delta.
     * Activa el estado visual de disparo durante una fracción de segundo.
     */
    public List<AlienProjectile> fireDue(float delta, Ship target) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (target == null) {
            throw new IllegalArgumentException("El objetivo del alien no puede ser null.");
        }

        fireCooldownRemaining -= delta;
        List<AlienProjectile> projectiles = new ArrayList<>();
        while (fireCooldownRemaining <= 0f) {
            List<AlienProjectile> volley = attackStrategy.createVolley(this, target);
            if (!volley.isEmpty()) {
                projectiles.addAll(volley);
                firingTimer = FIRING_FRAME_DURATION_SECONDS;
            }
            fireCooldownRemaining += GameConfig.ALIEN_FIRE_COOLDOWN_SECONDS;
        }
        return Collections.unmodifiableList(projectiles);
    }

    /** Indica si el alien ya salió por completo por el borde inferior del mundo. */
    public boolean hasLeftWorld() {
        return getY() + getHeight() < 0f;
    }
}