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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    /** The active ORMLite connection source. */
    private final ConnectionSource connectionSource;

    /**
     * Opens a JDBC connection and creates all required tables if they do not exist.
     *
     * @param jdbcUrl the JDBC URL for the SQLite database (e.g. {@code jdbc:sqlite:database.db})
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
     * <p>Inserts three books, three members, three loans (one returned, one active,
     * one overdue), and one reservation the first time the application starts.</p>
     *
     * @throws SQLException if any insert or query fails
     */
    public void seedIfEmpty() throws SQLException {
        Dao<Book, Integer> bookDao = DaoManager.createDao(connectionSource, Book.class);
        if (bookDao.queryForAll().isEmpty()) {
            log.debug("Seeding books...");
            bookDao.create(new Book("Clean Code", "Robert C. Martin", "9780132350884", 3));
            bookDao.create(new Book("The Pragmatic Programmer", "Hunt & Thomas", "9780201616224", 2));
            bookDao.create(new Book("Design Patterns", "Gamma et al.", "9780201633610", 4));
        }

        Dao<Member, Integer> memberDao = DaoManager.createDao(connectionSource, Member.class);
        if (memberDao.queryForAll().isEmpty()) {
            log.debug("Seeding members...");
            memberDao.create(new Member("Ada Lovelace", "ada@example.com"));
            memberDao.create(new Member("Grace Hopper", "grace@example.com"));
            memberDao.create(new Member("Alan Turing", "alan@example.com"));
        }

        Dao<Loan, Integer> loanDao = DaoManager.createDao(connectionSource, Loan.class);
        if (loanDao.queryForAll().isEmpty()) {
            log.debug("Seeding loans...");
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            // 1. Préstamo DEVUELTO — no decrementa availableCopies
            Loan returned = new Loan(members.get(0), books.get(0),
                    today.minusDays(30), today.minusDays(9));
            returned.setReturned(true);
            returned.setReturnDate(today.minusDays(10));
            loanDao.create(returned);
            log.debug("Created returned loan: id={}", returned.getId());

            // 2. Préstamo ACTIVO — decrementa availableCopies
            Loan active = new Loan(members.get(1), books.get(1),
                    today.minusDays(5), today.plusDays(16));
            loanDao.create(active);
            books.get(1).setAvailableCopies(books.get(1).getAvailableCopies() - 1);
            bookDao.update(books.get(1));
            log.debug("Created active loan: id={}", active.getId());

            // 3. Préstamo VENCIDO — decrementa availableCopies
            Loan overdue = new Loan(members.get(2), books.get(2),
                    today.minusDays(40), today.minusDays(19));
            loanDao.create(overdue);
            books.get(2).setAvailableCopies(books.get(2).getAvailableCopies() - 1);
            bookDao.update(books.get(2));
            log.debug("Created overdue loan: id={}", overdue.getId());
        }

        Dao<Reservation, Integer> reservationDao = DaoManager.createDao(connectionSource, Reservation.class);
        if (reservationDao.queryForAll().isEmpty()) {
            log.debug("Seeding reservations...");
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            reservationDao.create(new Reservation(members.get(0), books.get(0), LocalDate.now().minusDays(1)));
            log.debug("Seeded 1 reservation.");
        }
    }
}
