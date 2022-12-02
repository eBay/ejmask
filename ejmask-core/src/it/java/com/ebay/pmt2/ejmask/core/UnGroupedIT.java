package com.ebay.pmt2.ejmask.core;

import org.junit.jupiter.api.BeforeEach;

/**
 * Integration test to ensure old DataFilter patterns will work as it is with the new framework.
 * Note : replacement expression got standardized to make other test pass.
 * <p>
 * The test to verify execution SLA may fail for this test.
 *
 * @author prakv
 */
public class UnGroupedIT extends AbstractIT {

    @BeforeEach
    public void setUp() {
        // Big json
        for (char c = 'A'; c <= 'Z'; ++c) {
            addFilter("\\\\?\"key_" + c + "\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*(\\\\?\"|)", "\"key_" + c + "\":\"$1-xxxx\"");
        }
        // Headers
        addFilter("Authorization=([^\\&\"]{1,10})*", "Authorization=****$1");
        addFilter("X-API-Key=([^\\&\"]{1,10})*", "X-API-Key=****$1");
        addFilter("Auth-Assertion=([^\\&\"]{1,10})*", "Auth-Assertion=****$1");
        //KeyValuePair
        addFilter("\\\\?\"FIRST_NAME\",\\s*\"value\"\\s*:\\s*\"([^\"]{0,3})[^\"]*\\\\?\"", "\"FIRST_NAME\",\"value\":\"$1-xxxx\"");
        addFilter("\\\\?\"GIVEN_NAME\",\\s*\"value\"\\s*:\\s*\"([^\"]{0,3})[^\"]*\\\\?\"", "\"GIVEN_NAME\",\"value\":\"$1-xxxx\"");
        //Large Json
        addFilter("\\\\?\"simple_content(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{0,10})[^\\\"]*(\\\\?\\\"|)", "\"simple_content$1$2-xxxx$3");
        addFilter("\\\\?\"big_content(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{0,10})[^\\\"]*(\\\\?\\\"|)", "\"big_content$1$2-xxxx$3");
        //SimpleJson - Full Mask
        addFilter("\\\\?\"key\\\\?\"\\s*:\\s*\\\\?\"[^\"]*\\\\?\"", "\"key\":\"****\"");
        addFilter("\\\\?\"key_2\\\\?\"\\s*:\\s*\\\\?\"[^\"]*\\\\?\"", "\"key_2\":\"****\"");
        addFilter("\\\\?\"key-3\\\\?\"\\s*:\\s*\\\\?\"[^\"]*\\\\?\"", "\"key-3\":\"****\"");
        addFilter("\\\\?\"key4\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"key4\":\"****\"");
        // MultiLevel
        addFilter("\\\\?\"sensitive_data\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"sensitive_data\":\"$1-xxxx\"");
        //Nested
        addFilter("(\\\\*\\\"shipping\\\\*\\\"[^\\}]*\\\\*\\\"name\\\\*\\\"[\\s\\t\\n\\r]*:[\\s\\t\\n\\r]*\\\\*\\\")([^\\\"]{1,3})[^\\\"\\\\]*(\\\\*\\\"*)", "$1$2-xxxx$3");
        addFilter("(\\\\*\\\"billing\\\\*\\\"[^\\}]*\\\\*\\\"name\\\\*\\\"[\\s\\t\\n\\r]*:[\\s\\t\\n\\r]*\\\\*\\\")([^\\\"]{1,3})[^\\\"\\\\]*(\\\\*\\\"*)", "$1$2-xxxx$3");
        //Fist and last
        addFilter("\\\\?\"first3Chars\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"firstChars\":\"$1-xxxx\"");
        addFilter("\\\\?\"first10Chars\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,10})[^\"]*(\\\\?\"|)", "\"lastChars\":\"$1-xxxx\"");
        addFilter("\\\\?\"email\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\":\"$2-xxxx\"");
        // junk
        for (char c = 'A'; c <= 'Z'; ++c) {
            addFilter("\\\\?\"wrong_key_" + c + "\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,10})[^\"]*(\\\\?\"|)", "\"wrong_key_" + c + "\":\"$1-xxxx\"");
        }
        //
        EJMask.register(new DummyProcessor());
    }
}
