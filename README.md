# Space Navigation

Videojuego 2D de escritorio en Java 11 y LibGDX. El repositorio contiene una base arquitectónica
inicial para desarrollar el juego por etapas; todavía no implementa la partida completa.

## Requisitos y ejecución

- JDK 17 o superior para ejecutar Gradle (el plugin `construo` necesita ese runtime).
- El código de `core` y `lwjgl3` se compila con `--release 11`.
- Windows: `gradlew.bat lwjgl3:run`.
- Linux/macOS: `./gradlew lwjgl3:run`.

La aplicación abre el menú. Presiona **Enter** para iniciar y **W/A/S/D** para mover la nave. **Esc**
vuelve al menú. La partida de prueba muestra figuras temporales, vidas y el estado Game Over. La pauta
académica menciona JDK 11 u 8; confirmar con la cátedra si ejecutar Gradle con JDK 17 y generar clases
compatibles con Java 11 satisface ese requisito. JDK 11 puede compilar `core` directamente, pero no
ejecuta Gradle con la configuración actual de `construo`.

## Estructura

- `core`: reglas y modelo compartidos, entrada abstracta, patrones y pantallas.
- `lwjgl3`: lanzador de escritorio.
- `Planificacion/`: alcance, responsabilidades, diseño objetivo y plan de trabajo.
- [UML_INICIAL.md](UML_INICIAL.md): diagrama del corte implementado, separado de las funciones pendientes.

## Patrones de diseño de la base

| Requisito | Patrón | Código principal |
| --- | --- | --- |
| GM-6 | Singleton | `GameAssets` |
| GM-7 | Strategy | `MovementStrategy`, `LinearMovement`, `ZigZagMovement`, `Alien` |
| GM-8 | Template Method | `AbstractGameScreen`, `MenuScreen`, `GameScreen` |
| GM-9 | Builder | `AlienBuilder`, `LevelDirector` |

La clase abstracta `AbstractGameScreen` y las implementaciones de `MovementStrategy` aportan además
la base de GM-4. El mundo conserva sus entidades y entrega a la pantalla un `WorldSnapshot` de solo
lectura. En el UML se documenta cómo se aplican encapsulamiento, GRASP y SOLID.

## Alcance actual

Implementado: estructura de pantallas, adaptadores de entrada, movimiento de la nave y aliens,
construcción de oleadas, colisión física nave-alien, vidas, respawn e invulnerabilidad temporal.

Pendiente: proyectiles, disparos, puntaje, cuotas y avance de niveles, dash, meteoritos, victoria,
pantallas finales, récords persistentes y documentación final del reporte. Los tamaños de oleada y la
selección de movimiento por nivel siguen siendo provisionales. Consulta `Planificacion/` antes de
implementar decisiones que todavía no están acordadas.

## Recursos

Las figuras de la nave y los aliens son temporales, dibujadas con formas de LibGDX. Antes de usar los
recursos del directorio `assets/` en la versión final, se debe confirmar y registrar su procedencia y
licencia.
