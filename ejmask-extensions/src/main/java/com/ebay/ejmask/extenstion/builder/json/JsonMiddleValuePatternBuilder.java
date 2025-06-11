package com.ebay.ejmask.extenstion.builder.json;

import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * An implementation of IPatternBuilder to support JSON fields, masking characters between the first N/2 and last N/2 characters of the field value.
 */
public class JsonMiddleValuePatternBuilder extends AbstractRegexPatternBuilder {

    /**
     * Build pattern to match.
     *
     * @param visibleCharacters as the number of characters to be visible (should be even and >= 2).
     * @param fieldNames       as list of field names.
     * @return match pattern.
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 2 || visibleCharacters % 2 != 0) {
            throw new IllegalArgumentException("visibleCharacters must be an even number greater than or equal to 2.");
        }
        int half = visibleCharacters / 2;
        String patternTemplate = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{" + half + "})([^\\\"]+)([^\\\"]{" + half + "})(\\\\?\\\"|)";

        return String.format(patternTemplate, super.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Build pattern to replace.
     *
     * @param visibleCharacters as the number of characters to be visible (should be even and >= 2).
     * @param fieldNames        as list of field names.
     * @return replacement pattern.
     */
    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 2 || visibleCharacters % 2 != 0) {
            throw new IllegalArgumentException("visibleCharacters must be an even number greater than or equal to 2.");
        }
        return "\"$1$2$3****$5$6";
    }
}