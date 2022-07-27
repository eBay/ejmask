package com.ebay.pmt2.ejmask.api;

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
