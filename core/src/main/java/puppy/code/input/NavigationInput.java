package puppy.code.input;

/** Expone las acciones de navegación de las pantallas sin revelar el dispositivo de entrada. */
public interface NavigationInput {

    /** Indica si el jugador confirmó la acción principal de la pantalla en este cuadro. */
    boolean confirmJustPressed();

    /** Indica si el jugador pidió volver atrás en este cuadro. */
    boolean backJustPressed();
}
