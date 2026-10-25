# Reporte y Narrativa de Operacion: Space Navigation (Fuga del Regimen)

En los confines de un sistema estelar dominado por un regimen practicamente cruel y despiadado, una nave de la resistencia clandestina emprende la huida definitiva. Para alcanzar la libertad, la tripulacion debe atravesar tres sectores de una ruta espacial hostil, donde la flota tiranica incrementa su asedio constante. Este documento fusiona la narrativa de escape con el reporte tecnico de desarrollo del videojuego bajo las bases del proyecto.

\---

## Alcance Operativo de la Fuga

* Presentamos un arcade 2D para un jugador enfocado en la supervivencia y la evasion del bloqueo militar.
* Pilotamos la nave insignia a traves de tres sectores hiperespaciales vigilados; debemos despejar la cuota de naves enemigas en cada tramo para abrirnos paso.
* La mision culmina con la Victoria absoluta al completar el tercer sector o con un tragico Game Over si las fuerzas del regimen destruyen la nave al agotar sus tres vidas.
* El codigo primitivo del viejo simulador de pruebas se archiva en core/src/main/java/puppy/code/prototype como vestigio historico, mientras que la aplicacion moderna arranca oficialmente desde puppy.code.app.SpaceNavigation y se despliega mediante el paquete de pantallas screens.

\---

## GM-1 - El Piloto en Perspectiva (Experiencia de Juego)

La lucha por la libertad se libra bajo estrictas condiciones tacticas en la cabina:

* Comenzamos la mision con tres vidas operativas para resistir la ofensiva.
* El pilotaje se ejecuta con las teclas WASD, los cañones laser responden a la tecla I y el sistema de propulsion de emergencia (Dash) se acciona con O.
* El panel HUD integrado reporta en tiempo real el sector actual, la cuota de enemigos abatidos y el progreso de la huida.
* En el trayecto nos topamos con fuerzas hostiles del regimen: naves de oleada estandar, centinelas fijos, bombarderos de patrulla y meteoritos de los campos minados espaciales.
* Al asegurar los sectores uno y dos, el sistema proyecta la alerta de Nivel Superado en espera de orden manual, mientras que el sector final conduce directamente a la gloria de la Victoria.
* Los invasores de oleada portan 1, 2 o 3 puntos de integridad segun la peligrosidad del sector, empleando trayectorias lineales en el primero y patrones en zigzag en los subsiguientes.
* Los centinelas actuan como puestos de bloqueo estacionarios, y los bombarderos cruzan el firmamento patrullando horizontalmente con tacticas de fuego avanzadas.
* Los campos de meteoritos no forman parte de la cuota militar, pero destruirlos otorga recursos en forma de puntaje (100 puntos por cada nave del regimen abatida; 25, 50 o 100 puntos por desintegrar meteoritos chicos, medianos o grandes).
* Si la nave sufre daños fatales, el sistema de emergencia la reubica en la posicion de salto inicial dotandola de 1,5 segundos de invulnerabilidad tactica.
* La maniobra de Dash otorga un impulso hiperveloz de 0,15 segundos con un tiempo de recarga de 3 segundos, concediendo 0,5 segundos adicionales de proteccion al finalizar el desplazamiento.
* La velocidad base de crucero de la nave se calibra en 390 px/s con un multiplicador de dash de factor 3.
* Los rekords de puntuacion de los pilotos caidos o triunfadores se registran en el Top 3 local ingresando iniciales de tres letras (A-Z), corrigiendo con Retroceso y validando con Enter.
* La tecla Escape despliega el menu de pausa tactica, permitiendo reanudar la marcha, regular los niveles de audio o abortar la mision volviendo al menu principal.
* Como protocolo de emergencia para los ingenieros de la resistencia, introducir la secuencia secreta del Codigo Konami (Arriba, Arriba, Abajo, Abajo, Izquierda, Derecha, Izquierda, Derecha, I, O) durante la pausa destraba un panel oculto de depuracion capaz de activar el Modo Dios (invulnerabilidad total).
* Los planos tacticos y bocetos conceptuales de la interfaz, el combate y las pantallas de cierre se archivan en Planificacion/Bocetos/B1.jpeg hasta B4.jpeg.

\---

## GM-2 - Evolucion de la Flota (Juego Base frente a la Nueva Arquitectura)

* El esqueleto de pruebas original residia en el paquete puppy.code.prototype, donde la clase PantallaJuego centralizaba toda la simulacion, el control grafico y las listas de entidades en un solo lugar.
* La version actual desacopla por completo la simulacion tactica: GameWorld gestiona las reglas fisicas, la salud y el avance de sector; GameScreen procesa la interfaz visual interpretando un WorldSnapshot inmutable; PlayerWeapon regula la cadencia de fuego; CollisionSystem calcula las intercepciones; y LevelDirector programa las oleadas del enemigo.
* Se expandio el sistema para incorporar tres sectores distintos, multiples comportamientos de ataque y movimiento, centinelas, bombarderos, campos de meteoritos, propulsion dash, menus de pausa interactivos, efectos visuales avanzados, pantallas de cierre y un sistema persistente de rekords locales.

\---

## GM-3 - Planos Estelares (UML y Estructura de Codigo)

* El diagrama detallado de clases y relaciones de diseño se documenta en el archivo UML\_INICIAL.md.
* El codigo fuente se encuentra estructurado limpiamente en los paquetes logicos app, assets, behaviors, config, entities, factories, input, screens, systems y world, apuntando al lanzador de escritorio ubicado en lwjgl3/src/main/java/puppy/code/lwjgl3/Lwjgl3Launcher.java.

\---

## GM-4 - El Arsenal de Abstraccion (Clases Abstractas e Interfaces)

* La clase abstracta GameEntity sirve como cimiento estructural para todas las unidades moviles del espacio: Ship, Alien, Meteorite, PlayerProjectile y AlienProjectile.
* La clase abstracta AbstractGameScreen estandariza el flujo de renderizado para las pantallas operativas MenuScreen, GameScreen, GameOverScreen y VictoryScreen.
* El comportamiento dinamico se rige mediante contratos de interfaz: MovementStrategy con sus cuatro variantes (LinearMovementStrategy, ZigZagMovementStrategy, StaticMovementStrategy, HorizontalMovementStrategy) y AlienAttackStrategy con sus cinco modalidades tacticas (LinearAttackStrategy, VAttackStrategy, SpreadAttackStrategy, AlternatingAttackStrategy, BurstAttackStrategy).
* Se complementa el motor con interfaces de soporte como Collidable para las colisiones geometricas y WorldAudioListener para la transmision de eventos sonoros del mundo a la interfaz.

\---

## GM-5 - Protocolos de Blindaje (Encapsulamiento, GRASP y SOLID)

### Encapsulamiento

* El estado interno de las entidades y del mundo se mantiene rigurosamente privado.
* GameWorld protege sus colecciones entregando unicamente un WorldSnapshot con vistas de solo lectura (EntityView).
* GameAssets distribuye los fotogramas de la nave mediante indices controlados, ocultando la matriz cruda del spritesheet.
* Los parametros globales de balance se centralizan de forma inmutable en GameConfig.

### Principios GRASP

* Controller e Information Expert: GameWorld procesa las ordenes de control y administra las consecuencias globales, mientras cada entidad gobierna su propia integridad y desplazamiento.
* Creator: LevelDirector concentra los parametros tacticos para ensamblar los enemigos empleando al constructor especializado.
* Pure Fabrication: Componentes como CollisionSystem, PlayerWeapon, ScoreRepository y GameAssets aislan tareas tecnicas especializadas ajenas a las entidades del dominio.
* Low Coupling e Indirection: El mundo interactua mediante contratos abstractos como CollisionDetector y AlienFactory, mientras que las pantallas se comunican con el exterior a traves de ScreenNavigator, NavigationInput y ScoreStore.

### Principios SOLID

* SRP (Responsabilidad Unica): Cada modulo posee un unico motivo de cambio (la simulacion en el mundo, las colisiones en el sistema fisico, los puntajes en el repositorio).
* OCP (Abierto/Cerrado): Es posible incorporar nuevas trayectorias o metodos de fuego creando estrategias adicionales sin alterar el codigo central de los enemigos.
* LSP (Sustitucion de Liskov): Las clases derivadas de GameEntity respetan integramente el contrato geometrico de Collidable.
* ISP (Segregacion de Interfaces): Los contratos de entrada, navegacion, puntajes y audio se encuentran divididos en interfaces especificas segun lo requiera cada cliente.
* DIP (Inversion de Dependencias): El nucleo del mundo depende de abstracciones como AlienFactory y CollisionDetector, delegando la inyeccion de implementaciones concretas al componente de arranque SpaceNavigation.

\---

## GM-6 a GM-9 - Los Nucleos Tecnologicos (Patrones de Diseño)

### GM-6 - Singleton (GameAssets)

* Problema: Evitar la duplicacion de recursos pesados en memoria y prevenir conflictos en la liberacion de texturas y audios.
* Contexto: Las distintas pantallas operativas comparten constantemente texturas y efectos sonoros.
* Solucion: SpaceNavigation adquiere una unica instancia global a traves de GameAssets.getInstance(), controlando un constructor privado y liberando los recursos centralmente mediante dispose().

### GM-7 - Strategy (Estrategias de Movimiento y Ataque)

* Problema: La variedad de trayectorias y patrones ofensivos de los enemigos no debe sobrecargar la clase Alien con condicionales complejos.
* Contexto: El simulador procesa multiples tipos de naves enemigas simultaneamente.
* Solucion: Alien recibe por inyeccion las interfaces MovementStrategy y AlienAttackStrategy, delegando en ellas el calculo de posicion y cadencia ofensiva de forma polimorfica.

### GM-8 - Template Method (AbstractGameScreen)

* Problema: Garantizar que el menu, la partida y las pantallas de cierre ejecuten siempre la misma secuencia operativa por cada cuadro de renderizado.
* Contexto: Todas las pantallas activas comparten una estructura de ciclo de vida comun.
* Solucion: AbstractGameScreen.render(float delta) se declara como metodo final para fijar el orden estricto de limpieza, entrada, actualizacion y dibujo, delegando los detalles especificos a los metodos abstractos de cada pantalla.

### GM-9 - Builder (AlienBuilder y LevelDirector)

* Problema: La construccion de un enemigo requiere multiples parametros interdependientes que deben validarse antes de su despliegue.
* Contexto: Los sectores del espacio exigen la fabricacion dinamica de arquetipos complejos.
* Solucion: AlienBuilder permite ensamblar las propiedades de la nave por pasos encadenados asegurando su integridad mediante validaciones previas, bajo la direccion estrategica de LevelDirector.

\---

## GM-10 - Telemetria y Despliegue (GitHub y Control de Versiones)

* El desarrollo colaborativo se estructuro bajo la rama de trabajo feature/overhaul-assets, registrando un historial consolidado de mas de seis commits de avance tecnico frente a los puntos de control del repositorio.
* La persistencia de los registros locales opera mediante el mecanismo de Preferences de LibGDX en el equipo anfitrion.
* Para compilar y poner en orbita la aplicacion empleando Java 11, se ejecutan los comandos del gestor de dependencias de Gradle: gradlew.bat :lwjgl3:run en entornos Windows o ./gradlew :lwjgl3:run en sistemas Linux y macOS.

