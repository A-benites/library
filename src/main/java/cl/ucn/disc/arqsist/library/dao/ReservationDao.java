/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access object for {@link Reservation} entities.
 * <p>Inherits CRUD and transaction support from {@link BaseDao}.</p>
 */
public final class ReservationDao extends BaseDao<Reservation> {

    /**
     * Creates a new {@code ReservationDao}.
     *
     * @param connectionSource the active database connection source
     */
    public ReservationDao(ConnectionSource connectionSource) {
        super(connectionSource, Reservation.class);
    }
}
