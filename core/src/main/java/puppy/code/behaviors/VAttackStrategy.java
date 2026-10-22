package puppy.code.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import puppy.code.config.GameConfig;
import puppy.code.entities.Alien;
import puppy.code.entities.AlienProjectile;
import puppy.code.entities.Ship;

/** Dispara dos proyectiles separados 45 grados a cada lado de la dirección hacia la nave. */
public final class VAttackStrategy implements AlienAttackStrategy {

    @Override
    public List<AlienProjectile> createVolley(Alien alien, Ship target) {
        LinearAttackStrategy.validate(alien, target);
        Vector2 aim = LinearAttackStrategy.directionTo(alien, target);
        List<AlienProjectile> volley = new ArrayList<>(2);
        volley.add(LinearAttackStrategy.projectileFrom(
                alien, aim.cpy().rotateDeg(-GameConfig.V_ATTACK_SPREAD_ANGLE_DEGREES)));
        volley.add(LinearAttackStrategy.projectileFrom(
                alien, aim.cpy().rotateDeg(GameConfig.V_ATTACK_SPREAD_ANGLE_DEGREES)));
        return Collections.unmodifiableList(volley);
    }
}
