package com.intuit.dbtelemetry.sql.aspect;

import com.intuit.dbtelemetry.sql.aspect.aspectj.OracleAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OracleAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;


    @Test
    public void logSQLTestNotEnabled() throws Throwable {
        OracleAspect aspect = new OracleAspect();
        aspect.telemetryPointcut(joinPoint);
    }

    @Test
    public void logSQLTestNoSqlArg() throws Throwable {
        System.setProperty(OracleAspect.class.getSimpleName(), "true");
        OracleAspect aspect = new OracleAspect();
        aspect.telemetryPointcut(joinPoint);
        System.setProperty(OracleAspect.class.getSimpleName(), "false");
    }

}
