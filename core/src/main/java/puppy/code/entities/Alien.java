package puppy.code.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import puppy.code.behaviors.AlienAttackStrategy;
import puppy.code.behaviors.AlienPhaseTransition;
import puppy.code.behaviors.MovementStrategy;

/** Alien configurable que encapsula y delega movimiento, ataque y transición de fase. */
public final class Alien extends GameEntity {

    private static final float FIRING_FRAME_DURATION_SECONDS = 0.20f;

    private int hitPoints;
    private final int maxHitPoints;
    private MovementStrategy movement;
    private AlienAttackStrategy attackStrategy;
    private final AlienPhaseTransition phaseTransition;
    private float age;
    private float fireCooldownRemaining;
    private float firingTimer;
    private boolean phaseChanged;

    public Alien(
            float x,
            float y,
            float width,
            float height,
            int hitPoints,
            MovementStrategy movement,
            AlienAttackStrategy attackStrategy) {
        this(x, y, width, height, hitPoints, movement, attackStrategy, null);
    }

    public Alien(
            float x,
            float y,
            float width,
            float height,
            int hitPoints,
            MovementStrategy movement,
            AlienAttackStrategy attackStrategy,
            AlienPhaseTransition phaseTransition) {
        super(x, y, width, height);
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("La vida del alien debe ser positiva.");
        }
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
        this.phaseTransition = phaseTransition;
        setMovementStrategy(movement);
        setAttackStrategy(attackStrategy);
        // Tiempo de espera inicial aleatorio para que el primer tiro ocurra poco después de entrar
        this.fireCooldownRemaining = MathUtils.random(0.35f, 0.70f);
    }

    private void setMovementStrategy(MovementStrategy movement) {
        if (movement == null) {
            throw new IllegalArgumentException("La estrategia de movimiento no puede ser null.");
        }
        if (!Float.isFinite(movement.speed()) || movement.speed() < 0f) {
            throw new IllegalArgumentException("La velocidad del alien no puede ser negativa.");
        }
        this.movement = movement;
    }

    private void setAttackStrategy(AlienAttackStrategy attackStrategy) {
        if (attackStrategy == null) {
            throw new IllegalArgumentException("La estrategia de ataque no puede ser null.");
        }
        this.attackStrategy = attackStrategy;
        // Evita heredar esperas largas de la estrategia anterior
        this.fireCooldownRemaining = Math.min(
                this.fireCooldownRemaining,
                attackStrategy.nextCooldownSeconds());
    }

    private void applyPhaseTransition() {
        if (phaseTransition == null) {
            return;
        }
        setMovementStrategy(phaseTransition.nextMovement(getY(), movement.speed(), getWidth()));
        setAttackStrategy(phaseTransition.nextAttack());
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public boolean isFiring() {
        return firingTimer > 0f;
    }

    public boolean isStatic() {
        return movement.speed() == 0f;
    }

    public boolean isBomber() {
        return movement.isBomber();
    }

    @Override
    public boolean takeDamage() {
        if (!isActive()) {
            return false;
        }
        hitPoints--;
        triggerHurt();
        if (hitPoints <= 0) {
            hitPoints = 0;
            deactivate();
            return true;
        }

        // Cambio de estrategia en tiempo de ejecución al bajar a la mitad de vida
        if (!phaseChanged && phaseTransition != null && maxHitPoints > 1 && hitPoints <= maxHitPoints / 2) {
            phaseChanged = true;
            applyPhaseTransition();
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
        updateHurtTimer(delta);

        Vector2 next = movement.nextPosition(getX(), getY(), age, delta);
        float maxX = Math.max(0f, worldWidth - getWidth());
        setPosition(Math.max(0f, Math.min(next.x, maxX)), next.y);
    }

    public List<AlienProjectile> fireDue(float delta, Ship target, float worldHeight) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (target == null) {
            throw new IllegalArgumentException("El objetivo del alien no puede ser null.");
        }
        if (!Float.isFinite(worldHeight) || worldHeight <= 0f) {
            throw new IllegalArgumentException("La altura del mundo debe ser positiva y finita.");
        }

        // Mientras entra desde fuera de pantalla, precarga sus cañones sin acumular disparos múltiples
        if (getY() > worldHeight - getHeight()) {
            fireCooldownRemaining = Math.max(0.15f, fireCooldownRemaining - delta);
            return Collections.emptyList();
        }

        fireCooldownRemaining -= delta;
        List<AlienProjectile> projectiles = new ArrayList<>();
        while (fireCooldownRemaining <= 0f) {
            List<AlienProjectile> volley = attackStrategy.createVolley(this, target);
            if (!volley.isEmpty()) {
                projectiles.addAll(volley);
                firingTimer = FIRING_FRAME_DURATION_SECONDS;
            }
            // Consulta polimórfica a la estrategia del alien
            fireCooldownRemaining += attackStrategy.nextCooldownSeconds();
        }
        return Collections.unmodifiableList(projectiles);
    }

    public boolean hasLeftWorld() {
        return getY() + getHeight() < 0f;
    }
}
