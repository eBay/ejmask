package com.ebay.pmt2.ejmask.api;

/**
 * Defines a content pre processor
 *
 * @author prakv
 */
public interface IContentProcessor {

    int DEFAULT_ORDER = 50;

    /**
     * Return Name of the pre processor for debugging
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
