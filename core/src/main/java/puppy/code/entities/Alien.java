package puppy.code.entities;

/** Alien de prueba con un movimiento horizontal determinista. */
public final class Alien extends GameEntity {

    private float horizontalSpeed;

    public Alien(float x, float y, float width, float height, float horizontalSpeed) {
        super(x, y, width, height);
        if (!Float.isFinite(horizontalSpeed) || horizontalSpeed == 0f) {
            throw new IllegalArgumentException("La velocidad horizontal debe ser finita y distinta de cero.");
        }
        this.horizontalSpeed = horizontalSpeed;
    }

    public void update(float delta, float worldWidth) {
        if (!Float.isFinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("delta debe ser finito y no negativo.");
        }
        if (!Float.isFinite(worldWidth) || worldWidth <= 0f) {
            throw new IllegalArgumentException("El ancho del mundo debe ser positivo y finito.");
        }

        float nextX = getX() + horizontalSpeed * delta;
        float maxX = Math.max(0f, worldWidth - getWidth());
        if (nextX < 0f) {
            nextX = 0f;
            horizontalSpeed = Math.abs(horizontalSpeed);
        } else if (nextX > maxX) {
            nextX = maxX;
            horizontalSpeed = -Math.abs(horizontalSpeed);
        }
        setPosition(nextX, getY());
    }
}
