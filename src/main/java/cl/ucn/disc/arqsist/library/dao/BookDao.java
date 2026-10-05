/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access object for {@link Book} entities.
 * <p>Inherits CRUD and transaction support from {@link BaseDao}.</p>
 */
public final class BookDao extends BaseDao<Book> {

    /**
     * Creates a new {@code BookDao}.
     *
     * @param connectionSource the active database connection source
     */
    public BookDao(ConnectionSource connectionSource) {
        super(connectionSource, Book.class);
    }
}
