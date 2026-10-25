# Reporte y Narrativa de Operación: Space Navigation (Fuga del Régimen)

**Enlace del Repositorio:** [https://github.com/L3v1smpr/GameProject.git](https://github.com/L3v1smpr/GameProject.git) *(Destinado a la rama `main` tras el merge final de entrega)*[cite: 58]

En los confines de un sistema estelar dominado por un régimen prácticamente cruel y despiadado, una nave de la resistencia clandestina emprende la huida definitiva[cite: 58]. Para alcanzar la libertad, la tripulación debe atravesar tres sectores de una ruta espacial hostil, donde la flota tiránica incrementa su asedio constante[cite: 58]. Este documento fusiona la narrativa de escape con el reporte técnico de desarrollo del videojuego bajo las bases del proyecto[cite: 58].

---

## Alcance Operativo de la Fuga

* Presentamos un arcade 2D para un jugador enfocado en la supervivencia y la evasión del bloqueo militar[cite: 58].
* Pilotamos la insignia a través de tres sectores hiperespaciales vigilados; debemos despejar la cuota de naves enemigas en cada tramo para abrirnos paso[cite: 58].
* La misión culmina con la **Victoria** absoluta al completar el tercer sector o con un trágico **Game Over** si las fuerzas del régimen destruyen la nave al agotar sus tres vidas[cite: 58].
* El código primitivo del viejo simulador de pruebas se archiva en `core/src/main/java/puppy/code/prototype` como vestigio histórico, mientras que la aplicación moderna arranca oficialmente desde `puppy.code.app.SpaceNavigation` y se despliega mediante el paquete de pantallas `screens`[cite: 58].

---

## GM-1 - El Piloto en Perspectiva (Experiencia de Juego)

La lucha por la libertad se libra bajo estrictas condiciones tácticas en la cabina[cite: 58]:
* Comenzamos la misión con tres vidas operativas para resistir la ofensiva[cite: 58].
* El pilotaje se ejecuta con las teclas WASD, los cañones láser responden a la tecla I y el sistema de propulsión de emergencia (Dash) se acciona con O[cite: 58].
* El panel HUD integrado reporta en tiempo real el sector actual, la cuota de enemigos abatidos y el progreso de la huida[cite: 58].
* En el trayecto nos topamos con fuerzas hostiles del régimen: naves de oleada estándar, centinelas fijos, bombarderos de patrulla y meteoritos de los campos minados espaciales[cite: 58].
* Al asegurar los sectores uno y dos, el sistema proyecta la alerta de «Nivel Superado» en espera de orden manual, mientras que el sector final conduce directamente a la gloria de la Victoria[cite: 58].
* Los invasores de oleada portan 1, 2 o 3 puntos de integridad según la peligrosidad del sector, empleando trayectorias lineales en el primero y patrones en zigzag en los subsiguientes[cite: 58].
* Los centinelas actúan como puestos de bloqueo estacionarios, y los bombarderos cruzan el firmamento patrullando horizontalmente con tácticas de fuego avanzadas[cite: 58].
* Los campos de meteoritos no forman parte de la cuota militar, pero destruirlos otorga recursos en forma de puntaje (100 puntos por cada nave del régimen abatida; 25, 50 o 100 puntos por desintegrar meteoritos chicos, medianos o grandes)[cite: 58].
* Si la nave sufre daños fatales, el sistema de emergencia la reubica en la posición de salto inicial dotándola de 1,5 segundos de invulnerabilidad táctica[cite: 58].
* La maniobra de Dash otorga un impulso hiperveloz de 0,15 segundos con un tiempo de recarga de 3 segundos, concediendo 0,5 segundos adicionales de protección al finalizar el desplazamiento[cite: 58].
* La velocidad base de crucero de la nave se calibra en 390 px/s con un multiplicador de dash de factor 3[cite: 58].
* Los récords de puntuación de los pilotos caídos o triunfadores se registran en el Top 3 local ingresando iniciales de tres letras (A-Z), corrigiendo con Retroceso y validando con Enter[cite: 58].
* La tecla Escape despliega el menú de pausa táctica, permitiendo reanudar la marcha, regular los niveles de audio o abortar la misión volviendo al menú principal[cite: 58].
* Como protocolo de emergencia para los ingenieros de la resistencia, introducir la secuencia secreta del Código Konami ($\uparrow \uparrow \downarrow \downarrow \leftarrow \rightarrow \leftarrow \rightarrow \text{I O}$) durante la pausa destraba un panel oculto de depuración capaz de activar el Modo Dios (invulnerabilidad total)[cite: 58].
* Los planos tácticos y bocetos conceptuales de la interfaz, el combate y las pantallas de cierre se archivan en `Planificacion/Bocetos/B1.jpeg` hasta `B4.jpeg`[cite: 58].

---

## GM-2 - Evolución de la Flota (Juego Base frente a la Nueva Arquitectura)

* El esqueleto de pruebas original residía en el paquete `puppy.code.prototype`, donde la clase `PantallaJuego` centralizaba toda la simulación, el control gráfico y las listas de entidades en un solo lugar[cite: 58].
* La versión actual desacopla por completo la simulación táctica: `GameWorld` gestiona las reglas físicas, la salud y el avance de sector; `GameScreen` procesa la interfaz visual interpretando un `WorldSnapshot` inmutable; `PlayerWeapon` regula la cadencia de fuego; `CollisionSystem` calcula las intercepciones; y `LevelDirector` programa las oleadas del enemigo[cite: 58].
* Se expandió el sistema para incorporar tres sectores distintos, múltiples comportamientos de ataque y movimiento, centinelas, bombarderos, campos de meteoritos, propulsión dash, menús de pausa interactivos, efectos visuales avanzados, pantallas de cierre y un sistema persistente de récords locales[cite: 58].

---

## GM-3 - Planos Estelares (UML y Estructura de Código)

* El diagrama detallado de clases y relaciones de diseño se documenta en el archivo `UML_INICIAL.md`[cite: 58].
* El código fuente se encuentra estructurado limpiamente en los paquetes lógicos `app`, `assets`, `behaviors`, `config`, `entities`, `factories`, `input`, `screens`, `systems` y `world`, apuntando al lanzador de escritorio ubicado en `lwjgl3/src/main/java/puppy/code/lwjgl3/Lwjgl3Launcher.java`[cite: 58].

---

## GM-4 - El Arsenal de Abstracción (Clases Abstractas e Interfaces)

* La clase abstracta `GameEntity` sirve como cimiento estructural para todas las unidades móviles del espacio: `Ship`, `Alien`, `Meteorite`, `PlayerProjectile` y `AlienProjectile`[cite: 58].
* La clase abstracta `AbstractGameScreen` estandariza el flujo de renderizado para las pantallas operativas `MenuScreen`, `GameScreen`, `GameOverScreen` y `VictoryScreen`[cite: 58].
* El comportamiento dinámico se rige mediante contratos de interfaz: `MovementStrategy` con sus cuatro variantes (`LinearMovementStrategy`, `ZigZagMovementStrategy`, `StaticMovementStrategy`, `HorizontalMovementStrategy`) y `AlienAttackStrategy` con sus cinco modalidades tácticas (`LinearAttackStrategy`, `VAttackStrategy`, `SpreadAttackStrategy`, `AlternatingAttackStrategy`, `BurstAttackStrategy`)[cite: 58].
* Se complementa el motor con interfaces de soporte como `Collidable` para las colisiones geométricas y `WorldAudioListener` para la transmisión de eventos sonoros del mundo a la interfaz[cite: 58].

---

## GM-5 - Protocolos de Blindaje (Encapsulamiento, GRASP y SOLID)

### Encapsulamiento
* El estado interno de las entidades y del mundo se mantiene rigurosamente privado[cite: 58].
* `GameWorld` protege sus colecciones entregando únicamente un `WorldSnapshot` con vistas de solo lectura (`EntityView`)[cite: 58].
* `GameAssets` distribuye los fotogramas de la nave mediante índices controlados, ocultando la matriz cruda del spritesheet[cite: 58].
* Los parámetros globales de balance se centralizan de forma inmutable en `GameConfig`[cite: 58].

### Principios GRASP
* **Controller e Information Expert:** `GameWorld` procesa las órdenes de control y administra las consecuencias globales, mientras cada entidad gobierna su propia integridad y desplazamiento[cite: 58].
* **Creator:** `LevelDirector` concentra los parámetros tácticos para ensamblar los enemigos empleando al constructor especializado[cite: 58].
* **Pure Fabrication:** Componentes como `CollisionSystem`, `PlayerWeapon`, `ScoreRepository` y `GameAssets` aíslan tareas técnicas especializadas ajenas a las entidades del dominio[cite: 58].
* **Low Coupling e Indirection:** El mundo interactúa mediante contratos abstractos como `CollisionDetector` y `AlienFactory`, mientras que las pantallas se comunican con el exterior a través de `ScreenNavigator`, `NavigationInput` y `ScoreStore`[cite: 58].

### Principios SOLID
* **SRP (Responsabilidad Única):** Cada módulo posee un único motivo de cambio (la simulación en el mundo, las colisiones en el sistema físico, los puntajes en el repositorio)[cite: 58].
* **OCP (Abierto/Cerrado):** Es posible incorporar nuevas trayectorias o métodos de fuego creando estrategias adicionales sin alterar el código central de los enemigos[cite: 58].
* **LSP (Sustitución de Liskov):** Las clases derivadas de `GameEntity` respetan íntegramente el contrato geométrico de `Collidable`[cite: 58].
* **ISP (Segregación de Interfaces):** Los contratos de entrada, navegación, puntajes y audio se encuentran divididos en interfaces específicas según lo requiera cada cliente[cite: 58].
* **DIP (Inversión de Dependencias):** El núcleo del mundo depende de abstracciones como `AlienFactory` y `CollisionDetector`, delegando la inyección de implementaciones concretas al componente de arranque `SpaceNavigation`[cite: 58].

---

## GM-6 a GM-9 - Los Núcleos Tecnológicos (Patrones de Diseño)

### GM-6 - Singleton (`GameAssets`)
* **Problema:** Evitar la duplicación de recursos pesados en memoria y prevenir conflictos en la liberación de texturas y audios[cite: 58].
* **Contexto:** Las distintas pantallas operativas comparten constantemente texturas y efectos sonoros[cite: 58].
* **Solución:** `SpaceNavigation` adquiere una única instancia global a través de `GameAssets.getInstance()`, controlando un constructor privado y liberando los recursos centralmente mediante `dispose()`[cite: 58].

### GM-7 - Strategy (Estrategias de Movimiento y Ataque)
* **Problema:** La variedad de trayectorias y patrones ofensivos de los enemigos no debe sobrecargar la clase `Alien` con condicionales complejos[cite: 58].
* **Contexto:** El simulador procesa múltiples tipos de naves enemigas simultáneamente[cite: 58].
* **Solución:** `Alien` recibe por inyección las interfaces `MovementStrategy` y `AlienAttackStrategy`, delegando en ellas el cálculo de posición y cadencia ofensiva de forma polimórfica[cite: 58].

### GM-8 - Template Method (`AbstractGameScreen`)
* **Problema:** Garantizar que el menú, la partida y las pantallas de cierre ejecuten siempre la misma secuencia operativa por cada cuadro de renderizado[cite: 58].
* **Contexto:** Todas las pantallas activas comparten una estructura de ciclo de vida común[cite: 58].
* **Solución:** `AbstractGameScreen.render(float delta)` se declara como método final para fijar el orden estricto de limpieza, entrada, actualización y dibujo, delegando los detalles específicos a los métodos abstractos de cada pantalla[cite: 58].

### GM-9 - Builder (`AlienBuilder` y `LevelDirector`)
* **Problema:** La construcción de un enemigo requiere múltiples parámetros interdependientes que deben validarse antes de su despliegue[cite: 58].
* **Contexto:** Los sectores del espacio exigen la fabricación dinámica de arquetipos complejos[cite: 58].
* **Solución:** `AlienBuilder` permite ensamblar las propiedades de la nave por pasos encadenados asegurando su integridad mediante validaciones previas, bajo la dirección estratégica de `LevelDirector`[cite: 58].

---

## GM-10 - Telemetría y Despliegue (GitHub y Control de Versiones)

* **Enlace del Repositorio:** [https://github.com/L3v1smpr/GameProject.git](https://github.com/L3v1smpr/GameProject.git) *(Destinado a la rama `main` tras el merge final de entrega)*[cite: 58].
* El desarrollo colaborativo se estructuró bajo ramas de trabajo (como `feature/overhaul-assets`), registrando un historial consolidado de más de seis commits de avance técnico frente a los puntos de control del repositorio[cite: 58].
* La persistencia de los registros locales opera mediante el mecanismo de `Preferences` de LibGDX en el equipo anfitrión[cite: 58].
* Para compilar y poner en órbita la aplicación empleando Java 11, se ejecutan los comandos del gestor de dependencias de Gradle: `gradlew.bat :lwjgl3:run` en entornos Windows o `./gradlew :lwjgl3:run` en sistemas Linux y macOS[cite: 58].