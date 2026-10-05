/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a reservation made by a {@link Member} for a {@link Book}.
 *
 * <p>A reservation holds a spot for a member when the desired book is unavailable.
 * It can be fulfilled into a {@link Loan} once a copy becomes available.</p>
 */
@DatabaseTable(tableName = "reservations")
public final class Reservation {

    /** Auto-generated primary key. */
    @DatabaseField(generatedId = true)
    private int id;

    /** The member who made the reservation. */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;

    /** The book that was reserved. */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Book book;

    /** The date the reservation was created. */
    @DatabaseField(canBeNull = false)
    private String reservedAt;

    /** Whether the reservation has been fulfilled into a loan. */
    @DatabaseField
    private boolean fulfilled;

    /** No-arg constructor required by ORMLite. */
    public Reservation() {
    }

    /**
     * Creates a new reservation.
     *
     * @param member     the member making the reservation
     * @param book       the book being reserved
     * @param reservedAt the date the reservation is created
     */
    public Reservation(Member member, Book book, String reservedAt) {
        this.member = member;
        this.book = book;
        this.reservedAt = reservedAt;
        this.fulfilled = false;
    }

    /**
     * Returns the primary key.
     *
     * @return the reservation ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the primary key (used by ORMLite after insert).
     *
     * @param id the reservation ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the member who made the reservation.
     *
     * @return the {@link Member}
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member who made the reservation.
     *
     * @param member the {@link Member}
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Returns the reserved book.
     *
     * @return the {@link Book}
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the reserved book.
     *
     * @param book the {@link Book}
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Returns the date the reservation was created.
     *
     * @return the reservation date string
     */
    public String getReservedAt() {
        return reservedAt;
    }

    /**
     * Sets the reservation date.
     *
     * @param reservedAt the reservation date string
     */
    public void setReservedAt(String reservedAt) {
        this.reservedAt = reservedAt;
    }

    /**
     * Returns {@code true} if the reservation has been fulfilled into a loan.
     *
     * @return {@code true} if fulfilled
     */
    public boolean isFulfilled() {
        return fulfilled;
    }

    /**
     * Sets the fulfilled flag.
     *
     * @param fulfilled {@code true} if the reservation has been fulfilled
     */
    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }
}
