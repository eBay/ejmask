package com.ebay.pmt2.ejmask.core;

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

import com.ebay.pmt2.ejmask.api.IFilter;
import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;

/**
 * Default implementation of IFilter for easy extension.
 *
 * @author prakv
 */
public class BaseFilter implements IFilter {

    private static final int DEFAULT_VISIBLE_CHAR = 2;
    private static final int DEFAULT_PRIORITY = 50;

    private final int order;
    private final int visibleCharacters;
    private final Class<? extends IPatternBuilder> patternBuilder;
    private final String[] fieldNames;

    /**
     * Create new instance of BaseFilter
     *
     * @param patternBuilder new value of patternBuilder
     * @param fieldNames     new value of fieldNames
     */
    @SafeVarargs
    public BaseFilter(Class<? extends IPatternBuilder> patternBuilder, String... fieldNames) {
        this(patternBuilder, DEFAULT_VISIBLE_CHAR, DEFAULT_PRIORITY, fieldNames);
    }

    /**
     * Create new instance of BaseFilter
     *
     * @param patternBuilder    new value of patternBuilder
     * @param visibleCharacters new value of visibleCharacters
     * @param fieldNames        new value of fieldNames
     */
    @SafeVarargs
    public BaseFilter(Class<? extends IPatternBuilder> patternBuilder, int visibleCharacters, String... fieldNames) {
        this(patternBuilder, visibleCharacters, DEFAULT_PRIORITY, fieldNames);
    }

    /**
     * Create new instance of BaseFilter. Use order only for cases it is really
     * required.
     *
     * @param patternBuilder    new value of patternBuilder
     * @param visibleCharacters new value of visibleCharacters
     * @param order             new value of order
     * @param fieldNames        new value of fieldNames
     */
    @SafeVarargs
    public BaseFilter(Class<? extends IPatternBuilder> patternBuilder, int visibleCharacters, int order, String... fieldNames) {
        if (patternBuilder == null) {
            throw new IllegalArgumentException("patternBuilder cannot be null.");
        }
        if (CommonUtils.isAnEmptyArray(fieldNames)) {
            throw new IllegalArgumentException("fieldNames cannot be null or empty.");
        }
        this.patternBuilder = patternBuilder;
        this.visibleCharacters = visibleCharacters;
        this.order = order;
        this.fieldNames = fieldNames;
    }


    /**
     * Get the value of patternBuilder
     *
     * @return the value of patternBuilder
     */
    @Override
    public Class<? extends IPatternBuilder> getPatternBuilder() {
        return this.patternBuilder;
    }

    /**
     * Get the value of visibleCharacters
     *
     * @return the value of visibleCharacters
     */
    @Override
    public int getVisibleCharacters() {
        return this.visibleCharacters;
    }

    /**
     * Get the value of order
     *
     * @return the value of order
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Get the value of fieldNames
     *
     * @return the value of fieldNames
     */
    @Override
    public String[] getFieldNames() {
        return this.fieldNames;
    }
}
