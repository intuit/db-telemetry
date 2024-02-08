package com.intuit.dbtelemetry.sql;

import com.intuit.dbtelemetry.sql.aggregator.MapBasedAggregator;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.DepthFilterStackTraceExtractor;
import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class TelemetryRegistryBuilderTest {

    @Test
    public void testBuilder() {
        TelemetryRegistry result = TelemetryRegistryBuilder.builder()
                .withAggregator(new MapBasedAggregator())
                .withLogFlush(new DefaultLogFlush(null, null, false))
                .withExcludedTables(new ArrayList<>())
                .withStackTraceDepth(0)
                .withStackTraceFilter(null)
                .withStackTraceExtractor(new DepthFilterStackTraceExtractor(0, ""))
                .withStackTraceLogging(false)
                .withWorkflowFinder(null)
                .withStackTraceLogging(false)
                .build();
        Assert.assertNotNull(result.getLogFlush());
        Assert.assertNotNull(result.getAggregator());
        result.aspectState("test", "true");
    }

    @Test
    public void testBuilder2() {
        TelemetryRegistry result = TelemetryRegistryBuilder.builder()
                .withLogFlush(new DefaultLogFlush(null, null, false))
                .withExcludedTables(new ArrayList<>())
                .withStackTraceDepth(0)
                .withStackTraceFilter(null)
                .withStackTraceExtractor(new DepthFilterStackTraceExtractor(0, ""))
                .withStackTraceLogging(false)
                .withWorkflowFinder(null)
                .withStackTraceLogging(false)
                .build();
        Assert.assertNotNull(result.getLogFlush());
        Assert.assertNotNull(result.getAggregator());
        result.aspectState("test", "true");
    }

}
