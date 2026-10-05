/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Loan;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for {@link Loan} entities.
 *
 * <p>Wraps an ORMLite {@link Dao} and provides typed CRUD operations
 * for the {@code loans} table.</p>
 */
public final class LoanDao {

    /** The underlying ORMLite DAO. */
    private final Dao<Loan, Integer> dao;

    /**
     * Creates a new {@code LoanDao}.
     *
     * @param connectionSource the active database connection source
     * @throws SQLException if ORMLite cannot create the internal DAO
     */
    public LoanDao(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, Loan.class);
    }

    /**
     * Returns all loans in the database.
     *
     * @return a list of all {@link Loan} entities; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Loan> findAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Finds a loan by its primary key.
     *
     * @param id the loan's primary key
     * @return the {@link Loan}, or {@code null} if not found
     * @throws SQLException if the query fails
     */
    public Loan findById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /**
     * Persists a new loan.
     *
     * @param loan the loan to create; must not be {@code null}
     * @throws SQLException if the insert fails
     */
    public void create(Loan loan) throws SQLException {
        dao.create(loan);
    }

    /**
     * Updates an existing loan.
     *
     * @param loan the loan to update; must not be {@code null}
     * @throws SQLException if the update fails
     */
    public void update(Loan loan) throws SQLException {
        dao.update(loan);
    }
}
