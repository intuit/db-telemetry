package com.intuit.dbtelemetry.sql.flushlogs;

import com.intuit.dbtelemetry.sql.SqlTelemetry;
import com.intuit.dbtelemetry.sql.aggregator.Aggregator;
import com.intuit.dbtelemetry.sql.finder.WorkflowFinder;
import com.intuit.dbtelemetry.sql.logs.DefaultLogFlush;
import com.intuit.dbtelemetry.sql.logs.TelemetryFlushException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public class DefaultLogFlushSpringScheduler extends DefaultLogFlush {
    private final long threadSleep;

    public DefaultLogFlushSpringScheduler(Aggregator<Map<SqlTelemetry, SqlTelemetry>> aggregator, WorkflowFinder workflowFinder, boolean stackTrace, long threadSleep) {
        super(aggregator, workflowFinder, stackTrace);
        this.threadSleep= threadSleep;
    }

    @Override
    @Async
    @Scheduled(fixedDelayString = "${db-telemetry.stack-trace.flush.interval}")
    public void flushStackTrace() throws TelemetryFlushException {
        super.flushStackTrace(threadSleep);
    }
    @Override
    @Async
    @Scheduled(fixedDelayString = "${db-telemetry.flush.interval}")
    public void flushLogs() throws TelemetryFlushException {
        super.flushLogs(threadSleep);
    }
}
