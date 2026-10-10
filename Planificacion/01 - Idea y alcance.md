# Space Navigation

**Estado:** borrador inicial que estamos revisando  
**Primera entrega:** viernes 23 de octubre de 2026

## 1. Descripción general

En Space Navigation queremos hacer un juego arcade 2D de escritorio. Vamos a controlar una nave, esquivar ataques y destruir alienígenas para completar tres niveles cortos.

## 2. Objetivo de la partida

Para avanzar, tendremos que destruir la cuota de enemigos de cada nivel. Vamos a aumentar las cuotas entre niveles (N1 < N2 < N3). Iniciaremos cada partida con tres vidas y buscaremos completarla con un puntaje alto.

Sumaremos puntaje al destruir enemigos. No lo vamos a usar como una segunda condición para avanzar de nivel.

## 3. Reglas principales

1. Antes de empezar, mostraremos una guía breve de controles.
2. Vamos a iniciar cada partida con tres vidas y el puntaje en cero.
3. Mantendremos el mismo arquetipo de alienígena en todos los niveles y usaremos un único patrón diagonal de ataque.
4. Cada enemigo destruido sumará puntaje una sola vez.
5. Cuando recibamos un impacto válido, quitaremos una vida y daremos a la nave un periodo breve de invulnerabilidad.
6. Vamos a usar el dash para esquivar cuando esté disponible y después tendrá que recargarse.
7. Al completar la cuota del tercer nivel, mostraremos la pantalla de victoria. Si perdemos las tres vidas, mostraremos Game Over.
8. Al terminar, podremos guardar el resultado en una tabla local con los tres mejores puntajes.

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

Vamos a permitir diagonales al combinar dos direcciones y normalizaremos su velocidad para que no supere la horizontal o vertical.

## 5. Fuera de esta entrega

En esta entrega no vamos a incluir varios arquetipos de alienígena, ataques aleatorios, multijugador, cuentas, conexión de red, controles remapeables ni un tutorial interactivo. Podemos dejar fuera los meteoritos o usarlos como obstáculos simples; no les daremos IA ni disparos y no dejaremos que bloqueen la entrega.

## 6. Primer incremento jugable

En la primera demo integrada queremos iniciar una partida de escritorio y probar el núcleo del combate:

- Vamos a mover la nave en cuatro direcciones y en diagonal sin depender de los FPS.
- Dispararemos a un alienígena que use el patrón diagonal acordado.
- Destruiremos al enemigo y sumaremos puntaje una sola vez por impacto válido.
- Comprobaremos que un impacto enemigo quite una vida y active invulnerabilidad breve.
- Jugaremos la demo integrada y anotaremos los defectos que encontremos.

Tomaremos esta demo como un incremento técnico. Todavía no necesitamos tener los tres niveles, el top 3 persistente ni todas las pantallas finales.

## 7. Recursos del juego

Vamos a buscar nuestros propios recursos para imágenes, audio y otros elementos visuales. Antes de incorporarlos, anotaremos para cada uno:

- nombre y uso dentro del juego;
- autor o fuente;
- licencia o autorización;
- crédito que se incluirá en el README.

## 8. Pendientes de definición

- Cuotas por nivel (`N1`, `N2`, `N3`) y ajuste de dificultad.
- Velocidad de la nave y de los enemigos.
- Cadencia y velocidad del patrón diagonal.
- Puntaje por enemigo.
- Duración de la invulnerabilidad tras recibir un impacto.
- Duración y recarga del dash.
- Política de empates en la tabla local.
- Fecha única de la primera demo: el backlog y la minuta indican el 8 de octubre; el análisis posterior indica el 9.
- Confirmación de la cátedra sobre el uso del código fuente del proyecto entregado.

Probaremos los valores de balance en el juego y los registraremos cuando los acordemos. Afinarlos no ampliará el alcance.
