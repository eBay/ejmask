package com.ebay.ejmask.extenstion.builder.header;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author prakv
 */
public class HeaderFieldPatternBuilderTest {

    private static final HeaderFieldPatternBuilder instance = new HeaderFieldPatternBuilder();
    private static final String[] fieldNames = new String[]{"Authorization", "X-API-Key", "PayPal-Auth-Assertion", "Auth"};


    /**
     * Test of buildPattern method, of class HeaderFieldPatternBuilder.
     */
    @Test
    public void testBuildPattern() {
        int visibleCharacters = 0;
        String result = instance.buildPattern(visibleCharacters, fieldNames);
        Assertions.assertEquals("((?i)Authorization|X-API-Key|PayPal-Auth-Assertion|Auth)=([^\\&\"]{1,10})*", result);
    }

    /**
     * Test of buildPattern method, of class HeaderFieldPatternBuilder.
     */
    @Test
    public void testBuildPattern_vissible() {
        int visibleCharacters = 10;
        String result = instance.buildPattern(visibleCharacters, fieldNames);
        Assertions.assertEquals("((?i)Authorization|X-API-Key|PayPal-Auth-Assertion|Auth)=([^\\&\"]{1,10})*", result);
    }

    /**
     * Test of buildPattern method, of class HeaderFieldPatternBuilder.
     */
    @Test
    public void testBuildPattern_with_visible_characters() {
        int visibleCharacters = 1;
        instance.buildPattern(visibleCharacters, fieldNames);
    }

    /**
     * Test of buildReplacement method, of class HeaderFieldPatternBuilder.
     */
    @Test
    public void testBuildReplacement() {
        int visibleCharacters = 0;
        String result = instance.buildReplacement(visibleCharacters, fieldNames);
        Assertions.assertEquals("$1=******", result);
    }

    /**
     * Test of buildReplacement method, of class HeaderFieldPatternBuilder.
     */
    @Test
    public void testBuildReplacement_with_visible_characters() {
        int visibleCharacters = 10;
        String result = instance.buildReplacement(visibleCharacters, fieldNames);
        Assertions.assertEquals("$1=xxxx-$2", result);
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String data, String expected) {
        String replacement = instance.buildReplacement(10, fieldNames);
        String findByRegex = instance.buildPattern(10, fieldNames);
        Pattern pattern = Pattern.compile(findByRegex);
        String result = pattern.matcher(data).replaceAll(replacement);
        //Assertions.assertFalse(result.contains("sensitive-data"));
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                arguments(
                        "token @ start",
                        "Auth=Basic sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data&Accept=application/json,application/json&&Content-Type=application/json",
                        "Auth=xxxx-itive-data&Accept=application/json,application/json&&Content-Type=application/json"
                ),
                arguments(
                        "token @ end",
                        "Accept=application/json,application/json&Auth=Basic sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data&Content-Type=application/json",
                        "Accept=application/json,application/json&Auth=xxxx-itive-data&Content-Type=application/json"
                ),
                arguments(
                        "token @ middle",
                        "Accept=application/json,application/json&Content-Type=application/json&Auth=Basic sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data",
                        "Accept=application/json,application/json&Content-Type=application/json&Auth=xxxx-itive-data"
                ),
                arguments(
                        "token as small case",
                        "Accept=application/json,application/json&Content-Type=application/json&auth=Basic sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data",
                        "Accept=application/json,application/json&Content-Type=application/json&auth=xxxx-itive-data"
                ),
                //
                arguments(
                        "Authorization",
                        "Accept=application/json,application/json&Authorization=Basic sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data&Content-Type=application/json",
                        "Accept=application/json,application/json&Authorization=xxxx-itive-data&Content-Type=application/json"
                ),
                arguments(
                        "X-API-Key",
                        "Accept=application/json,application/json&X-API-Key=sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data&Content-Type=application/json",
                        "Accept=application/json,application/json&X-API-Key=xxxx-data&Content-Type=application/json"
                ),
                arguments(
                        "PayPal-Auth-Assertion",
                        "Accept=application/json,application/json&PayPal-Auth-Assertion=sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data_sensitive-data&Content-Type=application/json",
                        "Accept=application/json,application/json&PayPal-Auth-Assertion=xxxx-data&Content-Type=application/json"
                ));
    }

}
