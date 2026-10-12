# Space Navigation

**Estado:** borrador inicial que estamos revisando  
**Primera entrega:** viernes 23 de octubre de 2026 (fecha de referencia que todavía tenemos que confirmar con el equipo y la cátedra)

## 1. Descripción general

En Space Navigation queremos hacer un juego arcade 2D de escritorio. Vamos a controlar una nave, esquivar ataques y destruir alienígenas para completar tres niveles cortos.

## 2. Objetivo de la partida

Para avanzar, tendremos que destruir la cuota de aliens de cada nivel: 15 en el nivel 1, 30 en el nivel 2 y 45 en el nivel 3. Iniciaremos cada partida con tres vidas y buscaremos completarla con un puntaje alto.

Sumaremos puntaje al destruir aliens y meteoritos. No lo vamos a usar como una segunda condición para avanzar de nivel.

## 3. Reglas principales

1. Antes de empezar, mostraremos una guía breve de controles.
2. Vamos a iniciar cada partida con tres vidas y el puntaje en cero.
3. Mantendremos el mismo arquetipo de alienígena en todos los niveles. Los aliens apuntarán hacia la nave y alternarán entre dos formaciones de disparo: una lineal hacia abajo y otra en V con un ángulo de 45°. En cada descarga lanzarán dos proyectiles simultáneos cada 1,8 segundos.
4. Un impacto destruirá al alien y al proyectil que lo golpee. Sumaremos 100 puntos por alien destruido. Los proyectiles se desactivarán cuando superen el límite del área de juego.
5. Si un proyectil alienígena nos golpea o chocamos físicamente con un alien, perderemos una vida. En el choque físico también destruiremos al alien. Después de perder una vida reapareceremos en un punto fijo y tendremos 1,5 segundos de invulnerabilidad. Durante ese tiempo, los aliens y proyectiles podrán atravesar la nave sin hacerle daño ni desaparecer.
6. Al perder una vida, quitaremos de la pantalla los proyectiles activos; los aliens y meteoritos se quedarán donde están. Mantendremos el puntaje y el progreso del nivel.
7. El dash durará 0,15 segundos, tendrá una recarga de 3 segundos y nos protegerá del daño mientras dure. Lo haremos en la dirección que indiquemos con WASD; si no nos estamos moviendo, avanzaremos hacia adelante. Durante el dash no eliminaremos los objetos ni proyectiles de la pantalla. Todavía tenemos que definir la fuerza o velocidad del impulso.
8. Al completar la cuota del primer o segundo nivel, detendremos la aparición de aliens y mostraremos “Nivel Superado” con la indicación “Presione cualquier botón para continuar”. Prepararemos el escenario para el siguiente nivel. Al completar la cuota del tercer nivel, mostraremos Victoria; si perdemos las tres vidas, mostraremos Game Over. La partida tendrá cuatro fases: `PLAYING`, `LEVEL_COMPLETE`, `GAME_OVER` y `VICTORY`. El menú quedará fuera de esas fases.
9. Sumaremos 100 puntos por alien destruido, 50 por meteorito estándar, 25 por meteorito chico y 75 por meteorito grande. Los meteoritos serán obstáculos, no contarán para las cuotas de enemigos.
10. Guardaremos los puntajes localmente con SQLite. Cuando se agoten las vidas y termine la partida, registraremos el puntaje con un nombre de tres caracteres. Mantendremos el puntaje al perder una vida y al cambiar de nivel; lo reiniciaremos al comenzar otra partida. Nos gustaría consultar el top 3 desde el menú y al terminar la partida, pero todavía tenemos que confirmarlo con el equipo.

## 4. Controles

Vamos a mantener los controles fijos durante esta entrega. Pondremos las mismas teclas en la guía inicial y en el README.

| Acción | Tecla | Qué haremos |
| --- | --- | --- |
| Mover hacia arriba | W | Moveremos la nave hacia arriba. |
| Mover hacia abajo | S | Moveremos la nave hacia abajo. |
| Mover a la izquierda | A | Moveremos la nave a la izquierda. |
| Mover a la derecha | D | Moveremos la nave a la derecha. |
| Disparar | I | Lanzaremos un proyectil del jugador. |
| Dash | O | Haremos la esquiva si está disponible. |
| Continuar o reiniciar | Enter | Continuaremos después de completar un nivel o iniciaremos otra partida desde Victoria o Game Over. |
| Volver al menú | Esc | Saldremos de Victoria o Game Over y volveremos al menú. |

Vamos a permitir diagonales al combinar dos direcciones y normalizaremos su velocidad para que no supere la horizontal o vertical.
En la pantalla “Nivel Superado” continuaremos con cualquier botón. En las pantallas finales usaremos Enter para reiniciar y Esc para volver al menú.

Como valores iniciales, usaremos 260 px/s para la nave, 90 px/s para el alien con descenso lineal, 110 px/s para el alien con movimiento zigzag, 450 px/s para los proyectiles del jugador y 220 px/s para los proyectiles de los aliens. Los aliens dispararán cada 1,8 segundos.

## 5. Fuera de esta entrega

En esta entrega no vamos a incluir varios arquetipos de alienígena, multijugador, cuentas, conexión de red, controles remapeables ni un tutorial interactivo. Incluiremos meteoritos como obstáculos simples: no tendrán IA ni disparos. Todavía tenemos que acordar qué ocurre cuando chocan con la nave o reciben un disparo, y mantendremos esta función acotada para que no bloquee la entrega.

## 6. Primer incremento jugable

En la primera demo integrada queremos iniciar una partida de escritorio y probar el núcleo del combate:

- Vamos a mover la nave en cuatro direcciones y en diagonal sin depender de los FPS.
- Probaremos las formaciones de disparo lineal y en V de los aliens, con dos proyectiles por descarga.
- Destruiremos al enemigo y sumaremos puntaje una sola vez por impacto válido.
- Comprobaremos que un impacto enemigo quite una vida y active 1,5 segundos de invulnerabilidad.
- Jugaremos la demo integrada y anotaremos los defectos que encontremos.

Tomaremos esta demo como un incremento técnico. Todavía no necesitamos tener los tres niveles, el top 3 persistente ni todas las pantallas finales.

## 7. Recursos del juego

Vamos a buscar nuestros propios recursos para imágenes, audio y otros elementos visuales. Antes de incorporarlos, anotaremos para cada uno:

- nombre y uso dentro del juego;
- autor o fuente;
- licencia o autorización;
- crédito que se incluirá en el README.

## 8. Pendientes de definición

- Si los aliens restantes huyen o explotan cuando alcanzamos la cuota de un nivel.
- La fuerza o velocidad del impulso del dash.
- Cómo vamos a crear el nombre de tres caracteres y cómo resolveremos los empates del top 3.
- Si mostraremos el top 3 tanto en el menú como al terminar la partida; queremos proponer ambos lugares al equipo.
- Qué ocurrirá cuando los meteoritos choquen con la nave o reciban disparos.
- El límite exacto del área de juego que usaremos para desactivar los proyectiles.
- Qué niveles usarán el movimiento lineal y cuáles el movimiento zigzag.
- El calendario actualizado de los sprints y la fecha real de la primera demo y de la entrega. La fecha anterior de referencia es el 23 de octubre de 2026.
- Confirmación de la cátedra sobre el uso del código fuente del proyecto entregado.
- Confirmación de las reglas de la cátedra sobre el uso de IA en el proyecto.

Probaremos los valores de balance en el juego y los registraremos cuando los acordemos. Afinarlos no ampliará el alcance.
