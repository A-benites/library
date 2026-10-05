/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.model.Book;

import java.sql.SQLException;
import java.util.List;

/**
 * Application service for {@link Book} management.
 *
 * <p>Coordinates book inventory operations including listing, creating,
 * and adjusting available copies on borrow and return.</p>
 */
public final class BookService {

    /** DAO used to persist {@link Book} entities. */
    private final BookDao dao;

    /**
     * Creates a new {@code BookService}.
     *
     * @param dao the book data access object; must not be {@code null}
     */
    public BookService(BookDao dao) {
        this.dao = dao;
    }

    /**
     * Returns all books in the catalog.
     *
     * @return a list of all books; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Book> listAll() {
        return dao.findAll();
    }

    /**
     * Finds a book by its primary key.
     *
     * @param id the book's primary key
     * @return the {@link Book}, or {@code null} if not found
     * @throws SQLException if the query fails
     */
    public Book findById(int id) {
        return dao.findById(id);
    }

    /**
     * Adds a new book to the catalog.
     *
     * <p>Sets {@code availableCopies} to {@code totalCopies} on creation.</p>
     *
     * @param book the book to add; must not be {@code null}
     * @return the persisted book with its generated ID
     * @throws SQLException if the insert fails
     */
    public Book create(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        dao.create(book);
        return book;
    }

    /**
     * Decrements the available copy count when a book is borrowed.
     *
     * @param bookId the ID of the book being borrowed
     * @throws SQLException if the update fails
     */
    public void borrow(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies of book " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        dao.update(book);
    }

    /**
     * Increments the available copy count when a book is returned.
     *
     * @param bookId the ID of the book being returned
     * @throws SQLException if the update fails
     */
    public void returnCopy(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        dao.update(book);
    }
}
