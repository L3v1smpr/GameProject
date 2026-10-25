# Documentación del sistema

**Estado:** primera demo jugable preparada para recibir feedback
**Rama revisada:** feature/rama-Alex
**Fecha de revisión:** 23 de octubre de 2026

Nosotros integramos un ciclo de partida de escritorio con movimiento, disparos, daño, vidas, respawn, meteoritos, puntaje, progresión por niveles y registro local del Top 3 con iniciales. La documentación describe el corte compilado de la rama feature/rama-Alex.

## 1. Descripción general

Nosotros proponemos un juego arcade 2D en el que pilotamos una nave, esquivamos ataques y destruimos aliens. Empezamos con tres vidas y avanzamos destruyendo la cuota de cada nivel: 15, 30 y 45 aliens.

## 2. Requisitos y controles

| Tecla | Acción implementada |
| --- | --- |
| W/A/S/D | Mover la nave; normalizamos las diagonales. |
| I mantenida | Disparar cada 0,25 segundos. |
| O | Ejecutar dash durante 0,15 segundos; recarga de 3 segundos. |
| A-Z | Ingresar iniciales si el puntaje clasifica al Top 3. |
| Retroceso | Borrar una inicial durante el registro. |
| Cualquier tecla o clic | Continuar desde Nivel Superado. |
| Enter | Iniciar, guardar iniciales o reiniciar la partida. |
| Escape | Volver al menú. |

Nosotros usamos el tiempo delta en el movimiento y en los temporizadores. El multiplicador 3x del dash es provisional, porque las bases no fijan su fuerza.

## 3. Instalación y ejecución

Nosotros compilamos con JDK 11 y Gradle Wrapper:

- Windows: gradlew.bat :lwjgl3:run
- Linux/macOS: ./gradlew :lwjgl3:run
- Verificación: gradlew.bat test compileJava o ./gradlew test compileJava

En una verificación anterior Gradle compiló core y lwjgl3 y reportó test como NO-SOURCE. En esta revisión aislada el wrapper no pudo conectar con su daemon; compilamos core y lwjgl3 con javac 11 y el classpath local.

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
| GameAssets | Administra los recursos compartidos mediante AssetManager y Singleton. |
| ScoreRepository | Persiste el Top 3 local con Preferences. |
| InitialsInput | Aísla las teclas usadas para ingresar y borrar iniciales. |

## 6. Flujo de una partida

Nosotros iniciamos desde MenuScreen y creamos GameWorld al comenzar. El jugador mueve la nave con WASD y dispara manteniendo I. El dash O sigue la dirección elegida o avanza hacia arriba si no hay movimiento. Si el resultado final supera el tercer puntaje, Game Over o Victoria permiten registrar tres iniciales y mostrar el Top 3.

Los aliens del nivel 1 se desplazan en línea, tienen 1 HP y disparan una bala dirigida. Los niveles 2 y 3 usan zigzag, tienen 2 y 3 HP, y disparan en V. Destruir un alien suma 100 puntos y solo cuenta para la cuota cuando llega a 0 HP. Los meteoritos descienden, se destruyen con un impacto y suman 25, 50 o 75 puntos.

Al recibir daño perdemos una vida. Si quedan vidas, reaparecemos en el punto inicial con 1,5 segundos de invulnerabilidad; quitamos los proyectiles activos y conservamos aliens, meteoritos, puntaje y progreso. Al completar niveles 1 y 2 esperamos una acción para continuar. La cuota del nivel 3 conduce a Victoria; perder las tres vidas conduce a Game Over.

## 7. Manejo de recursos

Nosotros volvimos a activar en GameAssets los recursos gráficos ya incluidos en assets/: sprites de jugador y alienígenas, proyectiles, meteoritos, vidas y banners de Game Over y Victoria. La partida reproduce background_music.MP3 y Game Over reproduce gameover.mp3; mantenemos los efectos explosion.ogg, hurt.ogg y pop-sound.mp3. AssetManager conserva su ciclo de vida y GameAssets sigue siendo Singleton. No modificamos los archivos de assets/ ni el paquete histórico puppy.code.prototype. No conocemos la fuente original ni la licencia de los recursos, y el repositorio no contiene un registro verificable de esa atribución.

## 8. Puntajes locales

Nosotros sumamos puntaje por aliens y meteoritos y lo conservamos al perder una vida y al cambiar de nivel. ScoreRepository guarda y consulta un Top 3 local con Preferences. Cuando el resultado supera el tercer puntaje, Game Over o Victoria permite ingresar tres letras A-Z, Retroceso corrige la entrada y Enter guarda el resultado. El menú y las pantallas finales muestran el Top 3. Usamos Preferences en esta demo de escritorio; la propuesta inicial de SQLite no describe el adaptador implementado.

## 9. Decisiones de diseño

Nosotros dejamos a GameWorld como Controller de GRASP porque conoce y coordina las entidades y las consecuencias del juego. Aplicamos Information Expert en las entidades; Creator en LevelDirector y AlienBuilder; bajo acoplamiento mediante PlayerInput, NavigationInput, InitialsInput y estrategias; y alta cohesión al separar presentación, disparo, detección y coordinación.

En SOLID, nosotros aplicamos SRP, LSP e ISP en las responsabilidades y contratos descritos. MovementStrategy y AlienAttackStrategy permiten extender comportamientos. Mantenemos limitaciones de DIP porque GameWorld recibe clases concretas, las pantallas reciben ScoreRepository concreto y GameAssets se obtiene desde un Singleton.

## 10. Trazabilidad de requisitos

Nosotros relacionamos GM-4 con GameEntity, Collidable, AbstractGameScreen y MovementStrategy; GM-5 con entidades encapsuladas y WorldSnapshot; GM-6 con GameAssets; GM-7 con las estrategias de movimiento y ataque; GM-8 con AbstractGameScreen.render; GM-9 con AlienBuilder y LevelDirector. El UML actualizado está en UML_INICIAL.md.

## 11. Limitaciones conocidas

Nosotros dejamos pendiente validar jugando el balance provisional del dash y obtener los datos de autoría y licencia de los recursos. No agregamos pruebas automatizadas; Gradle informa NO-SOURCE para las tareas test.

## 12. Estado de la entrega

En una verificación anterior se registró la compilación de core y lwjgl3 con gradlew.bat test compileJava; en esta revisión aislada el wrapper no pudo conectar con el daemon. Compilamos el código actual de core y lwjgl3 con javac 11 y el classpath local. No encontramos pruebas automatizadas; la validación manual completa de controles y balance sigue pendiente.
