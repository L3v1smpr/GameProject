# Space Navigation - INF2236 Programación Avanzada PUCV

Demo arcade 2D para escritorio desarrollada en Java 11 con LibGDX 1.12.1. Esta guía explica cómo importar, ejecutar y probar la entrega.

## Requisitos

- Oracle JDK 11 u OpenJDK 11 instalado y configurado en `JAVA_HOME`.
- Gradle Wrapper incluido en el repositorio. No es necesario instalar Gradle por separado.
- Eclipse o NetBeans con soporte para proyectos Gradle, si se desea trabajar desde un IDE.

Comprueba la versión activa desde la raíz del proyecto:

```text
java -version
```

## Ejecutar desde la terminal

Abre una terminal en la carpeta raíz del repositorio.

### Windows

```powershell
.\gradlew.bat :lwjgl3:run
```

### Linux o macOS

```bash
./gradlew :lwjgl3:run
```

Gradle compila los módulos necesarios y abre la ventana del juego. El punto de entrada de escritorio está en el módulo `lwjgl3`.

## Compilar, ejecutar pruebas y crear el JAR

Con Java 11 activo, ejecuta desde la raíz:

```powershell
.\gradlew.bat test compileJava
.\gradlew.bat test build
.\gradlew.bat :lwjgl3:jar
java -jar .\lwjgl3\build\libs\SpaceNav2024-1.0.0.jar
```

En Linux o macOS, ejecuta `./gradlew test compileJava`, `./gradlew test build` y `./gradlew :lwjgl3:jar`; luego inicia el archivo con `java -jar ./lwjgl3/build/libs/SpaceNav2024-1.0.0.jar`. El JAR incluye las dependencias de ejecución y usa `puppy.code.lwjgl3.Lwjgl3Launcher` como punto de entrada. Para ejecutar la aplicación durante el desarrollo también se puede usar `:lwjgl3:run`.

## Importar en Eclipse

1. Selecciona **File > Import > Gradle > Existing Gradle Project**.
2. Indica la carpeta raíz del repositorio, donde están `settings.gradle` y `gradlew.bat`.
3. Mantén el Gradle Wrapper como distribución de Gradle y selecciona un JDK 11 para el proyecto.
4. Finaliza la importación y espera la sincronización de los módulos `core` y `lwjgl3`.
5. Ejecuta la tarea Gradle `:lwjgl3:run` desde la vista de tareas Gradle.

## Importar en NetBeans

1. Selecciona **File > Open Project** y abre la carpeta raíz del repositorio.
2. Acepta la detección del proyecto Gradle y espera a que termine la sincronización.
3. Configura JDK 11 como plataforma del proyecto si NetBeans seleccionó otra versión.
4. En las tareas Gradle del proyecto, ejecuta `:lwjgl3:run`.

## Controles

| Tecla | Acción |
| --- | --- |
| `W`, `A`, `S`, `D` | Mover la nave en ocho direcciones. El movimiento diagonal se normaliza. |
| `I` mantenida | Disparar continuamente, con una cadencia de 0,25 s y proyectiles a 450 px/s. |
| `O` | Activar dash en la dirección de movimiento: 0,15 s, velocidad multiplicada por 3 e invulnerabilidad durante el impulso. Enfriamiento de 3,0 s. |
| `ENTER` | Iniciar o continuar la partida, confirmar iniciales o reiniciar tras el final. |
| `ESC` | Volver al menú principal desde la partida o las pantallas finales. |
| `A` a `Z` | Ingresar iniciales cuando el puntaje puede entrar al Top 3. |
| `Retroceso` | Borrar la última inicial ingresada. |

La nave se mueve a 260 px/s. Al perder una vida reaparece en la posición inicial y obtiene 1,5 s de invulnerabilidad. El respawn limpia los proyectiles del jugador y de los aliens, y conserva aliens, meteoritos, puntaje y progreso del nivel. La partida comienza con tres vidas.

## Niveles y puntuación

| Nivel | Cuota de aliens | HP por alien | Movimiento | Ataque alienígena |
| --- | ---: | ---: | --- | --- |
| 1 | 15 | 1 | Lineal, 90 px/s | Un proyectil dirigido, 220 px/s, cada 1,8 s |
| 2 | 30 | 2 | Zigzag, 110 px/s | Dos proyectiles en V a 45 grados, 220 px/s, cada 1,8 s |
| 3 | 45 | 3 | Zigzag, 110 px/s | Dos proyectiles en V a 45 grados, 220 px/s, cada 1,8 s |

| Objetivo destruido | Puntaje |
| --- | ---: |
| Alien | 100 |
| Meteorito pequeño | 25 |
| Meteorito mediano | 50 |
| Meteorito grande | 75 |

Al completar el primer y segundo nivel aparece el estado Nivel superado. Una tecla o clic continúa al siguiente nivel. Completar la cuota del tercero lleva a Victoria; perder las tres vidas lleva a Game Over. Si el resultado califica para el Top 3, ingresamos tres iniciales de `A` a `Z` y pulsamos `ENTER` para guardarlas. La tabla se conserva localmente con LibGDX Preferences bajo `space_navigation_scores`.

## Recursos y estructura

`GameAssets` centraliza la carga y liberación de texturas, fuentes, música y efectos. Reactivamos los sprites ya incluidos en `assets/`: jugador, alienígenas, proyectiles, vidas, meteoritos y banners de Game Over y Victoria. La partida usa `background_music.MP3` y Game Over usa `gameover.mp3`; si algún recurso opcional falta, se mantienen alternativas geométricas o texto. No se modifican los archivos de `assets/` ni el código histórico de `puppy.code.prototype`. La fuente y licencia originales de los recursos no están identificadas en el repositorio.

El módulo `core` separa aplicación, recursos, comportamientos, configuración, entidades, fábricas, entrada, pantallas, sistemas y mundo. `GameWorld` coordina las reglas; `WorldSnapshot` entrega datos de presentación; `MovementStrategy` y `AlienAttackStrategy` permiten variar comportamientos; `AlienBuilder` y `LevelDirector` componen aliens por nivel. `AbstractGameScreen` fija el ciclo de las pantallas.

El diagrama de clases está en [UML_INICIAL.md](UML_INICIAL.md). La explicación de los requisitos GM-1 a GM-10 está en [REPORTE_PROYECTO.md](REPORTE_PROYECTO.md).
