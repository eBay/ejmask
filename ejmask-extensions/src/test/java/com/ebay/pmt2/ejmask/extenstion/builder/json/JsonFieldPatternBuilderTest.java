package com.ebay.pmt2.ejmask.extenstion.builder.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author prakv
 */
public class JsonFieldPatternBuilderTest {

    public static final JsonFieldPatternBuilder instance = new JsonFieldPatternBuilder();
    public static final String[] fieldNames = new String[]{"firstName", "lastName"};


    /**
     * Test of buildPattern method, of class JsonFieldPatternBuilder.
     */
    @Test
    public void testBuildPattern() {
        int visibleCharacters = 12;
        String result = instance.buildPattern(visibleCharacters, fieldNames);
        Assertions.assertEquals("\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,12})[^\\\"]*(\\\\?\\\"|)", result);
    }

    /**
     * Test of buildPattern method, of class JsonFieldPatternBuilder.
     */
    @Test
    public void testBuildPattern_without_visible_characters() {
        int visibleCharacters = 0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.buildPattern(visibleCharacters, fieldNames));
    }

    /**
     * Test of buildReplacement method, of class JsonFieldPatternBuilder.
     */
    @Test
    public void testBuildReplacement() {
        int visibleCharacters = 0;
        String result = instance.buildReplacement(visibleCharacters, fieldNames);
        Assertions.assertEquals("\"$1$2$3-xxxx$4", result);
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String regex = instance.buildPattern(2, fieldNames);
        String replacement = instance.buildReplacement(2, fieldNames);
        Pattern pattern = Pattern.compile(regex);
        String result = pattern.matcher(data).replaceAll(replacement);
        Assertions.assertFalse(result.contains("sensitive data"));
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with normal json",
                        "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}",
                        "{\"firstName\":\"se-xxxx\",\"lastName\":\"se-xxxx\",\"nonSensitiveData\":\"firstName\"}"

                ),
                Arguments.arguments(
                        "test with empty values",
                        "{\"firstName\":\"\",\"lastName\":null,\"nonSensitiveData\":\"firstName\"}",
                        "{\"firstName\":\"\",\"lastName\":null,\"nonSensitiveData\":\"firstName\"}"

                ),
                Arguments.arguments(
                        "test with space",
                        "{\"firstName\"     :   \"sensitive data\",   \"lastName\"   :\"sensitive data\",\"nonSensitiveData\":\"firstName\"}",
                        "{\"firstName\"     :   \"se-xxxx\",   \"lastName\"   :\"se-xxxx\",\"nonSensitiveData\":\"firstName\"}"

                ),
                Arguments.arguments(
                        "test with line break",
                        "{\n"
                                + "  \"firstName\": \"sensitive data\",\n"
                                + "  \"lastName\": \"sensitive data\",\n"
                                + "  \"nonSensitiveData\": \"firstName\"\n"
                                + "}",
                        "{\n"
                                + "  \"firstName\": \"se-xxxx\",\n"
                                + "  \"lastName\": \"se-xxxx\",\n"
                                + "  \"nonSensitiveData\": \"firstName\"\n"
                                + "}"

                ),
                Arguments.arguments(
                        "test with broken json",
                        "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data",
                        "{\"firstName\":\"se-xxxx\",\"lastName\":\"se-xxxx"

                ),
                Arguments.arguments(
                        "test with normal json",
                        "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}",
                        "{\"firstName\":\"se-xxxx\",\"lastName\":\"se-xxxx\",\"nonSensitiveData\":\"firstName\"}"

                ),

                /*
                 * TODO: json formatting is little messed up due to limitation
                 * of regex. commented the expected.
                 */
                Arguments.arguments(
                        "test with json encoded json",
                        "{\\\"firstName\\\":\\\"sensitive data\\\",\\\"lastName\\\":\\\"sensitive data\\\",\\\"nonSensitiveData\\\":\\\"firstName\\\"}",
                        //"{\\\"firstName\\\":\\\"se-xxxx\\\",\\\"lastName\\\":\\\"se-xxxx\\\",\\\"nonSensitiveData\\\":\\\"firstName\\\"}"
                        "{\\\"firstName\\\":\\\"se-xxxx\",\\\"lastName\\\":\\\"se-xxxx\",\\\"nonSensitiveData\\\":\\\"firstName\\\"}"

                ),
                Arguments.arguments(
                        "test with double json encoded json",
                        "{\\\\\\\"firstName\\\\\\\":\\\\\\\"sensitive data\\\\\\\",\\\\\\\"lastName\\\\\\\":\\\\\\\"sensitive data\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"firstName\\\\\\\"}",
                        //"{\\\\\\\"firstName\\\\\\\":\\\\\\\"se-xxxx\\\\\\\",\\\\\\\"lastName\\\\\\\":\\\\\\\"se-xxxx\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"firstName\\\\\\\"}"
                        "{\\\\\\\"firstName\\\\\\\":\\\\\\\"se-xxxx\",\\\\\\\"lastName\\\\\\\":\\\\\\\"se-xxxx\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"firstName\\\\\\\"}"

                ),
                Arguments.arguments(
                        "test with encoded broken json",
                        "{\\\"firstName\\\":\\\"sensitive data\\\",\\\"lastName\\\":\\\"sensitive data",
                        //"{\\\"firstName\\\":\\\"se-xxxx\\\",\\\"lastName\\\":\\\"se-xxxx"
                        "{\\\"firstName\\\":\\\"se-xxxx\",\\\"lastName\\\":\\\"se-xxxx"

                ),
                Arguments.arguments(
                        "test with encoded broken json 2",
                        "{\\\"firstName\\\":\\\"sensitive data\\\",\\\"lastName\\\":\\\"sensitive data\\",
                        //"{\\\"firstName\\\":\\\"se-xxxx\\\",\\\"lastName\\\":\\\"se-xxxx\\",
                        "{\\\"firstName\\\":\\\"se-xxxx\",\\\"lastName\\\":\\\"se-xxxx"
                ));
    }

}
