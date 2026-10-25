# Space Navigation - INF2236 Programacion Avanzada PUCV

Demo arcade 2D para escritorio desarrollada en Java 11 con LibGDX 1.12.1\[cite: 28]. Esta guia explica como importar, ejecutar y probar la entrega del proyecto\[cite: 28].

## Requisitos

* Java: Oracle JDK 11 u OpenJDK 11 instalado y configurado en JAVA\_HOME\[cite: 28].
* Gradle: Wrapper incluido en el repositorio (gradlew y gradlew.bat)\[cite: 28, 85]. No es necesario instalar Gradle por separado\[cite: 28].
* IDE: Eclipse o NetBeans con soporte para proyectos Gradle (opcional, si se desea ejecutar desde un entorno grafico)\[cite: 28].

Comprueba la version activa desde la raiz del proyecto\[cite: 28]:

&#x20;   java -version


## Compilar y Ejecutar desde la Terminal

Abre una terminal en la carpeta raiz del repositorio y ejecuta los comandos segun tu sistema operativo\[cite: 28].

### En Windows (CMD o PowerShell)

&#x20;   gradlew.bat test build
    gradlew.bat lwjgl3:run


### En Linux o macOS

&#x20;   ./gradlew test build
    ./gradlew lwjgl3:run


### Generar y ejecutar el archivo JAR independiente

En Linux o macOS\[cite: 28]:

&#x20;   ./gradlew lwjgl3:jar
    java -jar lwjgl3/build/libs/SpaceNav2024-1.0.0.jar


En Windows\[cite: 28]:

&#x20;   gradlew.bat lwjgl3:jar
    java -jar lwjgl3/build/libs/SpaceNav2024-1.0.0.jar


## Importar en IDEs

### Eclipse

1. Selecciona File > Import > Gradle > Existing Gradle Project\[cite: 28].
2. Indica la carpeta raiz del repositorio (donde se encuentran settings.gradle y gradlew.bat)\[cite: 28].
3. Manten la opcion Gradle Wrapper como distribucion y asegurate de usar un JDK 11\[cite: 28].
4. Finaliza la importacion, espera a que sincronicen los modulos core y lwjgl3, y ejecuta la tarea lwjgl3:run desde la pestana Gradle Tasks\[cite: 28].

### NetBeans

1. Selecciona File > Open Project y abre la carpeta raiz del repositorio\[cite: 28].
2. Acepta la deteccion automatica del proyecto Gradle y espera la sincronizacion\[cite: 28].
3. Verifica en las propiedades del proyecto que la plataforma Java seleccionada sea JDK 11\[cite: 28].
4. Ejecuta la tarea lwjgl3:run en las tareas Gradle del proyecto\[cite: 28].

## Controles de Juego

|Tecla|Accion|
|-|-|
|W, A, S, D|Mover la nave en ocho direcciones (390 px/s, con velocidad diagonal normalizada)\[cite: 28, 43, 49, 58].|
|I (Mantenida)|Disparo continuo con cadencia automatica de 0.25 s y proyectiles a 450 px/s\[cite: 28, 43, 58].|
|O|Activar Dash defensivo en la direccion de movimiento: multiplica la velocidad por 3 durante 0.15 s, otorga invulnerabilidad durante el impulso mas 0.5 s adicionales post-dash, y tiene un enfriamiento de 3.0 s\[cite: 28, 43, 49, 58].|
|ENTER|Confirmar seleccion en menus, iniciar partida, guardar iniciales en el Top 3 o reiniciar\[cite: 28, 57, 68, 71, 72, 73].|
|ESC|Pausar la partida (abre el menu modal con opciones de volumen) o volver al menu principal\[cite: 57, 68, 71, 72].|
|Flechas / WASD|Navegar por el menu de pausa y ajustar el volumen de musica y efectos (A/D o Izquierda/Derecha)\[cite: 57, 72].|
|A a Z|Ingresar iniciales de 3 letras cuando el puntaje final clasifica al Top 3 local\[cite: 28, 56, 68, 71].|
|Retroceso|Borrar la ultima letra ingresada en el registro de record\[cite: 28, 56, 68, 71].|

Secreto (Menu Debug / Codigo Konami): Con el juego en pausa (ESC), ingresa la secuencia Arriba, Arriba, Abajo, Abajo, Izquierda, Derecha, Izquierda, Derecha, I, O para desbloquear el submenu DEBUG y alternar el Modo Dios (invulnerabilidad permanente)\[cite: 72].

## Reglas, Sectores y Puntuacion

La partida comienza con 3 vidas y consta de 3 sectores espaciales (niveles) cuya progresion se rige por cuotas de enemigos destruidos\[cite: 28, 43, 72, 82]:

|Sector (Nivel)|Cuota de Aliens|HP por Alien|Variantes y Movimiento|Patrones de Ataque Enemigo|
|-|-|-|-|-|
|1|15|1 HP|Descenso lineal con velocidad variada (base 90 px/s)\[cite: 43, 54]|Disparo lineal vertical hacia abajo (220 px/s)\[cite: 36, 43, 54]|
|2|30|2 HP|Zigzag (base 110 px/s), Centinelas estaticos y Bombarderos horizontales\[cite: 43, 54, 82]|Disparo en V (45 grados), ataque alternado (recto/apuntado) y rafagas de 6 tiros\[cite: 33, 34, 40, 43, 54]|
|3|45|3 HP|Zigzag (base 110 px/s), Centinelas estaticos y alta frecuencia de Bombarderos\[cite: 43, 54, 82]|Disparo en V (45 grados), abanico triple (Spread a 15 grados) y rafagas continuas\[cite: 34, 39, 40, 43, 54]|

### Tabla de Puntuacion y Obstaculos

|Entidad Destruida|Resistencia|Puntaje Otorgado|
|-|-|-|
|Alien (cualquier variante)|1 / 2 / 3 HP (segun sector)\[cite: 43, 54]|100 pts\[cite: 28, 43]|
|Meteorito Pequeno|1 HP\[cite: 43, 47]|25 pts\[cite: 28, 43, 47]|
|Meteorito Mediano|2 HP\[cite: 43, 47]|50 pts\[cite: 28, 43, 47]|
|Meteorito Grande|3 HP\[cite: 43, 47]|100 pts\[cite: 43, 47]|

* Perdida de vida y Respawn: Si la nave recibe un impacto o choca contra un alien o meteorito sin estar invulnerable, pierde una vida, se activa un efecto de sacudida de camara (Screen Shake) y reaparece en su posicion inicial con 1.5 s de invulnerabilidad, limpiando los proyectiles activos en pantalla\[cite: 28, 43, 72, 82].
* Persistencia del Top 3: Al terminar la partida (Victoria o Game Over), si el puntaje supera al tercer lugar registrado, el jugador puede ingresar 3 iniciales (A-Z)\[cite: 28, 68, 71, 79]. El ranking se guarda localmente mediante Preferences de LibGDX bajo el identificador space\_navigation\_scores\[cite: 28, 79].

## Estructura y Documentacion

* El paquete historico puppy.code.prototype se conserva intacto para el contraste arquitectonico (GM-2)\[cite: 28, 29, 85].
* Los recursos graficos y sonoros estan organizados en assets/ (player/, aliens/, obstacles/, ui/, audio/music/ y audio/sfx/) y son administrados por el Singleton GameAssets\[cite: 30, 85].
* El diagrama de clases UML completo se encuentra en [UML\_INICIAL.md](UML_INICIAL.md)\[cite: 28].
* El desarrollo detallado de los requerimientos GM-1 a GM-10 se encuentra en [REPORTE\_PROYECTO.md](REPORTE_PROYECTO.md)\[cite: 28].

