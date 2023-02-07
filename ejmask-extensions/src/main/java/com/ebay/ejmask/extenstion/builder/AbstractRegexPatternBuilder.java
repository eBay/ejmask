package com.ebay.ejmask.extenstion.builder;

/**
 * Copyright (c) 2023 eBay Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ebay.ejmask.api.IPatternBuilder;
import com.ebay.ejmask.core.util.CommonUtils;

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
