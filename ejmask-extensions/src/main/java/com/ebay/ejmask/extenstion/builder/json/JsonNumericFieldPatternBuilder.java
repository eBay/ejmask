package com.ebay.ejmask.extenstion.builder.json;
/**
 * Copyright (c) 2024 eBay Inc.
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

import com.ebay.ejmask.api.PatternEntity;
import com.ebay.ejmask.extenstion.builder.AbstractRegexPatternBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of IPatternBuilder to support sensitive JSON field, whose value need to be partially masked.
 * This builder is for masking the field value with a Numeric type.
 *
 * @author fsun1
 */
public class JsonNumericFieldPatternBuilder extends AbstractRegexPatternBuilder {

    private static final List<PatternEntity> PATTERN_ENTITY_LIST = Arrays.asList(
            /**
             * Numeric field with value to be masked
             * @see <a href="https://regex101.com/r/rOeErB/1">Regular Expresseion For Testing</a>
             */
            new PatternEntity("\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*)(-?\\b\\d+(\\.\\d+)?(e-?\\d+)?\\b)([^\\\"]{1,2})", "\"$1$2\"xxxx\"$6")
    );

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        return this.buildPattern(null, visibleCharacters, fieldNames);
    }

    /**
     * Build pattern to match
     *
     * @param patternEntity     as instance of PatternEntity
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return
     */
    private String buildPattern(PatternEntity patternEntity, int visibleCharacters, String... fieldNames) {
        if (visibleCharacters < 1) {
            throw new IllegalArgumentException("visibleCharacters must be a possessive value instead of " + visibleCharacters);
        }
        if (patternEntity == null) {
            patternEntity = PATTERN_ENTITY_LIST.get(0);
        }
        return String.format(patternEntity.getPatternTemplate(), super.buildFieldNamesForRegexOr(fieldNames), visibleCharacters);
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
        return this.buildReplacement(null, visibleCharacters, fieldNames);
    }

    /**
     * Build pattern to replace.
     *
     * @param patternEntity     as instance of PatternEntity
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    private String buildReplacement(PatternEntity patternEntity, int visibleCharacters, String... fieldNames) {
        if (patternEntity == null) {
            patternEntity = PATTERN_ENTITY_LIST.get(0);
        }
        return patternEntity.getReplacementTemplate();
    }

    /**
     * Build list of PatternEntity
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern list
     */
    @Override
    public List<PatternEntity> buildPatternEntities(int visibleCharacters, String... fieldNames) {
        List<PatternEntity> result = new LinkedList<>();
        PATTERN_ENTITY_LIST.forEach(patternEntity -> {
            result.add(new PatternEntity(this.buildPattern(patternEntity, visibleCharacters, fieldNames), this.buildReplacement(patternEntity, visibleCharacters, fieldNames)));
        });
        return result;
    }

}
