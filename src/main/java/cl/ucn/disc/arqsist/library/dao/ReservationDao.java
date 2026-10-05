/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for {@link Reservation} entities.
 *
 * <p>Wraps an ORMLite {@link Dao} and provides typed CRUD operations
 * for the {@code reservations} table.</p>
 */
public final class ReservationDao {

    /** The underlying ORMLite DAO. */
    private final Dao<Reservation, Integer> dao;

    /**
     * Creates a new {@code ReservationDao}.
     *
     * @param connectionSource the active database connection source
     * @throws SQLException if ORMLite cannot create the internal DAO
     */
    public ReservationDao(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, Reservation.class);
    }

    /**
     * Returns all reservations in the database.
     *
     * @return a list of all {@link Reservation} entities; never {@code null}
     * @throws SQLException if the query fails
     */
    public List<Reservation> findAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Finds a reservation by its primary key.
     *
     * @param id the reservation's primary key
     * @return the {@link Reservation}, or {@code null} if not found
     * @throws SQLException if the query fails
     */
    public Reservation findById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /**
     * Persists a new reservation.
     *
     * @param reservation the reservation to create; must not be {@code null}
     * @throws SQLException if the insert fails
     */
    public void create(Reservation reservation) throws SQLException {
        dao.create(reservation);
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservation the reservation to update; must not be {@code null}
     * @throws SQLException if the update fails
     */
    public void update(Reservation reservation) throws SQLException {
        dao.update(reservation);
    }
}
