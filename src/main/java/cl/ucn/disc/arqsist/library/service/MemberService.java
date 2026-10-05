/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Application service for {@link Member} management.
 *
 * <p>Handles member registration, retrieval, and the checkout operation
 * that creates a new {@link Loan} for a given member and book.</p>
 */
public final class MemberService {

    /** DAO for member persistence. */
    private final MemberDao memberDao;

    /** DAO for book persistence (used to decrement available copies on checkout). */
    private final BookDao bookDao;

    /** DAO for loan persistence. */
    private final LoanDao loanDao;

    /**
     * Creates a new {@code MemberService}.
     *
     * @param memberDao the member DAO; must not be {@code null}
     * @param bookDao   the book DAO; must not be {@code null}
     * @param loanDao   the loan DAO; must not be {@code null}
     */
    public MemberService(MemberDao memberDao, BookDao bookDao, LoanDao loanDao) {
        this.memberDao = memberDao;
        this.bookDao = bookDao;
        this.loanDao = loanDao;
    }

    /**
     * Registers a new member in the system.
     *
     * @param member the member to register; must not be {@code null}
     * @return the persisted member with its generated ID
     * @throws SQLException if the insert fails
     */
    public Member register(Member member) throws SQLException {
        memberDao.create(member);
        return member;
    }

    /**
     * Returns all registered members.
     *
     * @return a list of all members; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Member> findAll() throws SQLException {
        return memberDao.findAll();
    }

    /**
     * Checks out a book for a member, creating a new loan.
     *
     * <p>Decrements the book's available-copy count and records the loan
     * with today's date as the loan date and the configured due date.</p>
     *
     * @param memberId the ID of the member borrowing the book
     * @param bookId   the ID of the book to borrow
     * @return the newly created {@link Loan}
     * @throws SQLException if any persistence operation fails
     */
    public Loan checkout(int memberId, int bookId) throws SQLException {
        Member member = memberDao.findById(memberId);
        Book book = bookDao.findById(bookId);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);

        String dueDate = LocalDate.now().plusDays(14).toString();
        Loan loan = new Loan(member, book, LocalDate.now().toString(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
