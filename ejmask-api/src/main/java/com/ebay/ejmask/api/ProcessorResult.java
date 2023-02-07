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

import javax.annotation.Nullable;

public class ProcessorResult {

    private final boolean executeNext;
    private final String content;

    /**
     * Set the value of executeNext
     *
     * @param executeNext new value of executeNext
     */
    public ProcessorResult(boolean executeNext) {
        this(executeNext, null);
    }

    /**
     * Set the value of executeNext
     *
     * @param executeNext new value of executeNext
     * @param content     new value of content
     */
    public ProcessorResult(boolean executeNext, String content) {
        this.executeNext = executeNext;
        this.content = content;
    }

    /**
     * Get the value of executeNext
     *
     * @return the value of executeNext
     */
    public boolean executeNext() {
        return this.executeNext;
    }

    /**
     * Get the value of content
     *
     * @return the value of content
     */
    @Nullable
    public String getContent() {
        return this.content;
    }
}
