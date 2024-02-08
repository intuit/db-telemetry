package com.intuit.dbtelemetry.sql;

import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.aggregator.MapBasedAggregator;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.DepthFilterStackTraceExtractor;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.StackTraceExtractor;
import com.intuit.dbtelemetry.sql.finder.WorkflowFinder;
import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builder class for constructing TelemetryRegistry objects.
 * This builder allows for configuring various aspects such as aggregator, stack trace extractor, log flushing mechanism,
 * and stack trace depth, among other things before instantiation of a TelemetryRegistry object.
 */
public class TelemetryRegistryBuilder {
    private Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator;
    private StackTraceExtractor stackTraceExtractor;
    private List<String> excludedTablesList;
    private int stackTraceDepth = -1;
    private String stackTraceFilter = "";
    private LogFlush logFlush;
    private WorkflowFinder workflowFinder;
    private boolean stackTrace = true;

    private TelemetryRegistryBuilder() {
    }
    /**
     * Creates a new instance of TelemetryRegistryBuilder.
     *
     * @return a new instance of TelemetryRegistryBuilder.
     */
    public static TelemetryRegistryBuilder builder() {
        return new TelemetryRegistryBuilder();
    }
    /**
     * Configures the builder with a provided aggregator.
     * This is used to aggregate SQL telemetry data and stackTrace (via StackTraceExtractor Or DepthFilterStackTraceExtractor).
     * Not used if the logFlush is configured in builder.
     *
     * @param aggregator: the aggregator to be used for telemetry data
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withAggregator(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator) {
        this.aggregator = aggregator;
        return this;
    }
    /**
     * Configures the builder with a provided logFlush.
     * Will be used to log the telemetry data aggregated via Aggregator and reset telemetry data.
     *
     * @param logFlush: Extracts the workflow name from the given stack trace string.
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withLogFlush(LogFlush logFlush) {
        this.logFlush = logFlush;
        return this;
    }
    /**
     * Configures the builder with a provided workflowFinder.
     * Will be used to extract the workflow name from the given stack trace string extracted by StackTraceExtractor.
     * Not used if the logFlush is configured in builder.
     *
     * @param workflowFinder: Extracts the workflow name from the given stack trace string.
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withWorkflowFinder(WorkflowFinder workflowFinder) {
        this.workflowFinder = workflowFinder;
        return this;
    }
    /**
     * Configures the builder to enable stacktrace logging
     * If true, stacktrace hash and stacktrace will be logged.
     * Not used if the logFlush is configured in builder.
     *
     * @param stackTrace: If true, stacktrace hash and stacktrace will be logged.
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withStackTraceLogging(boolean stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
    /**
     * Configures the builder to enable stacktrace logging
     * Used to extract the stack trace as a string from the given Throwable object.
     * If not provided Builder will create DepthFilterStackTraceExtractor with -1 as depth and no filter
     * Not used if the aggregator is configured in builder.
     *
     * @param stackTraceExtractor: any implementation of StackTraceExtractor e.g. DepthFilterStackTraceExtractor
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withStackTraceExtractor(StackTraceExtractor stackTraceExtractor) {
        this.stackTraceExtractor = stackTraceExtractor;
        return this;
    }
    /**
     * Configures the builder with a provided stacktrace Depth.
     * This filter string is used to exclude stack trace lines that doesn't contain this string
     * Not used if the stackTraceExtractor is configured in builder.
     *
     * @param stackTraceDepth: the maximum depth of the stack trace to include, or -1 to include all levels
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withStackTraceDepth(int stackTraceDepth) {
        this.stackTraceDepth = stackTraceDepth;
        return this;
    }
    /**
     * Configures the builder with a provided stack trace filter.
     * Not used if the stackTraceExtractor is configured in builder.
     *
     * @param stackTraceFilter: the filter used for stack traces
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withStackTraceFilter(String stackTraceFilter) {
        this.stackTraceFilter = stackTraceFilter;
        return this;
    }
    /**
     * Configures the builder with a list of excluded tables.
     * This is used to exclude the tables for which you don't want to collect the telemetry data.
     * Not used if the aggregator is configured in builder.
     *
     * @param excludedTablesList: the list of tables to be excluded from the telemetry data
     * @return the current instance of TelemetryRegistryBuilder for chain setting
     */
    public TelemetryRegistryBuilder withExcludedTables(List<String> excludedTablesList) {
        this.excludedTablesList = excludedTablesList;
        return this;
    }
    /**
     * Builds and returns a TelemetryRegistry using the properties set on the builder.
     *
     * @return a new TelemetryRegistry object
     */
    public TelemetryRegistry build() {

        stackTraceExtractor = Optional.ofNullable(stackTraceExtractor)
                .orElse(new DepthFilterStackTraceExtractor(stackTraceDepth, stackTraceFilter));

        aggregator = Optional.ofNullable(aggregator)
                .orElse(new MapBasedAggregator(excludedTablesList, stackTraceExtractor));

        logFlush = Optional.ofNullable(logFlush)
                .orElse(new DefaultLogFlush(aggregator, workflowFinder, stackTrace));

        return new TelemetryRegistry(aggregator, logFlush);
    }

}
