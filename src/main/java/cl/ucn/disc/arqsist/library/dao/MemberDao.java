/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access object for {@link Member} entities.
 * <p>Inherits CRUD and transaction support from {@link BaseDao}.</p>
 */
public final class MemberDao extends BaseDao<Member> {

    /**
     * Creates a new {@code MemberDao}.
     *
     * @param connectionSource the active database connection source
     */
    public MemberDao(ConnectionSource connectionSource) {
        super(connectionSource, Member.class);
    }
}
