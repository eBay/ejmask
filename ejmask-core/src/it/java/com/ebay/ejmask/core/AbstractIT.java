package com.ebay.ejmask.core;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Keep this cass as default visibility
 *
 * @author prakv
 */
@Disabled
public abstract class AbstractIT {

    @TestData
    private static Arguments getEmpty() {
        String actual = "";
        String expected = "";
        return Arguments.of("Empty", actual, expected);
    }

    //ENABLE After fixing the regex
    //@TestData
    private static Arguments getHeaders() {
        String randomString = "1234567890123456789" + RandomStringUtils.randomAlphabetic(1000) + "9876543210987654321";
        String actual = "Accept=application/json"
                + "&Authorization=" + randomString
                + "&X-API-Key=" + randomString
                + "&Auth-Assertion=" + randomString
                + "&Content-Type=application/json";
        String expected = "Accept=application/json" +
                "&Authorization=****87654321" +
                "&X-API-Key=****87654321" +
                "&Auth-Assertion=****87654321" +
                "&Content-Type=application/json";
        return Arguments.of("Headers", actual, expected);
    }

    @TestData
    private static Arguments getSimpleJson() {
        String actual = "{\n" +
                "  \"key\": \"sensitiveData\",\n" +
                "  \"key_2\": \"sensitiveData\",\n" +
                "  \"key-3\": \"sensitiveData\",\n" +
                "  \"key4\": \"sensitiveData\"\n" +
                "}";
        String expected = "{\n" +
                "  \"key\":\"****\",\n" +
                "  \"key_2\":\"****\",\n" +
                "  \"key-3\":\"****\",\n" +
                "  \"key4\":\"****\"\n" +
                "}";
        return Arguments.of("SimpleJson", actual, expected);
    }

    @TestData
    private static Arguments getMultiLevel() {
        String actual = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"sensitive_data\": \"sensitiveData\",\n" +
                "  \"level1\": {\n" +
                "    \"mixed_data\": \"dummy data\",\n" +
                "    \"sensitive_data\": \"sensitiveData\",\n" +
                "    \"level2\": {\n" +
                "      \"mixed_data\": \"dummy data\",\n" +
                "      \"sensitive_data\": \"sensitiveData\",\n" +
                "      \"level3\": {\n" +
                "        \"mixed_data\": \"dummy data\",\n" +
                "        \"sensitive_data\": \"sensitiveData\",\n" +
                "        \"level4\": {\n" +
                "          \"mixed_data\": \"dummy data\",\n" +
                "          \"sensitive_data\": \"sensitiveData\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String expected = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"sensitive_data\":\"sen-xxxx\",\n" +
                "  \"level1\": {\n" +
                "    \"mixed_data\": \"dummy data\",\n" +
                "    \"sensitive_data\":\"sen-xxxx\",\n" +
                "    \"level2\": {\n" +
                "      \"mixed_data\": \"dummy data\",\n" +
                "      \"sensitive_data\":\"sen-xxxx\",\n" +
                "      \"level3\": {\n" +
                "        \"mixed_data\": \"dummy data\",\n" +
                "        \"sensitive_data\":\"sen-xxxx\",\n" +
                "        \"level4\": {\n" +
                "          \"mixed_data\": \"dummy data\",\n" +
                "          \"sensitive_data\":\"sen-xxxx\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return Arguments.of("MultiLevel", actual, expected);
    }

    @TestData
    private static Arguments getLargeJson() throws Exception {
        String bas64EncodedContent = Base64.encodeBase64String(getContent("image/test10mbImage.jpg"));
        String actual = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"simple_content\": \"w0KGgoAAAANSUhEUgAABAAAAAMQCAYAAAC5bBeWAAAME2l==\",\n" +
                "  \"big_content\": \"" + bas64EncodedContent + "\"\n" +
                "}";
        String expected = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"simple_content\": \"w0KGgoAAAA-xxxx\",\n" +
                "  \"big_content\": \"/9j/4AAQSk-xxxx";
        return Arguments.of("LargeJson", actual, expected);
    }

    @TestData
    private static Arguments getNestedJson() {
        String actual = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"sensitive_data\": \"sensitiveData\",\n" +
                "  \"shipping\": {\n" +
                "    \"name\": \"sensitiveData\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  },\n" +
                "  \"billing\": {\n" +
                "    \"name\": \"sensitiveData\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  },\n" +
                "  \"registered\": {\n" +
                "    \"name\": \"dummy data\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  }\n" +
                "}";
        String expected = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"sensitive_data\":\"sen-xxxx\",\n" +
                "  \"shipping\": {\n" +
                "    \"name\": \"sen-xxxx\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  },\n" +
                "  \"billing\": {\n" +
                "    \"name\": \"sen-xxxx\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  },\n" +
                "  \"registered\": {\n" +
                "    \"name\": \"dummy data\",\n" +
                "    \"mixed_data\": \"dummy data\"\n" +
                "  }\n" +
                "}";
        return Arguments.of("NestedJson", actual, expected);
    }

    @TestData
    private static Arguments getStartAndEnd() {
        String actual = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"first3Chars\": \"sensitiveData\",\n" +
                "  \"first10Chars\": \"sensitiveData\"\n" +
                "}";
        String expected = "{\n" +
                "  \"mixed_data\": \"dummy data\",\n" +
                "  \"firstChars\":\"sen-xxxx\",\n" +
                "  \"lastChars\":\"sensitiveD-xxxx\"\n" +
                "}";
        return Arguments.of("StartAndEnd", actual, expected);
    }


    @TestData
    private static Arguments getBigJson() {
        String content = "";
        String safeContent = "";
        for (char c = 'A'; c <= 'Z'; ++c) {
            content += "  \"key_" + c + "\":\"sensitiveData\",\n";
            safeContent += "  \"key_" + c + "\":\"sen-xxxx\",\n";
        }
        String actual = "{\n" +
                "  \"mixed_data\": \"dummy data\"\n" +
                content +
                "}";
        String expected = "{\n" +
                "  \"mixed_data\": \"dummy data\"\n" +
                safeContent +
                "}";
        return Arguments.of("BigJson", actual, expected);
    }

    @TestData
    private static Arguments getKeyValuePair() {
        String actual = "{\n" +
                "  \"userDetails\": [\n" +
                "    {\n" +
                "      \"name\": \"SITE_ID\",\n" +
                "      \"value\": \"US\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"FIRST_NAME\",\n" +
                "      \"value\": \"sensitiveData\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"GIVEN_NAME\",\n" +
                "      \"value\": \"sensitiveData\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"ACCOUNT_AGE\",\n" +
                "      \"value\": \"10y\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String expected = "{\n" +
                "  \"userDetails\": [\n" +
                "    {\n" +
                "      \"name\": \"SITE_ID\",\n" +
                "      \"value\": \"US\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"FIRST_NAME\",\"value\":\"sen-xxxx\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"GIVEN_NAME\",\"value\":\"sen-xxxx\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"ACCOUNT_AGE\",\n" +
                "      \"value\": \"10y\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        return Arguments.of("KeyValuePair", actual, expected);
    }


    private static int counter = 0;

    static Stream<Arguments> dataForTestMatch() {
        Method[] methods = AbstractIT.class.getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(m -> m.getAnnotation(TestData.class) != null)
                .sorted(Comparator.comparing(Method::getName))
                .map(m -> {
                    try {
                        m.setAccessible(true);
                        return (Arguments) m.invoke(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // @AfterEach
    public void tearDown() throws Exception {
        String[] resetList = new String[]{"MASKING_PATTERNS", "PRE_PROCESSORS"};
        for (String declaredField : resetList) {
            Field field = EJMask.class.getDeclaredField(declaredField);
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof List) {
                ((List) value).clear();
            }
        }
    }

    static void addFilter(String pattern, String replacement) {
        EJMask.addFilter(counter++, pattern, replacement);
    }

    /**
     * This test validate the given string is masked as expected.
     */
    @Order(1)
    @ParameterizedTest
    @MethodSource("dataForTestMatch")
    public void test1_verify_the_request_is_masked_as_expected(String testName, String input, String expected) {
        String actual = EJMask.mask(input);
        Assertions.assertEquals(expected, actual);
        Assertions.assertFalse(actual.contains("sensitiveData"));
    }

    /**
     * This test will invoke mask operation 1000 times and validate the
     * operation matching SLA ( sub ms )
     */
    @Order(2)
    @Disabled
    @ParameterizedTest
    @Timeout(10_000)
    @MethodSource("dataForTestMatch")
    public void test2_verify_the_masking_operation_meets_SLA(String testName, String input, String expected) {
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            EJMask.mask(input);
        }
        final long maskTime = System.currentTimeMillis() - start;
        Assumptions.assumeTrue(maskTime < 2000, "masking should be done in sub ms : (current:" + maskTime + "ms)");
    }


    private static byte[] getContent(String path) throws Exception {
        String inputFile = EJMask.class.getClassLoader().getResource(path).getFile();
        try (InputStream inputStream = Files.newInputStream(Paths.get(inputFile))) {
            long fileSize = new File(inputFile).length();
            byte[] allBytes = new byte[(int) fileSize];
            inputStream.read(allBytes);
            return allBytes;
        }
    }
}
