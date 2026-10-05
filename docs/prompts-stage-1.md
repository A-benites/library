# Apéndice A — Log de Prompts (Stage 1)

| Etapa | Prompt (resumen) | Modelo de IA | Archivos modificados |
|-------|-----------------|--------------|----------------------|
| **1a** | "Add standard license header and Javadoc to all modified files in Stage 1." | Claude 3.5 Sonnet | `Loan.java`, `Reservation.java`, `BookDao.java`, `MemberDao.java`, `LoanDao.java`, `ReservationDao.java`, `BookService.java`, `Database.java`, `MemberService.java`, `LoanService.java`, `ReservationService.java`, `DueDateDuplicationTest.java` |
| **2** | "Replace String dates with LocalDate in models and create an ORMLite LocalDatePersister mapping it to ISO-8601 strings." | Claude 3.5 Sonnet | `LocalDatePersister.java` (NEW), `Loan.java`, `Reservation.java`, `MemberService.java`, `LoanService.java`, `ReservationService.java`, `Database.java` |
| **3** | "Create LoanPolicy class with DUE_DAYS, FEE_PER_DAY, and dueDate(). Update checkout and fulfill to use this policy." | Claude 3.5 Sonnet | `LoanPolicy.java` (NEW), `MemberService.java`, `ReservationService.java` |
| **4** | "Create a NotFoundException that extends RuntimeException for the service layer." | Claude 3.5 Sonnet | `NotFoundException.java` (NEW) |
| **5** | "Extract a generic BaseDao to handle ORMLite setup, CRUD, and wrap SQLException into RuntimeException. Make other DAOs extend it." | Claude 3.5 Sonnet | `BaseDao.java` (NEW), `BookDao.java`, `MemberDao.java`, `LoanDao.java`, `ReservationDao.java` |
| **6** | "Guard inventory in BookService: throw NotFoundException if book doesn't exist, and IllegalStateException if no copies available in borrow()." | Claude 3.5 Sonnet | `BookService.java` |
| **10** | "Rewrite Database seedIfEmpty to include 3 books, 3 members, 1 reservation, and 3 loans (one returned, one active, one overdue)." | Claude 3.5 Sonnet | `Database.java` |
| **13** | "Update app.js to compute the overdue status in the browser using the current date, rather than relying on l.overdue from the backend." | Claude 3.5 Sonnet | `app.js`, `index.html` |
| **14** | "Add a DEBUG level logger for cl.ucn.disc in logback.xml." | Claude 3.5 Sonnet | `logback.xml` |
| **15** | "Update DueDateDuplicationTest to test the new LoanPolicy integration." | Claude 3.5 Sonnet | `DueDateDuplicationTest.java` |
| **16a** | "Update class-diagram.puml to reflect all changes from Stage 1." | Claude 3.5 Sonnet | `class-diagram.puml` |

*Nota: Todos los prompts fueron diseñados e iterados durante la sesión de planificación con el modelo.*
