package com.intuit.dbtelemetry.sql.extractor.sql.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class OracleAspectJSqlExtractor extends AbstractAspectJSqlExtractor {

    private static final String[] WRAPPER_FIELDS = new String[]{"statement", "sqlObject", "originalSql"};
    private static final String[] NON_WRAPPER_FIELDS = new String[]{"sqlObject", "originalSql"};
    private static final String WRAPPER_TYPE = "Wrapper";

    /**
     * Returns an array of nested field names to extract the SQL statement from the target object.
     *
     * @param targetClassName the name of the class of the target object
     * @return an array of nested field names to extract the SQL statement from the target object
     */
    @Override
    public String[] getNestedFieldNames(String targetClassName) {
        return isWrapperType(targetClassName) ? WRAPPER_FIELDS : NON_WRAPPER_FIELDS;
    }

    private boolean isWrapperType(String targetClassName) {
        return StringUtils.contains(targetClassName, WRAPPER_TYPE);
    }

}
