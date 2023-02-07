package com.ebay.ejmask.api;

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
