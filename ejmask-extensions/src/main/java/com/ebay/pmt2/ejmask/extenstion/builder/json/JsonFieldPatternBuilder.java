package com.ebay.pmt2.ejmask.extenstion.builder.json;


import com.ebay.pmt2.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * An implementation of IPatternBuilder to support sensitive JSON field, whose value need to be partially masked.
 *
 * @author prakv
 */
public class JsonFieldPatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/ZDQWod/5
    //unescaped string --------------------------> \"(%s)(\\*\"\s*:\s*\\*\")([^\"]{1,%d})[^\"]*(\\?\"|)
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,%d})[^\\\"]*(\\\\?\\\"|)";
    //group $1 = field name
    //group $2 =  ":" (with json serialization support)
    //group $3 =  masked sting
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2$3-xxxx$4";

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 1) {
            throw new IllegalArgumentException("visibleCharacters must be a possessive value instead of " + visibleCharacters);
        }
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames), visibleCharacters);
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
