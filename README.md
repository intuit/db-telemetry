# db-telemetry
DB Telemetry provides the sql query telemetry to measure and improve the query quality, and performance. It uses the AOP (using AspectJ aspect) to get the query, query execution time and stacktrace. These stats get consolidated as one log per query and workflow (if stack trace enabled) for the time interval provided in configuration. Consolidated stats get logged periodically, so the logger is not getting overwhelmed.
The DB telemetry have 2 versions :- 
- spring ([db-telemetry-spring]) - This can be used with the spring project. Need to do some configuration to plug the telemetry.
- non spring ([db-telemetry-core]) - For non-spring application but aop.xml and scheduling need to be taken care by consumer. 

### Alternative
1. ORM show-sql - This provides all the queries with the execution time but with huge number of traffic the logger gets overwhelmed.
2. Database side stats - On database also we can get those stats, but we need to do map reduce to find out the average and max time taken. 

## Configure "db-telemetry-spring"
- Add the library as the dependency of the spring project. 
    ```
    <dependency>
        <groupId>com.intuit</groupId>
        <artifactId>db-telemetry-spring</artifactId>
        <version>${db-telemetry.version}</version>
    </dependency>
    ```
- Provide the config for the telemetry.
    ```
    db-telemetry.flush.interval = 10000 {logging intervel}
    db-telemetry.enable-aspect = C3POAspect,OracleAspect {which aspect to enable}
    db-telemetry.thread-sleep = 5000 {cooldown time before logging start}
    db-telemetry.stack-trace.enabled = true {to enable the stack trace}
    db-telemetry.stack-trace.flush.interval = 60000 {stack trace logging intervel}
    db-telemetry.stack-trace.filter = com.intuit {filter to remove the unwanterd stacktrace logs}
    db-telemetry.stack-trace.depth = -1 {depth of the stack trace (-1 means all)}
    db-telemetry.excluded-tables = table1,temp2
    ```
- Load the config file in your context vai importing the telemetry config or adding the component scan.
    ```
    import org.springframework.context.annotation.Import;
    import com.intuit.dbtelemetry.sql.DbTelemetryConfig;
    import org.springframework.context.annotation.ComponentScan;
    ....
    @Import({DbTelemetryConfig.class})
    //OR 
    @ComponentScan(basePackages = {"com.intuit.dbtelemetry"})
    ```
- Add the aspectjwever.jar as the java agent. 
    ```
    RUN curl -o /app/contrast/javaagent/aspectjweaver.jar https://artifact.intuit.com/artifactory/maven-proxy-cache/org/aspectj/aspectjweaver/1.8.13/aspectjweaver-1.8.13.jar
    aspectjweaver_jar=/app/contrast/javaagent/aspectjweaver.jar
    JAVA_OPTS="${JAVA_OPTS} -javaagent:${aspectjweaver_jar}"
    JAVA_OPTS="${JAVA_OPTS} -Dorg.aspectj.weaver.loadtime.configuration=META-INF/dbTelemetryaop.xml" 
    ```
- Example Logs.
    Aspect Registered logs
    ```
    [AppClassLoader@251a69d7] info using configuration file:/Users/shivay/.m2/repository/com/intuit/sbg/psp/db-telemetry-spring/1.1.6/db-telemetry-spring-  1.1.6.jar!/META-INF/dbTelemetryaop.xml
    [AppClassLoader@251a69d7] info register aspect aspect.com.intuit.dbtelemetry.C3POAspect
    [AppClassLoader@251a69d7] info register aspect aspect.com.intuit.dbtelemetry.OracleAspect
    [AppClassLoader@251a69d7] info register aspect aspect.com.intuit.dbtelemetry.PostgresAspect
    ```
    Aspect Applied logs (postgres example)
    ```
    [AppClassLoader@251a69d7] weaveinfo Join point 'method-execution(void org.postgresql.jdbc.PgStatement.execute(org.postgresql.core.CachedQuery, org.postgresql.core.ParameterList, int))' in Type 'org.postgresql.jdbc.PgStatement' (PgStatement.java:411) advised by around advice from 'aspect.com.intuit.dbtelemetry.PostgresAspect' (PostgresAspect.java)
    ```

## Configure "db-telemetry-core"
- Add [aop.xml] file in your "resource/META-INF" folder. 
- Add the library as the dependency of the spring project. 
    ```
    <dependency>
        <groupId>com.intuit</groupId>
        <artifactId>db-telemetry-core</artifactId>
        <version>${db-telemetry.version}</version>
    </dependency>
    ```
- Set system property to enable the specific type of Aspect
    ```
    System.setProperty("PostgresAspect","true");
    OR
    System.setProperty("OracleAspect","true");
    ```

- Configure the consolidator (One time configuration)
    ```
    TelemetryRegistry telemetryRegistry = TelemetryRegistryBuilder.builder()
                .withStackTraceDepth(-1)
                .withStackTraceFilter("com.intuit")
                .withExcludedTables(Arrays.asList("INTUIT_TEMP"))
                .withStackTraceLogging(true)
                .build();
    TelemetryRegistry.setTelemetryRegistry(telemetryRegistry);
    ```
    
- Schedule the flush (One time configuration) vai provided scheduler or your own.
    ```
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledFuture;
    
    Map<FlushLogScheduler.ScheduleType, FlushLogScheduler.ScheduleConfig> scheduleConfigMap = new HashMap<>();
    scheduleConfigMap.put(FlushLogScheduler.ScheduleType.Telemetry,new FlushLogScheduler.ScheduleConfig(10000,1000));
    scheduleConfigMap.put(FlushLogScheduler.ScheduleType.StackTrace,new FlushLogScheduler.ScheduleConfig(100000,1000));
    FlushLogScheduler flushLogScheduler = new FlushLogScheduler(, Executors.newScheduledThreadPool(2) ,scheduleConfigMap);
    flushLogScheduler.scheduleTask(FlushLogScheduler.ScheduleType.Telemetry);
    flushLogScheduler.scheduleTask(FlushLogScheduler.ScheduleType.StackTrace);
    ```
    OR call periodically following method
    ```
    TelemetryRegistry.getTelemetryRegistry().getLogFlush().flushLogs(1000);
    TelemetryRegistry.getTelemetryRegistry().getLogFlush().flushStackTrace(10000);
    ```

[aop.xml]: <./db-telemetry-spring/src/main/resources/META-INF/aop.xml>
[db-telemetry-core]: <./db-telemetry-core> 
[db-telemetry-spring]: <./db-telemetry-spring>
