package com.ebay.ejmask.extenstion.builder.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class JsonPathValuePatternBuilderTest {

    static final String FILTER_PATH =
            "$.deviceContext.fingerprints[?(@.source=='EBAY' && @.type=='DEVICE_ID')].value";
    static final String SIMPLE_PATH = "$.deviceContext.guid";

    // ── filter-predicate path (single expression, V=0) ──────────────────────

    static Stream<Arguments> filterPathData() {
        return Stream.of(
                Arguments.of("compact JSON",
                        "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4ef092963b7439107285a8062c94a\"}",
                        "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"****\"}"),
                Arguments.of("formatted JSON",
                        "{\n"
                                + "  \"source\": \"EBAY\",\n"
                                + "  \"type\": \"DEVICE_ID\",\n"
                                + "  \"value\": \"74f4ef092963b7439107285a8062c94a\"\n"
                                + "}",
                        "{\n"
                                + "  \"source\": \"EBAY\",\n"
                                + "  \"type\": \"DEVICE_ID\",\n"
                                + "  \"value\": \"****\"\n"
                                + "}"),
                Arguments.of("fields in reverse order (any-order matching via lookaheads)",
                        "{\"type\":\"DEVICE_ID\",\"source\":\"EBAY\",\"value\":\"74f4ef092963b7439107285a8062c94a\"}",
                        "{\"type\":\"DEVICE_ID\",\"source\":\"EBAY\",\"value\":\"****\"}"),
                Arguments.of("nested inside deviceContext",
                        "{\"deviceContext\":{\"fingerprints\":[{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4ef092963b7439107285a8062c94a\"}]}}",
                        "{\"deviceContext\":{\"fingerprints\":[{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"****\"}]}}"),
                Arguments.of("non-matching source is not masked",
                        "{\"source\":\"OTHER\",\"type\":\"DEVICE_ID\",\"value\":\"sensitiveValue\"}",
                        "{\"source\":\"OTHER\",\"type\":\"DEVICE_ID\",\"value\":\"sensitiveValue\"}"),
                Arguments.of("non-matching type is not masked",
                        "{\"source\":\"EBAY\",\"type\":\"OTHER_TYPE\",\"value\":\"sensitiveValue\"}",
                        "{\"source\":\"EBAY\",\"type\":\"OTHER_TYPE\",\"value\":\"sensitiveValue\"}")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("filterPathData")
    void filterPath_fullMask(String name, String input, String expected) {
        applyAndAssert(0, new String[]{FILTER_PATH}, input, expected);
    }

    // ── filter-predicate path with visible chars ─────────────────────────────

    @Test
    void filterPath_partialMask_showsFirstFourChars() {
        applyAndAssert(4, new String[]{FILTER_PATH},
                "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4ef092963b7439107285a8062c94a\"}",
                "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4****\"}");
    }

    // ── simple field path ────────────────────────────────────────────────────

    @Test
    void simplePath_fullMask() {
        applyAndAssert(0, new String[]{SIMPLE_PATH},
                "{\"guid\":\"some-guid-value\"}",
                "{\"guid\":\"****\"}");
    }

    @Test
    void simplePath_partialMask_showsFirstFourChars() {
        applyAndAssert(4, new String[]{SIMPLE_PATH},
                "{\"guid\":\"some-guid-value\"}",
                "{\"guid\":\"some****\"}");
    }

    @Test
    void simplePath_withWhitespace() {
        applyAndAssert(4, new String[]{SIMPLE_PATH},
                "{\"guid\" : \"some-guid-value\"}",
                "{\"guid\" : \"some****\"}");
    }

    // ── multiple expressions combined ────────────────────────────────────────

    @Test
    void multipleExpressions_bothMasked() {
        String input = "{\n"
                + "  \"deviceContext\": {\n"
                + "    \"guid\": \"some-guid-value\",\n"
                + "    \"fingerprints\": [\n"
                + "      {\n"
                + "        \"source\": \"EBAY\",\n"
                + "        \"type\": \"DEVICE_ID\",\n"
                + "        \"value\": \"74f4ef092963b7439107285a8062c94a\"\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";
        String expected = "{\n"
                + "  \"deviceContext\": {\n"
                + "    \"guid\": \"some****\",\n"
                + "    \"fingerprints\": [\n"
                + "      {\n"
                + "        \"source\": \"EBAY\",\n"
                + "        \"type\": \"DEVICE_ID\",\n"
                + "        \"value\": \"74f4****\"\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}";
        applyAndAssert(4, new String[]{FILTER_PATH, SIMPLE_PATH}, input, expected);
    }

    @Test
    void multipleExpressions_onlyFilterMatches() {
        applyAndAssert(4, new String[]{FILTER_PATH, SIMPLE_PATH},
                "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4ef092963b7439107285a8062c94a\"}",
                "{\"source\":\"EBAY\",\"type\":\"DEVICE_ID\",\"value\":\"74f4****\"}");
    }

    @Test
    void multipleExpressions_onlySimpleMatches() {
        applyAndAssert(4, new String[]{FILTER_PATH, SIMPLE_PATH},
                "{\"guid\":\"some-guid-value\"}",
                "{\"guid\":\"some****\"}");
    }

    // ── error cases ──────────────────────────────────────────────────────────

    @Test
    void buildPattern_throwsOnNegativeVisibleCharacters() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonPathValuePatternBuilder().buildPattern(-1, "$.user.name"));
    }

    @Test
    void buildPattern_throwsOnNullFieldNames() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonPathValuePatternBuilder().buildPattern(0, (String[]) null));
    }

    @Test
    void buildPattern_throwsOnBlankExpression() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonPathValuePatternBuilder().buildPattern(0, "   "));
    }

    @Test
    void buildPattern_throwsWhenFilterPredicateMalformed() {
        // [?( is present but closing )] is missing → FILTER_EXTRACT cannot match
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonPathValuePatternBuilder().buildPattern(0, "$.path[?(@.a=='b'].value"));
    }

    @Test
    void buildPattern_throwsWhenNoConditionsInFilter() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonPathValuePatternBuilder().buildPattern(0, "$.path[?(noConditions)].value"));
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private static void applyAndAssert(int visibleCharacters, String[] paths, String input, String expected) {
        JsonPathValuePatternBuilder builder = new JsonPathValuePatternBuilder();
        Pattern pattern = Pattern.compile(builder.buildPattern(visibleCharacters, paths));
        String replacement = builder.buildReplacement(visibleCharacters, paths);
        String actual = pattern.matcher(input).replaceAll(replacement);
        Assertions.assertEquals(expected, actual);
    }
}
