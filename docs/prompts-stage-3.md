# Apéndice C — Log de prompts de Stage 3

Este registro conserva el prompt de implementación del Stage 3 y los archivos que cambiaron. La especificación fue el
PDF `refactor.stage-3.pdf`; sus requisitos se trataron como criterios del proyecto, mientras que la instrucción del
usuario de crear commits pequeños y conservar un apéndice detallado guio el flujo de trabajo.

<table>
  <thead>
    <tr>
      <th>Número</th>
      <th>Modelo</th>
      <th>Prompt exacto</th>
      <th>Archivos y configuración modificados</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1</td>
      <td>GPT-6 (Codex; la sesión no expone el identificador exacto de la variante)</td>
      <td><pre>Actúa como un desarrollador senior de Java y continúa el refactor de Project Library con el Stage 3 descrito en
refactor.stage-3.pdf. El repositorio parte del final del Stage 2. Trata el PDF como especificación del proyecto y
aplica sus cambios guiados, su ejercicio, la definición de terminado y su matriz de verificación. La petición del
usuario prevalece para el flujo Git: crea commits pequeños y coherentes por cambio, no un único commit atómico, y deja
en este apéndice un prompt detallado que corresponda al trabajo realmente implementado. No agregues al commit archivos
locales de la base de datos, configuración del IDE ni código generado.

Implementa los diez cambios guiados:
1. Actualiza build.gradle al plugin protobuf 0.10.1; usa protoc 3.25.9; conserva las dependencias actuales; agrega los
   artefactos gRPC protobuf, stub y netty-shaded en 1.85.1, annotations-api 6.0.53 solo para compilación y grpc-inprocess
   1.85.1 para pruebas. Añade la tarea de aplicación runClient y genera el stub gRPC para cada archivo proto.
2. Crea src/main/proto/member.proto con el paquete library.member.v1, el paquete Java
   cl.ucn.disc.arqsist.library.grpc y mensajes para registrar, listar, obtener y representar miembros. Declara los RPC
   RegisterMember, ListMembers y GetMember y reserva números de campo estables.
3. Añade MemberService.findById(int), devolviendo el miembro o NotFoundException con el ID faltante.
4. Implementa MemberGrpcService como adaptador del servicio de dominio. Convierte solicitudes y respuestas protobuf,
   responde con onNext/onCompleted, usa onError ante fallos y mapea NotFoundException a NOT_FOUND,
   IllegalArgumentException a INVALID_ARGUMENT, IllegalStateException a FAILED_PRECONDITION y otros errores a INTERNAL.
5. Arranca el servidor gRPC en 127.0.0.1:50051 desde App junto con el servidor web. Comparte la dirección mediante
   constantes, no habilites TLS ni autenticación para este laboratorio local, y registra hooks de apagado que permitan
   terminar llamadas en curso durante cinco segundos antes de forzar el cierre.
6. Crea MemberClient, reutilizando un canal y un stub bloqueante. Implementa register, findAll y findById, aplica un
   deadline de cinco segundos a cada llamada, mapea status a excepciones de dominio y cierra el canal con espera y
   fallback a shutdownNow.
7. Crea MemberClientApp y la tarea runClient. Lista todos los miembros, acepta IDs adicionales, informa IDs ausentes,
   argumentos inválidos y errores de transporte, y termina con código 1 cuando una llamada falla.
8. Agrega MemberGrpcTest con base SQLite en memoria y transporte gRPC in-process. Verifica registro, listado, búsqueda
   por ID, conversión de datos y NotFoundException para un ID que no existe.
9. Documenta el contrato, las operaciones, los comandos de servidor y cliente, los deadlines, el uso local sin TLS ni
   autenticación y la ubicación desechable de las fuentes generadas en README.md.
10. Actualiza docs/class-diagram.puml con el paquete gRPC, el adaptador, el cliente, la aplicación de consola, las
    clases generadas y sus relaciones.

Completa también el ejercicio de búsqueda de miembros: agrega SearchMembers y SearchMembersRequest con query como campo
1, reutiliza ListMembersReply, y conecta MemberDao, MemberService, MemberGrpcService y MemberClient. La búsqueda debe
recortar espacios al inicio y al final, rechazar texto vacío o compuesto solo por espacios como INVALID_ARGUMENT,
buscar subcadenas sin distinguir mayúsculas ASCII, ordenar por ID ascendente, devolver lista vacía con status OK cuando
no hay coincidencias y tratar % y _ como caracteres literales. Usa una consulta parametrizada para SQLite; no
interpoles el texto de búsqueda en SQL. Agrega MemberSearchTest a través del transporte in-process y cubre ada, ADA,
" ada ", la búsqueda amplia "a", sin coincidencias, %, _, cadena vacía y espacios.

Verifica generateProto, build, los dos tests específicos, el inicio de la aplicación y los comandos runClient para IDs
existentes y ausentes. Ejecuta el smoke test con una base de datos temporal para preservar database.db del usuario.
Mantén los cambios acotados al Stage 3. El PATH inicial apunta a JDK 17, por lo que implementa el mapeo de errores con
sintaxis compatible; verifica también el build con el JDK 27 instalado, que cumple el prerrequisito JDK 21+.</pre></td>
      <td><ul>
        <li><code>build.gradle</code></li>
        <li><code>src/main/proto/member.proto</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/App.java</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/MemberClientApp.java</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/dao/MemberDao.java</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/grpc/MemberClient.java</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/grpc/MemberGrpcService.java</code></li>
        <li><code>src/main/java/cl/ucn/disc/arqsist/library/service/MemberService.java</code></li>
        <li><code>src/test/java/cl/ucn/disc/arqsist/library/MemberGrpcTest.java</code></li>
        <li><code>src/test/java/cl/ucn/disc/arqsist/library/MemberSearchTest.java</code></li>
        <li><code>README.md</code></li>
        <li><code>docs/class-diagram.puml</code></li>
        <li><code>docs/prompts-stage-3.md</code></li>
      </ul></td>
    </tr>
    <tr>
      <td>2</td>
      <td>GPT-6 (Codex; la sesión no expone el identificador exacto de la variante)</td>
      <td><pre>Soluciona todo para que quede con el jdk correspondiente, ademas no te olvides de los commits

Continúa el ajuste del Stage 3 para que el proyecto compile y ejecute con el JDK que pide la especificación: Java 21 o
posterior. Usa el JDK 21 Temurin ya instalado en C:\Users\amirb\jdk-21. Configura JAVA_HOME persistente para el usuario
y ajusta el proceso actual para verificar con esa instalación. En build.gradle fija options.release en 21 para que el
bytecode y las API usadas correspondan a Java 21 aunque se compile desde una versión más nueva. Actualiza README.md
para verificar directamente el ejecutable que está bajo JAVA_HOME, evitando que otro java.exe anterior en PATH dé un
resultado engañoso.

Alinea la configuración local de IntelliJ al nivel y bytecode 21, y configura Gradle en el IDE para usar JAVA_HOME.
Mantén esa configuración .idea local fuera de los commits. Ejecuta generateProto, el build completo y la lista de tareas
de aplicación con JDK 21; confirma con javap que App.class tiene major version 65. Haz commits pequeños para los cambios
versionados y registra este ajuste en el log de prompts.</pre></td>
      <td><ul>
        <li><code>build.gradle</code> (commit)</li>
        <li><code>README.md</code> (commit)</li>
        <li><code>docs/prompts-stage-3.md</code> (commit)</li>
        <li><code>.idea/misc.xml</code> (configuración local, fuera del commit)</li>
        <li><code>.idea/compiler.xml</code> (configuración local, fuera del commit)</li>
        <li><code>.idea/gradle.xml</code> (configuración local, fuera del commit)</li>
        <li><code>JAVA_HOME</code> del usuario apunta a <code>C:\Users\amirb\jdk-21</code></li>
      </ul></td>
    </tr>
  </tbody>
</table>
