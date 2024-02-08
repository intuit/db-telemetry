package com.intuit.dbtelemetry.sql.aspect.aspectj;

import com.intuit.dbtelemetry.sql.extractor.sql.aspectj.PostgresAspectJSqlExtractor;
import com.intuit.dbtelemetry.sql.extractor.sql.SqlExtractor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class PostgresAspect extends AbstractAspectJTelemetryAspect {

    /**
     * Constructor for creating a PostgresAspect object with a default PostgresAspectJSqlExtractor.
     */
    public PostgresAspect() {
        this(new PostgresAspectJSqlExtractor());
    }

    /**
     * Constructor for creating a PostgresAspect object with a provided SqlExtractor.
     *
     * @param sqlExtractor The SqlExtractor to utilize, must not be null.
     */
    public PostgresAspect(SqlExtractor<ProceedingJoinPoint> sqlExtractor) {
        super(sqlExtractor);
    }

    /**
     * Pointcut for the execution of Postgres SQL statements.
     * This method gets invoked when a SQL execute method is called in Postgres and data is
     * extracted and aggregated.
     *
     * @param proceedingJoinPoint Provides metadata about this specific join point, such as the method being executed,
     *                            its arguments, or its target object.
     * @return The execution result of the join point, which can be replaced or altered by the aspect.
     * @throws Throwable if an error occurs during the method execution or advice handling
     */
    @Around("within(java.sql.Statement+) && execution(* org.postgresql.jdbc.PgStatement.execute(org.postgresql.core.CachedQuery,..))")
    public Object telemetryPointcut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return extractAndAggregate(proceedingJoinPoint);
    }

}
