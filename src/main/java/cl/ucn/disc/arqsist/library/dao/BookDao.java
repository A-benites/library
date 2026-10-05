/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for {@link Book} entities.
 *
 * <p>Wraps an ORMLite {@link Dao} and provides typed CRUD operations
 * for the {@code books} table.</p>
 */
public final class BookDao {

    /** The underlying ORMLite DAO. */
    private final Dao<Book, Integer> dao;

    /**
     * Creates a new {@code BookDao}.
     *
     * @param connectionSource the active database connection source
     * @throws SQLException if ORMLite cannot create the internal DAO
     */
    public BookDao(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, Book.class);
    }

    /**
     * Returns all books in the database.
     *
     * @return a list of all {@link Book} entities; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Book> findAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Finds a book by its primary key.
     *
     * @param id the book's primary key
     * @return the {@link Book}, or {@code null} if not found
     * @throws SQLException if the query fails
     */
    public Book findById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /**
     * Persists a new book.
     *
     * @param book the book to create; must not be {@code null}
     * @throws SQLException if the insert fails
     */
    public void create(Book book) throws SQLException {
        dao.create(book);
    }

    /**
     * Updates an existing book.
     *
     * @param book the book to update; must not be {@code null}
     * @throws SQLException if the update fails
     */
    public void update(Book book) throws SQLException {
        dao.update(book);
    }

    /**
     * Deletes a book.
     *
     * @param book the book to delete; must not be {@code null}
     * @throws SQLException if the delete fails
     */
    public void delete(Book book) throws SQLException {
        dao.delete(book);
    }
}
