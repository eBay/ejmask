package com.ebay.pmt2.ejmask.extenstion.builder.json;
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

import com.ebay.pmt2.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

/**
 * An implementation of IPatternBuilder to support sensitive JSON field, whose value need to be partially masked.
 *
 * @author prakv
 */
public class JsonFieldPatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/ZDQWod/5
    //unescaped string --------------------------> \"(%s)(\\*\"\s*:\s*\\*\")([^\"]{1,%d})[^\"]*(\\?\"|)
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,%d})[^\\\"]*(\\\\?\\\"|)";
    //group $1 = field name
    //group $2 =  ":" (with json serialization support)
    //group $3 =  masked sting
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2$3-xxxx$4";

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
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames), visibleCharacters);
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
