package com.ebay.pmt2.ejmask.extenstion.builder.json;

import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;

/**
 * An implementation of IPatternBuilder to support sensitive JSON field, who's
 * value need to be partially masked with relative field
 *
 * @author manperumal
 */
public class JsonRelativeFieldPatternBuilder implements IPatternBuilder {

    /**
     * ignores any space, new line multiple times
     */
    private static final String SKIP_SPACE_TAB_NEWLINE = "[\\s\\t\\n\\r]*";
    /**
     * <pre>
     * (?ui)               - enable ignore case, unicode
     *  \"user"[^\}]
     *  "name\"   matches   "user" followed any char other than "}", then "name"
     * ([^"]{1,n})       matches any char othen than " , at most n char, at least 0
     */
    private static final String PATTERN_TEMPLATE = "(?ui)(\"%s\"[^\\}]*\"%s\"" + SKIP_SPACE_TAB_NEWLINE
            + ":" + SKIP_SPACE_TAB_NEWLINE + "\")([^\"]{1,%d})[^\"]*([\"|]?)";

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
        return this.buildRegex(visibleCharacters, fieldNames);
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

    @Override
    public boolean isGroupable() {
        return false;
    }

    /**
     * Convert the given list of fieldNames to regex or condition strings
     *
     * @param visibleCharacters as no of char to
     * @param fieldNames        as an array of fieldNames
     * @return single regex or condition
     */
    protected String buildRegex(int visibleCharacters, String... fieldNames) {
        if (CommonUtils.isAnEmptyArray(fieldNames)) {
            throw new IllegalArgumentException("empty / or more than 2 fieldNames is not allowed.");
        }
        return String.format(PATTERN_TEMPLATE, fieldNames[0], fieldNames[1], visibleCharacters);
    }
}
