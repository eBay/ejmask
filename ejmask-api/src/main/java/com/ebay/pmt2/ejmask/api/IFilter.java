package com.ebay.pmt2.ejmask.api;

/**
 * Defines a Basic filter
 *
 * @author prakv
 */
public interface IFilter {

    /**
     * Get the value of group
     *
     * @return the value of group
     */
    default String getGroup() {
        return "ejmask";
    }

    /**
     * Get the value of order
     *
     * @return the value of order
     */
    default int getOrder() {
        return 10;
    }

    /**
     * Get the value of visibleChar
     *
     * @return the value of visibleChar
     */
    default int getVisibleCharacters() {
        return 4;
    }

    /**
     * Get the value of patternBuilder
     *
     * @return the value of patternBuilder
     */
    Class<? extends IPatternBuilder> getPatternBuilder();

    /**
     * Get the value of fieldName
     *
     * @return the value of fieldName
     */
    String[] getFieldNames();
}
