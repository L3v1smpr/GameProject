
# UML Completo

Para poder leerlo cómodamente, realizamos una descomposición del diagrama en clases relacionadas. Más Abajo...


```mermaid
classDiagram
    direction LR

    namespace lwjgl3 {
        class Lwjgl3Launcher {
            +main(args) void
            -createApplication() Lwjgl3Application
            -getDefaultConfiguration() Lwjgl3ApplicationConfiguration
        }
        class StartupHelper {
            +startNewJvmIfRequired() boolean
            +startNewJvmIfRequired(redirectOutput: boolean) boolean
        }
    }

    namespace app {
        class SpaceNavigation {
            -assets: GameAssets
            -navigationInput: NavigationInput
            -initialsInput: InitialsInput
            -playerInput: PlayerInput
            -levelDirector: LevelDirector
            -collisionSystem: CollisionSystem
            -scoreRepository: ScoreStore
            -currentWorld: GameWorld
            +create() void
            +showMenu() void
            +showGame() void
            +showGameOver() void
            +showGameOver(finalScore: int, levelReached: int) void
            +showVictory() void
            +showVictory(finalScore: int) void
            +dispose() void
        }
        class ScreenNavigator {
            <<interface>>
            +showMenu() void
            +showGame() void
            +showGameOver() void
            +showGameOver(finalScore: int, levelReached: int) void
            +showVictory() void
            +showVictory(finalScore: int) void
        }
    }

    namespace assets {
        class GameAssets {
            <<Singleton>>
            -instance: GameAssets
            -textures: Map
            -sounds: Map
            -music: Map
            -playerFrames: TextureRegion
            -batch: SpriteBatch
            -shapes: ShapeRenderer
            -font: BitmapFont
            -musicVolume: float
            -soundVolume: float
            -GameAssets()
            +getInstance() GameAssets
            +getPlayerFrame(row: int, column: int) TextureRegion
            +getAlienStaticRegion(firing: boolean) TextureRegion
            +getAlienSentryRegion(firing: boolean) TextureRegion
            +getAlienBomberRegion(firing: boolean) TextureRegion
            +getAlienAmmoRegion() TextureRegion
            +getTexture(path: String) Texture
            +getSound(path: String) Sound
            +getMusic(path: String) Music
            +batch() SpriteBatch
            +shapes() ShapeRenderer
            +font() BitmapFont
            +getMusicVolume() float
            +setMusicVolume(volume: float) void
            +getSoundVolume() float
            +setSoundVolume(volume: float) void
            +playSound(path: String, volume: float) void
            +playMusic(path: String, loop: boolean, volume: float) void
            +stopMusic() void
            +dispose() void
        }
    }

    namespace config {
        class GameConfig {
            <<constants>>
            +WORLD_WIDTH: float
            +WORLD_HEIGHT: float
            +SHIP_SPEED: float
            +DASH_DURATION_SECONDS: float
            +DASH_COOLDOWN_SECONDS: float
            +INITIAL_LIVES: int
            +LEVEL_ONE_ALIEN_QUOTA: int
            +LEVEL_TWO_ALIEN_QUOTA: int
            +LEVEL_THREE_ALIEN_QUOTA: int
            +ALIEN_SCORE: int
            -GameConfig()
        }
    }

    namespace input {
        class PlayerInput {
            <<interface>>
            +movementDirection() Vector2
            +firePressed() boolean
            +fireJustPressed() boolean
            +dashJustPressed() boolean
        }
        class KeyboardPlayerInput
        class NavigationInput {
            <<interface>>
            +confirmJustPressed() boolean
            +backJustPressed() boolean
            +anyActionJustPressed() boolean
            +upJustPressed() boolean
            +downJustPressed() boolean
            +leftJustPressed() boolean
            +rightJustPressed() boolean
        }
        class KeyboardNavigationInput
        class InitialsInput {
            <<interface>>
            +letterJustPressed() char
            +deleteJustPressed() boolean
        }
        class KeyboardInitialsInput
    }

    namespace screens {
        class AbstractGameScreen {
            <<abstract>>
            #assets: GameAssets
            #camera: OrthographicCamera
            #viewport: Viewport
            #starfield: Starfield
            +render(delta: float) void
            +resize(width: int, height: int) void
            #clearScreen() void
            #renderBackground(delta: float) void
            #handleInput(delta: float) void
            #update(delta: float) void
            #draw(delta: float) void
        }
        class MenuScreen {
            -navigator: ScreenNavigator
            -navigationInput: NavigationInput
            -scoreRepository: ScoreStore
            -startRequested: boolean
        }
        class GameScreen {
            -navigator: ScreenNavigator
            -world: GameWorld
            -playerInput: PlayerInput
            -navigationInput: NavigationInput
            -snapshot: WorldSnapshot
            -paused: boolean
            -pauseState: PauseMenuState
            -activeTransition: TransitionType
            -sectorCompletedTimer: float
            -afterImages: List~AfterImage~
            -fireRings: List~FireRing~
            -playerRings: List~PlayerRing~
            -debrisParticles: List~DebrisParticle~
            +show() void
            +hide() void
            +onPlayerShoot() void
            +onPlayerDash() void
            +onPlayerHurt(centerX: float, centerY: float) void
            +onAlienHurt() void
            +onAlienDestroyed(centerX: float, centerY: float) void
            +onAlienShoot() void
            +onMeteoriteHit() void
            +onMeteoriteDestroyed(centerX: float, centerY: float, isMajor: boolean) void
            +onLevelCompleted() void
        }
        class GameOverScreen {
            -navigator: ScreenNavigator
            -input: NavigationInput
            -initialsInput: InitialsInput
            -scoreRepository: ScoreStore
            -finalScore: int
            -levelReached: int
            -eligibleForTop3: boolean
            -initials: StringBuilder
            -scoreSaved: boolean
            +show() void
            +hide() void
            #clearScreen() void
            #renderBackground(delta: float) void
        }
        class VictoryScreen {
            -navigator: ScreenNavigator
            -input: NavigationInput
            -initialsInput: InitialsInput
            -scoreRepository: ScoreStore
            -finalScore: int
            -eligibleForTop3: boolean
            -initials: StringBuilder
            -scoreSaved: boolean
            +show() void
            +hide() void
        }
        class Starfield {
            +updateAndRender(delta: float, shapes: ShapeRenderer, camera: OrthographicCamera) void
        }
        class AfterImage {
            -x: float
            -y: float
            -width: float
            -height: float
            -region: TextureRegion
            -lifeTime: float
            -alpha: float
            +update(delta: float) boolean
            +getAlpha() float
        }
        class PlayerRing {
            -radius: float
            -timer: float
            +update(delta: float) boolean
        }
        class FireRing {
            -radius: float
            -timer: float
            +update(delta: float) boolean
        }
        class DebrisParticle {
            -x: float
            -y: float
            -size: float
            -timer: float
            +update(delta: float) boolean
        }
        class TransitionType {
            <<enum>>
            NONE
            GAME_OVER_FADE
            VICTORY_FADE
        }
        class PauseMenuState {
            <<enum>>
            MAIN
            OPTIONS
            DEBUG
        }
        class KonamiKey {
            <<enum>>
            UP
            DOWN
            LEFT
            RIGHT
            FIRE
            DASH
        }
    }

    namespace world {
        class GameWorld {
            -collisions: CollisionDetector
            -levelDirector: AlienFactory
            -playerWeapon: PlayerWeapon
            -ship: Ship
            -aliens: List~Alien~
            -meteorites: List~Meteorite~
            -playerProjectiles: List~PlayerProjectile~
            -alienProjectiles: List~AlienProjectile~
            -level: int
            -lives: int
            -score: int
            -aliensDestroyedInLevel: int
            -phase: GamePhase
            -audioListener: WorldAudioListener
            +GameWorld(levelDirector: AlienFactory, collisions: CollisionDetector)
            +restart() void
            +update(delta: float, input: PlayerInput) void
            +advanceToNextLevel() void
            +snapshot() WorldSnapshot
            +setAudioListener(audioListener: WorldAudioListener) void
            +isGodMode() boolean
            +setGodMode(godMode: boolean) void
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
            -aliens: List~EntityView~
            -playerProjectiles: List~EntityView~
            -alienProjectiles: List~EntityView~
            -meteorites: List~EntityView~
            -lives: int
            -score: int
            -level: int
            -aliensDestroyedInLevel: int
            -quotaForLevel: int
            -phase: GamePhase
            -shipInvulnerable: boolean
            -shipDashReady: boolean
            -dashCooldownProgress: float
            -godMode: boolean
            +getShip() EntityView
            +getAliens() List~EntityView~
            +getPlayerProjectiles() List~EntityView~
            +getAlienProjectiles() List~EntityView~
            +getMeteorites() List~EntityView~
            +getLives() int
            +getScore() int
            +getLevel() int
            +getPhase() GamePhase
        }
        class EntityView {
            <<value object>>
            -x: float
            -y: float
            -width: float
            -height: float
            -assetPath: String
            -rotation: float
            -firing: boolean
            -hurt: boolean
            -isStatic: boolean
            -isBomber: boolean
        }
        class WorldAudioListener {
            <<interface>>
            +onPlayerShoot() void
            +onPlayerDash() void
            +onPlayerHurt(centerX: float, centerY: float) void
            +onAlienHurt() void
            +onAlienDestroyed(centerX: float, centerY: float) void
            +onAlienShoot() void
            +onMeteoriteHit() void
            +onMeteoriteDestroyed(centerX: float, centerY: float, isMajor: boolean) void
            +onLevelCompleted() void
        }
    }

    namespace entities {
        class Collidable {
            <<interface>>
            +bounds() Rectangle
        }
        class GameEntity {
            <<abstract>>
            -x: float
            -y: float
            -width: float
            -height: float
            -active: boolean
            -hurtTimer: float
            #GameEntity(x: float, y: float, width: float, height: float)
            +getX() float
            +getY() float
            +getWidth() float
            +getHeight() float
            +isActive() boolean
            +deactivate() void
            +isHurt() boolean
            +takeDamage() boolean
            +bounds() Rectangle
            #setPosition(x: float, y: float) void
            #setPositionInside(x: float, y: float, worldWidth: float, worldHeight: float) void
        }
        class Ship {
            -speed: float
            -invulnerabilityTimer: float
            -dashCooldownTimer: float
            -dashDurationTimer: float
            -godMode: boolean
            +bounds() Rectangle
            +update(delta: float, movementDirection: Vector2, worldWidth: float, worldHeight: float) void
            +triggerDash(inputDirection: Vector2) void
            +respawn(startX: float, startY: float, invulnerabilitySeconds: float) void
            +isInvulnerable() boolean
            +canDash() boolean
            +getDashCooldownProgress() float
            +isGodMode() boolean
            +setGodMode(godMode: boolean) void
        }
        class Alien {
            -hitPoints: int
            -movement: MovementStrategy
            -attackStrategy: AlienAttackStrategy
            -age: float
            -fireCooldownRemaining: float
            -firingTimer: float
            +takeDamage() boolean
            +update(delta: float, worldWidth: float) void
            +fireDue(delta: float, target: Ship) List~AlienProjectile~
            +getHitPoints() int
            +isFiring() boolean
            +isStatic() boolean
            +isBomber() boolean
            +hasLeftWorld() boolean
        }
        class Meteorite {
            -size: MeteoriteSize
            -descentSpeed: float
            -hitPoints: int
            +random(worldWidth: float, worldHeight: float) Meteorite
            +takeDamage() boolean
            +update(delta: float) void
            +getSize() MeteoriteSize
            +getScoreValue() int
            +getAssetPath() String
        }
        class MeteoriteSize {
            <<enum>>
            SMALL
            MEDIUM
            LARGE
            -assetPath: String
            -width: float
            -height: float
            -hitPoints: int
            -scoreValue: int
        }
        class PlayerProjectile {
            -speed: float
            +update(delta: float) void
        }
        class AlienProjectile {
            -direction: Vector2
            -speed: float
            +update(delta: float) void
            +getRotation() float
        }
    }

    namespace behaviors {
        class MovementStrategy {
            <<interface>>
            +speed() float
            +nextPosition(x: float, y: float, age: float, delta: float) Vector2
        }
        class LinearMovementStrategy {
            -speed: float
        }
        class ZigZagMovementStrategy {
            -speed: float
            -halfPeriodSeconds: float
        }
        class StaticMovementStrategy {
            -targetY: float
            -descentSpeed: float
            -hasTarget: boolean
        }
        class HorizontalMovementStrategy {
            -cruiseY: float
            -speed: float
            -descentSpeed: float
            -alienWidth: float
            -directionX: float
        }
        class AlienAttackStrategy {
            <<interface>>
            +createVolley(alien: Alien, target: Ship) List~AlienProjectile~
            +nextCooldownSeconds() float
        }
        class LinearAttackStrategy {
            +validate(alien: Alien, target: Ship) void
            +directionTo(alien: Alien, target: Ship) Vector2
            +projectileFrom(alien: Alien, direction: Vector2) AlienProjectile
        }
        class VAttackStrategy
        class SpreadAttackStrategy
        class AlternatingAttackStrategy
        class BurstAttackStrategy {
            -shotsFiredInBurst: int
            +nextCooldownSeconds() float
        }
    }

    namespace factories {
        class AlienFactory {
            <<interface>>
            +createWave(level: int, count: int) List~Alien~
            +createStaticAlien(level: int, x: float, targetY: float) Alien
            +createBomberAlien(level: int, x: float, cruiseY: float) Alien
        }
        class LevelDirector {
            <<Director>>
            -linearAttack: AlienAttackStrategy
            -vAttack: AlienAttackStrategy
            -spreadAttack: AlienAttackStrategy
            -alternatingAttack: AlienAttackStrategy
            +createWave(level: int) List~Alien~
            +createWave(level: int, count: int) List~Alien~
            +createStaticAlien(level: int, x: float, targetY: float) Alien
            +createBomberAlien(level: int, x: float, cruiseY: float) Alien
        }
        class AlienBuilder {
            <<Builder>>
            -x: float
            -y: float
            -positionDefined: boolean
            -hitPoints: int
            -movement: MovementStrategy
            -attackStrategy: AlienAttackStrategy
            +position(x: float, y: float) AlienBuilder
            +size(width: float, height: float) AlienBuilder
            +movement(movement: MovementStrategy) AlienBuilder
            +hitPoints(hitPoints: int) AlienBuilder
            +attack(attackStrategy: AlienAttackStrategy) AlienBuilder
            +build() Alien
        }
    }

    namespace systems {
        class CollisionDetector {
            <<interface>>
            +collides(first: Collidable, second: Collidable) boolean
        }
        class CollisionSystem {
            +collides(first: Collidable, second: Collidable) boolean
            +collidingWith(subject: Collidable, candidates: List~T~) List~T~
        }
        class PlayerWeapon {
            -cooldownRemaining: float
            +update(delta: float, firePressed: boolean, ship: Ship) Optional~PlayerProjectile~
        }
        class ScoreStore {
            <<interface>>
            +getTopScores() List~HighScore~
            +isEligibleForTop3(score: int) boolean
            +addScore(name: String, score: int) boolean
        }
        class ScoreRepository {
            -prefs: Preferences
            +getTopScores() List~HighScore~
            +isEligibleForTop3(score: int) boolean
            +addScore(name: String, score: int) boolean
        }
        class HighScore {
            -name: String
            -score: int
            +getName() String
            +getScore() int
            +compareTo(other: HighScore) int
        }
    }

    namespace libgdx {
        class Game {
            <<LibGDX>>
        }
        class ScreenAdapter {
            <<LibGDX>>
        }
        class Lwjgl3Application {
            <<LibGDX>>
        }
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
        class Sound {
            <<LibGDX>>
        }
        class Music {
            <<LibGDX>>
        }
        class Preferences {
            <<LibGDX>>
        }
        class OrthographicCamera {
            <<LibGDX>>
        }
        class Viewport {
            <<LibGDX>>
        }
        class Rectangle {
            <<LibGDX>>
        }
        class Vector2 {
            <<LibGDX>>
        }
    }

    %% ---------- Herencia e implementacion ----------
    Game <|-- SpaceNavigation
    ScreenNavigator <|.. SpaceNavigation
    ScreenAdapter <|-- AbstractGameScreen
    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    AbstractGameScreen <|-- GameOverScreen
    AbstractGameScreen <|-- VictoryScreen
    WorldAudioListener <|.. GameScreen
    Collidable <|.. GameEntity
    GameEntity <|-- Ship
    GameEntity <|-- Alien
    GameEntity <|-- Meteorite
    GameEntity <|-- PlayerProjectile
    GameEntity <|-- AlienProjectile
    MovementStrategy <|.. LinearMovementStrategy
    MovementStrategy <|.. ZigZagMovementStrategy
    MovementStrategy <|.. StaticMovementStrategy
    MovementStrategy <|.. HorizontalMovementStrategy
    AlienAttackStrategy <|.. LinearAttackStrategy
    AlienAttackStrategy <|.. VAttackStrategy
    AlienAttackStrategy <|.. SpreadAttackStrategy
    AlienAttackStrategy <|.. AlternatingAttackStrategy
    AlienAttackStrategy <|.. BurstAttackStrategy
    AlienFactory <|.. LevelDirector
    CollisionDetector <|.. CollisionSystem
    PlayerInput <|.. KeyboardPlayerInput
    NavigationInput <|.. KeyboardNavigationInput
    InitialsInput <|.. KeyboardInitialsInput
    ScoreStore <|.. ScoreRepository

    %% ---------- Arranque y aplicacion ----------
    Lwjgl3Launcher ..> StartupHelper : usa
    Lwjgl3Launcher ..> Lwjgl3Application : crea
    Lwjgl3Launcher ..> SpaceNavigation : crea
    SpaceNavigation --> GameAssets : obtiene una vez
    SpaceNavigation --> GameWorld : conserva la partida
    SpaceNavigation --> LevelDirector
    SpaceNavigation --> CollisionSystem
    SpaceNavigation --> PlayerInput
    SpaceNavigation --> NavigationInput
    SpaceNavigation --> InitialsInput
    SpaceNavigation --> ScoreStore
    SpaceNavigation ..> KeyboardPlayerInput : crea
    SpaceNavigation ..> KeyboardNavigationInput : crea
    SpaceNavigation ..> KeyboardInitialsInput : crea
    SpaceNavigation ..> ScoreRepository : crea
    SpaceNavigation ..> MenuScreen : crea
    SpaceNavigation ..> GameScreen : crea
    SpaceNavigation ..> GameOverScreen : crea
    SpaceNavigation ..> VictoryScreen : crea

    %% ---------- Recursos ----------
    GameAssets *-- SpriteBatch
    GameAssets *-- ShapeRenderer
    GameAssets *-- BitmapFont
    GameAssets o-- Texture
    GameAssets o-- TextureRegion
    GameAssets o-- Sound
    GameAssets o-- Music

    %% ---------- Pantallas ----------
    AbstractGameScreen --> GameAssets
    AbstractGameScreen *-- Starfield
    AbstractGameScreen --> OrthographicCamera
    AbstractGameScreen --> Viewport
    MenuScreen --> ScreenNavigator
    MenuScreen --> NavigationInput
    MenuScreen --> ScoreStore
    GameScreen --> ScreenNavigator
    GameScreen --> GameWorld : actualiza y consulta
    GameScreen --> WorldSnapshot : presenta
    GameScreen --> PlayerInput
    GameScreen --> NavigationInput
    GameScreen *-- AfterImage
    GameScreen *-- FireRing
    GameScreen *-- PlayerRing
    GameScreen *-- DebrisParticle
    GameScreen --> PauseMenuState
    GameScreen --> TransitionType
    GameScreen ..> KonamiKey
    GameOverScreen --> ScreenNavigator
    GameOverScreen --> NavigationInput
    GameOverScreen --> InitialsInput
    GameOverScreen --> ScoreStore
    VictoryScreen --> ScreenNavigator
    VictoryScreen --> NavigationInput
    VictoryScreen --> InitialsInput
    VictoryScreen --> ScoreStore

    %% ---------- Mundo de juego ----------
    GameWorld "1" *-- "1" Ship
    GameWorld "1" o-- "*" Alien
    GameWorld "1" o-- "*" Meteorite
    GameWorld "1" o-- "*" PlayerProjectile
    GameWorld "1" o-- "*" AlienProjectile
    GameWorld *-- PlayerWeapon
    GameWorld --> CollisionDetector
    GameWorld --> AlienFactory
    GameWorld --> GamePhase
    GameWorld --> WorldAudioListener : notifica eventos
    GameWorld ..> PlayerInput : lee acciones
    GameWorld ..> WorldSnapshot : crea
    WorldSnapshot *-- EntityView
    WorldSnapshot --> GamePhase
    EntityView ..> GameEntity : proyecta
    PlayerWeapon ..> PlayerProjectile : crea
    PlayerWeapon ..> Ship
    CollisionDetector ..> Collidable
    CollisionSystem ..> Collidable

    %% ---------- Entidades ----------
    Collidable ..> Rectangle
    Ship ..> Vector2
    AlienProjectile --> Vector2
    Meteorite --> MeteoriteSize
    Alien --> MovementStrategy
    Alien --> AlienAttackStrategy
    Alien ..> AlienProjectile : dispara
    Alien ..> Ship : apunta a

    %% ---------- Estrategias, fabrica y builder ----------
    MovementStrategy ..> Vector2
    AlienAttackStrategy ..> AlienProjectile
    VAttackStrategy ..> LinearAttackStrategy : usa utilidades
    SpreadAttackStrategy ..> LinearAttackStrategy : usa utilidades
    AlternatingAttackStrategy ..> LinearAttackStrategy : usa utilidades
    BurstAttackStrategy ..> LinearAttackStrategy : usa utilidades
    LevelDirector ..> AlienBuilder : configura y usa
    LevelDirector ..> MovementStrategy : selecciona
    LevelDirector ..> AlienAttackStrategy : selecciona
    AlienBuilder --> MovementStrategy
    AlienBuilder --> AlienAttackStrategy
    AlienBuilder ..> Alien : construye

    %% ---------- Entradas y puntajes ----------
    PlayerInput ..> Vector2
    ScoreRepository --> Preferences
    ScoreRepository ..> HighScore : crea
    ScoreStore ..> HighScore

    %% ---------- Notas de patrones ----------
    note for GameAssets "GM-6 Singleton: instancia unica, constructor privado y getInstance estatico y sincronizado"
    note for MovementStrategy "GM-7 Strategy: 4 variantes de movimiento. Alien las recibe sin conocer la clase concreta"
    note for AlienAttackStrategy "GM-7 Strategy: 5 variantes de ataque."
    note for AbstractGameScreen "GM-8 Template Method: render es final y ejecuta clearScreen, renderBackground, handleInput, update y draw"
    note for AlienBuilder "GM-9 Builder: valida posicion, estrategias, velocidad y vida en build. LevelDirector es el Director"
    note for GameConfig "Constantes usadas por entidades, mundo, fabricas, sistemas y pantallas."
    note for GameWorld "Controller: coordina simulacion, vidas, puntaje, cuotas y fases"
```



#Facilitación de Lectura :



### UML del proyecto: Space Navigation - Lectura Simple

**Lenguaje:** Java 11 · **Motor:** LibGDX

## Clases de aplicacion, pantallas y recursos

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

## Dominio y coordinacion de la partida

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

**Problema:** las pantallas comparten texturas, audio y objetos de dibujo; varias instancias podrian duplicar recursos o liberar recursos que todavia se usan.

**Contexto:** `SpaceNavigation` crea la aplicacion y conecta las dependencias.
**Solucion:** `GameAssets` mantiene una instancia, tiene constructor privado y libera sus recursos en `dispose()`. El punto de composicion obtiene esa instancia y la entrega a las pantallas.

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

**Problema:** las trayectorias y descargas varian por tipo de alien y sector.

**Contexto:** `Alien` participa en el mundo sin necesitar conocer cada algoritmo de movimiento o ataque.
**Solucion:** recibe los contratos `MovementStrategy` y `AlienAttackStrategy`; `LevelDirector` selecciona las variantes al construir la oleada.

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

**Problema:** las pantallas necesitan mantener el mismo orden de limpieza, entrada, actualizacion y dibujo.

**Contexto:** menu, partida, Game Over y Victoria comparten el ciclo, pero cada una presenta y actualiza contenido distinto.
**Solucion:** `AbstractGameScreen.render(delta)` fija el algoritmo y delega los pasos variables en metodos protegidos.

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

**Problema:** un alien requiere varios valores que deben formar una configuracion valida.

**Contexto:** los sectores y arquetipos combinan posicion, tamano, vida, movimiento y ataque.
**Solucion:** `AlienBuilder` permite definir esos valores por pasos y los valida en `build()`. `LevelDirector` decide las combinaciones para cada oleada y arquetipo.

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

- Los atributos de las instancias se muestran como privados cuando forman parte del estado encapsulado. Las constantes publicas de `GameConfig` se muestran aparte.
- `GameAssets` carga recursos con mapas propios de `Texture`, `Music` y `Sound`; no usa `AssetManager`.
- `ScoreRepository` implementa `ScoreStore` y guarda en LibGDX `Preferences`.
- Los datos que se dibujan durante la partida llegan a las pantallas mediante `WorldSnapshot`; las colecciones expuestas son de solo lectura.
- `puppy.code.prototype` queda como referencia del codigo base y no como parte del flujo activo.




