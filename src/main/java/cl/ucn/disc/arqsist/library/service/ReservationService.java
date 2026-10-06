/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.sql.SQLException;
import java.time.Clock;
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

    /** DAO for member persistence. */
    private final MemberDao memberDao;

    /** DAO for loan persistence (used when fulfilling a reservation). */
    private final LoanDao loanDao;

    /** Service that owns inventory changes. */
    private final BookService bookService;
    /** Clock used to make date-dependent behavior deterministic. */
    private final Clock clock;

    /**
     * Creates a new reservation service.
     *
     * @param reservationDao the reservation DAO
     * @param memberDao the member DAO
     * @param loanDao the loan DAO
     * @param bookService the book service
     * @param clock the clock used for current dates
     */
    public ReservationService(ReservationDao reservationDao, MemberDao memberDao, LoanDao loanDao,
                              BookService bookService, Clock clock) {
        this.reservationDao = reservationDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Creates a new reservation for the given book and member.
     *
     * @param bookId   the ID of the book to reserve
     * @param memberId the ID of the member making the reservation
     * @return the persisted {@link Reservation}
     */
    public Reservation reserve(int bookId, int memberId) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        Reservation reservation = new Reservation(member, book, LocalDate.now(clock));
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Returns all reservations in the system.
     *
     * @return a list of all reservations; never {@code null}
     */
    public List<Reservation> findAll() {
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
     * @throws NotFoundException if the reservation does not exist
     * @throws IllegalStateException if the reservation is already fulfilled
     */
    public Loan fulfill(int reservationId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }
        if (reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation already fulfilled: " + reservationId);
        }

        LocalDate today = LocalDate.now(clock);
        try {
            return reservationDao.transaction(() -> {
                bookService.borrow(reservation.getBook().getId());
                reservation.setFulfilled(true);
                reservationDao.update(reservation);
                Loan loan = new Loan(reservation.getMember(), reservation.getBook(),
                        today, LoanPolicy.dueDate(today));
                loanDao.create(loan);
                return loan;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
