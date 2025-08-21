package com.ebay.ejmask.extenstion.builder.json;

import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * JsonBodyPatternBuilder is used to build regex pattern for JSON body(without embed body) masking.
 *
 * @author yanchai
 * @since 08/14/25
 */
public class JsonBodyPatternBuilder extends AbstractRegexPatternBuilder {

    // https://regex101.com/r/s06mba/1
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\{)[^\\{\\}]*(\\})";
    // group $1 = field name
    // group $2 =  ": {" (with json serialization support)
    // group $3 =  sensitive information -> ignore
    // group $4 =  ending }
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2\"****\":\"****\"$3";

    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters != 0) {
            throw new IllegalArgumentException("visibleCharacters must be 0 instead of " + visibleCharacters);
        }
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames));
    }

    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return REPLACEMENT_TEMPLATE;
    }
}
