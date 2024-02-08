package com.intuit.dbtelemetry.sql.aspect.aspectj;

import com.intuit.dbtelemetry.sql.TelemetryRegistry;
import com.intuit.dbtelemetry.sql.SqlTelemetry;
import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.aspect.TelemetryAspect;
import com.intuit.dbtelemetry.sql.extractor.sql.SqlExtractor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
public abstract class AbstractAspectJTelemetryAspect implements TelemetryAspect<ProceedingJoinPoint, Map<SqlTelemetry, SqlTelemetry>> {

    //Constructor to agg and sqlFinder
    private final SqlExtractor<ProceedingJoinPoint> sqlExtractor;

    protected AbstractAspectJTelemetryAspect(SqlExtractor<ProceedingJoinPoint> sqlExtractor) {
        this.sqlExtractor = Objects.requireNonNull(sqlExtractor, "SQL Extractor cannot be null");
    }

    /**
     * This method extracts data from a join point, executes, measures the time taken, and aggregates the data.
     *
     * @param proceedingJoinPoint the join point being advised
     * @return the result object
     * @throws Throwable if an error occurs
     */
    public Object extractAndAggregate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (!isEnabled()) {
            return proceedingJoinPoint.proceed();
        }
        long beforeTime = System.nanoTime();
        Object result = proceedingJoinPoint.proceed();
        try {
            long callTime = (System.nanoTime() - beforeTime) / 1000;
            aggregate(proceedingJoinPoint, callTime);
        } catch (Exception e) {
            log.error("Error in getSqlFinder", e);
        }
        return result;
    }
    /**
     * Pointcut where telemetry data will be collected.
     *
     * @param joinPoint the join point for collected data
     * @return the object from the join point
     * @throws Throwable if an error occurs
     */
    public abstract Object telemetryPointcut(ProceedingJoinPoint joinPoint) throws Throwable;

    /**
     * Aggregates the advice information of the join point and the time taken to execute the join point.
     *
     * @param proceedingJoinPoint the join point being advised
     * @param callTime the time taken to proceed join point
     */
    protected void aggregate(ProceedingJoinPoint proceedingJoinPoint, long callTime) {
        Optional.ofNullable(getAggregator())
                .ifPresent(agg -> {
                    try {
                        getAggregator().aggregate(this.getClass().getName(), getSqlExtractor().extractSql(proceedingJoinPoint), callTime);
                    } catch (Exception e) {
                        log.error("Error in Aggregator", e);
                    }
                });
    }

    /**
     * Checks if this aspect is enabled based on the system property named after the class.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return Optional.ofNullable(System.getProperty(this.getClass().getSimpleName()))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    /**
     * Retrieve the aggregator for this aspect from the Telemetry Registry.
     *
     * @return the aggregator object
     */
    @Override
    public Aggregator<Map<SqlTelemetry, SqlTelemetry>> getAggregator() {
        return TelemetryRegistry.getTelemetryRegistry().getAggregator();
    }

    /**
     * Get the extractor that is responsible for processing SQL in join points.
     *
     * @return the SQL extractor
     */
    @Override
    public SqlExtractor<ProceedingJoinPoint> getSqlExtractor() {
        return this.sqlExtractor;
    }

}
