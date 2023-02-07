package com.ebay.ejmask.core;

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

import com.ebay.ejmask.api.IContentProcessor;
import com.ebay.ejmask.api.MaskingPattern;
import com.ebay.ejmask.api.ProcessorResult;
import com.ebay.ejmask.core.util.CommonUtils;
import com.ebay.ejmask.core.util.ExecutorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to mask any occurrence of sensitive data using preconfigured regular expression.
 *
 * @author prakv
 */
public class EJMask {

    private static final List<IContentProcessor> PROCESSORS = new ArrayList<>();
    private static final List<MaskingPattern> MASKING_PATTERNS = new ArrayList<>();

    /**
     * Register the given list of PreProcessors. once added we cant de register
     * these extensions
     *
     * @param preProcessor as list of PreProcessors
     */
    static synchronized void register(IContentProcessor... preProcessor) {
        PROCESSORS.addAll(Arrays.asList(preProcessor));
        PROCESSORS.sort(Comparator.comparingInt(IContentProcessor::getOrder));
    }

    /**
     * Set the value of FILTER_PATTERN
     *
     * @param patterns new value of FILTER_PATTERN
     */
    static synchronized void register(MaskingPattern... patterns) {
        MASKING_PATTERNS.addAll(Arrays.asList(patterns));
        Collections.sort(MASKING_PATTERNS);
        mask("Note: this is to prime internal java classes for first time hit performance.", false, false);
    }

    /**
     * Get the value of MASKING_PATTERN
     *
     * @return the value of MASKING_PATTERN
     */
    static List<MaskingPattern> getMaskingPatterns() {
        return Collections.unmodifiableList(MASKING_PATTERNS);
    }

    /**
     * Get the value of IContentPreProcessor
     *
     * @return the value of IContentPreProcessor
     */
    static List<IContentProcessor> getContentProcessors() {
        return Collections.unmodifiableList(PROCESSORS);
    }

    /**
     * Add to filter group
     *
     * @param order      as priority of this filter in chain
     * @param regex      as value to be replaced
     * @param substitute value replaced with
     */
    static void addFilter(int order, String regex, String substitute) {
        register(new MaskingPattern(order, regex, substitute));
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content as sting which need to be masked
     * @return cleaned up string
     */
    public static String mask(final String content) {
        return mask(content, true, true);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content     as sting which need to be masked
     * @param timeOutInMs as time out in mill sec
     * @return cleaned up string
     */
    public static String mask(String content, long timeOutInMs) {
        return mask(content, true, true, timeOutInMs);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content                as sting which need to be masked
     * @param preProcessingRequired  as boolean whether pre-processing step   required
     * @param postProcessingRequired as boolean whether post-processing step required
     * @param timeOutInMs            as time out in mill sec
     * @return cleaned up string
     */
    public static String mask(String content, boolean preProcessingRequired, boolean postProcessingRequired, long timeOutInMs) {
        return ExecutorUtil.execute(() -> mask(content, preProcessingRequired, postProcessingRequired), timeOutInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content as sting which need to be masked
     * @return cleaned up string
     */
    public static String maskWithOutProcessor(final String content) {
        return mask(content, false, false);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content                as sting which need to be masked
     * @param preProcessingRequired  as boolean whether pre-processing step   required
     * @param postProcessingRequired as boolean whether post-processing step required
     * @return cleaned up string
     */
    public static String mask(final String content, boolean preProcessingRequired, boolean postProcessingRequired) {
        try {
            //filterPattern on original content
            if (CommonUtils.isBlank(content)) {
                return content;
            }
            String contentInProgress = content;
            if (preProcessingRequired) {
                contentInProgress = process(contentInProgress, IContentProcessor::preProcess);
            }
            contentInProgress = maskSensitiveContent(contentInProgress);
            if (postProcessingRequired) {
                contentInProgress = process(contentInProgress, IContentProcessor::postProcess);
            }
            return contentInProgress;
        } catch (Exception ex) {
            return "masking sensitive content failed due to " + ex.getMessage();
        }
    }

    /**
     * This is to support special cases where the given content need to be
     * preprocessed
     *
     * @param content as content
     * @return preprocessed string
     */
    private static String process(String content, Operation operation) {
        for (IContentProcessor processor : PROCESSORS) {
            final ProcessorResult result = operation.process(processor, content);
            if (result != null) {
                if (CommonUtils.isNotBlank(result.getContent())) {
                    content = result.getContent();
                }
                //break chain
                if (!result.executeNext()) {
                    break;
                }
            }
        }
        return content;
    }

    /**
     * Filter Sensitive information in the given content
     *
     * @param content with possible sensitive information
     * @return masked content
     */
    private static String maskSensitiveContent(String content) {
        for (MaskingPattern filter : MASKING_PATTERNS) {
            content = filter.replaceAll(content);
        }
        return content;
    }

    /**
     * Defines a processor.
     */
    @FunctionalInterface
    interface Operation {
        ProcessorResult process(IContentProcessor processor, String content);
    }
}

