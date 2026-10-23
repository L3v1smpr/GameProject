package puppy.code.input;

/** Expone las letras y la acción de borrado usadas para ingresar iniciales. */
public interface InitialsInput {

    /** Devuelve la letra A-Z recién presionada, o un espacio si no hubo ninguna. */
    char letterJustPressed();

    /** Indica si se presionó retroceso en este cuadro. */
    boolean deleteJustPressed();
}
