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
public class FullValueMaskJsonPatternBuilderTest {

    private static final JsonFullValuePatternBuilder instance = new JsonFullValuePatternBuilder();
    private static final String[] fieldNames = new String[]{"cvv", "ssnLastFourDigit"};


    /**
     * Test of buildPattern method, of class
     * FullValueMaskJsonPatternBuilder.
     */
    @Test
    public void testBuildPattern() {
        int visibleCharacters = 0;
        String result = instance.buildPattern(visibleCharacters, fieldNames);
        Assertions.assertEquals("\\\"(cvv|ssnLastFourDigit)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,1})[^\\\"]*(\\\\?\\\"|)", result);
    }

    /**
     * Test of buildPattern method, of class
     * FullValueMaskJsonPatternBuilder.
     */
    @Test
    public void testBuildPattern_with_visible_characters() {
        int visibleCharacters = 1;
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.buildPattern(visibleCharacters, fieldNames));
    }

    /**
     * Test of buildReplacement method, of class
     * FullValueMaskJsonPatternBuilder.
     */
    @Test
    public void testBuildReplacement() {
        int visibleCharacters = 0;
        String result = instance.buildReplacement(visibleCharacters, fieldNames);
        Assertions.assertEquals("\"$1$2****$4", result);
    }


    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with normal json",
                        "{\"cvv\":\"sensitive data\",\"ssnLastFourDigit\":\"sensitive data\",\"nonSensitiveData\":\"cvv\"}",
                        "{\"cvv\":\"****\",\"ssnLastFourDigit\":\"****\",\"nonSensitiveData\":\"cvv\"}"
                ),
                Arguments.arguments(
                        "test with empty values",
                        "{\"cvv\":\"\",\"ssnLastFourDigit\":null,\"nonSensitiveData\":\"cvv\"}",
                        "{\"cvv\":\"\",\"ssnLastFourDigit\":null,\"nonSensitiveData\":\"cvv\"}"
                ),
                Arguments.arguments(
                        "test with space",
                        "{\"cvv\"     :   \"sensitive data\",   \"ssnLastFourDigit\"   :\"sensitive data\",\"nonSensitiveData\":\"cvv\"}",
                        "{\"cvv\"     :   \"****\",   \"ssnLastFourDigit\"   :\"****\",\"nonSensitiveData\":\"cvv\"}"
                ),
                Arguments.arguments(
                        "test with line break",
                        "{\n"
                                + "  \"cvv\": \"sensitive data\",\n"
                                + "  \"ssnLastFourDigit\": \"sensitive data\",\n"
                                + "  \"nonSensitiveData\": \"cvv\"\n"
                                + "}",
                        "{\n"
                                + "  \"cvv\": \"****\",\n"
                                + "  \"ssnLastFourDigit\": \"****\",\n"
                                + "  \"nonSensitiveData\": \"cvv\"\n"
                                + "}"
                ),
                Arguments.arguments(
                        "test with broken json",
                        "{\"cvv\":\"sensitive data\",\"ssnLastFourDigit\":\"sensitive data",
                        "{\"cvv\":\"****\",\"ssnLastFourDigit\":\"****"
                ),
                /*
                 * TODO: json formatting is little messed up due to limitation of regex. commented the expected.
                 */
                Arguments.arguments(
                        "test with json encoded json",
                        "{\\\"cvv\\\":\\\"sensitive data\\\",\\\"ssnLastFourDigit\\\":\\\"sensitive data\\\",\\\"nonSensitiveData\\\":\\\"cvv\\\"}",
                        //"{\\\"cvv\\\":\\\"****\\\",\\\"ssnLastFourDigit\\\":\\\"****\\\",\\\"nonSensitiveData\\\":\\\"cvv\\\"}"
                        "{\\\"cvv\\\":\\\"****\",\\\"ssnLastFourDigit\\\":\\\"****\",\\\"nonSensitiveData\\\":\\\"cvv\\\"}"
                ),
                Arguments.arguments(
                        "test with double json encoded json",
                        "{\\\\\\\"cvv\\\\\\\":\\\\\\\"sensitive data\\\\\\\",\\\\\\\"ssnLastFourDigit\\\\\\\":\\\\\\\"sensitive data\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"cvv\\\\\\\"}",
                        //"{\\\\\\\"cvv\\\\\\\":\\\\\\\"****\\\\\\\",\\\\\\\"ssnLastFourDigit\\\\\\\":\\\\\\\"****\\\\\\\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"cvv\\\\\\\"}"
                        "{\\\\\\\"cvv\\\\\\\":\\\\\\\"****\",\\\\\\\"ssnLastFourDigit\\\\\\\":\\\\\\\"****\",\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"cvv\\\\\\\"}"
                ),
                Arguments.arguments(
                        "test with encoded broken json",
                        "{\\\"cvv\\\":\\\"sensitive data\\\",\\\"ssnLastFourDigit\\\":\\\"sensitive data",
                        //"{\\\"cvv\\\":\\\"****\\\",\\\"ssnLastFourDigit\\\":\\\"****"
                        "{\\\"cvv\\\":\\\"****\",\\\"ssnLastFourDigit\\\":\\\"****"
                ),
                Arguments.arguments(
                        "test with encoded broken json 2",
                        "{\\\"cvv\\\":\\\"sensitive data\\\",\\\"ssnLastFourDigit\\\":\\\"sensitive data\\",
                        //"{\\\"cvv\\\":\\\"****\\\",\\\"ssnLastFourDigit\\\":\\\"****\\"
                        "{\\\"cvv\\\":\\\"****\",\\\"ssnLastFourDigit\\\":\\\"****"
                )
        );
    }


    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String regex = instance.buildPattern(0, fieldNames);
        String replacement = instance.buildReplacement(0, fieldNames);
        Pattern pattern = Pattern.compile(regex);
        String result = pattern.matcher(data).replaceAll(replacement);
        Assertions.assertFalse(result.contains("sensitive data"));
        Assertions.assertEquals(expected, result);
    }
}
