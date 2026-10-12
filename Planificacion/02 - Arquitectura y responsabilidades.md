# Arquitectura y responsabilidades

**Estado:** propuesta que queremos acordar antes de integrar las clases compartidas  
**Módulos:** core y lwjgl3

## 1. Organización de carpetas

Vamos a separar el código dentro de core/src/main/java/puppy/code/ de esta forma:

```text
app/          SpaceNavigation y ciclo de la aplicación
assets/       acceso compartido a imágenes y audio
behaviors/    MovementStrategy y las formaciones de disparo de los aliens
entities/     GameEntity, Ship, Alien, meteoritos y proyectiles
factories/    AlienBuilder y LevelDirector
input/        PlayerInput y adaptador de teclado
leaderboard/  ScoreEntry, ScoreRepository y HighScoreService
screens/      AbstractGameScreen y pantallas concretas
systems/      CollisionSystem, ScoreService y LevelManager
world/        GameWorld, estado de partida y WorldSnapshot
```

Usaremos SQLite para guardar los puntajes. Pondremos el adaptador `SQLiteScoreRepository` en `lwjgl3` y dejaremos la interfaz `ScoreRepository` en `core`, para que las reglas del juego no dependan de SQL ni de la ubicación de la base de datos.

## 2. Flujo general

```text
Lwjgl3Launcher
      |
      v
SpaceNavigation
      |
      +---- crea el ciclo de la aplicación y prepara recursos compartidos
      |
      v
MenuScreen ---- inicia ----> GameScreen
                                  |
                                  v
                              GameWorld
                                  |
                                  +---- actualiza entidades
                                  +---- coordina impactos y puntaje
                                  +---- consulta cuotas y estado del nivel
                                  |
                                  v
                            WorldSnapshot
                                  |
                                  v
                           GameScreen dibuja
                                  |
                      victoria o GameOver
```

Desde las pantallas presentaremos el estado y solicitaremos las transiciones. GameWorld coordinará la partida y usaremos sistemas para resolver reglas específicas.

Mantendremos el menú fuera de `GamePhase`. En la partida usaremos cuatro fases: simulación activa, nivel superado, Game Over y Victoria. Mostraremos “Nivel Superado” dentro de la partida y esperaremos cualquier botón para continuar; reservaremos las pantallas de Game Over y Victoria para el final correspondiente.

## 3. Responsabilidad de cada parte

### `SpaceNavigation`

Desde esta clase iniciaremos la aplicación, crearemos los servicios compartidos y abriremos las pantallas. También cerraremos los recursos que pertenezcan a toda la aplicación.

### Pantallas

Usaremos `MenuScreen`, `GameScreen`, `VictoryScreen` y `GameOverScreen` para mostrar información, recibir eventos de interfaz y solicitar transiciones. Mantendremos las reglas de vidas, puntaje y cuotas fuera de las pantallas para no duplicarlas. El menú de Game Over y el de Victoria permitirán reiniciar con Enter o volver al menú con Esc. Ya contamos con `ScreenNavigator` para las transiciones principales; le agregaremos `showGameOver()` y `showVictory()` para que las pantallas no dependan directamente de `SpaceNavigation`.

Nos gustaría mostrar el top 3 desde el menú y al terminar la partida. Lo consultaremos con el equipo antes de dejar ambas vistas como requisito confirmado.

### `GameWorld`

Haremos que `GameWorld` mantenga las entidades activas y coordine la actualización de la partida. No entregará listas mutables a las pantallas; les ofrecerá un `WorldSnapshot` de solo lectura.

### Entidades

Cada entidad (`Ship`, `Alien`, `PlayerProjectile`, `AlienProjectile` y `Meteorite`) mantendrá su propio estado. Los meteoritos serán obstáculos sin IA ni disparos, no contarán para las cuotas y podrán otorgar puntaje al destruirlos. Todavía tenemos que acordar si todos los meteoritos se destruyen de un impacto y qué efecto tendrá el contacto con la nave. Evitaremos que las entidades conozcan las pantallas o lean directamente el teclado.

### Sistemas

- Usaremos `CollisionSystem` para detectar impactos y comunicar sus resultados al mundo.
- Usaremos `ScoreService` para calcular el puntaje por alien y meteorito destruido.
- Usaremos `LevelManager` para mantener el nivel actual y comprobar las cuotas.
- Usaremos `HighScoreService` para decidir si un resultado puede entrar al top 3.

Dejaremos en `GameWorld` la coordinación de las entidades, las vidas y la fase de la partida. `ScoreService` será responsable del puntaje y `LevelManager` del nivel y su cuota. `CollisionSystem` detectará las colisiones y comunicará el resultado; desde `GameWorld` coordinaremos qué debe cambiar en las vidas, el puntaje o el nivel. Así evitaremos que la detección de una colisión también se encargue de todas las reglas que vienen después.

Con esta división, `GameWorld` actuará como Controller de GRASP para coordinar los eventos de la partida, `ScoreService` tendrá alta cohesión con las reglas de puntaje y `LevelManager` conocerá las cuotas y el avance. `CollisionSystem` no modificará por su cuenta las vidas ni el puntaje: reportará lo que detecte y el mundo aplicará las consecuencias. Así mantenemos cada responsabilidad en un lugar claro y reducimos el acoplamiento entre detección y reglas.

La cuota contará aliens destruidos, no meteoritos: pediremos 15 en el nivel uno, 30 en el nivel dos y 45 en el nivel tres. Un proyectil del jugador destruirá al alien que golpee y desaparecerá con él. Si un proyectil alienígena nos golpea o chocamos físicamente con un alien, perderemos una vida; al chocar con la nave, el alien también se destruirá.

Usaremos 260 px/s para la nave, 90 px/s para el descenso lineal, 110 px/s para el zigzag, 450 px/s para los proyectiles del jugador y 220 px/s para los proyectiles alienígenas. Los aliens apuntarán hacia la nave y alternarán las formaciones lineal hacia abajo y en V de 45°. Lanzarán dos proyectiles a la vez cada 1,8 segundos. Desactivaremos los proyectiles que superen el límite del área de juego, cuyo valor exacto todavía tenemos que acordar.

Al perder una vida, reapareceremos en un punto fijo, limpiaremos los proyectiles activos y mantendremos a los aliens y meteoritos en su lugar. Conservaremos el puntaje y el progreso del nivel. Durante 1,5 segundos ignoraremos el daño y dejaremos que aliens y proyectiles atraviesen la nave sin desaparecer. El dash durará 0,15 segundos, se recargará en 3 segundos y nos dará invulnerabilidad; durante el dash mantendremos los objetos en pantalla. Aún tenemos que definir la fuerza del impulso.

Al alcanzar la cuota de los niveles uno y dos, detendremos la aparición de aliens y pondremos `GameWorld` en la fase `LEVEL_COMPLETE`. `GameScreen` mostrará “Nivel Superado” y esperará cualquier botón antes de preparar el siguiente escenario. Todavía tenemos que elegir si los aliens restantes huyen o explotan. Al alcanzar la cuota del nivel tres pasaremos a `VICTORY`; si nos quedamos sin vidas, pasaremos a `GAME_OVER`.

Sumaremos 100 puntos por alien destruido, 50 por meteorito destruido, 25 si es chico y 75 si es grande. Mantendremos el puntaje al perder una vida y al cambiar de nivel. Cuando se agoten las vidas, registraremos el resultado con un nombre de tres caracteres; al iniciar otra partida volveremos a cero. Todavía tenemos que acordar cómo ingresaremos ese nombre, cómo ordenaremos los empates y si consultaremos el top 3 desde el menú y las pantallas finales.

La nave tendrá 1,5 segundos de invulnerabilidad al reaparecer. En ese intervalo ignoraremos el daño y dejaremos que aliens y proyectiles la atraviesen sin desaparecer. El dash durará 0,15 segundos, tendrá una recarga de 3 segundos y dará invulnerabilidad mientras se ejecuta. Seguirá la dirección indicada con WASD o avanzará hacia adelante si no indicamos movimiento. Aún tenemos que acordar la fuerza o velocidad del impulso.

### Entrada

Definiremos `PlayerInput` para describir movimiento, disparo, dash y las acciones de continuar, reiniciar y volver al menú. Nuestro adaptador de teclado traducirá `Gdx.input` a ese contrato, así las entidades y las pantallas no tendrán que consultar el teclado directamente.

### Recursos

Centralizaremos en `GameAssets` la carga y liberación de imágenes y audio compartidos. Entregaremos a cada pantalla los recursos que necesite y evitaremos que libere recursos que pertenecen a toda la aplicación.

## 4. Decisiones de diseño

### Vamos a dejar las colecciones bajo el control del mundo

**Decisión:** haremos que `GameWorld` guarde las entidades y entregue snapshots de solo lectura.  
**Motivo:** así evitamos que una pantalla agregue o elimine entidades directamente o mantenga otra lista paralela.  
**Principio:** aplicamos encapsulamiento e Information Expert de GRASP; la clase que conoce el conjunto coordina su ciclo.

### Las pantallas se encargan de presentar

**Decisión:** dejaremos que las pantallas dibujen el estado y soliciten cambios; mantendremos las reglas en el mundo y sus sistemas.  
**Motivo:** así podremos corregir las colisiones sin tener que cambiar el código de dibujo.  
**Principio:** aplicamos SRP y alta cohesión para que cada clase mantenga una responsabilidad principal.

### La entrada llega mediante un contrato

**Decisión:** haremos que el mundo reciba `PlayerInput` y que el adaptador de escritorio sea el único que interprete las teclas.  
**Motivo:** con este contrato evitamos ligar las entidades y el mundo a `Gdx.input` o a una pantalla concreta.  
**Principio:** buscamos bajo acoplamiento y aplicamos Indirection y DIP.

### El movimiento usa tiempo delta

**Decisión:** expresaremos las velocidades en unidades por segundo y pasaremos `delta` a cada actualización.  
**Motivo:** así la duración de una partida no dependerá de los FPS del equipo.  
**Principio:** modelaremos el tiempo de simulación como dato de entrada y no como número de fotogramas.

### `GameAssets` administra recursos compartidos

**Decisión:** haremos que `SpaceNavigation` obtenga la instancia de `GameAssets` al iniciar y la entregue a las pantallas por constructor.  
**Motivo:** así cargaremos y liberaremos una sola vez las texturas y los sonidos compartidos.  
**Principio:** aplicaremos Singleton para GM-6 y mantendremos explícito el ciclo de vida; limitaremos `getInstance()` al punto de inicio para no hacer que todas las clases dependan de un acceso global.

### `AbstractGameScreen` fija el ciclo común

**Decisión:** haremos que las pantallas concretas hereden de `AbstractGameScreen`, cuyo `render(delta)` mantendrá el orden limpiar, actualizar y dibujar.  
**Motivo:** con el método común evitaremos repetir o saltarnos el orden de renderizado. Lo incorporaremos cuando tengamos pantallas concretas que usen ese ciclo.  
**Principio:** aplicaremos Template Method para GM-8 cuando responda a un flujo real.

### El movimiento del alien se puede intercambiar

**Decisión:** haremos que `Alien` reciba una `MovementStrategy` y seleccionaremos de forma determinista el descenso lineal o el zigzag. Separaremos de ese movimiento las dos formaciones de disparo que alternaremos durante la partida.\
**Motivo:** podremos variar el desplazamiento sin añadir otra especie ni cambiar la lógica común del alien.  
**Principio:** aplicaremos polimorfismo, OCP y Protected Variations.

Usaremos inicialmente 90 px/s para el descenso lineal y 110 px/s para el zigzag. Nos falta acordar qué niveles usarán cada estrategia. Los aliens apuntarán hacia la nave y alternarán entre el disparo lineal hacia abajo y la formación en V de 45°. Cada descarga tendrá dos proyectiles simultáneos y saldrá cada 1,8 segundos.

### Las formaciones de disparo quedan separadas del movimiento

**Decisión:** alternaremos los disparos lineales y en V desde un contrato de comportamiento separado del movimiento del alien. Cada descarga incluirá dos proyectiles simultáneos y saldrá cada 1,8 segundos.\
**Motivo:** el alien tendrá dos formaciones de disparo y dos movimientos posibles; mantener esas decisiones separadas evitará mezclar reglas distintas dentro de una sola clase.\
**Principio:** aplicaremos alta cohesión y Strategy para variar la formación de disparo sin acoplarla al movimiento. Confirmaremos los patrones exigidos por la cátedra cuando tengamos a la vista la pauta correspondiente.

### Los niveles usan Builder y Director

**Decisión:** usaremos `LevelDirector` para preparar la configuración del nivel con `AlienBuilder`, que validará los valores antes de crear el alien.  
**Motivo:** así mantendremos consistentes entre niveles los varios valores de configuración del alien.  
**Principio:** aplicaremos Builder para crear el alien con su configuración y Creator/Controller de GRASP para coordinar la creación.

### El almacenamiento queda detrás de una interfaz

**Decisión:** guardaremos los puntajes con SQLite. Ubicaremos `ScoreRepository` en `core` y su implementación `SQLiteScoreRepository` en `lwjgl3`.\
**Motivo:** ya hemos trabajado con SQLite y así podremos guardar los puntajes sin hacer que las reglas del juego dependan directamente de esa tecnología.\
**Principio:** aplicaremos DIP e Indirection para mantener bajo el acoplamiento.

## 5. Reglas de implementación

- Mantendremos privados los atributos y validaremos sus valores.
- Evitaremos que los métodos entreguen colecciones internas mutables.
- Mantendremos a las entidades separadas de las pantallas y del teclado.
- Usaremos `delta` para el movimiento y los temporizadores.
- Usaremos los nombres de dominio `Ship`, `Alien`, `PlayerProjectile` y `AlienProjectile`.
- Conservaremos la compatibilidad con Java 11.
- Agregaremos cada abstracción cuando responda a una función o requisito concreto.
- Actualizaremos el UML y su explicación cuando cambiemos responsabilidades o relaciones.
