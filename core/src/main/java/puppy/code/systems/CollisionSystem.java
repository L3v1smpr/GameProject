package puppy.code.systems;

import java.util.ArrayList;
import java.util.List;
import puppy.code.entities.Collidable;

public final class CollisionSystem {

    public boolean collides(Collidable first, Collidable second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Los objetos a comparar no pueden ser null.");
        }
        return first.bounds().overlaps(second.bounds());
    }

    public <T extends Collidable> List<T> collidingWith(Collidable subject, List<T> candidates) {
        if (subject == null || candidates == null) {
            throw new IllegalArgumentException("El sujeto y los candidatos no pueden ser null.");
        }

        List<T> hits = new ArrayList<>();
        for (T candidate : candidates) {
            if (collides(subject, candidate)) {
                hits.add(candidate);
            }
        }
        return hits;
    }
}