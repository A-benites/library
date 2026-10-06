/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DatabaseSeedTest {
    @Test
    void seedsExpectedDataOnlyOnce() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        db.seedIfEmpty();
        LoanDao loans = new LoanDao(db.connectionSource());
        ReservationDao reservations = new ReservationDao(db.connectionSource());
        BookDao books = new BookDao(db.connectionSource());
        assertEquals(3, loans.findAll().size());
        assertEquals(1, reservations.findAll().size());
        assertTrue(loans.findAll().stream().anyMatch(l ->
                !l.isReturned() && l.getDueDate().isBefore(LocalDate.now())));
        assertTrue(loans.findAll().stream().filter(l -> !l.isReturned())
                .allMatch(l -> l.getBook().getAvailableCopies() < l.getBook().getTotalCopies()));
        db.seedIfEmpty();
        assertEquals(3, loans.findAll().size());
        assertEquals(1, reservations.findAll().size());
        assertEquals(3, books.findAll().size());
    }
}
