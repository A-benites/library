/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Application service for {@link Reservation} management.
 *
 * <p>Handles creating reservations and fulfilling them into loans.</p>
 */
public final class ReservationService {

    /** DAO for reservation persistence. */
    private final ReservationDao reservationDao;

    /** DAO for book persistence. */
    private final BookDao bookDao;

    /** DAO for member persistence. */
    private final MemberDao memberDao;

    /** DAO for loan persistence (used when fulfilling a reservation). */
    private final LoanDao loanDao;

    /**
     * Creates a new {@code ReservationService}.
     *
     * @param reservationDao the reservation DAO; must not be {@code null}
     * @param bookDao        the book DAO; must not be {@code null}
     * @param memberDao      the member DAO; must not be {@code null}
     * @param loanDao        the loan DAO; must not be {@code null}
     */
    public ReservationService(ReservationDao reservationDao, BookDao bookDao,
                              MemberDao memberDao, LoanDao loanDao) {
        this.reservationDao = reservationDao;
        this.bookDao = bookDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    /**
     * Creates a new reservation for the given book and member.
     *
     * @param bookId   the ID of the book to reserve
     * @param memberId the ID of the member making the reservation
     * @return the persisted {@link Reservation}
     * @throws SQLException if any persistence operation fails
     */
    public Reservation reserve(int bookId, int memberId) throws SQLException {
        Book book = bookDao.findById(bookId);
        Member member = memberDao.findById(memberId);
        Reservation reservation = new Reservation(member, book, LocalDate.now());
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Returns all reservations in the system.
     *
     * @return a list of all reservations; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Reservation> findAll() throws SQLException {
        return reservationDao.findAll();
    }

    /**
     * Fulfills a reservation by converting it into a loan.
     *
     * <p>Marks the reservation as fulfilled and creates a new {@link Loan}
     * for the same member and book using the configured loan period.</p>
     *
     * @param reservationId the ID of the reservation to fulfill
     * @return the newly created {@link Loan}
     * @throws IllegalStateException if the reservation does not exist or is already fulfilled
     * @throws SQLException          if any persistence operation fails
     */
    public Loan fulfill(int reservationId) throws SQLException {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null || reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation not available");
        }

        reservation.setFulfilled(true);
        reservationDao.update(reservation);

        LocalDate dueDate = LocalDate.now().plusDays(21);
        Loan loan = new Loan(reservation.getMember(), reservation.getBook(),
                LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
