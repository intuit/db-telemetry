package com.intuit.dbtelemetry.sql.aggregator;

public interface Aggregator<T> {
    /**
     * Aggregates the SQL telemetry data. This method is used to collect SQL execution data like execution time given by a certain producer.
     *
     * @param producer the source of the data
     * @param sql the SQL statement that was executed
     * @param executionTime the time it took to execute the SQL statement in milliseconds
     * @throws TelemetryAggregatorException if an error occurs during the aggregation process
     */
    void aggregate(String producer, String sql, long executionTime) throws TelemetryAggregatorException;

    /**
     * Retrieves the accumulated telemetry data and resets the internal data store.
     *
     * @param threadSleep the time to sleep in milliseconds before getting and resetting data
     * @return aggregated telemetry data of type T
     */
    T getAndResetDBTelemetryData(long threadSleep);

}
