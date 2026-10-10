# Plan de trabajo

**Estado:** propuesta de coordinación que queremos revisar como equipo  
**Primera entrega:** viernes 23 de octubre de 2026  
**Congelamiento propuesto:** martes 20 de octubre de 2026

## 1. Forma de trabajo

Vamos a avanzar por incrementos que podamos integrar y jugar. Para cada tarea acordaremos una persona responsable, alguien que la revise y un criterio de aceptación. Antes de cambiar contratos o firmas compartidas, lo conversaremos entre nosotros.

Vamos a trabajar en ramas breves, cada una con un objetivo. Mantendremos en `main` una versión integrada y usaremos `planificacion` para reunir los documentos que revisaremos antes de hacer commit.

## 2. Etapas

| Etapa | Fechas de referencia | Resultado esperado |
| --- | --- | --- |
| F0. Alineación y contratos | 5–6 oct. | Alcance, decisiones pendientes, contratos y accesos acordados. |
| F1. Núcleo jugable | 7–9 oct. | Mover, disparar, recibir y resolver impactos, perder una vida y sumar puntaje. |
| F2. Partida completa | 10–14 oct. | Niveles por cuotas, dash, HUD, pantallas finales, recursos compartidos y top 3 local. |
| F3. Calidad y evidencia | 15–20 oct. | Regresión, UML final, reporte, README y versión candidata. |
| F4. Entrega | 21–23 oct. | Verificar el paquete y subir la entrega antes del plazo. |

Encontramos fechas distintas para F1: la minuta y el backlog indican demo el 8 de octubre, mientras el análisis posterior indica el 9. Antes de repartir los vencimientos, acordaremos una fecha y la registraremos en Jira y en estos documentos.

## 3. Reparto propuesto

Tomamos del análisis del 5 de octubre estos frentes como propuesta. Si intercambiamos tareas, dejaremos acordado quién las implementa y quién las revisa.

### Frente A. Entidades y control

En este frente trabajaremos en `Ship`, `Alien`, los proyectiles, la entrada, el dash y `MovementStrategy`. Revisaremos su integración con el mundo y sus contratos.

### Frente B. Mundo y reglas

En este frente trabajaremos en `GameWorld`, snapshots, colisiones, vidas, puntaje, niveles, cuotas, `AlienBuilder` y `LevelDirector`. Mantendremos el UML sincronizado con el código.

### Frente C. Pantallas, recursos y entrega

Yo voy a liderar este frente según el reparto que proponemos: prepararé una pantalla mínima para integrar la primera demo y avanzaré con la guía, el flujo de pantallas, el HUD, el ciclo de vida de recursos, la tabla local, el README, la documentación y el paquete de entrega.

## 4. Tareas iniciales del frente C

### F0 — Preparación

- Voy a consultar la política de la cátedra sobre reutilizar el código fuente del proyecto entregado.
- Acordaremos las firmas compartidas de `GameWorld`, `PlayerInput` y `WorldSnapshot`.
- Prepararé un README propio con los controles, la ejecución y los créditos de recursos pendientes.
- Confirmaremos el acceso de escritura al repositorio y al tablero.

### F1 — Primera integración

- Prepararé una `GameScreen` mínima que dibuje el `WorldSnapshot`.
- Conectaré el menú con esa pantalla.
- Integraremos y mostraremos el núcleo de combate junto con los otros frentes.

Con la pantalla mínima vamos a adelantar la integración. Después incorporaremos las pantallas completas y `AbstractGameScreen`, cuando tengamos el flujo real de menú, partida, victoria y Game Over.

### F2 — Partida completa

- Implementaré la guía fija de controles, el menú, el HUD, las pantallas de victoria y Game Over, y el reinicio.
- Centralizaremos la carga y liberación de recursos en `GameAssets`.
- Integraremos la tabla local mediante `ScoreRepository` y el adaptador que acordemos.
- Consultaré el top 3 desde el menú o desde las pantallas de resultado.

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

Si el calendario se aprieta, proponemos recortar primero la persistencia local, después el dash y luego el tercer nivel. Mantendremos dentro del alcance académico los requisitos GM-3 a GM-10, el README, el reporte y el paquete de entrega. Registraremos cualquier recorte y su motivo.

## 8. Pendientes de coordinación

- Nos falta acordar si la demo de Sprint 1 será el 8 o el 9 de octubre.
- Nos falta confirmar con la cátedra si podemos usar el código base.
- Tenemos que elegir la tecnología y el driver de persistencia local.
- Tenemos que acordar los valores iniciales de balance.
- Nos falta definir una meta de commits que nos sirva como equipo.
- Tenemos que confirmar quiénes serán responsables y revisores.
