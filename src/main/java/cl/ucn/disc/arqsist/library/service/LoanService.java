/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Application service for {@link Loan} management.
 *
 * <p>Handles loan listing, returns, and overdue queries.</p>
 */
public final class LoanService {

    /** Number of days a standard loan is valid. */
    public static final int DUE_DAYS = 21;

    /** DAO for loan persistence. */
    private final LoanDao loanDao;

    /** DAO for book persistence (used to increment available copies on return). */
    private final BookDao bookDao;

    /**
     * Creates a new {@code LoanService}.
     *
     * @param loanDao the loan DAO; must not be {@code null}
     * @param bookDao the book DAO; must not be {@code null}
     */
    public LoanService(LoanDao loanDao, BookDao bookDao) {
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    /**
     * Returns all loans in the system.
     *
     * @return a list of all loans; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Loan> findAll() throws SQLException {
        return loanDao.findAll();
    }

    /**
     * Processes the return of a loan.
     *
     * <p>Marks the loan as returned, records the return date, computes any
     * overdue fee, and increments the book's available copy count.</p>
     *
     * @param loanId the ID of the loan to return
     * @return the updated {@link Loan}, or {@code null} if not found
     * @throws SQLException if any persistence operation fails
     */
    public Loan returnLoan(int loanId) throws SQLException {
        Loan loan = loanDao.findById(loanId);
        if (loan == null || loan.isReturned()) {
            return loan;
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        LocalDate due = loan.getDueDate();
        LocalDate today = LocalDate.now();
        if (today.isAfter(due)) {
            long daysOverdue = ChronoUnit.DAYS.between(due, today);
            loan.setOverdueFee(daysOverdue * 1.0);
        }

        loanDao.update(loan);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDao.update(book);

        return loan;
    }

    /**
     * Returns all loans that are currently overdue.
     *
     * @return a list of overdue loans; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Loan> overdueLoans() throws SQLException {
        return loanDao.findAll().stream().filter(l -> !l.isReturned() && l.getDueDate().isBefore(LocalDate.now())).toList();
    }
}
