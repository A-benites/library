/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * Represents a book loan in the library system.
 *
 * <p>A loan tracks which {@link Member} borrowed which {@link Book},
 * the dates involved, whether it has been returned, and any overdue fee accrued.</p>
 */
@DatabaseTable(tableName = "loans")
public final class Loan {

    /** Auto-generated primary key. */
    @DatabaseField(generatedId = true)
    private int id;

    /** The member who borrowed the book. */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;

    /** The book that was borrowed. */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Book book;

    /** The date on which the loan was created. */
    @DatabaseField(canBeNull = false)
    private String loanDate;

    /** The date by which the book must be returned. */
    @DatabaseField(canBeNull = false)
    private String dueDate;

    /** The date on which the book was actually returned; {@code null} if still on loan. */
    @DatabaseField
    private String returnDate;

    /** Whether the book has been returned. */
    @DatabaseField
    private boolean returned;

    /** Overdue fee accrued in local currency; 0.0 if returned on time. */
    @DatabaseField
    private double overdueFee;

    /** No-arg constructor required by ORMLite. */
    public Loan() {
    }

    /**
     * Creates a new loan.
     *
     * @param member   the member borrowing the book
     * @param book     the book being borrowed
     * @param loanDate the date the loan is created
     * @param dueDate  the date by which the book must be returned
     */
    public Loan(Member member, Book book, String loanDate, String dueDate) {
        this.member = member;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.overdueFee = 0.0;
    }

    /**
     * Returns the primary key.
     *
     * @return the loan ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the primary key (used by ORMLite after insert).
     *
     * @param id the loan ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the member who borrowed the book.
     *
     * @return the {@link Member}
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member who borrowed the book.
     *
     * @param member the {@link Member}
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Returns the borrowed book.
     *
     * @return the {@link Book}
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the borrowed book.
     *
     * @param book the {@link Book}
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Returns the loan date as a string.
     *
     * @return the loan date
     */
    public String getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loan date.
     *
     * @param loanDate the loan date string
     */
    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Returns the due date as a string.
     *
     * @return the due date
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the due date string
     */
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Returns the return date as a string, or {@code null} if not yet returned.
     *
     * @return the return date, or {@code null}
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the return date string
     */
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Returns {@code true} if the book has been returned.
     *
     * @return {@code true} if returned
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Returns {@code true} if the loan is overdue (not returned and past due date).
     *
     * @return {@code true} if overdue
     */
    public boolean isOverdue() {
        return !returned && LocalDate.parse(dueDate).isBefore(LocalDate.now());
    }

    /**
     * Sets the returned flag.
     *
     * @param returned {@code true} if the book has been returned
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Returns the overdue fee accrued.
     *
     * @return the overdue fee in local currency
     */
    public double getOverdueFee() {
        return overdueFee;
    }

    /**
     * Sets the overdue fee.
     *
     * @param overdueFee the overdue fee in local currency
     */
    public void setOverdueFee(double overdueFee) {
        this.overdueFee = overdueFee;
    }
}
