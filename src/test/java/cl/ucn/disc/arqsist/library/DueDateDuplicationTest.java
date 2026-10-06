/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that {@code checkout} and {@code fulfill} use the same loan period.
 *
 * <p>Both operations must produce a loan whose due date is computed
 * with the same rule so that the policy is applied consistently.</p>
 */
class DueDateDuplicationTest {

    /** Service under test for checkout. */
    private LoanService loanService;

    /** Service under test for reservation fulfillment. */
    private ReservationService reservationService;

    /** Book shared between the checkout and reservation scenarios. */
    private Book book;

    /** Member used to perform checkout and reservation. */
    private Member member;

    /**
     * Sets up an in-memory database and seeds it with one book and one member.
     *
     * @throws Exception if DAO initialization fails
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());
        ReservationDao reservationDao = new ReservationDao(db.connectionSource());

        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        var bookService = new cl.ucn.disc.arqsist.library.service.BookService(bookDao);
        loanService = new LoanService(loanDao, memberDao, bookService, clock);
        reservationService = new ReservationService(reservationDao, memberDao, loanDao, bookService, clock);

        book = new Book("Design Patterns", "Gamma et al.", "9780201633610", 2);
        bookDao.create(book);
        member = new Member("Grace Hopper", "grace@example.com");
        memberDao.create(member);
    }

    /**
     * Checks that checkout and fulfill produce loans with the same due date.
     *
     * @throws Exception if any service call fails
     */
    @Test
    void checkoutAndFulfillUseTheSameLoanPeriod() throws Exception {
        Loan fromCheckout = loanService.checkout(member.getId(), book.getId());

        Reservation reservation = reservationService.reserve(book.getId(), member.getId());
        Loan fromFulfill = reservationService.fulfill(reservation.getId());

        assertEquals(fromCheckout.getDueDate(), fromFulfill.getDueDate(),
                "checkout and fulfill must use the same loan period from LoanPolicy");
    }
}
