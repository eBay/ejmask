package com.ebay.pmt2.ejmask.extenstion.builder;

import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;

import java.util.HashSet;

/**
 * Host common builder helper methods.
 */
public abstract class AbstractRegexPatternBuilder implements IPatternBuilder {

    private static final String BLANK = "";
    private static final String SEPARATOR = "|";
    private static final int DEFAULT_CAPACITY = 500;

    /**
     * Convert the given list of fieldNames to regex or condition strings
     *
     * @param fieldNames as an array of fieldNames
     * @return single regex or condition
     */
    protected String buildFieldNamesForRegexOr(String... fieldNames) {
        if (CommonUtils.isAnEmptyArray(fieldNames)) {
            throw new IllegalArgumentException("empty / null fieldNames is not allowed.");
        }
        StringBuilder fieldBuilder = new StringBuilder(DEFAULT_CAPACITY);
        String separator = BLANK;
        HashSet<String> keywords = new HashSet<>();
        for (String fieldName : fieldNames) {
            //check for duplicates
            if (CommonUtils.isNotBlank(fieldName) && (!keywords.contains(fieldName))) {
                fieldBuilder.append(separator).append(fieldName);
                separator = SEPARATOR;
                keywords.add(fieldName);
            }
        }
        //in case of the list is full for blanks and nulls. this hardly ever happen
        if (fieldBuilder.length() == 0) {
            throw new IllegalArgumentException("empty set of fieldNames.");
        }
        return fieldBuilder.toString();
    }
}
