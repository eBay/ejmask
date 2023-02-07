package com.ebay.ejmask.extenstion.builder.xml;

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

import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * XML Field Patten Builder
 *
 * @author prakv
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
