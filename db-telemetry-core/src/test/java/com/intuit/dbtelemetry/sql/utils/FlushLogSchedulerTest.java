package com.intuit.dbtelemetry.sql.utils;

import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@RunWith(MockitoJUnitRunner.class)
public class FlushLogSchedulerTest {

    @Mock
    DefaultLogFlush logFlush;

    @Test(expected = IllegalStateException.class)
    public void scheduleTest() {
        FlushLogScheduler fls = new FlushLogScheduler(logFlush,
                Executors.newScheduledThreadPool(2),
                new HashMap<>());
        fls.scheduleTask(FlushLogScheduler.ScheduleType.Telemetry);
        fls.reSchedule(FlushLogScheduler.ScheduleType.Telemetry);
    }

    @Test(expected = IllegalStateException.class)
    public void scheduleTest2() {
        FlushLogScheduler fls = new FlushLogScheduler(logFlush,
                null,
                new HashMap<>());
        fls.scheduleTask(FlushLogScheduler.ScheduleType.Telemetry);
        fls.reSchedule(FlushLogScheduler.ScheduleType.Telemetry);
    }

    @Test
    public void scheduleTestToScheduleTelemetry() {
        Map<FlushLogScheduler.ScheduleType, FlushLogScheduler.ScheduleConfig> cmap = new HashMap<>();
        cmap.put(FlushLogScheduler.ScheduleType.Telemetry, new FlushLogScheduler.ScheduleConfig(2,2));
        Assert.assertNotNull(cmap.get(FlushLogScheduler.ScheduleType.Telemetry));
        cmap.get(FlushLogScheduler.ScheduleType.Telemetry).setInterval(2);
        cmap.get(FlushLogScheduler.ScheduleType.Telemetry).setLoggerSleepTime(2);
        Assert.assertEquals(2, cmap.get(FlushLogScheduler.ScheduleType.Telemetry).getInterval());
        Assert.assertEquals(2, cmap.get(FlushLogScheduler.ScheduleType.Telemetry).getLoggerSleepTime());
        FlushLogScheduler fls = new FlushLogScheduler(logFlush,
                Executors.newScheduledThreadPool(2),
                cmap);
        fls.scheduleTask(FlushLogScheduler.ScheduleType.Telemetry);
        fls.reSchedule(FlushLogScheduler.ScheduleType.Telemetry);
    }

    @Test
    public void scheduleTestToScheduleStackTrace() {
        Map<FlushLogScheduler.ScheduleType, FlushLogScheduler.ScheduleConfig> cmap = new HashMap<>();
        cmap.put(FlushLogScheduler.ScheduleType.StackTrace, new FlushLogScheduler.ScheduleConfig());
        Assert.assertNotNull(cmap.get(FlushLogScheduler.ScheduleType.StackTrace));
        cmap.get(FlushLogScheduler.ScheduleType.StackTrace).setInterval(2);
        cmap.get(FlushLogScheduler.ScheduleType.StackTrace).setLoggerSleepTime(2);
        Assert.assertEquals(2, cmap.get(FlushLogScheduler.ScheduleType.StackTrace).getInterval());
        Assert.assertEquals(2, cmap.get(FlushLogScheduler.ScheduleType.StackTrace).getLoggerSleepTime());
        FlushLogScheduler fls = new FlushLogScheduler(logFlush,
                Executors.newScheduledThreadPool(2),
                cmap);
        fls.scheduleTask(FlushLogScheduler.ScheduleType.StackTrace);
        fls.reSchedule(FlushLogScheduler.ScheduleType.StackTrace);
    }


}
