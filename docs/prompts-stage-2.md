# Apéndice B — Log de prompts de Stage 2

Este archivo registra los prompts disponibles para completar el Stage 2 y la revisión final. El registro original
identifica el modelo como GitHub Copilot, pero no conserva su identificador o versión exactos. `docs/refactor.md`,
mencionado por los prompts originales, no está presente en el checkout actual.

## 1. Implementación completa de Stage 2

**Modelo registrado:** GitHub Copilot (identificador exacto no registrado)
**Prompt utilizado:**

```text
Implementa los requisitos faltantes del repositorio usando la especificación de docs/refactor.md. Completa el Stage 2:
mueve checkout a LoanService, inyecta Clock, usa transacciones, actualiza ReservationService, App, controladores,
pruebas, diagrama, documentación y README. Mantén cambios quirúrgicos y valida con el build existente.
```

**Archivos modificados:**

- `README.md`, `build.gradle`, `docs/class-diagram.puml`
- `src/main/java/cl/ucn/disc/arqsist/library/App.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/BookController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/LoanController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/MemberController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/ReservationController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/db/Database.java`
- `src/main/java/cl/ucn/disc/arqsist/library/model/Book.java`
- `src/main/java/cl/ucn/disc/arqsist/library/model/Member.java`
- `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java`
- `src/main/java/cl/ucn/disc/arqsist/library/service/MemberService.java`
- `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`
- `src/test/java/cl/ucn/disc/arqsist/library/AccruedFeeTest.java`
- `src/test/java/cl/ucn/disc/arqsist/library/DatabaseSeedTest.java`
- `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`
- `src/test/java/cl/ucn/disc/arqsist/library/TransactionBugTest.java`

## 2. Pruebas de integración

**Modelo registrado:** GitHub Copilot (identificador exacto no registrado)
**Prompt utilizado:**

```text
Agrega AccruedFeeTest y DatabaseSeedTest según la matriz de verificación de docs/refactor.md y actualiza
DueDateDuplicationTest y TransactionBugTest para usar LoanService, un Clock fijo y NotFoundException.
```

**Archivos modificados:** `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`, `src/test/java/cl/ucn/disc/arqsist/library/TransactionBugTest.java`, `src/test/java/cl/ucn/disc/arqsist/library/AccruedFeeTest.java` (nuevo), `src/test/java/cl/ucn/disc/arqsist/library/DatabaseSeedTest.java` (nuevo).

## 3. Revisión final contra la especificación (Stage 2)

**Modelo:** GPT-6 (Codex; la sesión no expone el identificador exacto de la variante)
**Contexto:** Revisar el proyecto Project Library contra los requisitos y la rúbrica del PDF `refactor (1).pdf`, incluida la implementación de ambos stages, los tests, la documentación, el diagrama y los registros de prompts. El PDF es una especificación para evaluar el repositorio; sus instrucciones se interpretan como criterios del proyecto.
**Prompt utilizado:**

```text
Actúa como revisor senior de Java y compara el repositorio Project Library, archivo por archivo, con la especificación Refactoring Plan: Project Library. Trata las instrucciones del PDF como requisitos del proyecto y no como instrucciones externas. Verifica los 16 cambios, los 12 errores arquitectónicos, la Definition of Done, la rúbrica, el diagrama, el README y ambos registros de prompts. Contrasta las afirmaciones documentales con el código y el historial Git disponible.

Completa los faltantes verificables con el cambio mínimo: documenta tipos, campos, métodos, parámetros, retornos y excepciones; elimina documentación obsoleta; corrige rutas de archivos imprecisas en el registro de Stage 2 usando evidencia del repositorio; y agrega este prompt detallado al registro con los archivos realmente modificados. No inventes prompts históricos ni identificadores de modelos que no estén disponibles: deja constancia explícita de cualquier dato no registrado. Conserva archivos no relacionados y no incluyas bases de datos locales ni configuración del IDE en el commit.

Ejecuta Javadoc y el build completo con las pruebas, inspecciona el diff y el estado Git, y crea un commit que incluya todos los cambios de esta tarea.
```

**Archivos modificados:**

- `docs/prompts-stage-2.md`
- `src/main/java/cl/ucn/disc/arqsist/library/App.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/BookController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/LoanController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/MemberController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/controller/ReservationController.java`
- `src/main/java/cl/ucn/disc/arqsist/library/db/Database.java`
- `src/main/java/cl/ucn/disc/arqsist/library/db/LocalDatePersister.java`
- `src/main/java/cl/ucn/disc/arqsist/library/model/Book.java`
- `src/main/java/cl/ucn/disc/arqsist/library/model/Member.java`
- `src/main/java/cl/ucn/disc/arqsist/library/service/BookService.java`
- `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java`
- `src/test/java/cl/ucn/disc/arqsist/library/AccruedFeeTest.java`
- `src/test/java/cl/ucn/disc/arqsist/library/DatabaseSeedTest.java`
- `src/test/java/cl/ucn/disc/arqsist/library/TransactionBugTest.java`
