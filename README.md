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
3. Mantén la opción Gradle Wrapper como distribución y asegúrate de usar un JDK 11.
4. Finaliza la importación, espera a que sincronicen los módulos `core` y `lwjgl3`, y ejecuta la tarea `lwjgl3:run` desde la pestaña Gradle Tasks.

### NetBeans

1. Selecciona File > Open Project y abre la carpeta raíz del repositorio.
2. Acepta la detección automática del proyecto Gradle y espera la sincronización.
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

> **Secreto (Menú Debug / Código Konami):** Con el juego en pausa (ESC), ingresa la secuencia **Arriba, Arriba, Abajo, Abajo, Izquierda, Derecha, Izquierda, Derecha, I, O** para desbloquear el submenú DEBUG y alternar el Modo Dios (invulnerabilidad permanente).

## Reglas, Sectores y Puntuación

La partida comienza con 3 vidas y consta de 3 sectores espaciales (niveles) cuya progresión se rige por cuotas de enemigos destruidos.

### Progresión por Sectores

* **Sector 1:**
  * **Cuota:** 15 aliens.
  * **Resistencia:** 1 HP por alien.
  * **Movimiento:** Combinación de descenso lineal (base 90 px/s) y trayectoria en zigzag (base 110 px/s).
  * **Ataque enemigo:** Disparo lineal vertical hacia abajo (220 px/s).

* **Sector 2:**
  * **Cuota:** 30 aliens.
  * **Resistencia:** 2 HP por alien.
  * **Movimiento inicial:** Naves estándar con descenso lineal o zigzag, Centinelas estáticos y Bombarderos de patrullaje horizontal.
  * **Ataque enemigo inicial:** Disparo en V (45 grados), ataque alternado (50% recto / 50% apuntado) y ráfagas continuas de 6 tiros.
  * **Fase 2 en tiempo de ejecución (50% HP):** Al recibir daño, los enemigos cambian dinámicamente sus estrategias de vuelo y/o disparo manteniendo su identidad visual (`AlienType`).

* **Sector 3:**
  * **Cuota:** 45 aliens.
  * **Resistencia:** 3 HP por alien.
  * **Movimiento inicial:** Zigzag (base 110 px/s), Centinelas estáticos y alta frecuencia de Bombarderos pesados (140 px/s).
  * **Ataque enemigo inicial:** Disparo en V (45 grados), abanico triple (*Spread* a ±15 grados) y ráfagas continuas.
  * **Fase 2 en tiempo de ejecución (≤50% HP):** Evolución táctica de movimiento y ataque en las tres variantes enemigas.

### Puntuación y Obstáculos

* **Alien (cualquier variante):** 1 / 2 / 3 HP (según sector) — **100 pts**.
* **Meteorito Pequeño:** 1 HP — **25 pts**.
* **Meteorito Mediano:** 2 HP — **50 pts**.
* **Meteorito Grande:** 3 HP — **100 pts**.

### Mecánicas de Partida

* **Transición Dinámica de Estrategias (Patrón Strategy):** En los Sectores 2 y 3, cuando un enemigo reduce su vida al 50% o menos, ejecuta `AlienPhaseTransition` en tiempo real sin mutar su sprite: las naves estándar cambian su trayectoria (lineal, zigzag u horizontal) y su ataque (cualquiera de las 5 variantes); los Centinelas pasan a patrullar horizontalmente en la zona superior y cambian de disparo; y los Bombarderos conservan su ráfaga cambiando su vuelo a zigzag o descenso vertical.
* **Pérdida de vida y Respawn:** Si la nave recibe un impacto o choca contra un alien o meteorito sin estar invulnerable, pierde una vida, se activa un efecto de sacudida de cámara (*Screen Shake*) y reaparece en su posición inicial con 1.5 s de invulnerabilidad, limpiando los proyectiles activos en pantalla.
* **Persistencia del Top 3:** Al terminar la partida (Victoria o Game Over), si el puntaje supera al tercer lugar registrado, el jugador puede ingresar 3 iniciales (A-Z). El ranking se guarda localmente mediante `Preferences` de LibGDX bajo el identificador `space_navigation_scores`.

---

## Estructura y Documentación

* El paquete histórico `puppy.code.prototype` se conserva intacto para el contraste arquitectónico (**GM-2**).
* Los recursos gráficos y sonoros están organizados en `assets/` (`player/`, `aliens/`, `obstacles/`, `ui/`, `audio/music/` y `audio/sfx/`) y son administrados por el Singleton `GameAssets` con filtrado bilineal (`TextureFilter.Linear`).
* **Diagramas de Arquitectura UML:** El modelo de clases completo y sus vistas desglosadas por módulo y patrón de diseño se encuentran en [UML.md](UML.md).
* **Reporte Técnico y Narrativa (GM-1 a GM-10) — Disponible en doble formato:**
  * **Lectura directa en GitHub (Markdown):** [REPORTE_PROYECTO.md](REPORTE_PROYECTO.md) — Permite visualizar el informe completo, las evidencias de código y los diagramas Mermaid renderizados directamente desde el navegador en el repositorio.
  * **Vista formal descargable (Microsoft Word):** [REPORTE_PROYECTO.docx](REPORTE_PROYECTO.docx) — Documento formal para su lectura y evaluación tanto dentro como fuera de GitHub.
