package com.ebay.ejmask.extenstion.builder.string;
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
 * An implementation of {@link com.ebay.ejmask.api.IPatternBuilder} for masking fields in
 * pipe-separated {@code key=value} payloads, such as serialized event-pipeline data:
 *
 * <pre>
 * id=123456789|type=SELLER|type=CHARGE|...
 * </pre>
 *
 * <p>The value delimiter is {@code |} (or end of string). The value character class also
 * excludes {@code "} and {@code \} so that the replacement cannot bleed into adjacent JSON
 * syntax when the payload is embedded inside a JSON field value.
 *
 * <pre>
 * Input:  id=123456789|type=SELLER
 * Output: id=****|type=SELLER
 * </pre>
 *
 * <p>The {@code visibleCharacters} parameter is ignored; the entire value is always replaced
 * with {@code ****}.
 *
 * @author fsun1
 */
public class PipeSeparatedValuePatternBuilder extends AbstractRegexPatternBuilder {

    // Stops at |, ", or \ so replacement cannot bleed into adjacent fields or JSON syntax.
    private static final String PATTERN_TEMPLATE = "((?:%s)=)([^|\"\\\\]+)";
    // Group $1 = fieldName=  (preserved)
    // Group $2 = value       (replaced with ****)
    private static final String REPLACEMENT = "$1****";

    /**
     * Builds a regex pattern that matches the named field(s) in a pipe-separated payload.
     * The {@code visibleCharacters} parameter has no effect; all characters are always masked.
     *
     * @param visibleCharacters ignored
     * @param fieldNames        one or more field names to mask, combined via {@code |} alternation
     * @return the compiled regex pattern string
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        return String.format(PATTERN_TEMPLATE, super.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Returns the replacement string that substitutes {@code ****} for the matched value,
     * preserving the leading {@code fieldName=} prefix.
     *
     * @param visibleCharacters ignored
     * @param fieldNames        ignored
     * @return the replacement string {@code $1****}
     */
    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return REPLACEMENT;
    }
}
