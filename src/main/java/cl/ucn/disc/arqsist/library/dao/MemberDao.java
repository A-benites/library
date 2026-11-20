/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

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

    /**
     * Finds members whose names contain a literal, case-insensitive fragment.
     *
     * @param query the name fragment to find
     * @return matching members ordered by ID
     * @throws RuntimeException if the query fails
     */
    public List<Member> searchByName(String query) {
        try {
            var queryBuilder = dao.queryBuilder();
            queryBuilder.where().raw("instr(lower(name), lower(?)) > 0", new SelectArg(query));
            queryBuilder.orderBy("id", true);
            return queryBuilder.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
