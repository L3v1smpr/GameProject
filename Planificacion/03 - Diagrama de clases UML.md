# Diagrama de clases UML

**Estado:** diseño que proponemos y que contrastaremos con el código al integrar cada etapa.  
En el diagrama mostramos la arquitectura objetivo; todavía no afirmamos que esas clases estén implementadas.

```mermaid
classDiagram
    class Lwjgl3Launcher

    class SpaceNavigation {
        -assets: GameAssets
        +create() void
        +dispose() void
    }

    class GameAssets {
        -instance: GameAssets
        -manager: AssetManager
        -GameAssets()
        +getInstance() GameAssets
        +dispose() void
    }

    class AbstractGameScreen {
        <<abstract>>
        +render(delta: float) final
        #update(delta: float) abstract
        #draw() abstract
    }

    class MenuScreen
    class GameScreen
    class VictoryScreen
    class GameOverScreen

    class PlayerInput {
        <<interface>>
        +movementDirection() Vector2
        +fireJustPressed() boolean
        +dashJustPressed() boolean
    }

    class KeyboardPlayerInput

    class GameWorld {
        -entities: List~GameEntity~
        +update(delta: float, input: PlayerInput) void
        +snapshot() WorldSnapshot
        +restart() void
    }

    class WorldSnapshot {
        +level() int
        +lives() int
        +score() int
        +phase() GamePhase
    }

    class Updatable {
        <<interface>>
        +update(delta: float) void
    }

    class Renderable {
        <<interface>>
        +render(batch: SpriteBatch) void
    }

    class Collidable {
        <<interface>>
        +bounds() Rectangle
    }

    class GameEntity {
        <<abstract>>
        -active: boolean
        +isActive() boolean
        +deactivate() void
    }

    class Ship
    class Alien
    class PlayerProjectile
    class AlienProjectile

    class CollisionSystem
    class ScoreService
    class LevelManager
    class MovementStrategy {
        <<interface>>
        +move(alien: Alien, delta: float, bounds: Rectangle) void
    }
    class LinearMovement
    class ZigZagMovement

    class AlienBuilder {
        +position(x: float, y: float) AlienBuilder
        +speed(speed: float) AlienBuilder
        +movement(strategy: MovementStrategy) AlienBuilder
        +build() Alien
    }

    class LevelDirector {
        +createLevel(level: int) List~Alien~
    }

    class ScoreEntry
    class ScoreRepository {
        <<interface>>
        +topScores(limit: int) List~ScoreEntry~
        +saveIfTop(score: ScoreEntry) void
    }
    class HighScoreService
    class SQLiteScoreRepository

    Lwjgl3Launcher --> SpaceNavigation : inicia
    SpaceNavigation --> GameAssets : obtiene y libera
    SpaceNavigation --> MenuScreen : muestra
    SpaceNavigation --> AbstractGameScreen : administra

    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    AbstractGameScreen <|-- VictoryScreen
    AbstractGameScreen <|-- GameOverScreen

    KeyboardPlayerInput ..|> PlayerInput
    GameScreen --> GameWorld : actualiza y consulta
    GameWorld --> PlayerInput : recibe
    GameWorld --> WorldSnapshot : crea

    GameEntity ..|> Updatable
    GameEntity ..|> Renderable
    GameEntity <|-- Ship
    GameEntity <|-- Alien
    GameEntity <|-- PlayerProjectile
    GameEntity <|-- AlienProjectile

    Ship ..|> Collidable
    Alien ..|> Collidable
    PlayerProjectile ..|> Collidable
    AlienProjectile ..|> Collidable

    GameWorld o-- GameEntity : mantiene
    GameWorld --> CollisionSystem : coordina
    GameWorld --> LevelManager : consulta
    GameWorld --> ScoreService : consulta
    CollisionSystem --> Collidable : detecta impactos

    Alien --> MovementStrategy : delega movimiento
    MovementStrategy <|.. LinearMovement
    MovementStrategy <|.. ZigZagMovement
    LevelDirector --> AlienBuilder : construye
    LevelDirector --> LevelManager : prepara cuotas

    HighScoreService --> ScoreRepository : persiste resultados
    ScoreRepository <|.. SQLiteScoreRepository
    SQLiteScoreRepository --> ScoreEntry : guarda y recupera
```

## Notas del diagrama

- Vamos a usar `GameEntity` como clase abstracta para la nave, el alien y los proyectiles.
- Definimos `Collidable` como contrato de límites para colisión. Haremos que `bounds()` devuelva una copia para no exponer el rectángulo mutable interno.
- Mantendremos las entidades dentro de `GameWorld` y evitaremos entregar la lista interna a una pantalla.
- Ubicaremos el adaptador `SQLiteScoreRepository` en el módulo de escritorio si acordamos esa tecnología; todavía tenemos pendiente confirmarla.
- Usaremos `GameAssets` como Singleton para GM-6. Desde la aplicación obtendremos la instancia y la inyectaremos en los objetos que la necesiten.
- Aplicaremos Template Method en `AbstractGameScreen` para GM-8 cuando las pantallas concretas compartan ese ciclo.
- Actualizaremos las relaciones entre clases si cambia la implementación acordada. En el UML final mostraremos el código real y quitaremos las clases planeadas que no hayamos usado.
