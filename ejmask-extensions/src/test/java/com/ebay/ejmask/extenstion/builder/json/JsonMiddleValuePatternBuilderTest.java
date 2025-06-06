package com.ebay.ejmask.extenstion.builder.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.MethodSource;

class JsonMiddleValuePatternBuilderTest {

    @Test
    void testBuildPattern_ValidInput() {
        JsonMiddleValuePatternBuilder builder = new JsonMiddleValuePatternBuilder();
        String pattern = builder.buildPattern(4, "fieldName");
        String expectedPattern = "\\\"(fieldName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{2})([^\\\"]+)([^\\\"]{2})(\\\\?\\\"|)";
        Assertions.assertEquals(expectedPattern, pattern, "Generated pattern does not match the expected pattern.");
    }

    @Test
    void testBuildReplacement_ValidInput() {
        JsonMiddleValuePatternBuilder builder = new JsonMiddleValuePatternBuilder();
        String replacement = builder.buildReplacement(4, "fieldName");
        String expectedReplacement = "\"$1$2$3****$5$6";
        Assertions.assertEquals(expectedReplacement, replacement, "Generated replacement does not match the expected replacement.");
    }


    private final JsonMiddleValuePatternBuilder builder = new JsonMiddleValuePatternBuilder();
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestCasesForMultipleVisibleCharacters")
    void testJsonMaskingForMultipleVisibleCharacters(String description, int visibleCharacters, String inputJson, String expectedJson) {
        String pattern = builder.buildPattern(visibleCharacters, "name", "address");
        String replacement = builder.buildReplacement(visibleCharacters, "name", "address");
        String actualJson = inputJson.replaceAll(pattern, replacement);
        Assertions.assertEquals(expectedJson, actualJson, description);
    }

    private static Stream<Arguments> provideTestCasesForMultipleVisibleCharacters() {
        return Stream.of(
                Arguments.arguments(
                        "test with normal json (2 characters)",
                        2,
                        "{\"name\":\"John Doe\",\"address\":\"123 Main St\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"J****e\",\"address\":\"1****t\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with normal json (4 characters)",
                        4,
                        "{\"name\":\"John Doe\",\"address\":\"123 Main St\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"Jo****oe\",\"address\":\"12****St\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with normal json (6 characters)",
                        6,
                        "{\"name\":\"Johnathan Doe\",\"address\":\"123456 Main St\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"Joh****Doe\",\"address\":\"123**** St\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with short values (2 characters)",
                        2,
                        "{\"name\":\"Jo\",\"address\":\"12\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"Jo\",\"address\":\"12\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with short values (4 characters)",
                        4,
                        "{\"name\":\"Jo\",\"address\":\"12\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"Jo\",\"address\":\"12\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with short values (6 characters)",
                        6,
                        "{\"name\":\"John\",\"address\":\"123\",\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"John\",\"address\":\"123\",\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with empty values (2 characters)",
                        2,
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with empty values (4 characters)",
                        4,
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with empty values (6 characters)",
                        6,
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}",
                        "{\"name\":\"\",\"address\":null,\"nonSensitiveData\":\"example\"}"
                ),
                Arguments.arguments(
                        "test with broken json (2 characters)",
                        2,
                        "{\"name\":\"John Doe\",\"address\":\"123 Main St",
                        "{\"name\":\"J****e\",\"address\":\"1****t"
                ),
                Arguments.arguments(
                        "test with broken json (4 characters)",
                        4,
                        "{\"name\":\"John Doe\",\"address\":\"123 Main St",
                        "{\"name\":\"Jo****oe\",\"address\":\"12****St"
                ),
                Arguments.arguments(
                        "test with broken json (6 characters)",
                        6,
                        "{\"name\":\"Johnathan Doe\",\"address\":\"123456 Main St",
                        "{\"name\":\"Joh****Doe\",\"address\":\"123**** St"
                )
        );
    }



}