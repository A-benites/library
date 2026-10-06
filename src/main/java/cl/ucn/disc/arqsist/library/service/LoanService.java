/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Application service for {@link Loan} management.
 *
 * <p>Handles loan listing, returns, and overdue queries.</p>
 */
public final class LoanService {

    /** DAO for loan persistence. */
    private final LoanDao loanDao;

    /** DAO for member persistence. */
    private final MemberDao memberDao;

    /** Service that owns inventory changes. */
    private final BookService bookService;

    /** Clock used to make date-dependent behavior deterministic. */
    private final Clock clock;

    /**
     * Creates a new {@code LoanService}.
     *
     * @param loanDao the loan DAO
     * @param memberDao the member DAO
     * @param bookService the book service
     * @param clock the clock used for current dates
     */
    public LoanService(LoanDao loanDao, MemberDao memberDao, BookService bookService, Clock clock) {
        this.loanDao = loanDao;
        this.memberDao = memberDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Returns all loans in the system.
     *
     * @return a list of all loans; never {@code null}
     */
    public List<Loan> findAll() {
        return withAccruedFees(loanDao.findAll());
    }

    /**
     * Processes the return of a loan.
     *
     * <p>Marks the loan as returned, records the return date, computes any
     * overdue fee, and increments the book's available copy count.</p>
     *
     * @param loanId the ID of the loan to return
     * @return the updated {@link Loan}
     * @throws NotFoundException if the loan does not exist
     * @throws IllegalStateException if the loan was already returned
     */
    public Loan returnLoan(int loanId) {
        Loan loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new NotFoundException("Loan not found: " + loanId);
        }
        if (loan.isReturned()) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }

        LocalDate today = LocalDate.now(clock);
        return transaction(() -> {
            loan.setReturned(true);
            loan.setReturnDate(today);
            if (today.isAfter(loan.getDueDate())) {
                loan.setOverdueFee(accruedFee(loan, today));
            }
            loanDao.update(loan);
            bookService.returnCopy(loan.getBook().getId());
            return loan;
        });
    }

    /**
     * Returns all loans that are currently overdue.
     *
     * @return a list of overdue loans; never {@code null}
     */
    public List<Loan> overdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return withAccruedFees(loanDao.findAll()).stream()
                .filter(l -> !l.isReturned() && l.getDueDate().isBefore(today))
                .toList();
    }

    /**
     * Creates a loan after validating the member and atomically borrowing a copy.
     *
     * @param memberId the member ID
     * @param bookId the book ID
     * @return the new loan
     */
    public Loan checkout(int memberId, int bookId) {
        var member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        var book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        LocalDate today = LocalDate.now(clock);
        return transaction(() -> {
            bookService.borrow(bookId);
            Loan loan = new Loan(member, book, today, LoanPolicy.dueDate(today));
            loanDao.create(loan);
            return loan;
        });
    }

    private <R> R transaction(java.util.concurrent.Callable<R> callable) {
        try {
            return loanDao.transaction(callable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Loan> withAccruedFees(List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);
        loans.stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .forEach(loan -> loan.setOverdueFee(accruedFee(loan, today)));
        return loans;
    }

    private double accruedFee(Loan loan, LocalDate today) {
        return ChronoUnit.DAYS.between(loan.getDueDate(), today) * LoanPolicy.FEE_PER_DAY;
    }
}
