package puppy.code.systems;

import puppy.code.entities.Collidable;

/** Contrato geométrico que permite al mundo solicitar detección de colisiones. */
public interface CollisionDetector {

    boolean collides(Collidable first, Collidable second);
}
