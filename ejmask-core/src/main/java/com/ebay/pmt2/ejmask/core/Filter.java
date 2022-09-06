package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IPatternBuilder;

import java.util.LinkedList;
import java.util.List;

class Filter {

    private final int order;
    private final int visibleCharacters;
    private final IPatternBuilder builder;
    private final List<String> fieldNames;

    /**
     * Create new instance of Filter
     *
     * @param order             new value of order
     * @param visibleCharacters new value of visibleCharacters
     */
    Filter(int order, int visibleCharacters, IPatternBuilder builder) {
        this.order = order;
        this.visibleCharacters = visibleCharacters;
        this.builder = builder;
        this.fieldNames = new LinkedList<>();
    }

    /**
     * Get the value of order
     *
     * @return the value of order
     */
    int getOrder() {
        return this.order;
    }

    /**
     * Get the value of visibleCharacters
     *
     * @return the value of visibleCharacters
     */
    int getVisibleCharacters() {
        return this.visibleCharacters;
    }

    /**
     * Get the value of fieldNames
     *
     * @return the value of fieldNames
     */
    List<String> getFieldNames() {
        return this.fieldNames;
    }

    /**
     * Add the value of fieldName
     *
     * @param fieldName new value of fieldName
     */
    void add(String fieldName) {
        this.fieldNames.add(fieldName);
    }

    /**
     * Get the value of builder
     *
     * @return the value of builder
     */
    IPatternBuilder getBuilder() {
        return this.builder;
    }
}
