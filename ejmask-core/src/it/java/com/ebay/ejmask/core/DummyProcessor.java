package com.ebay.ejmask.core;

import com.ebay.ejmask.api.IContentProcessor;
import com.ebay.ejmask.api.ProcessorResult;

/**
 * @author prakv
 */
class DummyProcessor implements IContentProcessor {

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public ProcessorResult preProcess(String content) {
        if (content.length() > 10000) {
            return new ProcessorResult(true, content.substring(0, 4000));
        }
        return new ProcessorResult(true);
    }
}
