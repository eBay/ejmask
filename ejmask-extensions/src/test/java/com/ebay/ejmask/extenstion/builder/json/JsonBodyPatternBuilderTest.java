package com.ebay.ejmask.extenstion.builder.json;

import com.ebay.ejmask.core.BaseFilter;
import com.ebay.ejmask.core.EJMask;
import com.ebay.ejmask.core.EJMaskInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class JsonBodyPatternBuilderTest {

    static {
        EJMaskInitializer.addFilter(new BaseFilter(JsonBodyPatternBuilder.class, 0, "ccNumber"));
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(
                        "test with normal json",
                        "{\"id\":\"123456\",\"ccNumber\":{\"idd\":\"1234567890123456\",\"id\":\"1234567890123456\"},\"ssn\":{\"id\":\"123456789\"},\"nonSensitiveData\":\"data\"}",
                        "{\"id\":\"123456\",\"ccNumber\":{\"****\":\"****\"},\"ssn\":{\"id\":\"123456789\"},\"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with empty values",
                        "{\"id\":\"123456\",\"ccNumber\":{\"idd\":\"\",\"id\":\"\"},\"ssn\":{\"id\":null},\"nonSensitiveData\":\"data\"}",
                        "{\"id\":\"123456\",\"ccNumber\":{\"****\":\"****\"},\"ssn\":{\"id\":null},\"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with space",
                        "{\"id\"   :   \"123456\",    \"ccNumber\"     :   {\"idd\"  :  \"1234567890123456\",  \"id\"   :\"1234567890123456\"},   \"ssn\"   :{\"id\"  :  \"123456789\"},   \"nonSensitiveData\":\"data\"}",
                        "{\"id\"   :   \"123456\",    \"ccNumber\"     :   {\"****\":\"****\"},   \"ssn\"   :{\"id\"  :  \"123456789\"},   \"nonSensitiveData\":\"data\"}"
                ),
                Arguments.arguments(
                        "test with line break",
                        """
                                {
                                  "id": "123456",
                                  "ccNumber": {
                                    "idd": "1234567890123456",
                                    "id": "1234567890123456"
                                  },
                                  "ssn": {
                                    "id": "123456789"
                                  },
                                  "nonSensitiveData": "data"
                                }""",
                        """
                                {
                                  "id": "123456",
                                  "ccNumber": {"****":"****"},
                                  "ssn": {
                                    "id": "123456789"
                                  },
                                  "nonSensitiveData": "data"
                                }"""
                ),
                Arguments.arguments(
                        "test with json encoded json",
                        "{\\\"id\\\":\\\"123456\\\",\\\"ccNumber\\\":{\\\"idd\\\":\\\"1234567890123456\\\",\\\"id\\\":\\\"1234567890123456\\\"},\\\"ssn\\\":{\\\"id\\\":\\\"123456789\\\"},\\\"nonSensitiveData\\\":\\\"data\\\"}",
                        "{\\\"id\\\":\\\"123456\\\",\\\"ccNumber\\\":{\"****\":\"****\"},\\\"ssn\\\":{\\\"id\\\":\\\"123456789\\\"},\\\"nonSensitiveData\\\":\\\"data\\\"}"
                ),
                Arguments.arguments(
                        "test with double json encoded json",
                        "{\\\\\\\"id\\\\\\\":\\\\\\\"123456\\\\\\\",\\\\\\\"ccNumber\\\\\\\":{\\\\\\\"idd\\\\\\\":\\\\\\\"1234567890123456\\\\\\\",\\\\\\\"id\\\\\\\":\\\\\\\"1234567890123456\\\\\\\"},\\\\\\\"ssn\\\\\\\":{\\\\\\\"id\\\\\\\":\\\\\\\"123456789\\\\\\\"},\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"data\\\\\\\"}",
                        "{\\\\\\\"id\\\\\\\":\\\\\\\"123456\\\\\\\",\\\\\\\"ccNumber\\\\\\\":{\"****\":\"****\"},\\\\\\\"ssn\\\\\\\":{\\\\\\\"id\\\\\\\":\\\\\\\"123456789\\\\\\\"},\\\\\\\"nonSensitiveData\\\\\\\":\\\\\\\"data\\\\\\\"}"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void testMatch(String name, String input, String expected) {
        String output = EJMask.mask(input);
        Assertions.assertEquals(expected, output);
    }
}