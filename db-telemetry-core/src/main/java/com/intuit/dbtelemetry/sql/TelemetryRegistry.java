package com.intuit.dbtelemetry.sql;

import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.aggregator.MapBasedAggregator;
import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;

import java.util.Map;

/**
 * Singleton class for managing and configuring the telemetry data of SQL queries.
 * This class provides access to the aggregator and log flusher functions, allowing for the customization of these behaviors.
 */
public class TelemetryRegistry {
    private static TelemetryRegistry INSTANCE = new TelemetryRegistry();

    private Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator = new MapBasedAggregator();
    private LogFlush logFlush = new DefaultLogFlush(aggregator, null, true);

    private TelemetryRegistry() {
    }

    TelemetryRegistry(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator, LogFlush logFlush) {
        this.aggregator = aggregator;
        this.logFlush = logFlush;
    }

    /**
     * Returns the current TelemetryRegistry instance.
     *
     * @return the current TelemetryRegistry instance
     */
    public static TelemetryRegistry getTelemetryRegistry() {
        return INSTANCE;
    }

    /**
     * Overrides the current TelemetryRegistry instance with the specified one.
     *
     * @param telemetryRegistry the new TelemetryRegistry instance.
     */
    public static void setTelemetryRegistry(TelemetryRegistry telemetryRegistry) {
        INSTANCE = telemetryRegistry;
    }

    /**
     * Returns the current Aggregator for the TelemetryRegistry.
     *
     * @return the current Aggregator instance
     */
    public Aggregator<Map<SqlTelemetry, SqlTelemetry>> getAggregator() {
        return aggregator;
    }

    /**
     * Sets a new Aggregator for the TelemetryRegistry.
     *
     * @param aggregator the new Aggregator instance.
     */
    public void setAggregator(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * Returns the current LogFlush instance for the TelemetryRegistry.
     *
     * @return the current LogFlush instance
     */
    public LogFlush getLogFlush() {
        return logFlush;
    }

    /**
     * Sets a new LogFlush for the TelemetryRegistry.
     *
     * @param logFlush the new LogFlush instance.
     */
    public void setLogFlush(LogFlush logFlush) {
        this.logFlush = logFlush;
    }

    /**
     * Sets the state of a specific aspect.
     *
     * @param aspectClassName the class name of the aspect
     * @param state the new state of the aspect
     */
    public void aspectState(String aspectClassName, String state) {
        System.setProperty(aspectClassName, state);
    }

}
