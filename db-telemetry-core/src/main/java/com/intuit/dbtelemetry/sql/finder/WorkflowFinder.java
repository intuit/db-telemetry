package com.intuit.dbtelemetry.sql.finder;

public interface WorkflowFinder {
    /**
     * Extracts the workflow name from the given stack trace string.
     *
     * @param stackTraceString the stack trace string to extract the workflow name from
     * @return the workflow name, or null if the workflow name could not be extracted
     */
    String getWorkflowName(String stackTraceString);
}
