# Arquitectura y responsabilidades

**Estado:** propuesta que queremos acordar antes de integrar las clases compartidas  
**Módulos:** core y lwjgl3

## 1. Organización de carpetas

Vamos a separar el código dentro de core/src/main/java/puppy/code/ de esta forma:

```text
app/          SpaceNavigation y ciclo de la aplicación
assets/       acceso compartido a imágenes y audio
behaviors/    MovementStrategy y sus implementaciones
entities/     GameEntity, Ship, Alien y proyectiles
factories/    AlienBuilder y LevelDirector
input/        PlayerInput y adaptador de teclado
leaderboard/  ScoreEntry, ScoreRepository y HighScoreService
screens/      AbstractGameScreen y pantallas concretas
systems/      CollisionSystem, ScoreService y LevelManager
world/        GameWorld, estado de partida y WorldSnapshot
```

Si acordamos usar SQLite, ubicaremos el adaptador de la base local en lwjgl3. Desde core usaremos la interfaz ScoreRepository, sin depender de SQL ni de la ubicación del archivo de datos.

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

## 3. Responsabilidad de cada parte

### `SpaceNavigation`

Desde esta clase iniciaremos la aplicación, crearemos los servicios compartidos y abriremos las pantallas. También cerraremos los recursos que pertenezcan a toda la aplicación.

### Pantallas

Usaremos `MenuScreen`, `GameScreen`, `VictoryScreen` y `GameOverScreen` para mostrar información, recibir eventos de interfaz y solicitar transiciones. Mantendremos las reglas de vidas, puntaje y cuotas fuera de las pantallas para no duplicarlas.

### `GameWorld`

Haremos que `GameWorld` mantenga las entidades activas y coordine la actualización de la partida. No entregará listas mutables a las pantallas; les ofrecerá un `WorldSnapshot` de solo lectura.

### Entidades

Cada entidad (`Ship`, `Alien`, `PlayerProjectile` y `AlienProjectile`) mantendrá su propio estado. Evitaremos que conozcan las pantallas o lean directamente el teclado.

### Sistemas

- Usaremos `CollisionSystem` para detectar impactos y comunicar sus resultados al mundo.
- Usaremos `ScoreService` para calcular el puntaje por enemigo destruido.
- Usaremos `LevelManager` para mantener el nivel actual y comprobar las cuotas.
- Usaremos `HighScoreService` para decidir si un resultado puede entrar al top 3.

### Entrada

Definiremos `PlayerInput` para describir movimiento, disparo y dash. Nuestro adaptador de teclado traducirá `Gdx.input` a ese contrato, así las entidades no tendrán que consultar el teclado.

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

**Decisión:** haremos que `Alien` reciba una `MovementStrategy` y seleccionaremos dos estrategias deterministas según el nivel. Mantendremos un único patrón diagonal de ataque.  
**Motivo:** podremos variar el desplazamiento sin añadir otra especie ni cambiar la lógica común del alien.  
**Principio:** aplicaremos polimorfismo, OCP y Protected Variations.

### Los niveles usan Builder y Director

**Decisión:** usaremos `LevelDirector` para preparar la configuración del nivel con `AlienBuilder`, que validará los valores antes de crear el alien.  
**Motivo:** así mantendremos consistentes entre niveles los varios valores de configuración del alien.  
**Principio:** aplicaremos Builder para crear el alien con su configuración y Creator/Controller de GRASP para coordinar la creación.

### El almacenamiento queda detrás de una interfaz

**Decisión:** ubicaremos `ScoreRepository` en `core` y el adaptador local en `lwjgl3`. Todavía tenemos que confirmar la tecnología concreta.  
**Motivo:** así podremos trabajar con puntajes desde las reglas y las pantallas sin depender de detalles SQL o de archivos.  
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
