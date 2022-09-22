package com.ebay.pmt2.ejmask.extenstion.builder.xml;

import com.ebay.pmt2.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * <content>JVBERi0xLjQKJdPr6eEKMSAwIG9iago8PC9DcmVhdG9yIChNb3ppbGxhLzUuMCBcKE1hY2ludG9zaDsgSW50ZWwgTWF...</content>
 */
public class XmlFieldPattenBuilder extends AbstractRegexPatternBuilder {

    /**
     * <pre>
     * (?iu)                      i --enable ignore case, u --enable unicode support
     * (<content>)                match <content> pattern as 1 group
     *  ([^<]{1,10})[^<]*         get pattern without '<' char , get atmost 10 chars until '<' found
     * (<|)                       match '<' or not as a group
     */
    private static final String PATTERN_TEMPLATE = "(?iu)(<%s>)([^<]{1,%d})[^<]*(<|)";
    private static final String REPLACEMENT_TEMPLATE = "$1$2-xxxx$3";

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
        String fields = super.buildFieldNamesForRegexOr(fieldNames);
        return String.format(PATTERN_TEMPLATE, fields, visibleCharacters);
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
