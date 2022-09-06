package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.api.MaskingPattern;
import com.ebay.pmt2.ejmask.api.ProcessorResult;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    @SafeVarargs
    static synchronized void register(IContentProcessor... preProcessor) {
        PROCESSORS.addAll(Arrays.asList(preProcessor));
        Collections.sort(PROCESSORS, Comparator.comparingInt(IContentProcessor::getOrder));
    }

    /**
     * Set the value of FILTER_PATTERN
     *
     * @param patterns new value of FILTER_PATTERN
     */
    @SafeVarargs
    static synchronized void register(MaskingPattern... patterns) {
        MASKING_PATTERNS.addAll(Arrays.asList(patterns));
        Collections.sort(MASKING_PATTERNS);
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
    static List<IContentProcessor> getContentPreProcessors() {
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
     * @param content as sting which need to be masked
     * @return cleaned up string
     */
    public static String maskWithOutProcessor(final String content) {
        return mask(content, false, false);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content               as sting which need to be masked
     * @param preProcessingRequired as boolean whether pre-processing step
     *                              required
     * @return cleaned up string
     * @deprecated use mask (content,preProcessingRequired,postProcessingRequired) method.
     */
    @Deprecated
    private static String mask(final String content, boolean preProcessingRequired) {
        return mask(content, preProcessingRequired, false);
    }

    /**
     * Find and mask any pre-configured sensitive data into filter group
     *
     * @param content                as sting which need to be masked
     * @param preProcessingRequired  as boolean whether pre-processing step   required
     * @param postProcessingRequired as boolean whether post-processing step required
     * @return cleaned up string
     */
    private static String mask(final String content, boolean preProcessingRequired, boolean postProcessingRequired) {
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

