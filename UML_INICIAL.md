# UML del proyecto: Space Navigation

**Rama de referencia:** `feature/overhaul-assets`

**Lenguaje:** Java 11 · **Motor:** LibGDX

Este diagrama describe el código de la rama indicada. El paquete `puppy.code.prototype` conserva el esqueleto anterior para contrastar su organización con el flujo activo, pero no participa en la creación de la partida actual.

## Clases de aplicación, pantallas y recursos

```mermaid
classDiagram
    direction LR

    class Game {
        <<LibGDX>>
    }
    class ScreenAdapter {
        <<LibGDX>>
        +render(delta)
        +resize(width, height)
        +dispose()
    }
    class SpaceNavigation {
        -assets: GameAssets
        -navigationInput: NavigationInput
        -playerInput: PlayerInput
        -levelDirector: LevelDirector
        -collisionSystem: CollisionSystem
        -scoreStore: ScoreStore
        -currentWorld: GameWorld
        +create()
        +showMenu()
        +showGame()
        +showGameOver()
        +showVictory()
        +dispose()
    }
    class ScreenNavigator {
        <<interface>>
        +showMenu()
        +showGame()
        +showGameOver()
        +showVictory()
    }
    class GameAssets {
        <<Singleton>>
        -instance: GameAssets
        -textures: Map
        -sounds: Map
        -music: Map
        -batch: SpriteBatch
        -shapes: ShapeRenderer
        -font: BitmapFont
        -GameAssets()
        +getInstance()
        +getPlayerFrame(row, column)
        +dispose()
    }
    class AbstractGameScreen {
        <<abstract>>
        #assets: GameAssets
        +render(delta) final
        #clearScreen()
        #handleInput(delta) abstract
        #update(delta) abstract
        #draw(delta) abstract
    }
    class MenuScreen
    class GameScreen
    class GameOverScreen
    class VictoryScreen
    class Starfield
    class AfterImage

    class SpriteBatch {
        <<LibGDX>>
    }
    class ShapeRenderer {
        <<LibGDX>>
    }
    class BitmapFont {
        <<LibGDX>>
    }
    class Texture {
        <<LibGDX>>
    }
    class TextureRegion {
        <<LibGDX>>
    }
    class Music {
        <<LibGDX>>
    }
    class Sound {
        <<LibGDX>>
    }

    Game <|-- SpaceNavigation
    ScreenNavigator <|.. SpaceNavigation
    ScreenAdapter <|-- AbstractGameScreen
    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    AbstractGameScreen <|-- GameOverScreen
    AbstractGameScreen <|-- VictoryScreen
    SpaceNavigation --> GameAssets : obtiene una vez e inyecta
    SpaceNavigation ..> ScreenNavigator
    SpaceNavigation --> GameWorld : crea partida
    GameScreen --> GameWorld : actualiza y consulta
    GameScreen --> WorldSnapshot : presenta
    GameAssets *-- SpriteBatch
    GameAssets *-- ShapeRenderer
    GameAssets *-- BitmapFont
    GameAssets ..> Texture
    GameAssets ..> TextureRegion
    GameAssets ..> Music
    GameAssets ..> Sound
    GameScreen ..> Starfield
    GameScreen ..> AfterImage
```

## Dominio y coordinación de la partida

```mermaid
classDiagram
    direction LR

    class GameWorld {
        -collisions: CollisionDetector
        -levelDirector: AlienFactory
        -playerWeapon: PlayerWeapon
        -ship: Ship
        -aliens: List
        -meteorites: List
        -playerProjectiles: List
        -alienProjectiles: List
        -level: int
        -lives: int
        -score: int
        -phase: GamePhase
        +update(delta, input)
        +advanceToNextLevel()
        +snapshot() WorldSnapshot
        +setAudioListener(listener)
    }
    class GamePhase {
        <<enum>>
        ACTIVE
        LEVEL_COMPLETED
        GAME_OVER
        VICTORY
    }
    class WorldSnapshot {
        -ship: EntityView
        -aliens: List
        -meteorites: List
        -projectiles: List
        -score: int
        -lives: int
        -level: int
        -phase: GamePhase
    }
    class EntityView {
        <<value object>>
        -x: float
        -y: float
        -width: float
        -height: float
        -assetPath: String
        -rotation: float
    }
    class GameEntity {
        <<abstract>>
        -x: float
        -y: float
        -width: float
        -height: float
        -active: boolean
        +update(delta)
        +bounds() Rectangle
    }
    class Collidable {
        <<interface>>
        +bounds() Rectangle
    }
    class Ship
    class Alien {
        -movement: MovementStrategy
        -attackStrategy: AlienAttackStrategy
        -hitPoints: int
        +update(delta, worldWidth)
        +tryFire(target) List
    }
    class Meteorite
    class PlayerProjectile
    class AlienProjectile
    class MeteoriteSize {
        <<enum>>
    }
    class PlayerWeapon {
        -cooldown: float
        +tryFire(ship) PlayerProjectile
    }
    class CollisionDetector {
        <<interface>>
        +collides(first, second) boolean
    }
    class CollisionSystem {
        +collides(first, second) boolean
    }
    class AlienFactory {
        <<interface>>
        +createWave(level, count) List
        +createStaticAlien(level, x, targetY) Alien
        +createBomberAlien(level, x, cruiseY) Alien
    }
    class LevelDirector {
        +createWave(level, count) List
        +createStaticAlien(level, x, targetY) Alien
        +createBomberAlien(level, x, cruiseY) Alien
    }
    class WorldAudioListener {
        <<interface>>
        +onPlayerShoot()
        +onPlayerDash()
        +onPlayerHurt(x, y)
        +onAlienDestroyed(x, y)
        +onLevelCompleted()
    }
    class GameScreen

    GameEntity ..|> Collidable
    GameEntity <|-- Ship
    GameEntity <|-- Alien
    GameEntity <|-- Meteorite
    GameEntity <|-- PlayerProjectile
    GameEntity <|-- AlienProjectile
    Meteorite --> MeteoriteSize
    GameWorld *-- Ship
    GameWorld o-- Alien
    GameWorld o-- Meteorite
    GameWorld o-- PlayerProjectile
    GameWorld o-- AlienProjectile
    GameWorld --> GamePhase
    GameWorld --> WorldSnapshot : crea
    WorldSnapshot *-- EntityView
    GameWorld --> PlayerWeapon
    GameWorld --> CollisionDetector
    CollisionDetector <|.. CollisionSystem
    GameWorld --> AlienFactory
    AlienFactory <|.. LevelDirector
    GameWorld --> WorldAudioListener
    GameScreen ..|> WorldAudioListener
```

## Entradas, puntajes y tipos LibGDX

```mermaid
classDiagram
    class PlayerInput {
        <<interface>>
        +movementDirection() Vector2
        +firePressed() boolean
        +dashJustPressed() boolean
    }
    class KeyboardPlayerInput
    class NavigationInput {
        <<interface>>
        +confirmJustPressed() boolean
        +backJustPressed() boolean
        +upJustPressed() boolean
        +anyActionJustPressed() boolean
    }
    class KeyboardNavigationInput
    class InitialsInput {
        <<interface>>
        +letterJustPressed() char
        +deleteJustPressed() boolean
    }
    class KeyboardInitialsInput
    class ScoreStore {
        <<interface>>
        +getTopScores() List
        +isEligibleForTop3(score) boolean
        +addScore(name, score) boolean
    }
    class ScoreRepository {
        -prefs: Preferences
        +getTopScores() List
        +isEligibleForTop3(score) boolean
        +addScore(name, score) boolean
    }
    class HighScore {
        -name: String
        -score: int
    }
    class Preferences {
        <<LibGDX>>
    }
    class Rectangle {
        <<LibGDX>>
    }
    class Vector2 {
        <<LibGDX>>
    }
    class GameConfig {
        <<constants>>
        +WORLD_WIDTH
        +WORLD_HEIGHT
        +SHIP_SPEED
        +DASH_DURATION_SECONDS
        +DASH_COOLDOWN_SECONDS
        +LEVEL_ONE_ALIEN_QUOTA
        +LEVEL_TWO_ALIEN_QUOTA
        +LEVEL_THREE_ALIEN_QUOTA
    }
    class GameWorld
    class SpaceNavigation
    class MenuScreen
    class GameOverScreen
    class VictoryScreen

    PlayerInput <|.. KeyboardPlayerInput
    NavigationInput <|.. KeyboardNavigationInput
    InitialsInput <|.. KeyboardInitialsInput
    ScoreStore <|.. ScoreRepository
    ScoreRepository --> Preferences
    ScoreRepository ..> HighScore
    GameWorld ..> PlayerInput
    SpaceNavigation --> PlayerInput
    SpaceNavigation --> NavigationInput
    SpaceNavigation --> InitialsInput
    SpaceNavigation --> ScoreStore
    MenuScreen --> ScoreStore
    GameOverScreen --> ScoreStore
    VictoryScreen --> ScoreStore
    Collidable --> Rectangle
    PlayerInput ..> Vector2
```

## GM-6 Singleton

**Problema:** las pantallas comparten texturas, audio y objetos de dibujo; varias instancias podrían duplicar recursos o liberar recursos que todavía se usan.

**Contexto:** `SpaceNavigation` crea la aplicación y conecta las dependencias.
**Solución:** `GameAssets` mantiene una instancia, tiene constructor privado y libera sus recursos en `dispose()`. El punto de composición obtiene esa instancia y la entrega a las pantallas.

```mermaid
classDiagram
    class SpaceNavigation
    class GameAssets {
        <<Singleton>>
        -instance: GameAssets
        -GameAssets()
        +getInstance() GameAssets
        +dispose()
    }
    SpaceNavigation --> GameAssets : obtiene una instancia
```

## GM-7 Strategy

**Problema:** las trayectorias y descargas varían por tipo de alien y sector.

**Contexto:** `Alien` participa en el mundo sin necesitar conocer cada algoritmo de movimiento o ataque.
**Solución:** recibe los contratos `MovementStrategy` y `AlienAttackStrategy`; `LevelDirector` selecciona las variantes al construir la oleada.

```mermaid
classDiagram
    class Alien {
        -movement: MovementStrategy
        -attackStrategy: AlienAttackStrategy
    }
    class MovementStrategy {
        <<interface>>
        +speed()
        +nextPosition(x, y, age, delta)
    }
    class LinearMovementStrategy
    class ZigZagMovementStrategy
    class StaticMovementStrategy
    class HorizontalMovementStrategy
    class AlienAttackStrategy {
        <<interface>>
        +createVolley(alien, target)
    }
    class LinearAttackStrategy
    class VAttackStrategy
    class SpreadAttackStrategy
    class AlternatingAttackStrategy
    class BurstAttackStrategy
    class LevelDirector

    Alien --> MovementStrategy
    Alien --> AlienAttackStrategy
    MovementStrategy <|.. LinearMovementStrategy
    MovementStrategy <|.. ZigZagMovementStrategy
    MovementStrategy <|.. StaticMovementStrategy
    MovementStrategy <|.. HorizontalMovementStrategy
    AlienAttackStrategy <|.. LinearAttackStrategy
    AlienAttackStrategy <|.. VAttackStrategy
    AlienAttackStrategy <|.. SpreadAttackStrategy
    AlienAttackStrategy <|.. AlternatingAttackStrategy
    AlienAttackStrategy <|.. BurstAttackStrategy
    LevelDirector ..> MovementStrategy : selecciona
    LevelDirector ..> AlienAttackStrategy : selecciona
```

## GM-8 Template Method

**Problema:** las pantallas necesitan mantener el mismo orden de limpieza, entrada, actualización y dibujo.

**Contexto:** menú, partida, Game Over y Victoria comparten el ciclo, pero cada una presenta y actualiza contenido distinto.
**Solución:** `AbstractGameScreen.render(delta)` fija el algoritmo y delega los pasos variables en métodos protegidos.

```mermaid
classDiagram
    class ScreenAdapter {
        <<LibGDX>>
    }
    class AbstractGameScreen {
        <<abstract>>
        +render(delta) final
        #clearScreen()
        #handleInput(delta) abstract
        #update(delta) abstract
        #draw(delta) abstract
    }
    class MenuScreen
    class GameScreen
    class GameOverScreen
    class VictoryScreen

    ScreenAdapter <|-- AbstractGameScreen
    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    AbstractGameScreen <|-- GameOverScreen
    AbstractGameScreen <|-- VictoryScreen
```

## GM-9 Builder

**Problema:** un alien requiere varios valores que deben formar una configuración válida.

**Contexto:** los sectores y arquetipos combinan posición, tamaño, vida, movimiento y ataque.
**Solución:** `AlienBuilder` permite definir esos valores por pasos y los valida en `build()`. `LevelDirector` decide las combinaciones para cada oleada y arquetipo.

```mermaid
classDiagram
    class AlienFactory {
        <<interface>>
    }
    class LevelDirector {
        <<Director>>
    }
    class AlienBuilder {
        -positionDefined: boolean
        -hitPoints: int
        -movement: MovementStrategy
        -attackStrategy: AlienAttackStrategy
        +position(x, y) AlienBuilder
        +size(width, height) AlienBuilder
        +movement(strategy) AlienBuilder
        +hitPoints(value) AlienBuilder
        +attack(strategy) AlienBuilder
        +build() Alien
    }
    class Alien

    AlienFactory <|.. LevelDirector
    LevelDirector ..> AlienBuilder : configura y usa
    AlienBuilder ..> Alien : crea
```

## Notas de lectura

- Los atributos de las instancias se muestran como privados cuando forman parte del estado encapsulado. Las constantes públicas de `GameConfig` se muestran aparte.
- `GameAssets` carga recursos con mapas propios de `Texture`, `Music` y `Sound`; no usa `AssetManager`.
- `ScoreRepository` implementa `ScoreStore` y guarda en LibGDX `Preferences`.
- Los datos que se dibujan durante la partida llegan a las pantallas mediante `WorldSnapshot`; las colecciones expuestas son de solo lectura.
- `puppy.code.prototype` queda como referencia del código base y no como parte del flujo activo.
