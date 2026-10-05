/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Generic base DAO that wraps ORMLite and translates {@link SQLException}
 * into unchecked {@link RuntimeException}s.
 *
 * @param <T> the entity type managed by this DAO
 */
public abstract class BaseDao<T> {

    /**
     * The underlying ORMLite DAO; accessible to subclasses for
     * entity-specific queries.
     */
    protected final Dao<T, Integer> dao;

    /**
     * Creates the ORMLite DAO for the given entity class.
     *
     * @param connectionSource the active database connection source
     * @param clazz the entity class
     * @throws RuntimeException if ORMLite cannot create the DAO
     */
    protected BaseDao(ConnectionSource connectionSource, Class<T> clazz) {
        try {
            this.dao = DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DAO for " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Returns all entities of type {@code T}.
     *
     * @return a list of all persisted entities; never {@code null}
     * @throws RuntimeException if the query fails
     */
    public List<T> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds an entity by its primary key.
     *
     * @param id the primary key value
     * @return the entity, or {@code null} if not found
     * @throws RuntimeException if the query fails
     */
    public T findById(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Persists a new entity.
     *
     * @param entity the entity to create; must not be {@code null}
     * @throws RuntimeException if the insert fails
     */
    public void create(T entity) {
        try {
            dao.create(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing entity.
     *
     * @param entity the entity to update; must not be {@code null}
     * @throws RuntimeException if the update fails
     */
    public void update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an entity.
     *
     * @param entity the entity to delete; must not be {@code null}
     * @throws RuntimeException if the delete fails
     */
    public void delete(T entity) {
        try {
            dao.delete(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes {@code callable} inside a single database transaction.
     * <p>If the callable throws a {@link RuntimeException} that is wrapped
     * inside a {@link SQLException} by ORMLite, this method unwraps and
     * re-throws the original {@link RuntimeException} so that domain errors
     * (e.g. {@code NotFoundException}) propagate correctly to the caller.</p>
     *
     * @param <R> the return type of the callable
     * @param callable the work to run atomically; must not be {@code null}
     * @return the value returned by {@code callable}
     * @throws SQLException if a non-domain database error occurs
     */
    public <R> R transaction(Callable<R> callable) throws SQLException {
        try {
            return TransactionManager.callInTransaction(dao.getConnectionSource(), callable);
        } catch (SQLException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw e;
        }
    }
}
