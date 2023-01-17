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
