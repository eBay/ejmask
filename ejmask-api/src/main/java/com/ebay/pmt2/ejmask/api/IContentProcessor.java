package com.ebay.pmt2.ejmask.api;

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
 * Defines a content pre processor
 *
 * @author prakv
 */
public interface IContentProcessor {

    int DEFAULT_ORDER = 50;

    /**
     * Return Name of the pre-processor for debugging
     *
     * @return Name of preprocessor
     */
    String getName();

    /**
     * pre-process the given content if eligible
     *
     * @param content as sting which need to be masked
     * @return preprocessed content if eligible
     */
    default ProcessorResult preProcess(String content) {
        return new ProcessorResult(true, content);
    }

    /**
     * Executed after the data is masked.
     *
     * @param content as sting which need is masked
     * @return process content if eligible
     */
    default ProcessorResult postProcess(String content) {
        return new ProcessorResult(true, content);
    }

    /**
     * Get the value of order, lower will be having higher priority. default should be 50
     *
     * @return the value of order
     */
    default int getOrder() {
        return DEFAULT_ORDER;
    }
}
