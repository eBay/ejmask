package com.ebay.ejmask.api;

import java.util.Collection;
import java.util.Collections;

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

public interface IPatternBuilder {

    /**
     * Set true if the build can be groupable.
     *
     * @return true if groupable
     */
    default boolean isGroupable() {
        return true;
    }

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    String buildPattern(int visibleCharacters, String... fieldNames);

    /**
     * Build pattern to replace.
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return match pattern
     */
    String buildReplacement(int visibleCharacters, String... fieldNames);

    /**
     * Build pattern to match
     *
     * @param visibleCharacters as no of characters to be visible.
     * @param fieldNames        as list of field names
     * @return list of pattern entities
     */
    default Collection<PatternEntity> buildPatternEntities(int visibleCharacters, String... fieldNames) {
        return Collections.singletonList(new PatternEntity(this.buildPattern(visibleCharacters, fieldNames), this.buildReplacement(visibleCharacters, fieldNames)));
    }
}
