package com.intuit.dbtelemetry.sql.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

public class SqlHelperUtils {
    private static final Pattern SANITIZE_SQL_PATTERN = Pattern.compile("\\b[0-9]+");
    private static final String REGEX_ESCAPE_A_CHARACTER = "\\\\$0";
    private final static Pattern PATTERN_REGEX_CHARS_TO_ESCAPE = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    private final static Pattern IN_PATTERN = Pattern.compile("(\\((\\?,\\s)*\\?\\))");

    private static String replace(Pattern pattern, String string, String substr) {
        return Optional.ofNullable(string)
                .map(str -> pattern.matcher(str).replaceAll(substr))
                .orElse(StringUtils.EMPTY);
    }

    // Removes content between delimiters.  Can be used to strip the content of quoted literals.
    //
    // Examples:
    // SqlHelperUtils.stripDelimitedStrings("UPDATE foo SET bar='hello'", "'", "'", "") returns "UPDATE foo SET bar="
    // SqlHelperUtils.stripDelimitedStrings("UPDATE foo SET bar='O''Mally'", "'", "'", "") returns "UPDATE foo SET bar="
    // SqlHelperUtils.stripDelimitedStrings("UPDATE foo /*erase*/ SET bar='foo'","/*", "*/", " ") returns "UPDATE foo   SET bar='foo'"
    //
    private static String replaceDelimitedStrings(String s, String delimStart, String delimEnd, String replace) {
        if (StringUtils.isEmpty(s)) {
            return StringUtils.EMPTY;
        }

        // Escape the parameters sent in as they will be used as part of the regex.
        String start = PATTERN_REGEX_CHARS_TO_ESCAPE.matcher(delimStart).replaceAll(REGEX_ESCAPE_A_CHARACTER);
        String end = PATTERN_REGEX_CHARS_TO_ESCAPE.matcher(delimEnd).replaceAll(REGEX_ESCAPE_A_CHARACTER);

        // Compile the pattern to use with replaceAll - Pattern.DOTALL means . matches line break
        Pattern pattern = Pattern.compile(start + ".*?" + end, Pattern.DOTALL);

        // Replace and return the result.
        return pattern.matcher(s).replaceAll(replace);
    }

    public static String sanitizeSQL(String sql) {
        String result = replace(SANITIZE_SQL_PATTERN, sql, "?"); //remove all numeric literals
        result = replaceDelimitedStrings(result, "'", "'", "?"); //quoted strings
        result = replaceDelimitedStrings(result, "[", "]", "?"); //IN
        result = replace(IN_PATTERN, result, "(?)");
        return result;
    }
}
