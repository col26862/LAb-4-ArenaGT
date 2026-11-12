# ArenaGT - Laboratorio 4: Polimorfismo

Nombre: César Colorado  
Carné: 26862  
Curso: CC2008 - Programación Orientada a Objetos

Aplicación de consola Java. Los datos permanecen en memoria. No requiere Maven,
base de datos ni dependencias externas. Requiere **JDK 17 o superior** para compilar.

## Compilar y ejecutar

Abra una terminal dentro de la carpeta `ArenaGT`:

```sh
javac -encoding UTF-8 -d bin src/arenagt/*.java
java -cp bin arenagt.Main
```

Pruebas automáticas:

```sh
java -cp bin arenagt.Pruebas
```

También funciona `java -cp bin arenagt.Main --pruebas`.
Los `.class` incluidos tienen formato Java 17. Conviene recompilar el proyecto
con el JDK que tenga instalado. Los scripts `ejecutar.bat` y `ejecutar.sh`
compilan y abren el menú; pasan `--pruebas` cuando se proporciona ese argumento.
Si `javac` no aparece como comando, configure JAVA_HOME y PATH con un JDK.

## Uso del menú

1. Registre participantes nuevos con código, nombre y empresa.
2. Cree un torneo y elija la disciplina en la lista.
3. Cotice indicando una empresa, disciplina y cantidad positiva. No se cobra.
4. Inscriba los códigos de torneo y participante; la cuota se cobra al aceptar.
5. Cierre las inscripciones cuando se cumpla el mínimo. Se generan los eventos.
6. Consulte los eventos con la opción 8 y copie su código, por ejemplo `T1-E4`.
7. Programe con fecha `AAAA-MM-DD`, hora entera `0..23` y nombre de sede.
8. Registre resultados. En relevos escriba segundos o `DQ` por participante.
9. Consulte calendarios y reportes con las opciones 10 y 11. Salga con 0.

Las fechas y los nombres de sedes son datos ingresados por el operador.
Una programación ocupa exclusivamente la combinación de fecha y hora especificada;
el enunciado no define duraciones. Los nombres de sedes y empresas se comparan
ignorando mayúsculas y espacios externos. Los códigos se normalizan a mayúsculas.
Los marcadores admiten cero; los tiempos y cantidades de cotización deben ser positivos.
Una cotización es una estimación y no aplica el mínimo de apertura ni la disponibilidad
de un torneo. La inscripción real conserva el máximo de dos participantes por empresa.

## Datos iniciales

- P1 Aurora y P2 Bravos: TecnoGT.
- P3 Cóndores y P4 Dragones: Comercial Maya.
- P5 Estrellas y P6 Fénix: Servicios Quetzal.
- T1 fútbol sala; T2 fútbol sala con penales; T3 baloncesto; T4 voleibol;
  T5 relevos. Cada torneo inicia con P1-P4 inscritos directamente.
- T1-T4 están en juego, con todos sus encuentros programados y resultados parciales.
- T1 muestra un empate y un desempate por diferencia: P1 y P3 tienen cuatro puntos,
  con diferencias +3 y +1, respectivamente.
- T2 incluye un 2-2 resuelto por penales 4-3, además de victorias reglamentarias.
- T3 muestra desempate por victorias: P1 y P2 tienen tres puntos y diferencia -3;
  P2 tiene una victoria y P1 ninguna.
- T4 incluye 3-0, 3-1 y 2-3.
- T5 está finalizado con tiempos 50.1, 51.9 y 52.4, y P4 descalificado:
  puntos 10, 8, 6 y 0; diferencia cero. Aurora es el campeón precargado.
- La carga directa no registra ingresos ni premios, incluso para el torneo ya
  finalizado. Al terminar la carga se habilita la contabilidad normal: las nuevas
  inscripciones cobran y un torneo que finalice después sí entrega su premio.

## Estructura

- `src/arenagt/`: 28 archivos Java, incluido Main y las pruebas.
- `bin/`: clases compiladas para Java 17; se regeneran al compilar.
- `docs/Analisis_ArenaGT.pdf`: análisis completo, UML incorporado y pruebas.
- `docs/analisis.txt`: contenido del análisis en texto, para copiar.
- `docs/UML_ArenaGT.png`: vista UML resumida y sencilla.
- `docs/UML_completo.puml`: diseño completo editable en PlantUML.
- `docs/UML_*.svg`: láminas detalladas por grupo de clases.
- `docs/pruebas_obtenidas.txt`: salida real de la ejecución de Pruebas.
- `docs/casos_prueba.tsv`: entrada, esperado y obtenido por caso.

## Decisiones de diseño

Disciplina y DisciplinaEncuentros son abstractas. FutbolPenales hereda de
FutbolSala. Encuentro y Carrera implementan Evento y contienen GestionEvento;
no heredan una clase base de eventos. Los calendarios recorren Evento mediante
Iterable. Posicion y FilaEmpresa implementan Comparable. Los cálculos de cada
disciplina usan sobrescritura, sin instanceof ni selección del deporte en reportes.
La tabla se obtiene de aportes inmutables por evento y no guarda acumulados duplicados.
El dinero utiliza BigDecimal y la diferencia acumulada usa long.

## Entrega en Canvas

El PDF y los archivos están preparados para revisión. El enunciado también solicita
la URL de un repositorio público de GitHub. Cree ese repositorio, suba los archivos
Java y este README y entregue su URL. El ZIP no publica un repositorio por sí mismo.
