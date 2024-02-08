package com.intuit.dbtelemetry.sql.extractor.stacktrace;

public interface StackTraceExtractor {
    /**
     * Extracts the stack trace as a string from the given Throwable object.
     *
     * @param throwable the Throwable object to extract the stack trace from
     * @return the stack trace as a string, or null if the stack trace could not be extracted
     */
    String extractStackTrace(Throwable throwable);

}