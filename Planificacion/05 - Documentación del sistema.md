# Documentación del sistema

**Estado:** estructura inicial que completaremos a medida que integremos las funciones.
Vamos a documentar la versión que podamos ejecutar y revisar. Mientras no implementemos una función planeada, la mantendremos identificada como pendiente.

## Estado de la base de patrones

El corte inicial se encuentra en la rama `feature/base-patrones`. La base aplica los cuatro patrones
exigidos por GM-6 a GM-9 y mantiene separados el modelo, la entrada y la presentación. El estado
descrito aquí corresponde a las clases del paquete nuevo; el código legado del paquete `prototype`
se conserva fuera del flujo de escritorio actual.

| Requisito | Aplicación actual | Evidencia |
| --- | --- | --- |
| GM-4 | Clase abstracta usada por dos pantallas; `MovementStrategy` tiene dos implementaciones. `GameEntity` es padre de las entidades del modelo. | `AbstractGameScreen`, `MenuScreen`, `GameScreen`; `MovementStrategy`, `LinearMovementStrategy`, `ZigZagMovementStrategy`, `GameEntity`. |
| GM-5 | Estado privado, snapshots sin entidades mutables, entrada por interfaces y responsabilidades separadas. `GameWorld` coordina el flujo como Controller de GRASP. | `GameEntity`, `WorldSnapshot`, `PlayerInput`, `GameWorld`, `CollisionSystem`. |
| GM-6 | Singleton de recursos compartidos; `AssetManager` carga una vez y libera recursos al cerrar. | `GameAssets`, `SpaceNavigation`. |
| GM-7 | El alien delega movimiento y descarga en estrategias separadas. | `Alien`, `MovementStrategy`, `LinearMovementStrategy`, `ZigZagMovementStrategy`, `AlienAttackStrategy`, `LinearAttackStrategy`, `VAttackStrategy`. |
| GM-8 | Un método final fija limpiar, entrada, actualización y dibujo; cada pantalla implementa sus pasos variables. | `AbstractGameScreen.render()`, `MenuScreen`, `GameScreen`. |
| GM-9 | El Builder valida configuración de alien y el Director elige arquetipos por nivel. | `AlienBuilder`, `LevelDirector`. |

Las razones de diseño y el diagrama del corte implementado están en [UML_INICIAL.md](../UML_INICIAL.md).
Esta base no equivale a la partida completa. Nosotros dejamos los modelos de proyectil, meteorito,
arma del jugador y estrategias de ataque sin integrar todavía con `GameWorld`. La resolución completa
de colisiones/vidas/respawn, fases/progresión y puntaje/leaderboard queda reservada para la siguiente IA,
junto con las integraciones que dependen de esas reglas. `UML_INICIAL.md` distingue esos modelos del
comportamiento activo para no presentarlos como una partida ya terminada.

## 1. Descripción general

Pendiente: explicaremos el objetivo del juego y la experiencia que ofrecemos con la versión integrada.

## 2. Requisitos y controles

Pendiente: describiremos las reglas implementadas y comprobaremos que la guía del juego, el README y los controles reales coincidan.

## 3. Instalación y ejecución

Pendiente: registraremos la versión de Java, los pasos de compilación, el lanzador de escritorio y los requisitos para abrir el proyecto desde un checkout limpio.

## 4. Estructura del proyecto

Pendiente: describiremos los módulos `core` y `lwjgl3`, junto con los paquetes que incluyamos en la entrega.

## 5. Clases y responsabilidades

Pendiente: explicaremos las clases principales y cómo colaboran. Actualizaremos este texto junto con el UML final.

## 6. Flujo de una partida

Pendiente: documentaremos el inicio, los niveles, la victoria, Game Over y el reinicio tal como funcionen en la versión final.

## 7. Manejo de recursos

Pendiente: indicaremos qué recursos usamos, quién administra su ciclo de vida y dónde dejamos sus créditos y permisos.

## 8. Puntajes locales

Ya decidimos guardar los puntajes localmente con SQLite. Cuando implementemos esta parte, describiremos el formato de los registros, la ubicación de la base, cómo recuperaremos los resultados al iniciar el juego y cómo mantendremos el top 3. Documentaremos que registraremos el resultado cuando se agoten las vidas, con un nombre de tres caracteres, y que mantendremos el puntaje al perder una vida y al cambiar de nivel. Nos falta acordar cómo ingresaremos ese nombre y resolveremos los empates. Sumaremos 100 puntos por alien, 50 por meteorito destruido, 25 si es chico y 75 si es grande. Nos gustaría consultar el top 3 desde el menú y al terminar la partida, pero primero lo confirmaremos con el equipo.

## 9. Decisiones de diseño

Pendiente: explicaremos el problema, nuestra decisión y los principios de encapsulamiento, GRASP y SOLID que apliquemos, junto con cada patrón requerido. Incluiremos referencias a clases y archivos reales.

## 10. Trazabilidad de requisitos

Pendiente: relacionaremos cada requisito GM con sus clases, archivos, UML y evidencia de ejecución.

## 11. Limitaciones conocidas

Pendiente: anotaremos lo que dejemos fuera de la entrega y los defectos que aceptemos, si los hubiera.

## 12. Estado de la entrega

Pendiente: indicaremos qué versión revisamos, en qué fecha y cuál fue el resultado de la ruta de aceptación completa.
