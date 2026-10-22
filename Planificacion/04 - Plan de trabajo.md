# Plan de trabajo

**Estado:** propuesta de coordinación que queremos revisar como equipo  
**Entrega de referencia:** viernes 23 de octubre de 2026; todavía tenemos que confirmar si sigue vigente.\
**Fechas y duración de los sprints:** pendientes de acuerdo con el equipo.

## 1. Forma de trabajo

Vamos a avanzar por incrementos que podamos integrar y jugar. Para cada tarea acordaremos una persona responsable, alguien que la revise y un criterio de aceptación. Antes de cambiar contratos o firmas compartidas, lo conversaremos entre nosotros.

Vamos a trabajar en ramas breves, cada una con un objetivo. Mantendremos en `main` una versión integrada y usaremos `planificacion` para reunir los documentos que revisaremos antes de hacer commit.

Todavía no hemos organizado los sprints. Queremos empezar pronto y terminar el proyecto en el menor tiempo posible, así que fijaremos los objetivos y el calendario en cuanto tengamos la respuesta del equipo sobre disponibilidad y responsabilidades. Nos apoyaremos intensivamente en IA para analizar, implementar, revisar y documentar; antes de integrar cada cambio, nos aseguraremos de entenderlo y poder explicarlo.

## 2. Etapas

| Etapa | Orden de trabajo | Resultado esperado |
| --- | --- | --- |
| F0. Alineación y contratos | Primero | Alcance, reglas acordadas, contratos, responsabilidades y accesos listos para trabajar. |
| F1. Núcleo jugable | Después de F0 | Mover, disparar, recibir y resolver impactos, perder una vida y sumar puntaje. |
| F2. Partida completa | Después de F1 | Tres niveles por cuotas, dash, meteoritos simples, HUD, pantallas finales y guardado local con SQLite. |
| F3. Calidad y evidencia | Antes de entregar | Revisión de la partida completa, UML, reporte, README y versión candidata. |
| F4. Entrega | Último | Verificar el paquete y subir la entrega dentro del plazo que confirmemos. |

Las fechas anteriores de F0 y F1 ya no reflejan nuestro avance, y todavía no hemos acordado un nuevo calendario. No fijaremos vencimientos hasta confirmar la fecha de entrega y la disponibilidad del equipo.

## 3. Reparto propuesto

Tomamos del análisis del 5 de octubre estos frentes como propuesta. Si intercambiamos tareas, dejaremos acordado quién las implementa y quién las revisa.

### Frente A. Entidades y control

En este frente trabajaremos en `Ship`, `Alien`, los proyectiles, los meteoritos simples, la entrada, el dash, `MovementStrategy` y las formaciones de disparo. Revisaremos su integración con el mundo y sus contratos.

### Frente B. Mundo y reglas

En este frente trabajaremos en `GameWorld`, snapshots, colisiones, vidas, puntaje, niveles, cuotas, fases de la partida, `AlienBuilder` y `LevelDirector`. Mantendremos el UML sincronizado con el código.

### Frente C. Pantallas, recursos y entrega

Yo voy a liderar este frente según el reparto que proponemos: prepararé una pantalla mínima para integrar la primera demo y avanzaré con la guía, el flujo de pantallas, el HUD, el ciclo de vida de recursos, la tabla local, el README, la documentación y el paquete de entrega.

## 4. Tareas iniciales del frente C

### F0 — Preparación

- Voy a consultar la política de la cátedra sobre reutilizar el código fuente del proyecto entregado.
- Acordaremos las firmas compartidas de `GameWorld`, `PlayerInput`, `WorldSnapshot` y `ScreenNavigator`.
- Para GM-3, completaremos `ScreenNavigator` con `showGameOver()` y `showVictory()` y mantendremos el UML alineado con ese contrato.
- Prepararé un README propio con los controles, la ejecución y los créditos de recursos pendientes.
- Confirmaremos el acceso de escritura al repositorio y al tablero.
- Confirmaremos la fecha de entrega, la disponibilidad de cada integrante y quién se hará cargo de revisar cada frente.

### F1 — Primera integración

- Prepararé una `GameScreen` mínima que dibuje el `WorldSnapshot`.
- Conectaré el menú con esa pantalla.
- Integraremos y mostraremos el núcleo de combate junto con los otros frentes.
- Revisaremos que perder una vida conserve puntaje y progreso, limpie solo los proyectiles y aplique 1,5 segundos de invulnerabilidad al reaparecer.

Con la pantalla mínima vamos a adelantar la integración. Después incorporaremos las pantallas completas y `AbstractGameScreen`, cuando tengamos el flujo real de menú, partida, victoria y Game Over.

### F2 — Partida completa

- Implementaré la guía fija de controles, el menú, el HUD, las pantallas de victoria y Game Over, y el reinicio.
- Centralizaremos la carga y liberación de recursos en `GameAssets`.
- Guardaremos los puntajes en SQLite mediante `ScoreRepository` y `SQLiteScoreRepository`.
- Nos gustaría consultar el top 3 desde el menú y al terminar la partida; lo confirmaremos con el equipo antes de dejar ambas vistas como parte del alcance.
- Mostraremos “Nivel Superado” al completar los dos primeros niveles y prepararemos el escenario para continuar.
- Implementaremos las fases de nivel superado, Game Over y Victoria, junto con las transiciones mediante `ScreenNavigator`.
- Al alcanzar cada cuota detendremos la generación de aliens; todavía tenemos que definir cómo se retirarán los que queden en pantalla.
- Añadiremos meteoritos como obstáculos simples, sin IA ni disparos, y mantendremos sus interacciones acotadas.

### F3 — Cierre

- Actualizaré el reporte del frente C con evidencia de las pantallas, Singleton y Template Method.
- Completaré las instrucciones de instalación y ejecución, los créditos y las dependencias.
- Probaremos la ruta de aceptación desde un checkout limpio y revisaremos el paquete de entrega.

## 5. Revisión e integración

- Daremos a cada rama un objetivo y cambios que podamos revisar por separado.
- No cambiaremos unilateralmente los contratos compartidos: avisaremos y acordaremos el cambio con quienes dependan de ellos.
- Pediremos a otra persona del equipo que revise cada pull request y pruebe la versión integrada.
- Haremos commits pequeños que describan cambios reales. La pauta exige al menos seis; nos falta acordar la meta interna porque el plan inicial y el análisis posterior proponen cantidades diferentes.
- Actualizaremos el UML y la explicación del requisito cuando cambiemos responsabilidades o colaboraciones.

## 6. Criterio de tarea terminada

Daremos una tarea por terminada cuando cumpla su criterio de aceptación, se integre con el resto del juego, respete los contratos acordados y otra persona la haya revisado. Si cambiamos la arquitectura, también actualizaremos el UML y la explicación correspondiente.

Consideraremos terminada la tabla local cuando comprobemos que guarda los tres mejores resultados y los recupera después de cerrar y volver a abrir la aplicación.

## 7. Recorte si el calendario se aprieta

Si el calendario se aprieta, primero reorganizaremos las tareas y buscaremos qué podemos integrar en paralelo. Antes de recortar una función que ya acordamos —como SQLite, el dash o los tres niveles—, lo revisaremos con el equipo y comprobaremos qué permite la pauta de la cátedra. Mantendremos dentro del alcance los requisitos académicos GM-3 a GM-10, el README, el reporte y el paquete de entrega, salvo que la cátedra nos indique otra cosa.

## 8. Pendientes de coordinación

- Tenemos que fijar una nueva fecha para la primera demo; las referencias anteriores del 8 y 9 de octubre ya pasaron.
- Nos falta definir fechas y duración para los sprints y confirmar si la entrega de referencia del 23 de octubre sigue vigente.
- Nos falta confirmar con la cátedra si podemos usar el código base.
- Tenemos que definir cómo haremos que los aliens restantes huyan o exploten al completar la cuota.
- Tenemos que acordar la fuerza del dash, el límite exacto del área de juego y las interacciones de los meteoritos.
- Tenemos que acordar cómo ingresaremos el nombre de tres caracteres y el criterio que usaremos para resolver empates.
- Tenemos que confirmar con el equipo si consultaremos el top 3 tanto en el menú como al terminar la partida.
- Tenemos que revisar la lista de patrones exigidos por la cátedra y sus reglas sobre el uso de IA.
- Nos falta definir una meta de commits que nos sirva como equipo.
- Tenemos que confirmar quiénes serán responsables y revisores.
