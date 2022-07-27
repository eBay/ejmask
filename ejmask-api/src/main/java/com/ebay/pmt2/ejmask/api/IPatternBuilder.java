package com.ebay.pmt2.ejmask.api;

public interface IPatternBuilder {

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    String buildPattern(int visibleCharacters, String... fieldNames);

    /**
     * Build pattern to replace.
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    String buildReplacement(int visibleCharacters, String... fieldNames);

    /**
     * Set true if the build can be groupable.
     *
     * @return true if groupable
     */
    default boolean isGroupable() {
        return true;
    }
}
