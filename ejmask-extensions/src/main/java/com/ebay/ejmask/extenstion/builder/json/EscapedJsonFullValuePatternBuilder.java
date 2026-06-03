package com.ebay.ejmask.extenstion.builder.json;
/**
 * Copyright (c) 2026 eBay Inc.
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
 * An implementation of {@link com.ebay.ejmask.api.IPatternBuilder} that fully masks field values
 * inside <em>escaped</em> JSON strings — JSON that has been serialized as a string value within
 * another JSON document, where every quote character appears as {@code \"}.
 *
 * <p>Unlike {@link JsonFullValuePatternBuilder}, this builder explicitly matches the backslash
 * escape that precedes each delimiter quote, so the output remains syntactically valid after
 * masking.
 *
 * <pre>
 * Input:  {\"cvv\":\"sensitiveData\",\"amount\":\"100\"}
 * Output: {\"cvv\":\"****\",\"amount\":\"100\"}
 * </pre>
 *
 * <p>The {@code visibleCharacters} parameter is ignored; the entire value is always replaced
 * with {@code ****}.
 *
 * @author fsun1
 */
public class EscapedJsonFullValuePatternBuilder extends AbstractRegexPatternBuilder {

    // Matches: \"fieldName\":\"VALUE\"  (escaped-JSON delimiters)
    private static final String PATTERN_TEMPLATE = "(\\\\\"(?:%s)\\\\\":\\\\\")([^\\\\\"]+)(\\\\\")";
    // Group $1 = \"fieldName\":\"  (opening escaped delimiters, preserved)
    // Group $2 = value             (replaced with ****)
    // Group $3 = \"                (closing escaped delimiter, preserved)
    private static final String REPLACEMENT = "$1****$3";

    /**
     * Builds a regex pattern that matches the named field(s) in an escaped JSON string.
     * The {@code visibleCharacters} parameter has no effect; all characters are always masked.
     *
     * @param visibleCharacters ignored
     * @param fieldNames        one or more field names to mask, combined via {@code |} alternation
     * @return the compiled regex pattern string
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        if (visibleCharacters != 0) {
            throw new IllegalArgumentException("visibleCharacters must be 0 instead of " + visibleCharacters);
        }
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Returns the replacement string that substitutes {@code ****} for the matched value,
     * preserving the surrounding escaped delimiters.
     *
     * @param visibleCharacters ignored
     * @param fieldNames        ignored
     * @return the replacement string {@code $1****$3}
     */
    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return REPLACEMENT;
    }
}
