# Space Navigation

Videojuego 2D de escritorio hecho en Java y LibGDX. Construimos una base común para seguir
implementando por etapas. La versión actual permite abrir el menú, iniciar la partida y mover la nave;
las funciones pendientes están separadas para no confundir estructura con juego terminado.

## Requisitos y ejecución

- JDK 11.
- Windows: `gradlew.bat lwjgl3:run`.
- Linux/macOS: `./gradlew lwjgl3:run`.
- Verificación de compilación y pruebas existentes: `./gradlew test compileJava`.

El módulo `lwjgl3` conserva las tareas normales de ejecución, JAR y `dist`. Retiramos la configuración
opcional de empaquetado nativo `construo` porque requería ejecutar Gradle con Java 17, contrario al
entorno Java 11 solicitado.

## Importar el proyecto

- **Eclipse:** importa la carpeta del proyecto como **Existing Gradle Project** y usa el wrapper incluido.
- **NetBeans:** abre la carpeta raíz como proyecto Gradle y deja que NetBeans sincronice los módulos.

La raíz contiene `settings.gradle`, `build.gradle`, `gradlew`, `gradlew.bat` y el directorio `gradle/`.

## Controles disponibles y preparados

| Tecla | Estado en este corte |
| --- | --- |
| `W/A/S/D` | Mueve la nave, con dirección diagonal normalizada. |
| `ENTER` | Inicia una partida desde el menú. |
| `ESC` | Vuelve al menú durante la partida. |
| `I` mantenida | La entrada ya se detecta y `PlayerWeapon` aplica una cadencia de 0,25 s; falta conectar el arma al mundo y resolver impactos. |
| `O` | La entrada ya se detecta; el dash queda pendiente junto con su invulnerabilidad y resolución de daño. |

## Base implementada

- `GameAssets` es el Singleton de recursos compartidos. Las rutas existentes están centralizadas como
  constantes y las texturas, sonidos y música se cargan bajo demanda mediante `AssetManager`.
- `AbstractGameScreen` fija `clearScreen → handleInput → update → draw` con un `render(delta)` final.
- `AlienBuilder` valida posición, vida, movimiento, velocidad y ataque. `LevelDirector` prepara los
  arquetipos de los niveles 1 a 3.
- `Alien` delega movimiento y ataque en estrategias independientes. Los niveles tienen 1, 2 y 3 HP;
  el primero se desplaza en línea y dispara una bala dirigida; los otros dos usan zigzag y una descarga
  en V de dos balas a 45° por lado.
- `PlayerProjectile`, `AlienProjectile`, `Meteorite` y `PlayerWeapon` son modelos listos para integrarse;
  todavía no participan en el ciclo de `GameWorld`.
- `GameEntity` conserva el estado común y entrega límites defensivos mediante `Collidable`.

## Trabajo reservado para la siguiente IA

Dejamos pendientes las secciones de colisiones/vidas/respawn e invulnerabilidad, fases/progresión y
puntaje/leaderboard, junto con lo que depende de ellas: integrar proyectiles y meteoritos en el mundo,
resolver impactos y HP, limpiar solo proyectiles al perder vida, completar dash, cambiar de nivel y
pantalla, guardar el Top 3 e ingresar el nombre de tres caracteres. Las cuotas 15/30/45 ya están en la
especificación, pero aún no las conectamos a la partida.

## Recursos

Usamos únicamente las rutas ya presentes en `assets/`:

| Tipo | Constante en `GameAssets` | Archivo |
| --- | --- | --- |
| Imagen | `PLAYER_SHIP_TEXTURE` | `MainShip3.png` |
| Imagen | `PLAYER_PROJECTILE_TEXTURE` | `Rocket2.png` |
| Imagen | `SMALL_METEORITE_TEXTURE` | `aGreySmall.png` |
| Imagen | `MEDIUM_METEORITE_TEXTURE` | `aGreyMedium4.png` |
| Imagen | `LARGE_METEORITE_TEXTURE` | `aGreyLarge.png` |
| Audio | `EXPLOSION_SOUND` | `explosion.ogg` |
| Audio | `HURT_SOUND` | `hurt.ogg` |
| Audio | `POP_SOUND` | `pop-sound.mp3` |
| Música | `BACKGROUND_MUSIC` | `piano-loops.wav` |

No modificamos la carpeta `assets/` ni el paquete histórico `puppy.code.prototype`.

Consulta [UML_INICIAL.md](UML_INICIAL.md) para el diagrama del corte y
[`Planificacion/`](Planificacion/) para las decisiones de arquitectura.
