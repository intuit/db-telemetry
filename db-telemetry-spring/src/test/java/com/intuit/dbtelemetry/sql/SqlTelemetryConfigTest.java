package com.intuit.dbtelemetry.sql;

import org.junit.Assert;
import org.junit.Test;

public class SqlTelemetryConfigTest {

    @Test
    public void getEnableAspectNull(){
        DbTelemetryConfig c = new DbTelemetryConfig();
        Assert.assertNotNull(c.getStackTraceFinder("",0));
        Assert.assertNotNull(c.getAggregator(null,null));
        Assert.assertNotNull(c.getAggregator("a,b",null));
        Assert.assertNotNull(c.getLogFlush(null,null,false,1));
        Assert.assertNotNull(c.getDbTelemetryRegistry(null,null,null,null,null));
        Assert.assertNotNull(c.getDbTelemetryRegistry(null,null,null,"test1,test2","test2"));
    }

}
