package com.intuit.dbtelemetry.sql.logs;


public interface LogFlush {
    /**
     * Flushes logs after a specified thread sleep duration.
     *
     * @param threadSleep The sleep duration in milliseconds.
     * @throws TelemetryFlushException if an exception occurs during the log flush.
     */
    void flushLogs(long threadSleep) throws TelemetryFlushException;

    /**
     * Flush logs with a default delay of 5000 milliseconds.
     *
     * @throws TelemetryFlushException if an exception occurs during the log flush.
     */
    default void flushLogs() throws TelemetryFlushException {
        this.flushLogs(5000L);
    }
    /**
     * Flushes stack trace after a specified thread sleep duration.
     *
     * @param threadSleep The sleep duration in milliseconds.
     * @throws TelemetryFlushException if an exception occurs during the log flush.
     */
    void flushStackTrace(long threadSleep) throws TelemetryFlushException;

    /**
     * Flush stack traces with a default delay of 5000 milliseconds.
     *
     * @throws TelemetryFlushException if an exception occurs during the stack trace flush.
     */
    default void flushStackTrace() throws TelemetryFlushException {
        this.flushStackTrace(5000L);
    }
}
