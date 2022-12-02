package com.ebay.pmt2.ejmask.extenstion.builder.json;

import com.ebay.pmt2.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * An implementation of IPatternBuilder to support high sensitive JSON field, whose value need to be fully masked.
 *
 * @author prakv
 */
public class JsonFullValuePatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/ZDQWod/7
    //todo this may need optimization as this breaks the encoded json syntax though it removes sensitive data.
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,1})[^\\\"]*(\\\\?\\\"|)";
    //group $1 = field name
    //group $2 =  ":" (with json serialization support)
    //group $3 =  sensitive information -> ignore
    //group $4 =  ending qts
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2****$4";

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters != 0) {
            throw new IllegalArgumentException("visibleCharacters must be 0 instead of " + visibleCharacters);
        }
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Build pattern to replace.
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return REPLACEMENT_TEMPLATE;
    }
}
