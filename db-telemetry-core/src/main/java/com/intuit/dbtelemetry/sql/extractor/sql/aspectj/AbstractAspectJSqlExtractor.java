package com.intuit.dbtelemetry.sql.extractor.sql.aspectj;

import com.intuit.dbtelemetry.sql.extractor.sql.SqlExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractAspectJSqlExtractor implements SqlExtractor<ProceedingJoinPoint> {
    /**
     * Extracts the SQL statement from the given join point.
     *
     * @param joinPoint the join point to extract the SQL statement from
     * @return the SQL statement, or null if the statement could not be extracted
     */
    @Override
    public String extractSql(ProceedingJoinPoint joinPoint) {
        String sql = null;
        try {
            Object target = getJoinPointTarget(joinPoint);
            if (Objects.nonNull(target)) {
                String className = target.getClass().getName();
                sql = extractSqlFromTarget(target, getNestedFieldNames(className));
            }
        } catch (IllegalAccessException e) {
            log.warn("Unable to extract sql", e);
        } catch (Exception e) {
            log.warn("Unexpected error", e);
        }
        return sql;
    }
    /**
     * Returns an array of nested field names to extract the SQL statement from the target object.
     *
     * @param targetClassName the name of the class of the target object
     * @return an array of nested field names to extract the SQL statement from the target object
     */
    public abstract String[] getNestedFieldNames(String targetClassName);

    /**
     * Returns the position of the argument that contains the target object.
     *
     * @return the position of the argument that contains the target object
     */
    protected int getArgsPosition() {
        return -1;
    }

    private Object getJoinPointTarget(ProceedingJoinPoint joinPoint) {
        if (isArgBasedTarget()) {
            return Optional.ofNullable(joinPoint.getTarget());
        }

        Object[] args = joinPoint.getArgs();
        if (Objects.nonNull(args) && args.length > getArgsPosition()) {
            Object result = args[getArgsPosition()];
            if (Objects.nonNull(result))
                return result;
        }
        return null;
    }

    private boolean isArgBasedTarget() {
        return getArgsPosition() == -1;
    }

    private String extractSqlFromTarget(Object target, String[] nestedFieldNames) throws IllegalAccessException {
        Object resultObject = readField(target, nestedFieldNames);
        if (Objects.isNull(resultObject)){
            return null;
        }
        String resultString = resultObject.toString();
        return StringUtils.isEmpty(resultString)?null:resultString;
    }

    private Object readField(Object target, String[] fieldNames) throws IllegalAccessException {
        Object fieldValue = null;
        for (String fieldName : fieldNames) {
            fieldValue = readField(target, fieldName);
        }
        return fieldValue;
    }

    private Object readField(Object target, String fieldName) throws IllegalAccessException {
        return FieldUtils.readField(target, fieldName, true);
    }
}
