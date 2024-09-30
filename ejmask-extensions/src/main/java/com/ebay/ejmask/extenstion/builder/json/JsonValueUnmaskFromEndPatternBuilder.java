package com.ebay.ejmask.extenstion.builder.json;

import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * An implementation of IPatternBuilder to support high sensitive JSON field, whose value
 * need to be partially masked with relative field from end of the field.
 *
 * @author parasdharwal33
 */
public class JsonValueUnmaskFromEndPatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/LL3EY7/1/
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")[^\\\"]*([^\\\\\"]{%d})(\\\\?\\\"|)";
    //group $1 = field name
    //group $2 =  ":" (with json serialization support)
    //group $3 =  sensitive information -> unmasked
    //group $4 =  ending qts
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2xxxx-$3$4";

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 0) {
            throw new IllegalArgumentException("visibleCharacters must be a value greater than zero instead of " + visibleCharacters);
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