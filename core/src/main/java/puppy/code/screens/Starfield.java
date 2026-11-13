package puppy.code.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;
import puppy.code.config.GameConfig;

/**
 * Fondo estelar en paralaje generado de forma procedural.
 * Administra tres capas de estrellas a distintas velocidades y profundidades
 * utilizando primitivas vectoriales sin requerir texturas externas.
 */
public final class Starfield {

    private static final int TOTAL_STARS = 75;

    private static final class Star {
        float x;
        float y;
        final float speed;
        final float size;
        final Color color;

        Star(float x, float y, float speed, float size, Color color) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
            this.color = color;
        }
    }

    private final List<Star> stars = new ArrayList<>(TOTAL_STARS);

    public Starfield() {
        // Capa 1: Estrellas lejanas (40 estrellas: lentas, pequeñas y azuladas tenues)
        for (int i = 0; i < 40; i++) {
            stars.add(new Star(
                    MathUtils.random(0f, GameConfig.WORLD_WIDTH),
                    MathUtils.random(0f, GameConfig.WORLD_HEIGHT),
                    MathUtils.random(25f, 40f),
                    1.5f,
                    new Color(0.35f, 0.42f, 0.58f, 1f)
            ));
        }

        // Capa 2: Estrellas intermedias (25 estrellas: velocidad media, cian claro)
        for (int i = 0; i < 25; i++) {
            stars.add(new Star(
                    MathUtils.random(0f, GameConfig.WORLD_WIDTH),
                    MathUtils.random(0f, GameConfig.WORLD_HEIGHT),
                    MathUtils.random(60f, 85f),
                    2.0f,
                    new Color(0.65f, 0.80f, 0.95f, 1f)
            ));
        }

        // Capa 3: Estrellas cercanas (10 estrellas: rápidas y blanco brillante)
        for (int i = 0; i < 10; i++) {
            stars.add(new Star(
                    MathUtils.random(0f, GameConfig.WORLD_WIDTH),
                    MathUtils.random(0f, GameConfig.WORLD_HEIGHT),
                    MathUtils.random(110f, 150f),
                    2.8f,
                    new Color(1f, 1f, 1f, 1f)
            ));
        }
    }

    /**
     * Actualiza el desplazamiento vertical de las estrellas y las dibuja sobre ShapeRenderer.
     */
    public void updateAndRender(float delta, ShapeRenderer shapes, OrthographicCamera camera) {
        if (!Float.isFinite(delta) || delta < 0f || shapes == null || camera == null) {
            return;
        }

        // Desplazamiento vertical en base a delta
        for (Star star : stars) {
            star.y -= star.speed * delta;
            if (star.y + star.size < 0f) {
                star.y = GameConfig.WORLD_HEIGHT;
                star.x = MathUtils.random(0f, GameConfig.WORLD_WIDTH - star.size);
            }
        }

        // Renderizado vectorial directo en la GPU
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeType.Filled);
        for (Star star : stars) {
            shapes.setColor(star.color);
            shapes.rect(star.x, star.y, star.size, star.size);
        }
        shapes.end();
    }
}