package com.intuit.dbtelemetry.sql.extractor.stacktrace;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;
import java.util.Optional;

public class DepthFilterStackTraceExtractor implements StackTraceExtractor {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String DB_TELEMETRY_PACKAGE = "com.intuit.sbg.psp.dbtelemetry";
    private int depth;
    private final Optional<String> filter;

    /**
     * Constructs a DepthFilterStackTraceExtractor object with the given depth and filter string.
     *
     * @param depth the maximum depth of the stack trace to include, or -1 to include all levels
     * @param filter the filter string to exclude stack trace lines that doesn't contain this string
     */
    public DepthFilterStackTraceExtractor(int depth, String filter) {
        this.depth = depth;
        this.filter = Optional.of(StringUtils.isEmpty(filter) ? StringUtils.EMPTY : filter);
    }

    /**
     * Extracts the stack trace as a string from the given Throwable object, filtered by the depth and filter string
     * provided in the constructor.
     *
     * @param throwable the Throwable object to extract the stack trace from
     * @return the filtered stack trace as a string, or null if the stack trace could not be extracted
     */
    public String extractStackTrace(Throwable throwable) {
        if (Objects.isNull(throwable)) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        result.append(LINE_SEPARATOR);
        String[] stackTraceElements = ExceptionUtils.getStackFrames(throwable);
        for (int i = stackTraceElements.length - 1; i > 0 && (depth == -1 || depth > 0); i--) {
            String stack = stackTraceElements[i];
            if (shouldInclude(stack)) {
                if (depth != -1) depth--;
                result.append(stack);
                result.append(LINE_SEPARATOR);
            }
        }

        return result.toString();
    }

    private boolean shouldInclude(String stack) {
        return filter
                .map(f -> stack.contains(f) && !stack.contains(DB_TELEMETRY_PACKAGE))
                .orElse(true);
    }
}
