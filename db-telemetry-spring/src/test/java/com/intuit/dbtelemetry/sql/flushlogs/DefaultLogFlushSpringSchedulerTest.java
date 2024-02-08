package com.intuit.dbtelemetry.sql.flushlogs;

import com.intuit.dbtelemetry.sql.aggregator.TelemetryAggregatorException;
import com.intuit.dbtelemetry.sql.aggregator.MapBasedAggregator;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.DepthFilterStackTraceExtractor;
import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.logs.TelemetryFlushException;
import org.junit.Test;

public class DefaultLogFlushSpringSchedulerTest {

    @Test
    public void test() throws TelemetryFlushException, TelemetryAggregatorException {
        MapBasedAggregator aggregator = new MapBasedAggregator(null,new DepthFilterStackTraceExtractor(-1, "com.intuit"));
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        LogFlush mlf = new DefaultLogFlushSpringScheduler(aggregator, null, true,1);
        mlf.flushLogs();
        mlf.flushStackTrace();
    }


}
