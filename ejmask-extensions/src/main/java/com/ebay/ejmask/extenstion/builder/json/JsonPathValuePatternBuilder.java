package com.ebay.ejmask.extenstion.builder.json;

import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Builds regex patterns and replacements from JSONPath expressions used for masking JSON values.
 *
 * <p>Supported expressions:
 * <ul>
 *   <li>Filter predicate: {@code $.path[?(@.f1=='v1' && @.f2=='v2')].target}</li>
 *   <li>Simple field: {@code $.path.to.field}</li>
 * </ul>
 *
 * <p>Each expression contributes 3 capture groups in this order:
 * <ol>
 *   <li>JSON prefix plus visible characters of the value</li>
 *   <li>masked remainder of the value</li>
 *   <li>closing quote</li>
 * </ol>
 *
 * <p>When multiple expressions are provided, sub-patterns are joined with alternation.
 * Group indexes for expression {@code i} are {@code 3*i+1}, {@code 3*i+2}, and {@code 3*i+3}.
 */
public class JsonPathValuePatternBuilder extends AbstractRegexPatternBuilder {

    private static final String SKIP = "\\s*";
    private static final Pattern FILTER_EXTRACT = Pattern.compile("\\[\\?\\((.+?)\\)]");
    private static final Pattern CONDITION_EXTRACT = Pattern.compile("@\\.(\\w+)\\s*==\\s*['\"]([^'\"]+)['\"]");

    /**
     * Builds a combined regex pattern from one or more JSONPath expressions.
     *
     * @param visibleCharacters number of leading value characters to keep visible (0 means full mask)
     * @param fieldNames one or more JSONPath expressions
     * @return combined regex pattern string
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 0 || fieldNames == null || fieldNames.length == 0) {
            throw new IllegalArgumentException("At least one JSONPath expression is required");
        }
        List<String> subPatterns = new ArrayList<>();
        for (String jsonPath : fieldNames) {
            if (jsonPath == null || jsonPath.isEmpty()) {
                throw new IllegalArgumentException("JSONPath expression must not be blank");
            }
            subPatterns.add(buildSubPattern(jsonPath.trim(), visibleCharacters));
        }
        return subPatterns.stream()
                .map(p -> "(?:" + p + ")")
                .collect(Collectors.joining("|"));
    }

    /**
     * Builds the replacement string for the configured expressions.
     *
     * <p>The replacement keeps each expression's prefix and closing quote and inserts
     * {@code ****} between them.
     *
     * @param visibleCharacters ignored; visible characters are captured by the pattern
     * @param fieldNames JSONPath expressions used to determine capture group layout
     * @return replacement string
     */
    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        int n = (fieldNames == null || fieldNames.length == 0) ? 1 : fieldNames.length;
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < n; i++) {
            prefix.append("$").append(3 * i + 1);
            suffix.append("$").append(3 * i + 3);
        }
        return prefix + "****" + suffix;
    }

    // ── sub-pattern builders ──────────────────────────────────────────────────

    private static String buildSubPattern(String jsonPath, int visibleCharacters) {
        if (jsonPath.contains("[?(")) {
            return buildFilterSubPattern(jsonPath, visibleCharacters);
        }
        return buildSimpleFieldSubPattern(jsonPath, visibleCharacters);
    }

    /**
     * Builds a 3-group sub-pattern for filter-predicate JSONPath expressions.
     *
     * <p>The pattern starts at the current object boundary, asserts all filter conditions
     * with lookaheads, then captures the target field value.
     */
    private static String buildFilterSubPattern(String jsonPath, int visibleCharacters) {
        String targetField = extractFilterTargetField(jsonPath);
        List<String[]> conditions = extractConditions(jsonPath);

        StringBuilder sb = new StringBuilder("(\\{");
        for (String[] cond : conditions) {
            sb.append("(?=[^{}]*\"").append(cond[0]).append("\"").append(SKIP)
                    .append(":").append(SKIP).append("\"").append(cond[1]).append("\")");
        }
        sb.append("[^{}]*\"").append(targetField).append("\"").append(SKIP)
                .append(":").append(SKIP).append("\"[^\"]{0,").append(visibleCharacters).append("})");
        sb.append("([^\"]*)");
        sb.append("(\")");
        return sb.toString();
    }

    /**
     * Builds a 3-group sub-pattern for simple-field JSONPath expressions.
     */
    private static String buildSimpleFieldSubPattern(String jsonPath, int visibleCharacters) {
        String targetField = extractSimpleField(jsonPath);
        return "(\"" + targetField + "\"" + SKIP + ":" + SKIP + "\"[^\"]{0," + visibleCharacters + "})"
                + "([^\"]*)"
                + "(\")";
    }

    // ── JSONPath parsing ──────────────────────────────────────────────────────

    private static String extractFilterTargetField(String jsonPath) {
        int lastBracket = jsonPath.lastIndexOf("].");
        if (lastBracket < 0) {
            throw new IllegalArgumentException("Filter JSONPath must end with ].targetField, got: " + jsonPath);
        }
        String field = jsonPath.substring(lastBracket + 2).trim();
        if (field.isEmpty()) {
            throw new IllegalArgumentException("Target field is empty in JSONPath: " + jsonPath);
        }
        return field;
    }

    private static String extractSimpleField(String jsonPath) {
        int lastDot = jsonPath.lastIndexOf('.');
        if (lastDot < 0) {
            throw new IllegalArgumentException("Simple JSONPath must contain at least one '.', got: " + jsonPath);
        }
        String field = jsonPath.substring(lastDot + 1).trim();
        if (field.isEmpty()) {
            throw new IllegalArgumentException("Target field is empty in JSONPath: " + jsonPath);
        }
        return field;
    }

    private static List<String[]> extractConditions(String jsonPath) {
        Matcher filterMatcher = FILTER_EXTRACT.matcher(jsonPath);
        if (!filterMatcher.find()) {
            throw new IllegalArgumentException("No filter predicate [?(...)] found in JSONPath: " + jsonPath);
        }
        String filterContent = filterMatcher.group(1);
        List<String[]> conditions = new ArrayList<>();
        Matcher condMatcher = CONDITION_EXTRACT.matcher(filterContent);
        while (condMatcher.find()) {
            conditions.add(new String[]{condMatcher.group(1), condMatcher.group(2)});
        }
        if (conditions.isEmpty()) {
            throw new IllegalArgumentException("No @.field=='value' conditions found in filter: " + filterContent);
        }
        return conditions;
    }
}
