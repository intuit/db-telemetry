package com.intuit.dbtelemetry.sql.extractor.stacktrace;

import org.junit.Assert;
import org.junit.Test;

public class DepthFilterStackTraceExtractorTests {

    @Test
    public void getStackTrace() {
        String s = new DepthFilterStackTraceExtractor(-1, "java.lang").extractStackTrace(new Exception());
        Assert.assertNotNull(s);
        Assert.assertTrue(s.contains("java.lang"));
    }

    @Test
    public void getStackTraceNoDepth() {
        String s = new DepthFilterStackTraceExtractor(0, "java.lang").extractStackTrace(new Exception());
        Assert.assertEquals(DepthFilterStackTraceExtractor.LINE_SEPARATOR, s);
    }

    @Test
    public void getStackTraceNoDepth1() {
        String s = new DepthFilterStackTraceExtractor(1, "java.lang").extractStackTrace(new Exception());
        Assert.assertNotNull(s);
        Assert.assertTrue(s.contains("java.lang"));
    }
}
