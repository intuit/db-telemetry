package com.intuit.dbtelemetry.sql.extractor.sql;

import java.util.Optional;

public interface SqlExtractor<T> {

    /**
     * Extracts the SQL statement from the given join point.
     *
     * @param joinPoint the join point to extract the SQL statement from
     * @return the SQL statement, or null if the statement could not be extracted
     */
    String extractSql(T joinPoint);
}
