/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AccruedFeeTest {
    @Test
    void computesFeeForOpenOverdueLoans() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());
        Book book = new Book("Book", "Author", "isbn", 2);
        Member member = new Member("Member", "member@example.com");
        bookDao.create(book);
        memberDao.create(member);
        loanDao.create(new Loan(member, book, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 22)));
        loanDao.create(new Loan(member, book, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 30)));

        LoanService service = new LoanService(loanDao, memberDao, new BookService(bookDao),
                Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC));

        var loans = service.findAll();
        assertEquals(24.0, loans.get(0).getOverdueFee());
        assertEquals(0.0, loans.get(1).getOverdueFee());
    }
}
