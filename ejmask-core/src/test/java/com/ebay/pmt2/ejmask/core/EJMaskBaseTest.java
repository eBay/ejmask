package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import org.junit.jupiter.api.AfterEach;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author prakv
 */
abstract class EJMaskBaseTest {

    @AfterEach
    public void tearDown() throws Exception {
        String[] resetList = new String[]{"MASKING_PATTERNS", "PROCESSORS"};
        for (String declaredField : resetList) {
            Field field = EJMask.class.getDeclaredField(declaredField);
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof List) {
                ((List<?>) value).clear();
            }
        }
    }


    protected IContentProcessor mockIContentProcessor(int order, String name) {
        IContentProcessor preProcessor = mock(IContentProcessor.class);
        when(preProcessor.getOrder()).thenReturn(order);
        when(preProcessor.getName()).thenReturn(name);
        return preProcessor;
    }
}
