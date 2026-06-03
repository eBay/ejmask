package com.ebay.ejmask.extenstion.builder.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author fsun1
 */
public class EscapedJsonFullValuePatternBuilderTest {

    private static final EscapedJsonFullValuePatternBuilder instance = new EscapedJsonFullValuePatternBuilder();
    private static final String[] fieldNames = new String[]{"cvv", "ssnLastFourDigit"};

    /**
     * Test of buildPattern method, of class EscapedJsonFullValuePatternBuilder.
     */
    @Test
    public void testBuildPattern() {
        String result = instance.buildPattern(0, fieldNames);
        Assertions.assertEquals("(\\\\\"(?:cvv|ssnLastFourDigit)\\\\\":\\\\\")([^\\\\\"]+)(\\\\\")", result);
    }

    /**
     * Test of buildReplacement method, of class EscapedJsonFullValuePatternBuilder.
     */
    @Test
    public void testBuildReplacement() {
        String result = instance.buildReplacement(0, fieldNames);
        Assertions.assertEquals("$1****$3", result);
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String regex = instance.buildPattern(0, fieldNames);
        String replacement = instance.buildReplacement(0, fieldNames);
        Pattern pattern = Pattern.compile(regex);
        String result = pattern.matcher(data).replaceAll(replacement);
        Assertions.assertFalse(result.contains("sensitiveData"));
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with escaped json - both sensitive fields masked",
                        "{\\\"cvv\\\":\\\"sensitiveData\\\",\\\"ssnLastFourDigit\\\":\\\"sensitiveData\\\",\\\"nonSensitiveData\\\":\\\"cvv\\\"}",
                        "{\\\"cvv\\\":\\\"****\\\",\\\"ssnLastFourDigit\\\":\\\"****\\\",\\\"nonSensitiveData\\\":\\\"cvv\\\"}"
                ),
                Arguments.arguments(
                        "test with empty value - not matched, left unchanged",
                        "{\\\"cvv\\\":\\\"\\\",\\\"other\\\":\\\"data\\\"}",
                        "{\\\"cvv\\\":\\\"\\\",\\\"other\\\":\\\"data\\\"}"
                ),
                Arguments.arguments(
                        "test with non-sensitive field not masked",
                        "{\\\"nonSensitiveField\\\":\\\"cvv\\\"}",
                        "{\\\"nonSensitiveField\\\":\\\"cvv\\\"}"
                ),
                Arguments.arguments(
                        "test with field name appearing in value - value not masked",
                        "{\\\"otherField\\\":\\\"cvv-value\\\",\\\"cvv\\\":\\\"sensitiveData\\\"}",
                        "{\\\"otherField\\\":\\\"cvv-value\\\",\\\"cvv\\\":\\\"****\\\"}"
                ),
                Arguments.arguments(
                        "test embedded as value inside a regular json field",
                        "{\"body\":\"{\\\"cvv\\\":\\\"sensitiveData\\\",\\\"amount\\\":\\\"100\\\"}\"}",
                        "{\"body\":\"{\\\"cvv\\\":\\\"****\\\",\\\"amount\\\":\\\"100\\\"}\"}"
                )
        );
    }
}
