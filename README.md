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
| A-Z | Ingresar iniciales si el puntaje clasifica al Top 3 |
| Retroceso | Borrar una inicial |
| Enter | Iniciar, continuar, guardar iniciales o reiniciar |
| Escape | Volver al menú durante la partida o desde una pantalla final |

El movimiento usa delta y normaliza las diagonales. El dash usa provisionalmente un multiplicador de velocidad de 3; todavía debemos validar ese balance al jugar.

Los aliens disparan cada 1,8 segundos. Perder una vida devuelve la nave a su posición inicial y la protege durante 1,5 segundos. En el respawn limpiamos los proyectiles activos y conservamos aliens, meteoritos, puntaje y progreso del nivel. Destruir un alien suma 100 puntos; los meteoritos destructibles suman 25, 50 o 75 según su tamaño.

## Estructura y estado de esta entrega

GameWorld coordina la partida y expone un WorldSnapshot de solo lectura. PlayerWeapon regula el disparo; CollisionSystem detecta impactos; Alien delega movimiento y ataque en estrategias; AlienBuilder y LevelDirector crean aliens para cada nivel. GameAssets administra recursos compartidos con Singleton y AbstractGameScreen fija el ciclo de renderizado mediante Template Method.

ScoreRepository conserva el Top 3 con LibGDX Preferences. Cuando un resultado supera el tercer puntaje, ingresamos tres letras A-Z en Game Over o Victoria y pulsamos Enter para guardarlo. El menú y las pantallas finales muestran los resultados persistidos en el equipo local.

GameAssets solo carga MainShip3.png, Rocket2.png, aGreySmall.png, aGreyMedium4.png, aGreyLarge.png, explosion.ogg, hurt.ogg, pop-sound.mp3 y piano-loops.wav. Dibujamos aliens y proyectiles enemigos con formas geométricas. Dejamos intactos assets/ y el código histórico de puppy.code.prototype. No conocemos la fuente original ni la licencia de los recursos permitidos, y el repositorio no contiene un registro verificable de esa información. El UML está en UML_INICIAL.md; la explicación de los requisitos GM y los patrones está en REPORTE_PROYECTO.md.
