# Space Navigation

Demo jugable de escritorio en Java 11 y LibGDX para recibir feedback sobre el combate base.

## Ejecutar

- Requisito: JDK 11.
- Windows: gradlew.bat :lwjgl3:run
- Linux/macOS: ./gradlew :lwjgl3:run
- Compilar y ejecutar las pruebas disponibles: gradlew.bat test compileJava o ./gradlew test compileJava

Importamos la carpeta raíz como proyecto Gradle existente en Eclipse o NetBeans. Usamos el Gradle Wrapper incluido.

## Partida

Comenzamos con tres vidas. El objetivo es destruir aliens, esquivar sus ataques y completar las cuotas:

| Nivel | Cuota | Vida del alien | Movimiento y ataque |
| --- | ---: | ---: | --- |
| 1 | 15 aliens | 1 impacto | Movimiento lineal y un disparo dirigido |
| 2 | 30 aliens | 2 impactos | Zigzag y dos disparos en V |
| 3 | 45 aliens | 3 impactos | Zigzag y dos disparos en V |

Al completar los niveles 1 o 2, esperamos una tecla o un clic para continuar. Completar el nivel 3 muestra Victoria. Si perdemos las tres vidas, aparece Game Over. Enter reinicia desde el nivel 1 y Escape vuelve al menú desde las pantallas finales.

## Controles

| Tecla | Acción |
| --- | --- |
| W/A/S/D | Mover la nave en cuatro direcciones y en diagonal |
| I mantenida | Disparar cada 0,25 segundos |
| O | Hacer dash e ignorar daño durante 0,15 segundos; recarga de 3 segundos |
| Enter | Iniciar, continuar desde una pantalla final o reiniciar |
| Escape | Volver al menú durante la partida o desde una pantalla final |

El movimiento usa delta y normaliza las diagonales. El dash usa provisionalmente un multiplicador de velocidad de 3; todavía debemos validar ese balance al jugar.

Los aliens disparan cada 1,8 segundos. Perder una vida devuelve la nave a su posición inicial y la protege durante 1,5 segundos. En el respawn limpiamos los proyectiles activos y conservamos aliens, meteoritos, puntaje y progreso del nivel. Destruir un alien suma 100 puntos; los meteoritos destructibles suman 25, 50 o 75 según su tamaño.

## Estructura y estado de esta entrega

GameWorld coordina la partida y expone un WorldSnapshot de solo lectura. Las pantallas dibujan ese estado. PlayerWeapon regula el disparo; CollisionSystem detecta impactos; Alien delega movimiento y ataque en estrategias; AlienBuilder y LevelDirector crean aliens para cada nivel. GameAssets administra los recursos compartidos y AbstractGameScreen fija el ciclo de renderizado.

La clase ScoreRepository ya permite guardar resultados localmente, pero todavía no la conectamos con Game Over, Victoria ni una interfaz para ingresar el nombre de tres caracteres. La captura y consulta del Top 3 queda pendiente para la siguiente etapa. También debemos validar jugando el balance provisional del dash y los recursos audiovisuales.

Usamos las rutas de assets/ existentes y dejamos intactos tanto ese directorio como el código histórico de puppy.code.prototype. El UML de la arquitectura está en UML_INICIAL.md; las decisiones y pendientes del equipo están en Planificacion/.