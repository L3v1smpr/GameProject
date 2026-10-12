package puppy.code.world;

import java.util.ArrayList;
import java.util.List;
import puppy.code.entities.Alien;
import puppy.code.entities.Ship;
import puppy.code.input.PlayerInput;

/** Es dueño de las entidades y coordina sus actualizaciones. */
public final class GameWorld {

    public static final float WIDTH = 1200f;
    public static final float HEIGHT = 800f;

    private final Ship ship;
    private final List<Alien> aliens;

    public GameWorld() {
        ship = new Ship(WIDTH / 2f - 20f, 55f, 40f, 36f, 280f);
        List<Alien> initialAliens = new ArrayList<>();
        initialAliens.add(new Alien(WIDTH / 2f - 20f, HEIGHT - 100f, 40f, 32f, 75f));
        aliens = initialAliens;
    }

    public void update(float delta, PlayerInput input) {
        if (input == null) {
            throw new IllegalArgumentException("PlayerInput no puede ser null.");
        }

        ship.update(delta, input.movementDirection(), WIDTH, HEIGHT);
        for (Alien alien : aliens) {
            alien.update(delta, WIDTH);
        }
    }

    public WorldSnapshot snapshot() {
        return new WorldSnapshot(ship, aliens);
    }
}
