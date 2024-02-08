package com.intuit.dbtelemetry.sql;

import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.aggregator.MapBasedAggregator;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.DepthFilterStackTraceExtractor;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.StackTraceExtractor;
import com.intuit.dbtelemetry.sql.finder.WorkflowFinder;
import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.flushlogs.DefaultLogFlushSpringScheduler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.*;

@Configuration
@ComponentScan(basePackages = {"com.intuit.dbtelemetry"})
@EnableScheduling
@EnableAsync
public class DbTelemetryConfig {
    @Bean
    public StackTraceExtractor getStackTraceFinder(@Value("${db-telemetry.stack-trace.filter:#{null}}") String stacktraceFilter,
                                                   @Value("${db-telemetry.stack-trace.depth:-1}") int stacktraceDepth){
        return new DepthFilterStackTraceExtractor(stacktraceDepth,stacktraceFilter);
    }
    @Bean
    public Aggregator<Map<SqlTelemetry, SqlTelemetry>> getAggregator(@Value("${db-telemetry.excluded-tables:#{null}}") String excludedTables, StackTraceExtractor stackTraceExtractor){
        List<String> excludedTablesList = Objects.isNull(excludedTables)?new ArrayList<>():Arrays.asList(StringUtils.stripAll(excludedTables.split(",")));
        return new MapBasedAggregator(excludedTablesList, stackTraceExtractor);
    }
    @Bean
    public LogFlush getLogFlush(Aggregator<Map<SqlTelemetry, SqlTelemetry>> mapBasedAggregator,
                                @Autowired(required = false) WorkflowFinder workflowFinder,
                                @Value("${db-telemetry.stack-trace.enabled:true}") boolean stacktrace,
                                @Value("${db-telemetry.thread-sleep:1000}") long threadSleep){
        return new DefaultLogFlushSpringScheduler(mapBasedAggregator,workflowFinder,stacktrace,threadSleep);
    }
    @Bean
    public TelemetryRegistry getDbTelemetryRegistry(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator,
                                                    LogFlush logFlush,
                                                    StackTraceExtractor stackTraceExtractor,
                                                    @Value("${db-telemetry.enable-aspect:#{null}}") String enableAspect,
                                                    @Value("${db-telemetry.disabled-aspect:#{null}}") String disabledAspect){
        TelemetryRegistry telemetryRegistry = TelemetryRegistryBuilder.builder().withAggregator(aggregator)
                .withLogFlush(logFlush).withStackTraceExtractor(stackTraceExtractor).build();
        TelemetryRegistry.setTelemetryRegistry(telemetryRegistry);
        if(Objects.nonNull(enableAspect)) {
            for (String aspect: enableAspect.split(",")) {
                TelemetryRegistry.getTelemetryRegistry().aspectState(aspect,"true");
            }
        }
        if(Objects.nonNull(disabledAspect)) {
            for (String aspect: disabledAspect.split(",")) {
                TelemetryRegistry.getTelemetryRegistry().aspectState(aspect,"false");
            }
        }
        return telemetryRegistry;
    }

}
