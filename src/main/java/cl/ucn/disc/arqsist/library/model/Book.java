/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** A catalog entry and its total and available inventory counts. */
@DatabaseTable(tableName = "books")
public final class Book {

    /** Auto-generated primary key. */
    @DatabaseField(generatedId = true)
    private int id;

    /** Book title. */
    @DatabaseField(canBeNull = false)
    private String title;

    /** Book author. */
    @DatabaseField(canBeNull = false)
    private String author;

    /** ISBN identifier. */
    @DatabaseField(canBeNull = false)
    private String isbn;

    /** Number of copies owned by the library. */
    @DatabaseField(canBeNull = false)
    private int totalCopies;

    /** Number of copies currently available to borrow. */
    @DatabaseField(canBeNull = false)
    private int availableCopies;

    /** No-argument constructor required by ORMLite. */
    public Book() {
    }

    /**
     * Creates a book and initializes all copies as available.
     *
     * @param title the book title
     * @param author the book author
     * @param isbn the ISBN identifier
     * @param totalCopies the number of copies owned
     */
    public Book(String title, String author, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    /** @return the generated book ID */
    public int getId() {
        return id;
    }

    /** @param id the generated book ID */
    public void setId(int id) {
        this.id = id;
    }

    /** @return the book title */
    public String getTitle() {
        return title;
    }

    /** @param title the book title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the book author */
    public String getAuthor() {
        return author;
    }

    /** @param author the book author */
    public void setAuthor(String author) {
        this.author = author;
    }

    /** @return the ISBN identifier */
    public String getIsbn() {
        return isbn;
    }

    /** @param isbn the ISBN identifier */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /** @return the total number of copies owned */
    public int getTotalCopies() {
        return totalCopies;
    }

    /** @param totalCopies the total number of copies owned */
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    /** @return the number of copies currently available */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /** @param availableCopies the number of copies currently available */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
