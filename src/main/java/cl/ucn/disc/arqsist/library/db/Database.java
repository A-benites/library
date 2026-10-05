/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.db;

import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Manages the database connection and initial seed data.
 *
 * <p>Creates the SQLite schema via ORMLite's {@link TableUtils} and populates
 * the tables with representative sample data on first startup.</p>
 */
public final class Database {

    /** The active ORMLite connection source. */
    private final ConnectionSource connectionSource;

    /**
     * Opens a JDBC connection and creates all required tables if they do not exist.
     *
     * @param jdbcUrl the JDBC URL for the SQLite database (e.g. {@code jdbc:sqlite:database.sqlite})
     * @throws SQLException if the connection or table creation fails
     */
    public Database(String jdbcUrl) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(jdbcUrl);
        TableUtils.createTableIfNotExists(connectionSource, Book.class);
        TableUtils.createTableIfNotExists(connectionSource, Member.class);
        TableUtils.createTableIfNotExists(connectionSource, Loan.class);
        TableUtils.createTableIfNotExists(connectionSource, Reservation.class);
    }

    /**
     * Returns the active connection source for use by DAOs.
     *
     * @return the {@link ConnectionSource}
     */
    public ConnectionSource connectionSource() {
        return connectionSource;
    }

    /**
     * Seeds the database with sample data if the tables are empty.
     *
     * <p>Inserts three books, six members, three loans (one returned, one active,
     * one overdue), and two reservations the first time the application starts.</p>
     *
     * @throws SQLException if any insert or query fails
     */
    public void seedIfEmpty() throws SQLException {
        Dao<Book, Integer> bookDao = DaoManager.createDao(connectionSource, Book.class);
        if (bookDao.queryForAll().isEmpty()) {
            bookDao.create(new Book("Clean Code", "Robert C. Martin", "9780132350884", 3));
            bookDao.create(new Book("The Pragmatic Programmer", "Hunt & Thomas", "9780201616224", 2));
            bookDao.create(new Book("Design Patterns", "Gamma et al.", "9780201633610", 4));
        }

        Dao<Member, Integer> memberDao = DaoManager.createDao(connectionSource, Member.class);
        if (memberDao.queryForAll().isEmpty()) {
            memberDao.create(new Member("Ada Lovelace", "ada@example.com"));
            memberDao.create(new Member("Grace Hopper", "grace@example.com"));
            memberDao.create(new Member("Alan Turing", "alan@example.com"));
            memberDao.create(new Member("Edsger Dijkstra", "edsger@example.com"));
            memberDao.create(new Member("Barbara Liskov", "barbara@example.com"));
            memberDao.create(new Member("Donald Knuth", "donald@example.com"));
        }

        Dao<Loan, Integer> loanDao = DaoManager.createDao(connectionSource, Loan.class);
        if (loanDao.queryForAll().isEmpty()) {
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            createLoan(bookDao, loanDao, members.get(0), books.get(0), today.minusDays(5), today.plusDays(9));
            createLoan(bookDao, loanDao, members.get(1), books.get(2), today.minusDays(2), today.plusDays(12));
            createLoan(bookDao, loanDao, members.get(2), books.get(1), today.minusDays(30), today.minusDays(9));
        }

        Dao<Reservation, Integer> reservationDao = DaoManager.createDao(connectionSource, Reservation.class);
        if (reservationDao.queryForAll().isEmpty()) {
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            reservationDao.create(new Reservation(members.get(0), books.get(1), today.minusDays(1)));
            reservationDao.create(new Reservation(members.get(1), books.get(0), today.minusDays(3)));
        }
    }

    /**
     * Helper that creates a loan and decrements the book's available copies.
     *
     * @param bookDao  the ORMLite book DAO
     * @param loanDao  the ORMLite loan DAO
     * @param member   the borrowing member
     * @param book     the borrowed book
     * @param loanDate the loan creation date
     * @param dueDate  the loan due date
     * @throws SQLException if any persistence operation fails
     */
    private void createLoan(Dao<Book, Integer> bookDao, Dao<Loan, Integer> loanDao,
                            Member member, Book book,
                            LocalDate loanDate, LocalDate dueDate) throws SQLException {
        loanDao.create(new Loan(member, book, loanDate, dueDate));
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);
    }
}
