package com.ebay.pmt2.ejmask.extenstion.builder.json;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JsonRelatveFieldPattenBuilderTest {

    public static Pattern pattern;
    public static String replaceTemplate;

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(testSimpleCase()),
                Arguments.arguments(testBeautified()),
                Arguments.arguments(testJsonSliced()),
                Arguments.arguments(testIgnoreCase()),
                Arguments.arguments(testAnyOrderCase())
        );
    }

    private static Object[] testSimpleCase() {
        return new Object[]{
                "simple case",
                "\"buyer\":{\"name\":\"sensitiveData\",\"merchant_id\":\"SR5UP9W7LAH38\",\"id\":\"eBay\"}",
                "\"buyer\":{\"name\":\"se-xxxx\",\"merchant_id\":\"SR5UP9W7LAH38\",\"id\":\"eBay\"}"
        };
    }

    private static Object[] testBeautified() {
        return new Object[]{
                "minimum length",
                "{\n" +
                        "  \"buyer\": {\n" +
                        "    \"name\": \"sensitiveData\"\n" +
                        "  }\n" +
                        "}",
                "{\n" +
                        "  \"buyer\": {\n" +
                        "    \"name\": \"se-xxxx\"\n" +
                        "  }\n" +
                        "}"
        };
    }

    private static Object[] testJsonSliced() {
        return new Object[]{
                "json sliced",
                "{\n" +
                        "  \"buyer\": {\n" +
                        "    \"name\": \"sensitiveDatafshkfhsshfhsjfs",
                "{\n" +
                        "  \"buyer\": {\n" +
                        "    \"name\": \"se-xxxx"
        };
    }

    private static Object[] testIgnoreCase() {
        return new Object[]{
                "ignore case",
                "{\n" +
                        "  \"Buyer\": {\n" +
                        "    \"name\": \"sensitiveData\"\n" +
                        "  }\n" +
                        "}",
                "{\n" +
                        "  \"Buyer\": {\n" +
                        "    \"name\": \"se-xxxx\"\n" +
                        "  }\n" +
                        "}"
        };
    }

    private static Object[] testAnyOrderCase() {
        return new Object[]{
                "any order in field",
                "{\n" +
                        "  \"Buyer\": {\n" +
                        "    \"id\": \"1234\"\n" +
                        "    \"name\": \"sensitiveData\"\n" +
                        "  }\n" +
                        "}",
                "{\n" +
                        "  \"Buyer\": {\n" +
                        "    \"id\": \"1234\"\n" +
                        "    \"name\": \"se-xxxx\"\n" +
                        "  }\n" +
                        "}"
        };
    }

    @BeforeAll
    public static void setUp() {
        JsonRelativeFieldPatternBuilder builder = new JsonRelativeFieldPatternBuilder();
        String pattenStr = builder.buildPattern(2, "buyer", "name");
        pattern = Pattern.compile(pattenStr);
        replaceTemplate = builder.buildReplacement(0, "");

    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void runTests(String testcase, String input, String expected) {
        String actual = pattern.matcher(input).replaceAll(replaceTemplate);
        Assertions.assertEquals(expected, actual, testcase);
    }
}