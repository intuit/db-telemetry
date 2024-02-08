package com.intuit.dbtelemetry.sql.extractor.sql.aspectj;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgresAspectJSqlExtractor extends AbstractAspectJSqlExtractor {

    private static final String[] QUERY_FIELD_NAMES = new String[]{"query"};

    /**
     * Returns an array of nested field names to extract the SQL statement from the target object.
     *
     * @param targetClassName the name of the class of the target object
     * @return an array of nested field names to extract the SQL statement from the target object
     */
    @Override
    public String[] getNestedFieldNames(String targetClassName) {
        return QUERY_FIELD_NAMES;
    }

    @Override
    protected int getArgsPosition() {
        return 0;
    }
}
