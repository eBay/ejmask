package com.ebay.ejmask.core;


import org.junit.jupiter.api.BeforeAll;

/**
 * Integration test to ensure grouping of filedName really works with improved performance.
 * The test to verify execution SLA must pass for this test.
 *
 * @author prakv
 */
public class GroupedIT extends AbstractIT {

    @BeforeAll
    public static void setUp() {
        String charX = "";
        // Big json
        for (char c = 'A'; c <= 'Z'; ++c) {
            charX += (charX.length() > 0) ? "|" + "key_" + c : "key_" + c;
        }
        addFilter("\\\\?\"(" + charX + ")\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*(\\\\?\"|)", "\"$1\":\"$2-xxxx\"");
        // header
        addFilter("(Authorization|X-API-Key|Auth-Assertion)=([^\\&\"]{1,10})*", "$1=****$2");
        //KeyValuePair
        addFilter("\\\\?\"(FIRST_NAME|GIVEN_NAME)\",\\s*\"value\"\\s*:\\s*\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\",\"value\":\"$2-xxxx\"");
        //Large Json
        addFilter("\\\\?\"(big_content|simple_content)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{0,10})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        //SimpleJson - Full Mask
        addFilter("\\\\?\"(key|key_2|key-3|key4)\\\\?\"\\s*:\\s*\\\\?\"[^\"]*\\\\?\"", "\"$1\":\"****\"");
        // MultiLevel
        addFilter("\\\\?\"(sensitive_data)\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\":\"$2-xxxx\"");//Nested
        //Nested
        addFilter("(\\\\*\\\"(shipping|billing)\\\\*\\\"[^\\}]*\\\\*\\\"name\\\\*\\\"[\\s\\t\\n\\r]*:[\\s\\t\\n\\r]*\\\\*\\\")([^\\\"]{1,3})[^\\\"\\\\]*(\\\\*\\\"*)", "$1$3-xxxx$4");
        //Fist and last
        addFilter("\\\\?\"first3Chars\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"firstChars\":\"$1-xxxx\"");
        addFilter("\\\\?\"first10Chars\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,10})[^\"]*(\\\\?\"|)", "\"lastChars\":\"$1-xxxx\"");
        addFilter("\\\\?\"email\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\":\"$2-xxxx\"");
        //for big object
        EJMask.register(new DummyProcessor());
    }
}
