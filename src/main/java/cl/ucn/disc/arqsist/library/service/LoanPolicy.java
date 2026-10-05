/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

/**
 * Central policy for library loans.
 * <p>All loan-period and fee rules live here so that checkout and fulfill
 * always agree on the same values.</p>
 */
public final class LoanPolicy {

    /** Number of days a standard loan is valid. */
    public static final int DUE_DAYS = 21;

    /** Overdue fee charged per day past the due date (in local currency). */
    public static final double FEE_PER_DAY = 1.0;

    /** Prevent instantiation. */
    private LoanPolicy() { }

    /**
     * Computes the due date for a loan that starts on {@code loanDate}.
     *
     * @param loanDate the date the loan is created; must not be {@code null}
     * @return the due date, exactly {@link #DUE_DAYS} days after {@code loanDate}
     */
    public static LocalDate dueDate(LocalDate loanDate) {
        return loanDate.plusDays(DUE_DAYS);
    }
}
