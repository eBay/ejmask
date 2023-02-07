package com.ebay.ejmask.extenstion.builder.header;

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
 * An implementation of IPatternBuilder to support sensitive Header Fields
 *
 * @author prakv
 */
public class HeaderFieldPatternBuilder extends AbstractRegexPatternBuilder {

    //https://regex101.com/r/yFABRL/7
    private static final String TOTAL_PATTERN_TEMPLATE = "((?i)%s)=([^\\&\"]{1,10})*";
    private static final String TOTAL_REPLACEMENT_TEMPLATE = "$1=******";
    //https://regex101.com/r/yFABRL/7
    private static final String PATTERN_TEMPLATE = "((?i)%s)=([^\\&\"]{1,%d})*";
    private static final String REPLACEMENT_TEMPLATE = "$1=xxxx-$2";

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match patternAbstractRegexPatternBuilderTest
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        String regexOr = super.buildFieldNamesForRegexOr(fieldNames);
        return (visibleCharacters > 0) ? String.format(PATTERN_TEMPLATE, regexOr, visibleCharacters) : String.format(TOTAL_PATTERN_TEMPLATE, regexOr);
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
        return visibleCharacters > 0 ? REPLACEMENT_TEMPLATE : TOTAL_REPLACEMENT_TEMPLATE;
    }
}
