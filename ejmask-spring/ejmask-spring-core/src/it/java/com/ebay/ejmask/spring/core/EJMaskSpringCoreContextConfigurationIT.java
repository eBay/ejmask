package com.ebay.ejmask.spring.core;

import com.ebay.ejmask.core.EJMask;
import com.ebay.ejmask.core.EJMaskInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Objective of this Integration test is ensure that all spring bean wiring works as expected.
 * The test to verify execution SLA must pass for this test.
 *
 * @author prakv
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FilterContextConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EJMaskSpringCoreContextConfigurationIT {

    @BeforeEach
    public void setUp() throws Exception {
        new TestContextManager(this.getClass()).prepareTestInstance(this);
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("Verify the request is masked as expected")
    public void test1_verify_the_request_is_masked_as_expected(String testName, String input, String expected) {
        Assertions.assertFalse(EJMaskInitializer.getMaskingPatterns().isEmpty());
        Assertions.assertFalse(EJMaskInitializer.getContentPreProcessors().isEmpty());
        //
        String actual = EJMask.mask(input);
        Assertions.assertEquals(expected, actual);
        Assertions.assertFalse(actual.contains("sensitiveData"));
    }

    @Order(3)
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("Verify the request is masked as expected even if the string got json serialized")
    public void test2_verify_the_request_is_masked_as_expected_even_if_the_string_got_json_serialized(String testName, String input, String expected) throws Exception {
        Assertions.assertFalse(EJMaskInitializer.getMaskingPatterns().isEmpty());
        Assertions.assertFalse(EJMaskInitializer.getContentPreProcessors().isEmpty());
        //
        String actual = EJMask.mask(new ObjectMapper().writeValueAsString(input));
        Assumptions.assumeFalse(actual.contains("sensitiveData"));
    }

    @Order(4)
    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("Verify the masking operation meets SLA")
    public void test3_verify_the_masking_operation_meets_SLA(String testName, String input, String expected) {
        Assertions.assertFalse(EJMaskInitializer.getMaskingPatterns().isEmpty());
        Assertions.assertFalse(EJMaskInitializer.getContentPreProcessors().isEmpty());
        //
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            EJMask.mask(input);
        }
        long maskTime = System.currentTimeMillis() - start;
        Assumptions.assumeTrue(maskTime < 2000, "masking should be done in sub ms : (current:" + maskTime + "ms)");
    }

    static Stream<Object[]> data() throws Exception {
        return Stream.of(
                new Object[]{"testEmpty", "", ""},
                testHTTPHeaders(),
                testJson(),
                testFlatString(),
                test_big_string(),
                test_largeData()
        );
    }

    private static Object[] testHTTPHeaders() {
        String apiKey = "1234567890123456789" + RandomStringUtils.randomAlphabetic(1000) + "9876543210987654321";
        String actual = "Accept=application/json,application/json"
                + "&Authorization=" + apiKey
                + "&X-API-Key=" + apiKey
                + "&Auth-Assertion=" + apiKey
                + "&Content-Type=application/json";

        String expected = "Accept=application/json,application/json" +
                "&Authorization=xxxx-87654321" +
                "&X-API-Key=xxxx-87654321" +
                "&Auth-Assertion=xxxx-87654321" +
                "&Content-Type=application/json";
        return new Object[]{"testHTTPHeaders", actual, expected};
    }


    private static Object[] testFlatString() {
        String actual = "insensitiv:afasdf\n"
                + ",Authorization=Basic 9876543210987654321&\n"
                + ",x-api-key=Bearer 9876543210987654321&\n"
                + ",\"city\":\"sensitiveData\"\n"
                + ",\"postalCode\":\"sensitiveData\"\n"
                + ",\"recipient\":\"sensitiveData\"\n"
                + ",\"addressLine1\":\"sensitiveData\"\n"
                + ",\"addressLine2\":\"sensitiveData\"\n"
                + ",\"phoneNumber\":\"sensitiveData\"\n"
                + ",\"telephoneNumber\":\"sensitiveData\"\n"
                + ",\"holderName\":\"sensitiveData\"\n"
                + ",\"number\":\"sensitiveData\"\n"
                + ",\"cvc\":\"sensitiveData\"\n"
                + ",\"cavv\":\"sensitiveData\"\n"
                + ",\"merchantAccount\":\"sensitiveData\"\n"
                + ",\"transferCode\":\"CREDIT_INCENTIVE_PAYMENT\"\n"
                + ",\"sourceAccountCode\":\"1111111\"\n"
                + ",\"destinationAccountCode\":\"2222222\"\n"
                + ",\"accountHolderCode\":\"333333\"\n"
                + ",\"accountCode\":\"4444444\"\n"
                + ",\"bankAccountUUID\":\"5555abcd-5555abcd\"\n"
                + ",\"split.item1.account\":\"666666\"\n"
                + ",\"account\":\"777777\"\n"
                + ",\"threeds2.threeDS2Result.authenticationValue\":\"3q2+78r+ur7erb7vyv66vv////8=\"\n"
                + ",\"emailAddress\":\"sensitiveData@email.com\"\n"
                + ",\"expiryMonth\":\"sensitiveData\"\n"
                + ",\"expiryYear\":\"sensitiveData\"\n"
                + ",\"houseNumberOrName\":\"sensitiveData\"\n"
                + ",\"street\":\"sensitiveData\"\n"
                + ",\"county\":\"sensitiveData\"\n"
                + ",\"cardHolderName\":\"sensitiveData\"\n"
                + ",\"errorDescription\":\"sensitiveData\"\n"
                + ",\"expiryDate\":\"sensitiveData\"\n"
                + ",\"shopperEmail\":\"sensitiveData@email.com\"\n"
                + ",\"businessEmail\":\"sensitiveData@email.com\"\n";

        String expected = "insensitiv:afasdf\n" +
                ",Authorization=xxxx-54321&\n" +
                ",x-api-key=xxxx-654321&\n" +
                ",\"city\":\"sen-xxxx\"\n" +
                ",\"postalCode\":\"sen-xxxx\"\n" +
                ",\"recipient\":\"****\"\n" +
                ",\"addressLine1\":\"sen-xxxx\"\n" +
                ",\"addressLine2\":\"sen-xxxx\"\n" +
                ",\"phoneNumber\":\"sen-xxxx\"\n" +
                ",\"telephoneNumber\":\"sen-xxxx\"\n" +
                ",\"holderName\":\"sen-xxxx\"\n" +
                ",\"number\":\"****\"\n" +
                ",\"cvc\":\"****\"\n" +
                ",\"cavv\":\"sen-xxxx\"\n" +
                ",\"merchantAccount\":\"sen-xxxx\"\n" +
                ",\"transferCode\":\"CRE-xxxx\"\n" +
                ",\"sourceAccountCode\":\"111-xxxx\"\n" +
                ",\"destinationAccountCode\":\"222-xxxx\"\n" +
                ",\"accountHolderCode\":\"333-xxxx\"\n" +
                ",\"accountCode\":\"444-xxxx\"\n" +
                ",\"bankAccountUUID\":\"555-xxxx\"\n" +
                ",\"split.item1.account\":\"666-xxxx\"\n" +
                ",\"account\":\"777-xxxx\"\n" +
                ",\"threeds2.threeDS2Result.authenticationValue\":\"3q2-xxxx\"\n" +
                ",\"emailAddress\":\"sen-xxxx\"\n" +
                ",\"expiryMonth\":\"****\"\n" +
                ",\"expiryYear\":\"****\"\n" +
                ",\"houseNumberOrName\":\"sen-xxxx\"\n" +
                ",\"street\":\"sen-xxxx\"\n" +
                ",\"county\":\"sen-xxxx\"\n" +
                ",\"cardHolderName\":\"****\"\n" +
                ",\"errorDescription\":\"sen-xxxx\"\n" +
                ",\"expiryDate\":\"****\"\n" +
                ",\"shopperEmail\":\"sen-xxxx\"\n" +
                ",\"businessEmail\":\"sen-xxxx\"\n";

        return new Object[]{"testFlatString", actual, expected};
    }

    private static Object[] testJson() {
        String actual = "{\n" +
                "  \"accountHolderDetails\": {\n" +
                "    \"address\": {\n" +
                "      \"city\":\"sensitiveData\",\n" +
                "      \"country\":\"US\",\n" +
                "      \"houseNumberOrName\":\"sensitiveData\",\n" +
                "      \"postalCode\":\"sensitiveData\",\n" +
                "      \"stateOrProvince\":\"CA\",\n" +
                "      \"street\":\"sensitiveData\"\n" +
                "    },\n" +
                "    \"bankAccountDetails\": [\n" +
                "      {\n" +
                "        \"BankAccountDetail\": {\n" +
                "          \"accountNumber\":\"sensitiveData\",\n" +
                "          \"bankAccountUUID\":\"sensitiveData-sensitiveData\",\n" +
                "          \"bankName\":\"sensitiveData\",\n" +
                "          \"branchCode\":\"sensitiveData\",\n" +
                "          \"bankCode\":\"sensitiveData\",\n" +
                "          \"countryCode\":\"US\",\n" +
                "          \"currencyCode\":\"USD\",\n" +
                "          \"ownerCity\":\"sensitiveData\",\n" +
                "          \"ownerDateOfBirth\":\"sensitiveData\",\n" +
                "          \"ownerNationality\":\"US\",\n" +
                "          \"ownerCountryCode\":\"US\",\n" +
                "          \"ownerHouseNumberOrName\":\"sensitiveData\",\n" +
                "          \"ownerName\":\"sensitiveData\",\n" +
                "          \"ownerPostalCode\":\"sensitiveData\",\n" +
                "          \"ownerState\":\"CA\",\n" +
                "          \"ownerStreet\":\"sensitiveData\",\n" +
                "          \"primaryAccount\":\"false\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"email\":\"sensitiveData@adyen.com\",\n" +
                "    \"individualDetails\": {\n" +
                "      \"name\": {\n" +
                "        \"firstName\":\"sensitiveData\",\n" +
                "        \"gender\":\"UNKNOWN\",\n" +
                "        \"lastName\":\"sensitiveData\"\n" +
                "      },\n" +
                "      \"personalData\": {\n" +
                "        \"dateOfBirth\":\"sensitiveData\",\n" +
                "        \"idNumber\":\"sensitiveData\",\n" +
                "        \"nationality\":\"US\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phoneNumber\": {\n" +
                "      \"phoneCountryCode\":\"US\",\n" +
                "      \"phoneNumber\":\"sensitiveData\",\n" +
                "      \"phoneType\":\"Mobile\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"verification\": {\n" +
                "    \"bankAccounts\": [\n" +
                "      {\n" +
                "        \"bankAccountUUID\":\"sensitiveData-sensitiveData\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        String expected = "{\n" +
                "  \"accountHolderDetails\": {\n" +
                "    \"address\": {\n" +
                "      \"city\":\"sen-xxxx\",\n" +
                "      \"country\":\"US\",\n" +
                "      \"houseNumberOrName\":\"sen-xxxx\",\n" +
                "      \"postalCode\":\"sen-xxxx\",\n" +
                "      \"stateOrProvince\":\"CA\",\n" +
                "      \"street\":\"sen-xxxx\"\n" +
                "    },\n" +
                "    \"bankAccountDetails\": [\n" +
                "      {\n" +
                "        \"BankAccountDetail\": {\n" +
                "          \"accountNumber\":\"sen-xxxx\",\n" +
                "          \"bankAccountUUID\":\"sen-xxxx\",\n" +
                "          \"bankName\":\"****\",\n" +
                "          \"branchCode\":\"sen-xxxx\",\n" +
                "          \"bankCode\":\"****\",\n" +
                "          \"countryCode\":\"US\",\n" +
                "          \"currencyCode\":\"USD\",\n" +
                "          \"ownerCity\":\"sen-xxxx\",\n" +
                "          \"ownerDateOfBirth\":\"****\",\n" +
                "          \"ownerNationality\":\"US\",\n" +
                "          \"ownerCountryCode\":\"US\",\n" +
                "          \"ownerHouseNumberOrName\":\"sen-xxxx\",\n" +
                "          \"ownerName\":\"sen-xxxx\",\n" +
                "          \"ownerPostalCode\":\"sen-xxxx\",\n" +
                "          \"ownerState\":\"****\",\n" +
                "          \"ownerStreet\":\"sen-xxxx\",\n" +
                "          \"primaryAccount\":\"****\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"email\":\"sen-xxxx\",\n" +
                "    \"individualDetails\": {\n" +
                "      \"name\": {\n" +
                "        \"firstName\":\"sen-xxxx\",\n" +
                "        \"gender\":\"UNKNOWN\",\n" +
                "        \"lastName\":\"sen-xxxx\"\n" +
                "      },\n" +
                "      \"personalData\": {\n" +
                "        \"dateOfBirth\":\"****\",\n" +
                "        \"idNumber\":\"****\",\n" +
                "        \"nationality\":\"US\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phoneNumber\": {\n" +
                "      \"phoneCountryCode\":\"US\",\n" +
                "      \"phoneNumber\":\"sen-xxxx\",\n" +
                "      \"phoneType\":\"Mobile\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"verification\": {\n" +
                "    \"bankAccounts\": [\n" +
                "      {\n" +
                "        \"bankAccountUUID\":\"sen-xxxx\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        return new Object[]{"testJson", actual, expected};
    }

    private static Object[] test_big_string() {
        String actual = "{\n"
                + "  \"userId\": 1360951038,\n"
                + "  \"processorName\":\"ADYEN\",\n"
                + "  \"requestId\":\"5134860816\",\n"
                + "  \"verificationEntity\":{\n"
                + "    \"id\":\"DD_105544458\",\n"
                + "    \"type\":\"PAYOUT_ACCOUNT\"\n"
                + "  },\n"
                + "  \"document\":{\n"
                + "    \"type\":\"BANK_STATEMENT\",\n"
                + "    \"filename\":\"1.png\",\n"
                + "    \"description\":\"\",\n"
                + "    \"contentEncoding\":\"BASE_64\",\n"
                + "    \"documentContent\":\"w0KGgoAAAANSUhEUgAABAAAAAMQCAYAAAC5bBeWAAAME2l==\",\n"
                + "    \"content\":\"iVBORw0KGgoAAAANSUhEUgAABAAAAAMQCAYAAAC5bBeWAAAME2lDQ1BJQ0MgUHJvZmlsZQAASImVVwdUk8kWnr+kEBICBCIgJfQmSJEuNVRBQDrYCEmAUAIkBBU7sqjgihULVnRVRNG1AGLHriwK9vpAREVZFws21LxJAV1fO++eM/983Ln3zndvZoYZAOgOnPz8HFQdgFxhoSgmJICVlJzCIj0GGCAAGmABdQ5XnO8fHR0BoAz1f5f3twAi66/byWL96/h/FQ0eX8wFAImGOI0n5uZCfAgAXIebLyoEgNAK9abTCvNluB9iLREkCAARl+EMBdaR4TQFHiW3iYthQ+wHAJnK4YgyAFCT8WYVcTNgHDUZRwchTyCEeAPEPtxMDg/iBxCPys3Ng5hOhtgq7Yc4GX+LmTYck8PJGMaKXORCDhSI83M4M/7Pcvxvyc2RDM1hAhs1UxQaI8sZ1m1Xdl64DFMhPiZMi4yCWBPiiwKe3F6G72VKQuOV9n1cMRvWDDABQAGPExgOsT7ETEl2vL8SO3FEcl9oj0YKCsPilDhNlBejjI8WCXMiI5RxFmXyw4bwJr44KHbIJl0QHAYxXGnooeLMuEQFT/RskSAhEmI1iK+Js2PDlb6PijPZkUM2IkmMjLMZxO/SRcExChtMJ1c8lBdmz+XI54JrAfMrzIwLVfhiSXxxUsQQBx4/MEjBAePxhfFKbhhcXQExSt+y/JxopT22iZ8TEqOoM7ZfXBQ75NtRCBeYog7Y4yzOuGjlXO/zC6PjFNxwFEQANgiEO0gCWxrIA1lA0NbX2Af/UowEAw4QgQzAB3ZKzZBHonxECL+xoBj8CREfiIf9AuSjfFAE9V+HtYqvHUiXjxbJPbLBU4hzcT3cB/fCI+DXDzYn3B33GPJj0YdmJQYRA4mhxGCi9TAPLmSdA5sICP6NLhz2fJidjItwK==\"\n"
                + "  }\n"
                + "}";
        String expected = "{\n"
                + "  \"userId\": 1360951038,\n"
                + "  \"processorName\":\"ADYEN\",\n"
                + "  \"requestId\":\"5134860816\",\n"
                + "  \"verificationEntity\":{\n"
                + "    \"id\":\"DD_105544458\",\n"
                + "    \"type\":\"PAYOUT_ACCOUNT\"\n"
                + "  },\n"
                + "  \"document\":{\n"
                + "    \"type\":\"BANK_STATEMENT\",\n"
                + "    \"filename\":\"1.png\",\n"
                + "    \"description\":\"\",\n"
                + "    \"contentEncoding\":\"BASE_64\",\n"
                + "    \"documentContent\":\"w0KGgoAAAA-xxxx\",\n"
                + "    \"content\":\"iVBORw0KGg-xxxx\"\n"
                + "  }\n"
                + "}";

        return new Object[]{"test_big_string", actual, expected};
    }

    private static Object[] test_largeData() throws IOException {
        String inputFile = new EJMask().getClass().getClassLoader().getResource("image/test10mbImage.jpg").getFile();
        InputStream inputStream = new FileInputStream(inputFile);
        long fileSize = new File(inputFile).length();
        byte[] allBytes = new byte[(int) fileSize];
        inputStream.read(allBytes);
        inputStream.close();
        String bas64EncodedContent = Base64.encodeBase64String(allBytes);
        String actual = "{" + "  \"userId\": 1360951038," + "  \"processorName\":\"ADYEN\","
                + "  \"requestId\":\"5134860816\","
                + "  \"verificationEntity\":{" + "    \"id\":\"DD_105544458\","
                + "    \"type\":\"PAYOUT_ACCOUNT\"" + "  },"
                + "  \"document\":{" + "    \"type\":\"BANK_STATEMENT\","
                + "    \"filename\":\"1.png\"," + "    \"description\":\"\","
                + "    \"contentEncoding\":\"BASE_64\","
                + "    \"documentContent\":\"w0KGgoAAAANSUhEUgAABAAAAAMQCAYAAAC5bBeWAAAME2l==\","
                + "    \"content\":\""
                + bas64EncodedContent
                + "\"" + "  }" + "}";
        String expected = "{" + "  \"userId\": 1360951038," + "  \"processorName\":\"ADYEN\","
                + "  \"requestId\":\"5134860816\","
                + "  \"verificationEntity\":{" + "    \"id\":\"DD_105544458\","
                + "    \"type\":\"PAYOUT_ACCOUNT\"" + "  },"
                + "  \"document\":{" + "    \"type\":\"BANK_STATEMENT\","
                + "    \"filename\":\"1.png\"," + "    \"description\":\"\","
                + "    \"contentEncoding\":\"BASE_64\","
                + "    \"documentContent\":\"w0KGgoAAAA-xxxx\","
                + "    \"content\":\"/9j/4AAQSk-xxxx";
        return new Object[]{"test_largeData", actual, expected};
    }
}