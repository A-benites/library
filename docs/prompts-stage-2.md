# Apéndice B — Log de prompts de Stage 2

Este archivo registra los prompts utilizados para completar el Stage 2 durante esta sesión. Los cambios se aplicaron
directamente sobre `docs/refactor.md` y el estado existente del repositorio.

## 1. Implementación completa de Stage 2

**Modelo:** GitHub Copilot  
**Prompt utilizado:**

```text
Implementa los requisitos faltantes del repositorio usando la especificación de docs/refactor.md. Completa el Stage 2:
mueve checkout a LoanService, inyecta Clock, usa transacciones, actualiza ReservationService, App, controladores,
pruebas, diagrama, documentación y README. Mantén cambios quirúrgicos y valida con el build existente.
```

**Archivos modificados:** servicios, controladores, `App.java`, pruebas, `README.md`, `docs/class-diagram.puml`.

## 2. Pruebas de integración

**Modelo:** GitHub Copilot  
**Prompt utilizado:**

```text
Agrega AccruedFeeTest y DatabaseSeedTest según la matriz de verificación de docs/refactor.md y actualiza
DueDateDuplicationTest y TransactionBugTest para usar LoanService, un Clock fijo y NotFoundException.
```

**Archivos modificados:** `src/test/java/.../DueDateDuplicationTest.java`,
`src/test/java/.../TransactionBugTest.java`; archivos nuevos `AccruedFeeTest.java` y `DatabaseSeedTest.java`.
