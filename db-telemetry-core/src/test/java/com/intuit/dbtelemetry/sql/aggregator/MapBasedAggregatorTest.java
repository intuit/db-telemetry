package com.intuit.dbtelemetry.sql.aggregator;

import com.intuit.dbtelemetry.sql.SqlTelemetry;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.DepthFilterStackTraceExtractor;
import com.intuit.dbtelemetry.sql.extractor.stacktrace.StackTraceExtractor;
import com.intuit.dbtelemetry.sql.logs.TelemetryFlushException;
import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(MockitoJUnitRunner.class)
public class MapBasedAggregatorTest {

    @Mock
    StackTraceExtractor stackTraceExtractor;

    @Test
    public void aggregateDefaultTest() throws TelemetryAggregatorException {
        MapBasedAggregator aggregator = new MapBasedAggregator();
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        ConcurrentHashMap<SqlTelemetry, SqlTelemetry> result = (ConcurrentHashMap<SqlTelemetry, SqlTelemetry>) aggregator.getAndResetDBTelemetryData(0);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        SqlTelemetry telemetry = result.keys().nextElement();
        Assert.assertEquals(1, telemetry.getCount());
        Assert.assertEquals(sql, telemetry.getSql());
        telemetry.setWorkflow("test");
        Assert.assertEquals("test", telemetry.getWorkflow());
        Assert.assertEquals("DbTelemetry {  telemetryHash=\"-1411642067\", type=test\", count=1\", min=0\", max=10\", callTime=10\", avg=10.0\", sql=\"SQL TEST\", sqlHash='-294336572\", stacktraceHash=0\", workflow=\"test\" }", telemetry.toString());

    }

    @Test
    public void aggregateWithMapLogFlushTest() throws TelemetryAggregatorException, TelemetryFlushException {
        MapBasedAggregator aggregator = new MapBasedAggregator(null,new DepthFilterStackTraceExtractor(-1, "com.intuit"));
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        LogFlush mlf = new DefaultLogFlush(aggregator, null, true);
        mlf.flushLogs();
        mlf.flushStackTrace();
    }

    @Test
    public void aggregateSameSqlDefaultTest() throws TelemetryAggregatorException {
        MapBasedAggregator aggregator = new MapBasedAggregator();
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        aggregator.aggregate("test", sql, 10);
        ConcurrentHashMap<SqlTelemetry, SqlTelemetry> result = (ConcurrentHashMap)aggregator.getAndResetDBTelemetryData(0);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        SqlTelemetry telemetry = result.keys().nextElement();
        Assert.assertEquals(2, telemetry.getCount());
        Assert.assertEquals(sql, telemetry.getSql());
    }

    @Test
    public void aggregateExcludedTablesDefaultTest() throws TelemetryAggregatorException {
        List<String> etl = Arrays.asList("SQL");
        MapBasedAggregator aggregator = new MapBasedAggregator(etl);
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        sql = "sql TEST";
        aggregator.aggregate("test", sql, 10);
        sql = "Test sql TEST";
        aggregator.aggregate("test", sql, 10);
        sql = "Testsql TEST";
        aggregator.aggregate("test", sql, 10);
        sql = "Test sqlTEST";
        aggregator.aggregate("test", sql, 10);
        sql = "Test TEST sql";
        aggregator.aggregate("test", sql, 10);
        ConcurrentHashMap<SqlTelemetry, SqlTelemetry> result = (ConcurrentHashMap<SqlTelemetry, SqlTelemetry>) aggregator.getAndResetDBTelemetryData(0);
        Assert.assertNull(result);
    }

    @Test
    public void aggregateStackTraceFinderTest() throws TelemetryAggregatorException {
        MapBasedAggregator aggregator = new MapBasedAggregator(null,new DepthFilterStackTraceExtractor(-1, "com.intuit"));
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
        ConcurrentHashMap<SqlTelemetry, SqlTelemetry> result = (ConcurrentHashMap<SqlTelemetry, SqlTelemetry>) aggregator.getAndResetDBTelemetryData(0);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        SqlTelemetry telemetry = result.keys().nextElement();
        Assert.assertEquals(1, telemetry.getCount());
        Assert.assertEquals(sql, telemetry.getSql());
    }

    @Test(expected = TelemetryAggregatorException.class)
    public void aggregateStackTraceFinderExceptionTest() throws TelemetryAggregatorException {
        MapBasedAggregator aggregator = new MapBasedAggregator(null,stackTraceExtractor);
        Mockito.when(stackTraceExtractor.extractStackTrace(Mockito.any())).thenThrow(new IllegalStateException());
        String sql = "SQL TEST";
        aggregator.aggregate("test", sql, 10);
    }


}
