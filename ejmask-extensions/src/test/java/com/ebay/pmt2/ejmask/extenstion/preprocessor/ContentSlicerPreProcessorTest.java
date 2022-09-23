package com.ebay.pmt2.ejmask.extenstion.preprocessor;

import com.ebay.pmt2.ejmask.api.ProcessorResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author prakv
 */
public class ContentSlicerPreProcessorTest {

    ContentSlicerPreProcessor instance = new ContentSlicerPreProcessor();

    /**
     * Test of getName method, of class ContentSlicerPreProcessor.
     */
    @Test
    public void testGetName() {
        String expResult = "ejmask.content-slicer";
        String result = this.instance.getName();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getOrder method, of class ContentSlicerPreProcessor.
     */
    @Test
    public void testGetOrder() {
        int result = this.instance.getOrder();
        Assertions.assertEquals(49, result);
    }

    /**
     * Test of process method, of class ContentSlicerPreProcessor.
     */
    @Test
    public void testProcess() {
        String content = "12";
        ProcessorResult result = this.instance.preProcess(content);
        Assertions.assertTrue(result.executeNext());
        Assertions.assertNull(result.getContent());
    }

    /**
     * Test of process method, of class ContentSlicerPreProcessor.
     */
    @Test
    public void testProcess_long_string() {
        String content = RandomStringUtils.randomAlphabetic(15000);
        ProcessorResult result = this.instance.preProcess(content);
        Assertions.assertTrue(result.executeNext());
        Assertions.assertNotNull(result.getContent());
        Assertions.assertTrue(result.getContent().length() < 4001);
    }
}
