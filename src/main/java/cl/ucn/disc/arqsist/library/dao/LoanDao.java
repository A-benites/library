/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Loan;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access object for {@link Loan} entities.
 * <p>Inherits CRUD and transaction support from {@link BaseDao}.</p>
 */
public final class LoanDao extends BaseDao<Loan> {

    /**
     * Creates a new {@code LoanDao}.
     *
     * @param connectionSource the active database connection source
     */
    public LoanDao(ConnectionSource connectionSource) {
        super(connectionSource, Loan.class);
    }
}
