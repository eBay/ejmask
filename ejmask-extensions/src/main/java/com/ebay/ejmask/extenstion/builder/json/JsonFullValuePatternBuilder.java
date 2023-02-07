package com.ebay.ejmask.extenstion.builder.json;
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
 * An implementation of IPatternBuilder to support high sensitive JSON field, whose value need to be fully masked.
 *
 * @author prakv
 */
public class JsonFullValuePatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/ZDQWod/7
    //todo this may need optimization as this breaks the encoded json syntax though it removes sensitive data.
    private static final String PATTERN_TEMPLATE = "\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,1})[^\\\"]*(\\\\?\\\"|)";
    //group $1 = field name
    //group $2 =  ":" (with json serialization support)
    //group $3 =  sensitive information -> ignore
    //group $4 =  ending qts
    private static final String REPLACEMENT_TEMPLATE = "\"$1$2****$4";

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters != 0) {
            throw new IllegalArgumentException("visibleCharacters must be 0 instead of " + visibleCharacters);
        }
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames));
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
