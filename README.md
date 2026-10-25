# Space Navigation

Juego arcade 2D de escritorio hecho con Java 11 y LibGDX. Pilotamos una nave por tres sectores, combatimos aliens y meteoritos y tratamos de completar las cuotas sin perder las tres vidas.

## Requisitos

- Oracle JDK 11.
- Gradle Wrapper incluido en el proyecto.
- Windows, Linux o macOS con soporte para escritorio.

## Abrir y ejecutar

En Windows, desde la carpeta raíz del proyecto:

```powershell
.\gradlew.bat :lwjgl3:run
```

En Linux o macOS:

```sh
./gradlew :lwjgl3:run
```

Para compilar los módulos y ejecutar las tareas de prueba disponibles:

```powershell
.\gradlew.bat test compileJava
```

En Eclipse, importamos la carpeta raíz como un proyecto Gradle existente, esperamos la sincronización y ejecutamos la tarea `lwjgl3:run` desde el panel de tareas Gradle. El lanzador de escritorio es `puppy.code.lwjgl3.Lwjgl3Launcher`.

## Partida

Empezamos con tres vidas. Para ganar, destruimos la cuota de aliens de cada sector:

| Sector | Cuota | Resistencia del alien de oleada |
| --- | ---: | ---: |
| 1 | 15 | 1 impacto |
| 2 | 30 | 2 impactos |
| 3 | 45 | 3 impactos |

Los aliens de oleada cambian su movimiento y patrón de disparo por sector. En los sectores dos y tres también aparecen centinelas y bombarderos. Los meteoritos descienden con tamaños y resistencias distintos; no cuentan para la cuota. Destruir un alien suma 100 puntos. Los meteoritos chicos, medianos y grandes suman 25, 50 y 100 puntos.

Al perder una vida, reaparecemos en la posición inicial con 1,5 segundos de invulnerabilidad. El dash dura 0,15 segundos, se recarga en 3 segundos y concede 0,5 segundos adicionales de invulnerabilidad al terminar. El factor de velocidad actual del dash es 3.

Al completar los dos primeros sectores aparece «Nivel Superado» y esperamos una tecla o un clic para continuar. El tercero lleva a Victoria. Si perdemos las tres vidas, aparece Game Over. El Top 3 se guarda localmente con las preferencias de LibGDX; si nuestra puntuación califica, ingresamos tres iniciales.

## Controles

| Tecla | Acción |
| --- | --- |
| W/A/S/D | Mover la nave en cuatro direcciones y en diagonal |
| I | Disparar mientras la mantenemos presionada |
| O | Hacer dash |
| Escape | Abrir la pausa durante la partida; volver al menú desde las pantallas finales |
| Flechas o W/A/S/D | Recorrer las opciones de pausa |
| Enter | Confirmar una opción; iniciar desde el menú; guardar iniciales o reiniciar desde una pantalla final |
| A-Z | Ingresar iniciales cuando la puntuación califica |
| Retroceso | Borrar la última inicial |

Desde la pausa podemos reanudar, ajustar volúmenes o volver al menú. Para pruebas, la secuencia ↑ ↑ ↓ ↓ ← → ← → I O durante la pausa habilita el menú secreto de depuración, donde podemos alternar el modo dios (invulnerabilidad).

## Código y diseño

El módulo `core` contiene las reglas, entidades, entradas, pantallas y sistemas; `lwjgl3` contiene el lanzador de escritorio. `GameWorld` coordina la simulación y entrega un `WorldSnapshot`; las pantallas presentan ese estado.

- **Singleton:** `GameAssets` comparte y libera los recursos gráficos y de audio.
- **Strategy:** `Alien` usa estrategias intercambiables de movimiento y ataque.
- **Template Method:** `AbstractGameScreen` fija el ciclo de renderizado de las pantallas.
- **Builder:** `AlienBuilder` valida la configuración y `LevelDirector` prepara las oleadas.
- **SOLID y GRASP:** separamos las reglas del dibujo, usamos contratos para entradas, colisiones, creación de aliens y puntajes, y dejamos la composición de dependencias en `SpaceNavigation`.

El diagrama de clases está en [UML_INICIAL.md](UML_INICIAL.md). La explicación punto por punto de los requisitos GM está en [REPORTE_PROYECTO.md](REPORTE_PROYECTO.md). Los bocetos de menú, partida y pantallas finales están en `Planificacion/Bocetos/`.

## Recursos

Las imágenes y los sonidos usados por el juego están organizados bajo `assets/player`, `assets/aliens`, `assets/obstacles`, `assets/ui` y `assets/audio`. Aún no tenemos información verificable sobre la fuente y la licencia de algunos recursos; dejamos ese antecedente señalado en el reporte. El código legado del primer prototipo sigue en `core/src/main/java/puppy/code/prototype`; no forma parte del flujo activo de pantallas.
