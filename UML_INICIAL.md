# Space Navigation — estructura inicial

Este diagrama refleja las clases que ya usamos como punto de partida para el nuevo flujo de escritorio. Las figuras de la nave y el alien son temporales; todavía no representan el combate ni los recursos finales.

```mermaid
classDiagram
    class Lwjgl3Launcher

    class SpaceNavigation {
        -assets: GameAssets
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
        +render(delta: float) final
        #updateScreen(delta: float) abstract
        #drawScreen() abstract
    }

    class MenuScreen
    class GameScreen

    class PlayerInput {
        <<interface>>
        +movementDirection() Vector2
    }

    class KeyboardPlayerInput

    class GameWorld {
        +update(delta: float, input: PlayerInput) void
        +snapshot() WorldSnapshot
    }

    class WorldSnapshot {
        -ship: EntityView
        -aliens: List~EntityView~
        +getShip() EntityView
        +getAliens() List~EntityView~
    }

    class GameEntity {
        <<abstract>>
        -x: float
        -y: float
        -width: float
        -height: float
        +bounds() Rectangle
    }

    class Ship {
        +update(delta: float, direction: Vector2, worldWidth: float, worldHeight: float) void
    }

    class Alien {
        +update(delta: float, worldWidth: float) void
    }

    Lwjgl3Launcher --> SpaceNavigation : inicia
    SpaceNavigation --|> Game
    SpaceNavigation ..|> ScreenNavigator
    SpaceNavigation --> GameAssets : crea y libera
    SpaceNavigation --> MenuScreen : muestra
    SpaceNavigation --> GameScreen : inicia

    AbstractGameScreen <|-- MenuScreen
    AbstractGameScreen <|-- GameScreen
    AbstractGameScreen --> GameAssets : usa recursos compartidos
    MenuScreen --> ScreenNavigator : solicita transiciones
    GameScreen --> ScreenNavigator : solicita transiciones

    KeyboardPlayerInput ..|> PlayerInput
    GameScreen --> GameWorld : actualiza y consulta
    GameScreen --> PlayerInput : recibe control
    GameWorld *-- Ship : posee
    GameWorld *-- Alien : posee
    GameWorld --> WorldSnapshot : crea copia de lectura
    Ship --|> GameEntity
    Alien --|> GameEntity
```

## Responsabilidades actuales

- `app.SpaceNavigation` inicia el juego y conecta sus dependencias. Es el único lugar donde consultamos el Singleton de `GameAssets`.
- `ScreenNavigator` limita las transiciones que pueden solicitar las pantallas.
- `GameAssets` es dueño del `SpriteBatch`, la fuente y el `ShapeRenderer`; las pantallas los usan, pero no los liberan.
- `AbstractGameScreen` fija el orden limpiar, actualizar y dibujar. `MenuScreen` y `GameScreen` implementan el comportamiento específico.
- `PlayerInput` describe la intención de movimiento. `KeyboardPlayerInput` es el adaptador que lee las teclas.
- `GameWorld` mantiene privadas sus entidades, actualiza la nave y el alien y produce un `WorldSnapshot`.
- `WorldSnapshot` copia los valores necesarios para dibujar. No expone las entidades ni una colección mutable.
- `GameEntity` mantiene privada la posición y entrega una copia de sus límites.

## Siguiente etapa

Todavía tenemos que definir e implementar proyectiles, colisiones, puntaje, vidas, cuotas de nivel, dash, victoria y Game Over. Incorporaremos `MovementStrategy`, Builder y persistencia cuando tengamos reglas concretas que justifiquen esas abstracciones. El código anterior se conserva en el paquete `puppy.code.prototype` y no lo usa el lanzador nuevo.
