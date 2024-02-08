package com.intuit.dbtelemetry.sql;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Getter
public class SqlTelemetry {
    private final String type;
    private final String sql;
    private int count = 0;
    private long min = 0;
    private long max = 0;
    private long callTime = 0;
    private final String stackTrace;
    private final int hashCode;
    private String workflow = StringUtils.EMPTY;
    private final String sqlWithParameters;

    /**
     * Constructor for creating a SqlTelemetry object.
     *
     * @param type The type of the SQL telemetry.
     * @param sql The SQL query string.
     * @param stackTrace The stack trace.
     * @param sqlWithParameters The SQL query with parameters.
     */
    public SqlTelemetry(String type, String sql, String stackTrace, String sqlWithParameters) {
        this.type = StringUtils.defaultString(type, StringUtils.EMPTY);
        this.sql = StringUtils.defaultString(sql, StringUtils.EMPTY);
        this.stackTrace = StringUtils.defaultString(stackTrace, StringUtils.EMPTY);
        this.hashCode = Objects.hash(type, sql, stackTrace);
        this.sqlWithParameters = sqlWithParameters;
    }

    /**
     * Increases the count of this telemetry instance and
     * updates the max, min, and call time based on the given time.
     *
     * @param time The call time for a SQL query.
     */
    public void setInstanceValue(long time) {
        max = Math.max(max, time);
        min = Math.min(min, time);
        callTime += time;
        count++;
    }

    /**
     * Overrides the standard toString method to provide a comprehensive string
     * representation of the SqlTelemetry object.
     *
     * @return string representation of the object
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("DbTelemetry { ");
        result.append(" telemetryHash=\"").append(hashCode).append("\",");
        result.append(" type=").append(type).append("\",");
        result.append(" count=").append(count).append("\",");
        result.append(" min=").append(min).append("\",");
        result.append(" max=").append(max).append("\",");
        result.append(" callTime=").append(callTime).append("\",");
        result.append(" avg=").append(getAverageCallTime()).append("\",");
        result.append(" sql=\"").append(sql).append("\",");
        result.append(" sqlHash='").append(sql.hashCode()).append("\",");
        result.append(" stacktraceHash=").append(stackTrace.hashCode()).append("\",");
        result.append(" workflow=\"").append(workflow).append("\"");
        if (!ObjectUtils.isEmpty(sqlWithParameters)) {
            result.append(" sqlWithParameter=\"").append(sqlWithParameters).append("\"");
        }
        result.append(" }");

        return result.toString();
    }

    /**
     * Overrides default equals method - checks equality based on the type, SQL, and stack trace.
     *
     * @param o Object to be compared for equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTelemetry that = (SqlTelemetry) o;
        return type.equals(that.type) &&
                sql.equals(that.sql) &&
                stackTrace.equals(that.stackTrace);
    }

    /**
     * Overrides default hashCode method - returns hashCode of this class instance.
     *
     * @return specific hashCode value of this class instance
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * Returns the average call time for an SQL query.
     *
     * @return average call time as a double
     */
    protected double getAverageCallTime() {
        return (callTime == 0 || count == 0) ? 0D : (double) callTime / count;
    }

    /**
     * Sets the workflow for the SQL telemetry instance.
     *
     * @param workflow Workflow string.
     */
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

}
