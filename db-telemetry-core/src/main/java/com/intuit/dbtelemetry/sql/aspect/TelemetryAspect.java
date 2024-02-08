package com.intuit.dbtelemetry.sql.aspect;

import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.extractor.sql.SqlExtractor;


public interface TelemetryAspect<U, V> {
    /**
     * Extracts Telemetry data from a join point and aggregates it.
     *
     * @param joinPoint the join point from which to extract data
     * @return an aggregated object
     * @throws Throwable if any error occurs while processing
     */
    Object extractAndAggregate(U joinPoint) throws Throwable;

    /**
     * Check if the aspect is enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Used to aggregate the SQL telemetry data in extractAndAggregate
     *
     * @return the aggregator object
     */
    Aggregator<V> getAggregator();

    /**
     * Get the SQL extractor to extracts the SQL statement from the given join point in extractAndAggregate
     *
     * @return the SQL extractor object
     */
    SqlExtractor<U> getSqlExtractor();

}
