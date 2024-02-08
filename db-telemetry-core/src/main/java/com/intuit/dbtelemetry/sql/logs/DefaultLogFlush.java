package com.intuit.dbtelemetry.sql.logs;

import com.intuit.dbtelemetry.sql.SqlTelemetry;
import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.finder.WorkflowFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultLogFlush implements LogFlush {
    private final Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator;
    private final WorkflowFinder workflowFinder;
    private Map<String, String> stackTraceMap = new ConcurrentHashMap<>();
    private final boolean stackTraceEnabled;

    /**
     * Constructor for creating a DefaultLogFlush.
     *
     * @param aggregator Responsible for aggregating telemetry data.
     * @param stackTraceEnabled If true, stacktrace hash and stacktrace will be logged.
     */
    public DefaultLogFlush(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator, boolean stackTraceEnabled) {
        this(aggregator, null, stackTraceEnabled);
    }
    /**
     * Constructor for creating a DefaultLogFlush.
     *
     * @param aggregator Responsible for aggregating telemetry data.
     * @param workflowFinder Tool to find workflow from stacktrace.
     * @param stackTraceEnabled If true, stacktrace hash and stacktrace will be logged.
     */
    public DefaultLogFlush(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator, WorkflowFinder workflowFinder, boolean stackTraceEnabled) {
        this.aggregator = aggregator;
        this.workflowFinder = workflowFinder;
        this.stackTraceEnabled = stackTraceEnabled;
    }

    protected Map<String, String> getAndResetStackTraceData(long threadSleep) {
        if (CollectionUtils.isEmpty(stackTraceMap)) {
            return null;
        }
        Map<String, String> previousMap = stackTraceMap;
        stackTraceMap = new ConcurrentHashMap<>();
        try {
            Thread.sleep(threadSleep);
        } catch (InterruptedException ex) {
            log.error("SLEEP_BEFORE_FLUSH Interrupted proceeding with flush");
        }
        return previousMap;
    }

    /**
     * Log the content of the previous map of SQL telemetry data and reset telemetry data.
     *
     *
     * @param threadSleep Delay before logs and data are reset.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Override
    public void flushLogs(long threadSleep) throws TelemetryFlushException {
        try {
            Map<SqlTelemetry, SqlTelemetry> previousMap = aggregator.getAndResetDBTelemetryData(threadSleep);
            if(Objects.nonNull(previousMap))
                logPreviousMap(previousMap);
        } catch (Exception e) {
            throw new TelemetryFlushException("Exception in telemetry flush", e);
        }

    }
    /**
     * Writes the current aggregated stack trace data to logs.
     *
     * @param threadSleep The amount of time in milliseconds for the log flush thread to wait before executing.
     */
    @Override
    public void flushStackTrace(long threadSleep) throws TelemetryFlushException {
        try {
            if (!stackTraceEnabled) {
                return;
            }
            Map<String, String> previousMap = getAndResetStackTraceData(threadSleep);
            if(Objects.nonNull(previousMap))
                logStackTrace(previousMap);
        } catch (Exception e) {
            throw new TelemetryFlushException("Exception in StackTrace flush", e);
        }
    }

    /**
     * Log the content of the previous map of SQL telemetry data and collect stacktrace to log later
     *
     * @param previousMap the map containing the SQL telemetry data to log.
     */
    private void logPreviousMap(Map<SqlTelemetry, SqlTelemetry> previousMap) {
        long beforeTime = System.currentTimeMillis();
        // Log the entire previous map.
        previousMap.forEach((key, value) -> logEntries(key));
        long callTime = System.currentTimeMillis() - beforeTime;
        log.info("MapConsolidatorLogFlush{LogFlushCount=" + previousMap.size() + ", timeTakenLogFlush=" + callTime + "}");
    }
    /**
     * Log the content of the previous map of SQL telemetry data and collect stacktrace to log later
     *
     * @param info SqlTelemetry object.
     */
    private void logEntries(SqlTelemetry info) {
        Optional.ofNullable(workflowFinder).ifPresent(wf -> info.setWorkflow(wf.getWorkflowName(info.getStackTrace())));
        log.info(info.toString());
        if (stackTraceEnabled && StringUtils.isNotEmpty(info.getStackTrace())) {
            stackTraceMap.put(info.getStackTrace(), info.getStackTrace());
        }
    }

    private void logStackTrace(Map<String, String> previousMap) {
        long beforeTime = System.currentTimeMillis();
        previousMap.forEach((key, value) -> log.info("MapConsolidatorLogFlush{StackTraceHash=\\\"" + key.hashCode() + "\\\", PrintStackTrace=\\\"" + key + "\\\"}"));
        long callTime = System.currentTimeMillis() - beforeTime;
        log.info("MapConsolidatorLogFlush{StackTraceCount=" + previousMap.size() + ", timeTakenStackTraceFlush=" + callTime + "}");
    }

}
