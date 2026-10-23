# Documentación del sistema

**Estado:** primera demo jugable preparada para recibir feedback
**Rama revisada:** feature/rama-Alex
**Fecha de revisión:** 23 de octubre de 2026

Nosotros integramos un ciclo de partida de escritorio con movimiento, disparos, daño, vidas, respawn, meteoritos, puntaje y progresión por niveles. La documentación describe el corte que se compila. Dejamos el ingreso del nombre y la conexión del Top 3 para otra etapa.

## 1. Descripción general

Nosotros proponemos un juego arcade 2D en el que pilotamos una nave, esquivamos ataques y destruimos aliens. Empezamos con tres vidas y avanzamos destruyendo la cuota de cada nivel: 15, 30 y 45 aliens.

## 2. Requisitos y controles

| Tecla | Acción implementada |
| --- | --- |
| W/A/S/D | Mover la nave; normalizamos las diagonales. |
| I mantenida | Disparar cada 0,25 segundos. |
| O | Ejecutar dash durante 0,15 segundos; recarga de 3 segundos. |
| Cualquier tecla o clic | Continuar desde Nivel Superado. |
| Enter | Iniciar o reiniciar la partida. |
| Escape | Volver al menú. |

Nosotros usamos el tiempo delta en el movimiento y en los temporizadores. El multiplicador 3x del dash es provisional, porque las bases no fijan su fuerza.

## 3. Instalación y ejecución

Nosotros compilamos con JDK 11 y Gradle Wrapper:

- Windows: gradlew.bat :lwjgl3:run
- Linux/macOS: ./gradlew :lwjgl3:run
- Verificación: gradlew.bat test compileJava o ./gradlew test compileJava

La verificación compila core y lwjgl3. El repositorio no contiene fuentes de prueba, por lo que Gradle informa las tareas test como NO-SOURCE.

## 4. Estructura del proyecto

Nosotros separamos la aplicación en core y lwjgl3. El paquete app conecta la aplicación y las pantallas; assets administra recursos; behaviors contiene estrategias; entities modela los objetos; factories crea aliens; input traduce teclado; screens presenta la partida; systems contiene colisiones y arma; world coordina el estado de la partida.

## 5. Clases y responsabilidades

| Clase | Responsabilidad |
| --- | --- |
| GameWorld | Coordina entidades, impactos, vidas, puntaje, cuotas y fases. |
| WorldSnapshot | Entrega valores inmutables para que las pantallas no modifiquen el mundo. |
| CollisionSystem | Detecta contactos y devuelve resultados sin aplicar las reglas de puntaje o vidas. |
| PlayerWeapon | Mantiene la cadencia del disparo del jugador. |
| Alien | Mantiene HP y delega movimiento y ataque en estrategias. |
| LevelDirector y AlienBuilder | Construyen las oleadas con la configuración de cada nivel. |
| GameScreen | Lee controles de juego, actualiza GameWorld y dibuja el snapshot. |
| ScreenNavigator | Expone las transiciones a menú, juego, Game Over y Victoria. |
| GameAssets | Administra los recursos compartidos mediante AssetManager. |

## 6. Flujo de una partida

Nosotros iniciamos desde MenuScreen y creamos GameWorld al comenzar. El jugador mueve la nave con WASD y dispara manteniendo I. El dash O sigue la dirección elegida o avanza hacia arriba si no hay movimiento.

Los aliens del nivel 1 se desplazan en línea, tienen 1 HP y disparan una bala dirigida. Los niveles 2 y 3 usan zigzag, tienen 2 y 3 HP, y disparan en V. Destruir un alien suma 100 puntos y solo cuenta para la cuota cuando llega a 0 HP. Los meteoritos descienden, se destruyen con un impacto y suman 25, 50 o 75 puntos.

Al recibir daño perdemos una vida. Si quedan vidas, reaparecemos en el punto inicial con 1,5 segundos de invulnerabilidad; quitamos los proyectiles activos y conservamos aliens, meteoritos, puntaje y progreso. Al completar niveles 1 y 2 esperamos una acción para continuar. La cuota del nivel 3 conduce a Victoria; perder las tres vidas conduce a Game Over.

## 7. Manejo de recursos

Nosotros centralizamos las rutas existentes en GameAssets y dejamos su ciclo de vida en AssetManager. No modificamos assets ni el paquete histórico puppy.code.prototype. Nos falta verificar y registrar la atribución y las licencias de los recursos.

## 8. Puntajes locales

Nosotros sumamos puntaje por aliens y meteoritos y lo conservamos al perder una vida y al cambiar de nivel. ScoreRepository guarda y consulta un Top 3 local con Preferences, pero no lo conectamos a las pantallas finales ni añadimos el ingreso del nombre de tres caracteres. La decisión de usar SQLite que figura en la propuesta de arquitectura todavía no coincide con este adaptador.

## 9. Decisiones de diseño

Nosotros dejamos a GameWorld como Controller de GRASP porque conoce y coordina las entidades y las consecuencias del juego. Aplicamos Information Expert en las entidades; Creator en LevelDirector y AlienBuilder; bajo acoplamiento mediante PlayerInput, NavigationInput y estrategias; y alta cohesión al separar presentación, disparo, detección y coordinación.

En SOLID, nosotros aplicamos SRP, LSP e ISP en las responsabilidades y contratos descritos. MovementStrategy y AlienAttackStrategy permiten extender comportamientos. Mantenemos una limitación de DIP porque GameWorld recibe clases concretas y GameAssets se obtiene desde un Singleton.

## 10. Trazabilidad de requisitos

Nosotros relacionamos GM-4 con GameEntity, Collidable, AbstractGameScreen y MovementStrategy; GM-5 con entidades encapsuladas y WorldSnapshot; GM-6 con GameAssets; GM-7 con las estrategias de movimiento y ataque; GM-8 con AbstractGameScreen.render; GM-9 con AlienBuilder y LevelDirector. El UML actualizado está en UML_INICIAL.md.

## 11. Limitaciones conocidas

Nosotros dejamos pendientes el ingreso del nombre, la captura y visualización del Top 3 al finalizar, confirmar la fuerza del dash y revisar licencias de recursos. No agregamos pruebas automatizadas. Las tareas test completan correctamente como NO-SOURCE.

## 12. Estado de la entrega

Nosotros verificamos test compileJava: la compilación de core y lwjgl3 terminó correctamente. La ejecución de escritorio llegó a la tarea lwjgl3:run sin mostrar un error de carga; todavía debemos completar una prueba manual de controles y registrar el feedback del equipo.
