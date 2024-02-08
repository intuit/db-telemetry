package com.intuit.dbtelemetry.sql.aggregator;

import com.intuit.dbtelemetry.sql.SqlTelemetry;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.StackTraceExtractor;
import com.intuit.dbtelemetry.sql.utils.SqlHelperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MapBasedAggregator implements Aggregator<Map<SqlTelemetry, SqlTelemetry>> {

    private static Map<SqlTelemetry, SqlTelemetry> currentLogInfoMap = new ConcurrentHashMap<>();
    private final List<String> excludedTablesList;
    private final StackTraceExtractor stackTraceExtractor;

    public MapBasedAggregator() {
        this(null);
    }

    public MapBasedAggregator(List<String> excludedTablesList) {
        this(excludedTablesList, null);
    }

    public MapBasedAggregator(List<String> excludedTablesList, StackTraceExtractor stackTraceExtractor) {
        this.excludedTablesList = Objects.isNull(excludedTablesList) ? new ArrayList<>() : excludedTablesList;
        this.stackTraceExtractor = stackTraceExtractor;
    }

    /**
     * Aggregates SQL telemetry data. This method accepts the producer details, SQL, and execution time
     * for further use, whilst handling the SQL with missing parameters.
     *
     * @param producer      the producer of the SQL statement
     * @param sql           the SQL statement itself
     * @param executionTime the time taken by the SQL statement to execute
     * @throws TelemetryAggregatorException if there's an error during the aggregation process
     */
    @Override
    public void aggregate(String producer, String sql, long executionTime) throws TelemetryAggregatorException {
        aggregate(producer, sql, executionTime, null);
    }

    /**
     * Aggregates SQL telemetry data and also handles specific SQL statement with given parameters.
     *
     * @param producer      the producer of the SQL statement
     * @param sql           the SQL statement itself
     * @param executionTime the time taken by the SQL statement to execute
     * @param sqlWithParameters SQL statement with parameters
     * @throws TelemetryAggregatorException if there's an error during the aggregation process
     */
    public void aggregate(String producer, String sql, long executionTime, String sqlWithParameters) throws TelemetryAggregatorException {
        if (Objects.isNull(sql)){
            return;
        }
        try {

            String finalSql = sql;
            boolean excludedTablePresent = excludedTablesList.stream().anyMatch(excludedTable -> StringUtils.containsIgnoreCase(finalSql, excludedTable));
            if (excludedTablePresent) {
                return;
            }

            sql = SqlHelperUtils.sanitizeSQL(sql);

            SqlTelemetry logInfo = isStackTraceEnabled() ?
                    getSqlTelemetry(producer,
                            sql,
                            stackTraceExtractor.extractStackTrace(new Throwable()), sqlWithParameters) :
                    getSqlTelemetry(producer, sql, sqlWithParameters);

            logInfo.setInstanceValue(executionTime);
        } catch (Exception e) {
            throw new TelemetryAggregatorException("Fail to aggregate SQL" +
                    "producer=" + producer +
                    " sql=" + sql +
                    " calltime=" + executionTime, e);
        }
    }

    /**
     * Extract and reset the current SQL telemetry data set.
     * If there is no collected telemetry data, it returns null.
     *
     * @param threadSleep delay time before resetting the telemetry data
     * @return a map of the current telemetry data if available; null otherwise
     */
    @Override
    public Map<SqlTelemetry, SqlTelemetry> getAndResetDBTelemetryData(long threadSleep) {
        if (CollectionUtils.isEmpty(currentLogInfoMap)) {
            return null;
        }
        // Create a new map for log entries while we log the current map.  This doesn't need to be
        // synchronized because the assignment operator is atomic. Other threads may still be updating
        // previousMap, but since we wait X seconds before actually outputting it, we should get those.
        Map<SqlTelemetry, SqlTelemetry> previousMap = currentLogInfoMap;
        currentLogInfoMap = new ConcurrentHashMap<>();

        waitFor(threadSleep);

        return previousMap;
    }

    private void waitFor(long threadSleep) {
        // Sleep for seconds duration provided in "db-telemetry.thread-sleep" property
        // to allow those threads that were updating previousMap to finish.
        try {
            Thread.sleep(threadSleep);
        } catch (InterruptedException ex) {
            log.error("SLEEP_BEFORE_FLUSH Interrupted proceeding with flush");
        }
    }

    protected SqlTelemetry getSqlTelemetry(String producer, String sanitizedSQL, String sqlWithParameters) {
        return getSqlTelemetry(producer, sanitizedSQL, null, sqlWithParameters);
    }

    protected SqlTelemetry getSqlTelemetry(String producer, String sanitizedSQL, String stacktrace, String sqlWithParameters) {
        SqlTelemetry logInfo = new SqlTelemetry(producer, sanitizedSQL, stacktrace, sqlWithParameters);
        SqlTelemetry existingLogInfo = currentLogInfoMap.putIfAbsent(logInfo, logInfo);

        return Objects.isNull(existingLogInfo) ? logInfo : existingLogInfo;
    }

    private boolean isStackTraceEnabled() {
        return Objects.nonNull(stackTraceExtractor);
    }

}
