package com.intuit.dbtelemetry.sql.aspect.aspectj;

import com.intuit.dbtelemetry.sql.extractor.sql.aspectj.OracleAspectJSqlExtractor;
import com.intuit.dbtelemetry.sql.extractor.sql.SqlExtractor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class OracleAspect extends AbstractAspectJTelemetryAspect {
    /**
     * Default constructor for OracleAspect.
     * Installs a new OracleAspectJSqlExtractor for extracting SQL data from Oracle.
     */
    public OracleAspect() {
        this(new OracleAspectJSqlExtractor());
    }
    /**
     * Overloaded constructor for OracleAspect.
     * Allows a custom SqlExtractor to be used for extracting SQL data from Oracle.
     *
     * @param sqlExtractor The SqlExtractor to use.
     */
    public OracleAspect(SqlExtractor<ProceedingJoinPoint> sqlExtractor) {
        super(sqlExtractor);
    }
    /**
     * Specifies the AspectJ pointcut to monitor. In this case, execution of any Oracle SQL statement
     * encapsulated by the oracle.jdbc.driver. Upon invocation, telemetry data is extracted and aggregated.
     *
     * @param proceedingJoinPoint The JoinPoint encapsulating the method to be invoked and monitored.
     * @return The result of the method invocation.
     * @throws Throwable If any error occurs during the method invocation or the telemetry extraction/aggregation process.
     */
    @Around("within(java.sql.Statement+) && execution(* oracle.jdbc.driver..*.execute*(..))")
    public Object telemetryPointcut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return extractAndAggregate(proceedingJoinPoint);
    }

}
