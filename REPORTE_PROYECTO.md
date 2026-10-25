# Reporte del proyecto: Space Navigation

**Entrega:** videojuego LibGDX de escritorio

**Rama revisada:** `feature/overhaul-assets`
**Versión de Java:** 11

## Alcance

Presentamos un arcade 2D para un jugador. Pilotamos una nave por tres sectores de una ruta espacial hostil; la actividad alienígena aumenta y debemos despejar la cuota de cada sector para avanzar. La partida termina con Victoria al completar el tercero o con Game Over si perdemos las tres vidas.

El código legado permanece en `core/src/main/java/puppy/code/prototype` para conservar el punto de comparación. La aplicación activa inicia en `puppy.code.app.SpaceNavigation` y usa las pantallas del paquete `screens`.

## GM-1 - Juego desde la perspectiva del jugador

Empezamos con tres vidas. Movemos la nave con WASD, disparamos con I y usamos O para hacer dash. El HUD muestra el sector, la cuota y el progreso. Enfrentamos aliens de oleada, centinelas, bombarderos y meteoritos. Al completar los sectores uno y dos, aparece «Nivel Superado» y esperamos una tecla o un clic; la cuota del tercero conduce a Victoria.

Los aliens de oleada tienen 1, 2 o 3 puntos de vida según el sector. El movimiento lineal corresponde al primero y el zigzag a los dos siguientes. Los centinelas mantienen su posición y los bombarderos patrullan horizontalmente. Sus estrategias de ataque también varían. Los meteoritos no cuentan para la cuota. Ganamos 100 puntos por alien y 25, 50 o 100 por meteorito chico, mediano o grande.

Si perdemos una vida, reaparecemos en el punto inicial y recibimos 1,5 segundos de invulnerabilidad. El dash dura 0,15 segundos, se recarga en 3 segundos y concede 0,5 segundos adicionales de invulnerabilidad al terminar. La velocidad actual de la nave es 390 px/s y el multiplicador del dash es 3; dejamos esos valores para validación de balance.

Cuando un resultado entra al Top 3, ingresamos tres iniciales con A-Z, corregimos con Retroceso y confirmamos con Enter. El menú y las pantallas finales muestran los puntajes locales. Escape abre la pausa durante la partida; desde ella podemos reanudar, ajustar los volúmenes o regresar al menú. Para pruebas, la secuencia ↑ ↑ ↓ ↓ ← → ← → I O durante la pausa revela el menú secreto de depuración, donde podemos alternar el modo dios (invulnerabilidad).

Los bocetos del menú, la partida y las pantallas finales están en `Planificacion/Bocetos/B1.jpeg` a `B4.jpeg`. El README resume los controles y la ejecución.

**Evidencia:** `README.md`; `Planificacion/Bocetos/B1.jpeg-B4.jpeg`; `core/src/main/java/puppy/code/screens/GameScreen.java`; `core/src/main/java/puppy/code/world/GameWorld.java`.

![Boceto del menú e interfaz](Planificacion/Bocetos/B1.jpeg)

![Boceto de la partida](Planificacion/Bocetos/B2.jpeg)

![Boceto de Game Over](Planificacion/Bocetos/B3.jpeg)

![Boceto de Victoria](Planificacion/Bocetos/B4.jpeg)

## GM-2 - Juego base y cambios

El prototipo inicial está en el paquete `puppy.code.prototype`. Allí, `PantallaJuego` concentra el ciclo de la partida y mantiene las colecciones de naves, obstáculos y proyectiles. La aplicación actual separa ese código legado del flujo activo.

En la versión implementada, `GameWorld` coordina la simulación, las vidas, el puntaje, las colisiones y el avance de sector. `GameScreen` procesa las acciones de pantalla y dibuja un `WorldSnapshot`; las entradas del teclado se traducen mediante contratos. `PlayerWeapon` mantiene la cadencia de disparo, `CollisionSystem` resuelve intersecciones y `LevelDirector` prepara las oleadas.

También incorporamos tres sectores, enemigos con estrategias de movimiento y ataque, centinelas, bombarderos, meteoritos de varios tamaños, dash, pausa, efectos visuales, pantallas de victoria y Game Over, y un Top 3 local. Estas son diferencias respecto del esqueleto, no características que atribuimos al prototipo.

**Evidencia:** `core/src/main/java/puppy/code/prototype/PantallaJuego.java`; `core/src/main/java/puppy/code/app/SpaceNavigation.java`; `core/src/main/java/puppy/code/world/GameWorld.java`; `core/src/main/java/puppy/code/screens/GameScreen.java`.

## GM-3 - UML y código Java

El diagrama actualizado está en [UML_INICIAL.md](UML_INICIAL.md). Incluye las clases propias, los tipos LibGDX que usa la aplicación y las relaciones activas de Singleton, Strategy, Template Method y Builder.

El código está organizado en `app`, `assets`, `behaviors`, `config`, `entities`, `factories`, `input`, `screens`, `systems` y `world`. El lanzador de escritorio está en `lwjgl3/src/main/java/puppy/code/lwjgl3/Lwjgl3Launcher.java`.

**Evidencia:** `UML_INICIAL.md`; fuentes Java bajo `core/src/main/java/puppy/code/`; lanzador en `lwjgl3/src/main/java/puppy/code/lwjgl3/Lwjgl3Launcher.java`.

## GM-4 - Clase abstracta e interfaz

`GameEntity` es abstracta y constituye la base de `Ship`, `Alien`, `Meteorite`, `PlayerProjectile` y `AlienProjectile`. `AbstractGameScreen` es otra clase abstracta: mantiene el orden de renderizado para `MenuScreen`, `GameScreen`, `GameOverScreen` y `VictoryScreen`.

`MovementStrategy` tiene cuatro implementaciones: `LinearMovementStrategy`, `ZigZagMovementStrategy`, `StaticMovementStrategy` y `HorizontalMovementStrategy`. `AlienAttackStrategy` tiene cinco: `LinearAttackStrategy`, `VAttackStrategy`, `SpreadAttackStrategy`, `AlternatingAttackStrategy` y `BurstAttackStrategy`. Ambos contratos se usan en las partidas. Además, `Collidable` define los límites geométricos de las entidades y `WorldAudioListener` comunica eventos del mundo a la pantalla.

**Evidencia:** `entities/GameEntity.java`, `entities/Collidable.java`, `behaviors/MovementStrategy.java`, `behaviors/AlienAttackStrategy.java`, `screens/AbstractGameScreen.java` y las pantallas concretas bajo `core/src/main/java/puppy/code/`.

## GM-5 - Encapsulamiento, GRASP y SOLID

### Encapsulamiento

Mantenemos privado el estado mutable de las entidades y del mundo. `GameWorld` no entrega sus colecciones: crea un `WorldSnapshot` con vistas de entidades y listas de solo lectura. `GameAssets` entrega los frames de la nave mediante un acceso por índice, sin exponer la matriz del spritesheet. La configuración general se concentra en constantes de `GameConfig`.

### GRASP

- **Controller e Information Expert:** `GameWorld` recibe las acciones de juego y coordina sus consecuencias; cada entidad administra su propio movimiento, daño y estado.
- **Creator:** `LevelDirector` reúne la configuración de las oleadas y crea aliens con `AlienBuilder`.
- **Pure Fabrication:** `CollisionSystem`, `PlayerWeapon`, `ScoreRepository` y `GameAssets` aíslan responsabilidades que no pertenecen a una entidad del dominio.
- **Low Coupling e Indirection:** el mundo solicita colisiones mediante `CollisionDetector` y creación de enemigos mediante `AlienFactory`. Las pantallas reciben `ScreenNavigator`, `NavigationInput` y `ScoreStore`.

### SOLID

- **SRP:** `GameWorld` mantiene las reglas de la simulación; `CollisionSystem` detecta intersecciones; `PlayerWeapon` regula el disparo; el repositorio guarda puntajes; las pantallas presentan el estado.
- **OCP:** agregamos variaciones de movimiento y ataque implementando estrategias sin añadir condicionales por cada trayectoria en `Alien`.
- **LSP:** las entidades concretas derivadas de `GameEntity` conservan el contrato geométrico `Collidable`.
- **ISP:** separamos los contratos de entrada, navegación, puntajes, colisiones, creación de aliens y eventos de audio según las operaciones que necesita cada colaborador.
- **DIP:** `GameWorld` depende de `AlienFactory` y `CollisionDetector`; las pantallas dependen de `ScoreStore` y `ScreenNavigator`. `SpaceNavigation` reúne las implementaciones concretas al iniciar la aplicación.

**Evidencia:** `world/GameWorld.java`, `world/WorldSnapshot.java`, `factories/AlienFactory.java`, `systems/CollisionDetector.java`, `systems/ScoreStore.java` y `app/SpaceNavigation.java`.

## GM-6 - Singleton: GameAssets

**Problema:** si cada pantalla creara su propio gestor, duplicaríamos recursos y sus ciclos de liberación podrían interferir entre sí.

**Contexto:** las pantallas comparten texturas, audio y objetos de dibujo durante toda la aplicación.

**Solución:** `SpaceNavigation` obtiene una sola instancia de `GameAssets` y la inyecta en las pantallas. El constructor es privado, `getInstance()` entrega la instancia y `dispose()` libera los recursos al cerrar la aplicación. Las texturas, sonidos y músicas se organizan en mapas propios.

El UML del patrón está en [UML_INICIAL.md, GM-6](UML_INICIAL.md#gm-6-singleton).

**Evidencia:** `core/src/main/java/puppy/code/assets/GameAssets.java` y `core/src/main/java/puppy/code/app/SpaceNavigation.java`.

## GM-7 - Strategy: movimiento y ataque

**Problema:** las trayectorias y descargas varían entre enemigos y sectores; incluir cada variante dentro de `Alien` mezclaría esos algoritmos con el estado común del enemigo.

**Contexto:** el mismo mundo procesa aliens de oleada, centinelas y bombarderos.

**Solución:** `Alien` recibe las interfaces `MovementStrategy` y `AlienAttackStrategy`. `LevelDirector` selecciona las implementaciones para cada configuración, sin añadir una rama nueva a la actualización común del alien.

Las variantes actuales incluyen movimiento lineal, zigzag, estacionario y patrulla horizontal; los ataques incluyen fuego lineal, formación en V, abanico, disparo alternado y ráfagas.

El UML de las dos familias de estrategias está en [UML_INICIAL.md, GM-7](UML_INICIAL.md#gm-7-strategy).

**Evidencia:** `core/src/main/java/puppy/code/behaviors/` y `core/src/main/java/puppy/code/factories/LevelDirector.java`.

## GM-8 - Template Method: AbstractGameScreen

**Problema:** el menú, la partida y las pantallas finales necesitan el mismo orden de trabajo por cuadro, aunque el contenido que presentan sea distinto.

**Contexto:** las cuatro pantallas activas heredan de `AbstractGameScreen`.

**Solución:** `AbstractGameScreen.render(float delta)` fija la secuencia de limpiar, procesar entrada, actualizar y dibujar; cada pantalla concreta implementa los pasos que varían.

El UML del patrón está en [UML_INICIAL.md, GM-8](UML_INICIAL.md#gm-8-template-method).

**Evidencia:** `core/src/main/java/puppy/code/screens/AbstractGameScreen.java`, `GameScreen.java`, `MenuScreen.java`, `GameOverScreen.java` y `VictoryScreen.java`.

## GM-9 - Builder: AlienBuilder y LevelDirector

**Problema:** un alien combina posición, tamaño, vida, movimiento y ataque; una configuración incompleta o inválida debe rechazarse antes de entrar al mundo.

**Contexto:** los sectores y arquetipos usan combinaciones distintas de esos valores.

**Solución:** `AlienBuilder` permite configurar los valores por pasos y los valida antes de construir. `LevelDirector` selecciona la configuración de cada sector y tipo de enemigo.

El UML del patrón está en [UML_INICIAL.md, GM-9](UML_INICIAL.md#gm-9-builder).

**Evidencia:** `core/src/main/java/puppy/code/factories/AlienBuilder.java` y `LevelDirector.java`.

## GM-10 - GitHub y commits

La rama `feature/overhaul-assets` tiene más de seis commits. En la comparación con `feature/rama-Alex`, GitHub registra 48 commits posteriores al punto común `2b3fcb8`. La entrega debe conservar el enlace al repositorio y la rama usada para evaluar el código.

**Evidencia:** [rama `feature/overhaul-assets`](https://github.com/L3v1smpr/GameProject/tree/feature/overhaul-assets) y [comparación con `feature/rama-Alex`](https://github.com/L3v1smpr/GameProject/compare/feature/rama-Alex...feature/overhaul-assets).

## Persistencia y recursos

El Top 3 se guarda con LibGDX Preferences en el equipo donde se ejecuta el juego. El almacenamiento sigue siendo local y no usa SQLite.

Los gráficos y sonidos se cargan desde `assets/player`, `assets/aliens`, `assets/obstacles`, `assets/ui` y `assets/audio`. No tenemos información verificable de autoría y licencia para algunos recursos; lo dejamos señalado para que el equipo complete esa atribución.

## Instalación y ejecución

Requerimos Oracle JDK 11. En Windows ejecutamos `gradlew.bat :lwjgl3:run`; en Linux o macOS, `./gradlew :lwjgl3:run`. Para abrirlo en Eclipse, importamos la raíz como proyecto Gradle y ejecutamos `lwjgl3:run`. El README contiene los pasos completos.

## Límites conocidos

El dash conserva su multiplicador actual de 3 y necesita validación manual de balance. El paquete `prototype` sigue dentro del módulo `core` como referencia del juego base. La fuente y licencia de algunos assets siguen sin confirmarse. Estas limitaciones no cambian los controles ni las reglas de la partida descritas arriba.

En esta revisión ejecutamos `gradlew.bat test compileJava`: compilaron `core` y `lwjgl3`. Gradle informó `NO-SOURCE` para las tareas de prueba porque el proyecto no contiene pruebas automatizadas.
