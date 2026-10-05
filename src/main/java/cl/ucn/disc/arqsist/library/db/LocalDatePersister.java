/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.db;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * ORMLite custom persister that maps {@link LocalDate} to a SQL STRING column.
 *
 * <p>Stores dates as ISO-8601 strings (e.g. {@code "2026-01-15"}) so they are
 * human-readable in the SQLite file and can be compared lexicographically.</p>
 */
public final class LocalDatePersister extends BaseDataType {

    /** Singleton instance registered with ORMLite. */
    private static final LocalDatePersister INSTANCE = new LocalDatePersister();

    /**
     * Returns the singleton instance.
     *
     * @return the single {@code LocalDatePersister} instance
     */
    public static LocalDatePersister getSingleton() {
        return INSTANCE;
    }

    /** Private constructor — use {@link #getSingleton()}. */
    private LocalDatePersister() {
        super(SqlType.STRING, new Class<?>[]{ LocalDate.class });
    }

    /**
     * Parses the default string from an annotation value.
     *
     * @param fieldType  the ORMLite field descriptor
     * @param defaultStr the raw default string
     * @return the default string unchanged
     */
    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return defaultStr;
    }

    /**
     * Reads the SQL column value as a plain string.
     *
     * @param results   the current result-set row
     * @param columnPos the zero-based column index
     * @return the raw string stored in the column
     * @throws SQLException if the column cannot be read
     */
    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos)
            throws SQLException {
        return results.getString(columnPos);
    }

    /**
     * Converts the SQL string argument to a {@link LocalDate}.
     *
     * @param fieldType the ORMLite field descriptor
     * @param sqlArg    the raw string from the database
     * @param columnPos the zero-based column index (unused)
     * @return the parsed {@link LocalDate}
     */
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return LocalDate.parse((String) sqlArg);
    }

    /**
     * Converts a {@link LocalDate} to its ISO-8601 string for storage.
     *
     * @param fieldType  the ORMLite field descriptor
     * @param javaObject the {@link LocalDate} to persist
     * @return the ISO-8601 string representation
     */
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        return javaObject.toString();
    }
}
