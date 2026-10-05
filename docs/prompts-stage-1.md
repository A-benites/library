# Apéndice A — Log de Prompts Elaborados (Stage 1)

Este documento registra los prompts detallados utilizados durante la ejecución del Stage 1. Cada prompt fue diseñado utilizando técnicas de *Prompt Engineering* (como dar rol, contexto, instrucciones claras, restricciones y ejemplos) para guiar al modelo de forma precisa, evitando ambigüedades.

---

## 1. Setup y Documentación (Change 1a)

**Modelo:** Claude 3.5 Sonnet  
**Contexto:** Necesitamos inicializar las cabeceras legales estándar en los archivos modificados.  
**Prompt utilizado:**
```markdown
Actúa como un desarrollador Senior en Java. Tu tarea es asegurar que todos los archivos que toquemos en el "Stage 1" cumplan con los estándares de documentación y licenciamiento del curso de Arquitectura de Sistemas (UCN).

**Contexto:**
El proyecto es una aplicación Javalin con ORMLite en SQLite. El Stage 1 abarca mejoras en la capa de datos y la política de préstamos.

**Instrucciones:**
1. Añade exactamente el siguiente encabezado de copyright en la línea 1 de todos los archivos `.java` que modifiques (como `Loan.java`, `Reservation.java`, DAOs, y Services):
   `/*`
   ` * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.`
   ` */`
2. Para archivos `.xml` (como `logback.xml`) o `.html` (como `index.html`), utiliza el formato de comentario correspondiente (`<!-- ... -->`).
3. Agrega Javadoc completo a todas las clases públicas y sus métodos, explicando el propósito de los parámetros (`@param`), valores de retorno (`@return`) y excepciones (`@throws`).
4. Genera el código completo de la clase `Loan.java` y `Reservation.java` como tu primera salida, incluyendo esta documentación.
```
**Archivos modificados:** `Loan.java`, `Reservation.java`, `BookDao.java`, `MemberDao.java`, `LoanDao.java`, `ReservationDao.java`, `BookService.java`, `MemberService.java`, `LoanService.java`, `ReservationService.java`, `Database.java`, `logback.xml`, `index.html`.

---

## 2. Refactor de Fechas (Change 2)

**Modelo:** Claude 3.5 Sonnet  
**Contexto:** Reemplazar el anti-patrón de guardar fechas como `String` a usar `LocalDate`.  
**Prompt utilizado:**
```markdown
Actúa como un arquitecto de software especializado en bases de datos. 
Actualmente, nuestro modelo de dominio guarda fechas (como `loanDate` y `dueDate`) usando tipos `String`, lo cual es propenso a errores y dificulta las comparaciones de tiempo.

**Objetivo:**
Migrar el uso de fechas de `String` a `java.time.LocalDate` en todo el modelo, garantizando que se sigan guardando como cadenas legibles (ISO-8601) en SQLite mediante ORMLite.

**Instrucciones Paso a Paso:**
1. **Crear Persister:** Escribe una clase `LocalDatePersister` en el paquete `db` que extienda `BaseDataType` de ORMLite. Configúrala para mapear `LocalDate` hacia y desde columnas SQL tipo `STRING`. Usa un Singleton (patrón `getSingleton()`).
2. **Actualizar Modelos:** En `Loan.java` y `Reservation.java`:
   - Cambia los tipos de `String` a `LocalDate` en `loanDate`, `dueDate`, `returnDate` y `reservedAt`.
   - Ajusta los constructores, getters y setters correspondientes.
   - En las anotaciones `@DatabaseField`, añade el parámetro `persisterClass = LocalDatePersister.class`.
   - **Restricción:** Elimina el método `isOverdue()` de la clase `Loan` (la lógica se moverá a otra capa después).
3. **Actualizar Servicios:** En `MemberService`, `LoanService` y `ReservationService`, modifica la forma en que se instancian los modelos, pasando objetos `LocalDate` en lugar de `.toString()`.
4. Entrega sólo el código modificado de estas clases, sin explicaciones largas.
```
**Archivos modificados:** `LocalDatePersister.java` (NEW), `Loan.java`, `Reservation.java`, `MemberService.java`, `LoanService.java`, `ReservationService.java`, `Database.java`.

---

## 3. Política de Préstamos (Change 3 & 4)

**Modelo:** Claude 3.5 Sonnet  
**Contexto:** Centralizar las reglas de negocio sueltas (como los 14 o 21 días de plazo) en una política única y estandarizar el manejo de errores.  
**Prompt utilizado:**
```markdown
Actúa como un ingeniero de software aplicando Domain-Driven Design (DDD).
Tenemos lógica de negocio esparcida: `MemberService.checkout` suma 14 días para el vencimiento, mientras que `ReservationService.fulfill` suma 21 días. Necesitamos unificarlos.

**Requerimientos de la Política (Change 3):**
1. Crea una clase inmutable `LoanPolicy` en el paquete `service`.
2. Define dos constantes públicas: `DUE_DAYS = 21` y `FEE_PER_DAY = 1.0`.
3. Crea un método estático `public static LocalDate dueDate(LocalDate loanDate)` que sume `DUE_DAYS` a la fecha entregada.
4. Refactoriza `MemberService` y `ReservationService` para que usen `LoanPolicy.dueDate(LocalDate.now())` al crear préstamos.

**Requerimientos de Errores (Change 4):**
5. Crea una excepción personalizada `NotFoundException` en el paquete `service` que extienda de `RuntimeException`. Debe recibir un mensaje como parámetro en el constructor. Esta excepción servirá para abstraer los problemas de persistencia en la capa web (HTTP 404).

**Salida:**
Dame el código de `LoanPolicy`, `NotFoundException`, y los difs de las modificaciones en los servicios.
```
**Archivos modificados:** `LoanPolicy.java` (NEW), `NotFoundException.java` (NEW), `MemberService.java`, `ReservationService.java`.

---

## 4. Refactor de DAOs y BaseDao (Change 5 & 6)

**Modelo:** Claude 3.5 Sonnet  
**Contexto:** DRY (Don't Repeat Yourself). Los 4 DAOs tienen código idéntico. También debemos agregar validaciones de inventario.  
**Prompt utilizado:**
```markdown
Eres un experto en refactorización de código limpio.

**Parte 1: BaseDao (Change 5)**
Nuestros DAOs (`BookDao`, `MemberDao`, `LoanDao`, `ReservationDao`) tienen el mismo código CRUD repetido y lanzan `SQLException` (checked), lo que ensucia la firma de todos nuestros servicios.
1. Crea una clase abstracta genérica `BaseDao<T>` que reciba el `ConnectionSource` y la clase de entidad.
2. Implementa los métodos `findAll`, `findById`, `create`, `update` y `delete`. Dentro de estos métodos, atrapa cualquier `SQLException` y relánzala como un `RuntimeException` para no contaminar la firma.
3. Agrega un método `public <R> R transaction(Callable<R> callable) throws SQLException` para soportar transacciones.
4. Refactoriza los 4 DAOs existentes para que simplemente hereden de `BaseDao<T>`.

**Parte 2: Guardas en BookService (Change 6)**
1. En `BookService.borrow(int bookId)` y `BookService.returnCopy(int bookId)`, primero busca el libro.
2. Si el libro es `null`, lanza `NotFoundException("Book not found: " + bookId)`.
3. En `borrow()`, antes de restar copias, verifica `book.getAvailableCopies() <= 0`. Si es cierto, lanza una `IllegalStateException`.
4. Elimina todos los `throws SQLException` de las firmas de los métodos en `BookService`.
```
**Archivos modificados:** `BaseDao.java` (NEW), `BookDao.java`, `MemberDao.java`, `LoanDao.java`, `ReservationDao.java`, `BookService.java`.

---

## 5. Seed Data, Frontend y Testing (Changes 10, 13, 14, 15)

**Modelo:** Claude 3.5 Sonnet  
**Contexto:** Ajustar los datos de prueba y mover la lógica de "overdue" al cliente web.  
**Prompt utilizado:**
```markdown
Como desarrollador Full-Stack, necesitamos sincronizar nuestra semilla de base de datos con las modificaciones hechas y empoderar al frontend.

**Instrucciones:**
1. **Base de datos (Change 10):** En la clase `Database.java` (`seedIfEmpty`), reescribe la inserción de datos. Inserta exactamente 3 libros, 3 miembros y 1 reserva. Inserta 3 préstamos con fechas simuladas usando `LocalDate.now()` y `.plusDays()`/`.minusDays()`:
   - Un préstamo completado (`returned = true`).
   - Un préstamo activo (aún no vencido).
   - Un préstamo vencido (fecha `dueDate` en el pasado y `returned = false`).
   *Asegúrate de ajustar los copies disponibles de los libros en consecuencia.*
2. **Frontend (Change 13):** En `app.js`, la API ya no envía un campo booleano `overdue` (porque lo quitamos de la entidad en Java). Crea una función en JS para calcular el estado `overdue` comparando `dueDate` con la fecha actual del sistema. Usa esta nueva función en las funciones `loadLoans()` y `updateStats()`.
3. **Logging (Change 14):** En `logback.xml`, añade un logger a nivel DEBUG para el paquete `cl.ucn.disc`.
4. **Test (Change 15):** En `DueDateDuplicationTest`, asegúrate de que ambos, `checkout` y `fulfill`, devuelvan un `Loan` con la misma `dueDate`, comprobando que `LoanPolicy` ha unificado la política (necesitarás inicializar al menos 2 copias del libro de prueba).

Dame solo las modificaciones de código necesarias en formato de bloques Markdown.
```
**Archivos modificados:** `Database.java`, `app.js`, `index.html`, `logback.xml`, `DueDateDuplicationTest.java`.
