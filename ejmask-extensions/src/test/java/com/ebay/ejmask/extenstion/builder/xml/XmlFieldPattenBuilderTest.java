package com.ebay.ejmask.extenstion.builder.xml;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class XmlFieldPattenBuilderTest {

    public static Pattern pattern;
    public static String replaceTemplate;

    @BeforeAll
    public static void setUp() {
        XmlFieldPattenBuilder xmlFieldPattenBuilder = new XmlFieldPattenBuilder();
        String pattenStr = xmlFieldPattenBuilder.buildPattern(10, "content");
        pattern = Pattern.compile(pattenStr);
        replaceTemplate = xmlFieldPattenBuilder.buildReplacement(0, "");

    }

    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void runTests(String testcase, String input, String expected) {
        String actual = XmlFieldPattenBuilderTest.pattern.matcher(input).replaceAll(XmlFieldPattenBuilderTest.replaceTemplate);
        Assertions.assertEquals(expected, actual, testcase);
    }

    static Stream<Arguments> dataForTestMatch() {
        return Stream.of(
                Arguments.arguments(testSimpleCase()),
                Arguments.arguments(testMinimumLength()),
                Arguments.arguments(testContentSliced()),
                Arguments.arguments(testIgnoreCase())
        );
    }

    private static Object[] testSimpleCase() {
        return new Object[]{
                "simple case",
                "<content>SensitiveData</content>",
                "<content>SensitiveD-xxxx</content>"
        };
    }

    private static Object[] testMinimumLength() {
        return new Object[]{
                "minimum length",
                "<content>S</content>",
                "<content>S-xxxx</content>"
        };
    }

    private static Object[] testContentSliced() {
        return new Object[]{
                "content sliced",
                "<content>SensitiveData fdfdfdfdfdfdfdfdfO@#23sdsxsxsxz ....",
                "<content>SensitiveD-xxxx"
        };
    }

    private static Object[] testIgnoreCase() {
        return new Object[]{
                "ignore case",
                "<Content>SensitiveData...</Content>",
                "<Content>SensitiveD-xxxx</Content>"
        };
    }
}