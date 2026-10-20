# Space Navigation — base implementada

Este diagrama refleja el corte inicial que compila y sirve como punto de partida. Incluye los
patrones exigidos GM-6 a GM-9 y la separación básica entre entrada, mundo, entidades y pantallas.
No representa todavía el juego completo: proyectiles, puntaje, cuotas, dash, victoria, persistencia
y pantallas finales siguen pendientes.

```mermaid
classDiagram
    class Lwjgl3Launcher
    class Game

    class SpaceNavigation {
        -assets: GameAssets
        -playerInput: PlayerInput
        -navigationInput: NavigationInput
        -levelDirector: LevelDirector
        -collisionSystem: CollisionSystem
        +create() void
        +showMainMenu() void
        +startNewGame() void
        +dispose() void
    }

    class ScreenNavigator {
        <<interface>>
        +showMainMenu() void
        +startNewGame() void
    }

    class GameAssets {
        -instance: GameAssets
        -batch: SpriteBatch
        -font: BitmapFont
        -shapes: ShapeRenderer
        +getInstance() GameAssets
        +dispose() void
    }

    class AbstractGameScreen {
        <<abstract>>
        #assets: GameAssets
        #camera: OrthographicCamera
        #viewport: Viewport
        +render(delta: float) final
        +resize(width: int, height: int) final
        #updateScreen(delta: float) abstract
        #drawScreen() abstract
    }

    class MenuScreen
    class GameScreen

    class NavigationInput {
        <<interface>>
        +confirmJustPressed() boolean
        +backJustPressed() boolean
    }
    class KeyboardNavigationInput

    class PlayerInput {
        <<interface>>
        +movementDirection() Vector2
    }
    class KeyboardPlayerInput

    class GameWorld {
        -ship: Ship
        -aliens: List~Alien~
        -level: int
        -lives: int
        -phase: GamePhase
        +update(delta: float, input: PlayerInput) void
        +snapshot() WorldSnapshot
        +restart() void
    }

    class WorldSnapshot {
        -ship: EntityView
        -aliens: List~EntityView~
        -lives: int
        -phase: GamePhase
        +getShip() EntityView
        +getAliens() List~EntityView~
        +getLives() int
        +getPhase() GamePhase
    }

    class GamePhase {
        <<enumeration>>
        PLAYING
        GAME_OVER
    }

    class GameEntity {
        <<abstract>>
        -x: float
        -y: float
        -width: float
        -height: float
        -active: boolean
        +bounds() Rectangle
        +deactivate() void
    }
    class Collidable {
        <<interface>>
        +bounds() Rectangle
    }
    class Ship {
        -speed: float
        -invulnerabilityRemaining: float
        +update(delta: float, direction: Vector2, worldWidth: float, worldHeight: float) void
        +respawn(x: float, y: float, invulnerabilitySeconds: float) void
    }
    class Alien {
        -movement: MovementStrategy
        -age: float
        +update(delta: float, worldWidth: float) void
    }

    class CollisionSystem {
        +collides(first: Collidable, second: Collidable) boolean
        +collidingWith(subject: Collidable, candidates: List) List
    }

    class MovementStrategy {
        <<interface>>
        +nextPosition(x: float, y: float, age: float, delta: float) Vector2
    }
    class LinearMovement
    class ZigZagMovement

    class AlienBuilder {
        +position(x: float, y: float) AlienBuilder
        +size(width: float, height: float) AlienBuilder
        +movement(strategy: MovementStrategy) AlienBuilder
        +build() Alien
    }
    class LevelDirector {
        +createWave(level: int) List~Alien~
    }

    Lwjgl3Launcher --> SpaceNavigation : inicia
    SpaceNavigation --|> Game
    SpaceNavigation ..|> ScreenNavigator
    SpaceNavigation --> GameAssets : crea y libera
    SpaceNavigation --> MenuScreen : muestra
    SpaceNavigation --> GameScreen : inicia
    SpaceNavigation --> LevelDirector : comparte
    SpaceNavigation --> CollisionSystem : comparte

    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    MenuScreen --> NavigationInput : consulta
    GameScreen --> NavigationInput : consulta
    KeyboardNavigationInput ..|> NavigationInput
    KeyboardPlayerInput ..|> PlayerInput
    GameScreen --> GameWorld : actualiza y consulta
    GameScreen --> PlayerInput : recibe
    GameWorld --> WorldSnapshot : crea
    WorldSnapshot --> GamePhase : expone

    GameEntity ..|> Collidable
    GameEntity <|-- Ship
    GameEntity <|-- Alien
    GameWorld *-- Ship : posee
    GameWorld *-- Alien : posee
    GameWorld --> CollisionSystem : solicita detección
    GameWorld --> LevelDirector : solicita oleadas

    Alien --> MovementStrategy : delega
    MovementStrategy <|.. LinearMovement
    MovementStrategy <|.. ZigZagMovement
    LevelDirector --> AlienBuilder : construye
```

## Patrones y principios visibles en el código

- **GM-4 — abstracción e interfaz:** `AbstractGameScreen` es la clase abstracta usada por `MenuScreen` y `GameScreen`; `MovementStrategy` tiene dos implementaciones usadas por `Alien`.
- **GM-5 — encapsulamiento y responsabilidades:** `GameWorld` coordina la partida (Controller); las pantallas dibujan snapshots inmutables; las entidades no consultan teclado ni pantallas; `CollisionSystem` detecta contactos y `GameWorld` aplica sus consecuencias.
- **GM-6 — Singleton:** `GameAssets` limita su construcción y centraliza los recursos compartidos; `SpaceNavigation` obtiene la instancia y controla su ciclo de vida.
- **GM-7 — Strategy:** `Alien` delega su movimiento en `LinearMovement` o `ZigZagMovement` mediante `MovementStrategy`.
- **GM-8 — Template Method:** `AbstractGameScreen.render()` es final y conserva el orden limpiar, actualizar y dibujar; cada pantalla concreta aporta actualización y dibujo.
- **GM-9 — Builder:** `AlienBuilder` valida la posición y la estrategia requeridas; `LevelDirector` define y construye las oleadas.

## Pendiente para las siguientes etapas

La base solo modela movimiento y contacto físico nave-alien. Faltan proyectiles, combate, puntaje,
cuotas y avance de nivel, dash, meteoritos, fases de victoria/nivel superado, pantallas finales,
repositorio de récords, y completar las transiciones de `ScreenNavigator`. La alternancia de movimiento
por paridad de nivel y el tamaño de oleada son provisionales.
