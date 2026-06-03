package com.ebay.ejmask.extenstion.builder.string;

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
public class PipeSeparatedValuePatternBuilderTest {

    private static final PipeSeparatedValuePatternBuilder instance = new PipeSeparatedValuePatternBuilder();
    private static final String[] fieldNames = new String[]{"userId", "orgId"};

    /**
     * Test of buildPattern method, of class PipeSeparatedValuePatternBuilder.
     */
    @Test
    public void testBuildPattern() {
        String result = instance.buildPattern(0, fieldNames);
        Assertions.assertEquals("((?:userId|orgId)=)([^|\"\\\\]+)", result);
    }

    /**
     * Test of buildReplacement method, of class PipeSeparatedValuePatternBuilder.
     */
    @Test
    public void testBuildReplacement() {
        String result = instance.buildReplacement(0, fieldNames);
        Assertions.assertEquals("$1****", result);
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String regex = instance.buildPattern(0, fieldNames);
        String replacement = instance.buildReplacement(0, fieldNames);
        Pattern pattern = Pattern.compile(regex);
        String result = pattern.matcher(data).replaceAll(replacement);
        Assertions.assertFalse(result.contains("1234567890"));
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with value before pipe delimiter",
                        "userId=1234567890|payerType=SELLER|type=CHARGE",
                        "userId=****|payerType=SELLER|type=CHARGE"
                ),
                Arguments.arguments(
                        "test with value at end of string",
                        "type=CHARGE|userId=1234567890",
                        "type=CHARGE|userId=****"
                ),
                Arguments.arguments(
                        "test with multiple sensitive fields",
                        "userId=1234567890|orgId=987654321|type=CHARGE",
                        "userId=****|orgId=****|type=CHARGE"
                ),
                Arguments.arguments(
                        "test does not bleed into adjacent fields",
                        "userId=1234567890|payerType=SELLER",
                        "userId=****|payerType=SELLER"
                ),
                Arguments.arguments(
                        "test with unrelated fields not masked",
                        "orderId=07-14661-52696|itemId=137276407571",
                        "orderId=07-14661-52696|itemId=137276407571"
                ),
                Arguments.arguments(
                        "test embedded inside a json string field value",
                        "\"decodePayload\":\"userId=1234567890|payerType=SELLER|type=CHARGE\"",
                        "\"decodePayload\":\"userId=****|payerType=SELLER|type=CHARGE\""
                )
        );
    }
}
