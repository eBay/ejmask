package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.api.MaskingPattern;
import com.ebay.pmt2.ejmask.api.ProcessorResult;
import com.ebay.pmt2.ejmask.core.util.CommonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author prakv
 */
class EJMaskTest extends EJMaskBaseTest {

    /**
     * Test of register method, of class EJMask.
     */
    @Test
    void testRegister_IContentProcessor_verify_sort() {
        EJMask.register(super.mockIContentProcessor(20, "mock-processor-20"));
        EJMask.register(super.mockIContentProcessor(10, "mock-processor-10"));
        //then
        Assertions.assertEquals(2, EJMask.getContentProcessors().size());
        Assertions.assertEquals(10, EJMask.getContentProcessors().get(0).getOrder());
        Assertions.assertEquals(20, EJMask.getContentProcessors().get(1).getOrder());
    }

    /**
     * Test of register method, of class EJMask.
     */
    @Test
    void testRegister_MaskingPattern_verify_sort() {
        EJMask.register(new MaskingPattern(50, "(auth)=([^\\\"]{1,10})[^&]*", "$1xxxx"));
        EJMask.register(new MaskingPattern(10, "(X-API-Key|PayPal-Auth-Assertionsion)=([^\\\"]{1,10})[^&]*", "$1****"));
        //then
        Assertions.assertEquals(2, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals(10, this.getThroughReflection(EJMask.getMaskingPatterns().get(0), "order"));
        Assertions.assertEquals(50, this.getThroughReflection(EJMask.getMaskingPatterns().get(1), "order"));
    }


    /**
     * Test of addFilter method, of class EJMask.
     */
    @Test
    void testAddFilter() {
        EJMask.addFilter(50, "(auth)=([^\\\"]{1,10})[^&]*", "$1xxxx");
        EJMask.addFilter(10, "(X-API-Key|PayPal-Auth-Assertionsion)=([^\\\"]{1,10})[^&]*", "$1****");
        //then
        Assertions.assertEquals(10, this.getThroughReflection(EJMask.getMaskingPatterns().get(0), "order"));
        Assertions.assertEquals(50, this.getThroughReflection(EJMask.getMaskingPatterns().get(1), "order"));
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_null() {
        String content = null;
        String result = EJMask.mask(content);
        Assertions.assertNull(result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_empty() {
        String content = "";
        String result = EJMask.mask(content);
        Assertions.assertEquals(content, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_fail_safe() {
        IContentProcessor preProcessor = this.mockIContentProcessor(10, "mock-processor-10");
        when(preProcessor.preProcess(anyString())).thenThrow(new RuntimeException("mock exception please ignore"));
        EJMask.register(preProcessor);
        EJMask.addFilter(50, "(auth)=([^\\\"]{1,10})[^&]*", "$1xxxx");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        Assertions.assertEquals("masking sensitive content failed due to mock exception please ignore", result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_without_any_filter() {
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        Assertions.assertEquals(content, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_without_any_matching_filter() {
        EJMask.addFilter(50, "(auth)=([^\\\"]{1,10})[^&]*", "$1xxxx");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        Assertions.assertEquals(content, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_a_matching_filter() {
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_a_matching_filter_amount_multipe_with_same_order() {
        EJMask.addFilter(50, "\\\"(address1|address2)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        EJMask.addFilter(50, "\\\"(phoneNumber|number)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        //
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_a_matching_filter_amount_multipe_with_different_order() {
        EJMask.addFilter(10, "(auth)=([^\\\"]{1,10})[^&]*", "$1xxxx");
        EJMask.addFilter(95, "\\\"(address1|address2)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        EJMask.addFilter(40, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        EJMask.addFilter(50, "\\\"(phoneNumber|number)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        //
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_with_timeout() {
        IContentProcessor processor = this.mockIContentProcessor(10, "mock-processor-10", new ProcessorResult(true));
        when(processor.preProcess(anyString())).thenAnswer((i) -> {
            Thread.sleep(1000);
            return new ProcessorResult(true, i.getArgument(0, String.class));
        });
        EJMask.register(processor);
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        //when
        String result = EJMask.mask(content, 500);
        //then
        Assertions.assertNull(result);
        verify(processor, times(1)).preProcess(eq(content));
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_unmatching_processor() {
        EJMask.register(this.mockIContentProcessor(10, "mock-processor-10", new ProcessorResult(true)));
        EJMask.register(this.mockIContentProcessor(20, "mock-processor-20", new ProcessorResult(true)));
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        String result = EJMask.mask(content);
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
        List<IContentProcessor> prepros = EJMask.getContentProcessors();
        for (IContentProcessor mock : prepros) {
            verify(mock, times(1)).preProcess(eq(content));
        }
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_matching_processor1_breaks_chain() {
        EJMask.register(this.mockIContentProcessor(10, "mock-processor-10", new ProcessorResult(false)));
        EJMask.register(this.mockIContentProcessor(20, "mock-processor-20", new ProcessorResult(true, "wrong data")));
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        //when
        String result = EJMask.mask(content);
        //then
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
        verify(EJMask.getContentProcessors().get(0), times(1)).preProcess(anyString());
        verify(EJMask.getContentProcessors().get(0), times(1)).postProcess(anyString());
        verify(EJMask.getContentProcessors().get(1), never()).preProcess(anyString());
        verify(EJMask.getContentProcessors().get(1), times(1)).postProcess(anyString());
    }

    /**
     * Test of mask method, of class EJMask.
     */
    @Test
    void testMask_without_processor_with_matching_processor3_breaks_chain() {
        String content = "{\"firstName\":\"sensitive data\",\"lastName\":\"sensitive data\",\"nonSensitiveData\":\"firstName\"}";
        EJMask.register(this.mockIContentProcessor(10, "mock-processor-10", new ProcessorResult(true), "start_keyword"));
        EJMask.register(this.mockIContentProcessor(20, "mock-processor-20", new ProcessorResult(true, "breaking_keyword")));
        EJMask.register(this.mockIContentProcessor(30, "mock-processor-30", new ProcessorResult(false, content), "breaking_keyword"));
        EJMask.register(this.mockIContentProcessor(40, "mock-processor-40", new ProcessorResult(true, "wrong data")));
        EJMask.addFilter(50, "\\\"(firstName|lastName)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,3})[^\\\"]*(\\\\?\\\"|)", "\"$1$2$3-xxxx$4");
        //when
        String result = EJMask.mask("start_keyword");
        //then
        String expected = "{\"firstName\":\"sen-xxxx\",\"lastName\":\"sen-xxxx\",\"nonSensitiveData\":\"firstName\"}";
        Assertions.assertEquals(expected, result);
        List<IContentProcessor> prepros = EJMask.getContentProcessors();
        verify(prepros.get(0), times(1)).preProcess(anyString());
        verify(prepros.get(0), times(1)).postProcess(anyString());
        verify(prepros.get(1), times(1)).preProcess(anyString());
        verify(prepros.get(1), times(1)).postProcess(anyString());
        verify(prepros.get(2), times(1)).preProcess(anyString());
        verify(prepros.get(2), times(1)).postProcess(anyString());
        verify(prepros.get(3), never()).preProcess(anyString());// supposed to break
        verify(prepros.get(3), times(1)).postProcess(anyString());
    }

    private IContentProcessor mockIContentProcessor(int order, String name, ProcessorResult result, String... match) {
        IContentProcessor preProcessor = this.mockIContentProcessor(order, name);
        if (CommonUtils.isNotAnEmptyArray(match)) {
            when(preProcessor.preProcess(eq(match[0]))).thenReturn(result);
        } else {
            when(preProcessor.preProcess(anyString())).thenReturn(result);
        }
        return preProcessor;
    }

    private Object getThroughReflection(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
