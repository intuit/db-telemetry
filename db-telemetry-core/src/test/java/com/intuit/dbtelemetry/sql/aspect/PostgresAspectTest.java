package com.intuit.dbtelemetry.sql.aspect;

import com.intuit.dbtelemetry.sql.aspect.aspectj.PostgresAspect;
import com.intuit.dbtelemetry.sql.utils.SqlHelperUtilsTests;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostgresAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Test
    public void logSQLTestNotEnabled() throws Throwable {
        PostgresAspect aspect = new PostgresAspect();
        aspect.telemetryPointcut(joinPoint);
    }

    @Test
    public void logSQLTestNoSqlArg() throws Throwable {
        System.setProperty(PostgresAspect.class.getSimpleName(), "true");
        PostgresAspect aspect = new PostgresAspect();
        aspect.telemetryPointcut(joinPoint);
        System.setProperty(PostgresAspect.class.getSimpleName(), "false");
    }

    @Test
    public void logSQLTestSqlArg() throws Throwable {
        System.setProperty(PostgresAspect.class.getSimpleName(), "true");
        PostgresAspect aspect = new PostgresAspect();
        Object[] args = new Object[2];
        args[0] = SqlHelperUtilsTests.insertQuery;
        when(joinPoint.getArgs()).thenReturn(args);
        aspect.telemetryPointcut(joinPoint);
        System.setProperty(PostgresAspect.class.getSimpleName(), "false");
    }

    @Test
    public void logSQLTestSqlArgObject() throws Throwable {
        System.setProperty(PostgresAspect.class.getSimpleName(), "true");
        PostgresAspect aspect = new PostgresAspect();
        Object[] args = new Object[2];
        args[0] = new TestObject();
        Assert.assertNotNull(((TestObject) args[0]).query);
        when(joinPoint.getArgs()).thenReturn(args);
        aspect.telemetryPointcut(joinPoint);
        System.setProperty(PostgresAspect.class.getSimpleName(), "false");
    }

    public static class TestObject {
        public final String query = SqlHelperUtilsTests.insertQuery;
    }

}
