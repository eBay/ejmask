package com.ebay.ejmask.extenstion.builder.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JsonValueUnmaskFromEndPatternBuilderTest {

    public static final JsonValueUnmaskFromEndPatternBuilder instance = new JsonValueUnmaskFromEndPatternBuilder();
    public static final String[] fieldNames = new String[]{"ccNumber", "ssn"};

    @Test
    public void testBuildPattern() {
        int visibleCharacters = 4;
        String result = instance.buildPattern(visibleCharacters, fieldNames);
        Assertions.assertEquals("\\\"(ccNumber|ssn)(\\\\*\\\"\\s*:\\s*\\\\*\\\")[^\\\"]*([^\\\\\"]{4})(\\\\?\\\"|)", result);
    }

    @Test
    public void testBuildPattern_withNegativeVisibleCharacters() {
        int visibleCharacters = -1;
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.buildPattern(visibleCharacters, fieldNames));
    }

    @Test
    public void testBuildReplacement() {
        int visibleCharacters = 4;
        String result = instance.buildReplacement(visibleCharacters, fieldNames);
        Assertions.assertEquals("\"$1$2xxxx-$3$4", result);
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String regex = instance.buildPattern(4, fieldNames);
        String replacement = instance.buildReplacement(4, fieldNames);
        Pattern pattern = Pattern.compile(regex);
        String result = pattern.matcher(data).replaceAll(replacement);
        Assertions.assertFalse(result.contains("1234567890"));
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with normal json",
                        "{\"ccNumber\":\"1234567890123456\",\"ssn\":\"123456789\",\"nonSensitiveData\":\"data\"}",
                        "{\"ccNumber\":\"xxxx-3456\",\"ssn\":\"xxxx-6789\",\"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with empty values",
                        "{\"ccNumber\":\"\",\"ssn\":null,\"nonSensitiveData\":\"data\"}",
                        "{\"ccNumber\":\"\",\"ssn\":null,\"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with space",
                        "{\"ccNumber\"     :   \"1234567890123456\",   \"ssn\"   :\"123456789\",\"nonSensitiveData\":\"data\"}",
                        "{\"ccNumber\"     :   \"xxxx-3456\",   \"ssn\"   :\"xxxx-6789\",\"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with line break",
                        "{\n"
                                + "  \"ccNumber\": \"1234567890123456\",\n"
                                + "  \"ssn\": \"123456789\",\n"
                                + "  \"nonSensitiveData\": \"data\"\n"
                                + "}",
                        "{\n"
                                + "  \"ccNumber\": \"xxxx-3456\",\n"
                                + "  \"ssn\": \"xxxx-6789\",\n"
                                + "  \"nonSensitiveData\": \"data\"\n"
                                + "}"
                ),
                Arguments.arguments(
                        "test with broken json",
                        "{\"ccNumber\":\"1234567890123456\",\"ssn\":\"123456789",
                        "{\"ccNumber\":\"xxxx-3456\",\"ssn\":\"xxxx-6789"
                ),
                Arguments.arguments(
                        "test with json encoded json",
                        "{\\\"ccNumber\\\":\\\"1234567890123456\\\",\\\"ssn\\\":\\\"123456789\\\",\\\"nonSensitiveData\\\":\\\"data\\\"}",
                        "{\\\"ccNumber\\\":\\\"xxxx-3456\\\",\\\"ssn\\\":\\\"xxxx-6789\\\",\\\"nonSensitiveData\\\":\\\"data\\\"}"
                ),
                Arguments.arguments(
                        "test with double json encoded json",
                        "{\\\\\\\"ccNumber\\\\\\\":\\\\\\\"1234567890123456\\\\\\\",\\\\\\\"ssn\\\\\\\":\\\\\\\"123456789\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"data\\\\\\\"}",
                        "{\\\\\\\"ccNumber\\\\\\\":\\\\\\\"xxxx-3456\\\\\\\",\\\\\\\"ssn\\\\\\\":\\\\\\\"xxxx-6789\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"data\\\\\\\"}"
                ),
                Arguments.arguments(
                        "test with encoded broken json",
                        "{\\\"ccNumber\\\":\\\"1234567890123456\\\",\\\"ssn\\\":\\\"123456789",
                        "{\\\"ccNumber\\\":\\\"xxxx-3456\\\",\\\"ssn\\\":\\\"xxxx-6789"
                ),
                Arguments.arguments(
                        "test with encoded broken json 2",
                        "{\\\"ccNumber\\\":\\\"1234567890123456\\\",\\\"ssn\\\":\\\"123456789\\",
                        "{\\\"ccNumber\\\":\\\"xxxx-3456\\\",\\\"ssn\\\":\\\"xxxx-6789\\"
                )
        );
    }
}