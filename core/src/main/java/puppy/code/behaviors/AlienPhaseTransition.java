package puppy.code.behaviors;

/**
 * Contrato para definir las estrategias que adoptará un alien
 * al entrar en su segunda fase durante el combate.
 */
public interface AlienPhaseTransition {

    /**
     * Genera la nueva estrategia de movimiento a partir del estado actual del alien.
     *
     * @param currentY posición vertical actual del alien
     * @param currentSpeed velocidad actual del alien
     * @param alienWidth ancho del alien
     * @return nueva estrategia de movimiento
     */
    MovementStrategy nextMovement(float currentY, float currentSpeed, float alienWidth);

    /**
     * Genera la nueva estrategia de ataque para la segunda fase.
     *
     * @return nueva estrategia de ataque
     */
    AlienAttackStrategy nextAttack();
}
