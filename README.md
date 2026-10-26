# Space Navigation — INF2236 Programación Avanzada PUCV

**Pontificia Universidad Católica de Valparaíso**  
**Escuela de Ingeniería Informática**  
**Asignatura:** INF2236 - Programación Avanzada  
**Docente:** Claudio Cubillos  
**Semestre:** 2026-2  
**Repositorio Oficial:** [https://github.com/L3v1smpr/GameProject.git](https://github.com/L3v1smpr/GameProject.git) (Rama: `main`)

**Integrantes:**
* Elvis Peñaloza (L3v1smpr)
* Cristian Ávalos (NotChinoski)
* Alex Reyes (MrLynel)

---

Demo arcade 2D para escritorio desarrollada en Java 11 con LibGDX 1.12.1. Esta guía detalla los requisitos, pasos de importación, compilación y controles del juego.

> **Nota sobre el código base (`puppy.code.prototype`):**  
> El paquete `puppy.code.prototype` se conserva intacto en el repositorio exclusivamente como respaldo empírico para el análisis comparativo de ingeniería de software requerido en **GM-2**. El flujo de ejecución del videojuego inicia en `puppy.code.app.SpaceNavigation`.

---

## Requisitos del Sistema

* **Java:** Oracle JDK 11 u OpenJDK 11 instalado y configurado en la variable de entorno `JAVA_HOME`.
* **Gradle:** Wrapper incluido en el repositorio (`gradlew` y `gradlew.bat`). No requiere instalación externa de Gradle.
* **IDE compatible:** Eclipse o NetBeans con soporte para proyectos Gradle (opcional si se ejecuta desde consola).

Verificación de la versión activa de Java:

```
java -version
```
## Compilar y Ejecutar desde la Terminal

Abre una terminal en la carpeta raíz del repositorio y ejecuta los comandos según tu sistema operativo.

### En Windows (CMD o PowerShell)
```
.\gradlew.bat test build
.\gradlew.bat lwjgl3:run
```

### En Linux o macOS
```
./gradlew test build
./gradlew lwjgl3:run
```

### Generar y ejecutar el archivo JAR independiente

En Linux o macOS:

```
./gradlew lwjgl3:jar
java -jar lwjgl3/build/libs/SpaceNav2024-1.0.0.jar
```

En Windows:

```
.\gradlew.bat lwjgl3:jar
java -jar lwjgl3/build/libs/SpaceNav2024-1.0.0.jar
```

## Importar en IDEs

### Eclipse

1. Selecciona File > Import > Gradle > Existing Gradle Project.
2. Indica la carpeta raíz del repositorio (donde se encuentran `settings.gradle` y `gradlew.bat`).
3. Manten la opcion Gradle Wrapper como distribucion y asegurate de usar un JDK 11.
4. Finaliza la importación, espera a que sincronicen los modulos `core` y `lwjgl3`, y ejecuta la tarea `lwjgl3:run` desde la pestaña Gradle Tasks.

### NetBeans

1. Selecciona File > Open Project y abre la carpeta raiz del repositorio.
2. Acepta la detección automática del proyecto Gradle y espera la sincronizacion.
3. Verifica en las propiedades del proyecto que la plataforma Java seleccionada sea JDK 11.
4. Ejecuta la tarea `lwjgl3:run` en las tareas Gradle del proyecto.

## Controles de Juego

* **W, A, S, D:** Mover la nave en ocho direcciones (velocidad de 390 px/s con vector diagonal normalizado).
* **I (mantenida):** Disparo continuo automático con cadencia de 0.25 s y proyectiles a 450 px/s.
* **O:** Activar Dash defensivo en la dirección de movimiento: multiplica la velocidad por 3 durante 0.15 s, otorga invulnerabilidad durante el impulso más 0.5 s de gracia post-dash, y tiene 3.0 s de enfriamiento.
* **ENTER:** Confirmar selección en menús, iniciar partida, guardar iniciales en el Top 3 o reiniciar tras una derrota.
* **ESC:** Pausar la partida (despliega el menú modal con opciones de audio) o volver al menú principal.
* **Flechas / WASD:** Navegar por las opciones del menú de pausa y ajustar el volumen de música y efectos sonoros (con A/D o Izquierda/Derecha).
* **A a Z:** Ingresar las 3 iniciales cuando la puntuación final clasifica al Top 3 local.
* **Retroceso:** Borrar el último carácter ingresado en el registro de récord.

>**Secreto (Menú Debug / Código Konami):** Con el juego en pausa (ESC), ingresa la secuencia **Arriba, Arriba, Abajo, Abajo, Izquierda, Derecha, Izquierda, Derecha, I, O** para desbloquear el submenú DEBUG y alternar el Modo Dios (invulnerabilidad permanente).

## Reglas, Sectores y Puntuación

La partida comienza con 3 vidas y consta de 3 sectores espaciales (niveles) cuya progresión se rige por cuotas de enemigos destruidos.

### Progresión por Sectores

* **Sector 1:**
  * **Cuota:** 15 aliens.
  * **Resistencia:** 1 HP por alien.
  * **Movimiento:** Descenso lineal con velocidad variada (base 90 px/s).
  * **Ataque enemigo:** Disparo lineal vertical hacia abajo (220 px/s).


* **Sector 2:**
  * **Cuota:** 30 aliens.
  * **Resistencia:** 2 HP por alien.
  * **Movimiento:** Zigzag (base 110 px/s), Centinelas estáticos y Bombarderos horizontales.
  * **Ataque enemigo:** Disparo en V (45 grados), ataque alternado (recto/apuntado) y ráfagas de 6 tiros.


* **Sector 3:**
  * **Cuota:** 45 aliens.
  * **Resistencia:** 3 HP por alien.
  * **Movimiento:** Zigzag (base 110 px/s), Centinelas estáticos y alta frecuencia de Bombarderos.
  * **Ataque enemigo:** Disparo en V (45 grados), abanico triple (Spread a 15 grados) y ráfagas continuas.

### Puntuación y Obstáculos

* **Alien (cualquier variante):** 1 / 2 / 3 HP (según sector) — **100 pts**.
* **Meteorito Pequeño:** 1 HP — **25 pts**.
* **Meteorito Mediano:** 2 HP — **50 pts**.
* **Meteorito Grande:** 3 HP — **100 pts**.

### Mecánicas de Partida

* **Pérdida de vida y Respawn:** Si la nave recibe un impacto o choca contra un alien o meteorito sin estar invulnerable, pierde una vida, se activa un efecto de sacudida de cámara (Screen Shake) y reaparece en su posición inicial con 1.5 s de invulnerabilidad, limpiando los proyectiles activos en pantalla.
* **Persistencia del Top 3:** Al terminar la partida (Victoria o Game Over), si el puntaje supera al tercer lugar registrado, el jugador puede ingresar 3 iniciales (A-Z). El ranking se guarda localmente mediante Preferences de LibGDX bajo el identificador `space_navigation_scores`.

---

## Estructura y Documentación

* El paquete histórico `puppy.code.prototype` se conserva intacto para el contraste arquitectónico (**GM-2**).
* Los recursos gráficos y sonoros están organizados en `assets/` (`player/`, `aliens/`, `obstacles/`, `ui/`, `audio/music/` y `audio/sfx/`) y son administrados por el Singleton `GameAssets`.
* El diagrama de clases UML completo se encuentra en [UML.md](UML.md).
* El desarrollo detallado de los requerimientos GM-1 a GM-10 se encuentra en [REPORTE_PROYECTO.md](REPORTE_PROYECTO.md).
