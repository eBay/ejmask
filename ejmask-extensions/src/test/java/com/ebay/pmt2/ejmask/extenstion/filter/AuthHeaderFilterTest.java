package com.ebay.pmt2.ejmask.extenstion.filter;

import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.extenstion.builder.header.HeaderFieldPatternBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author prakv
 */
class AuthHeaderFilterTest {

    final AuthHeaderFilter instance = new AuthHeaderFilter();

    /**
     * Test of getGroup method, of class BaseFilter.
     */
    @Test
    void testGetGroup() {
        String result = this.instance.getGroup();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("ejmask", result);
    }

    /**
     * Test of getPatternBuilder method, of class BaseFilter.
     */
    @Test
    void testGetPatternBuilder() {
        Class<? extends IPatternBuilder> result = this.instance.getPatternBuilder();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HeaderFieldPatternBuilder.class, result);
    }

    /**
     * Test of getVisibleCharacters method, of class BaseFilter.
     */
    @Test
    void testGetVisibleCharacters() {
        int result = this.instance.getVisibleCharacters();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(10, result);
    }

    /**
     * Test of getOrder method, of class BaseFilter.
     */
    @Test
    void testGetOrder() {
        int result = this.instance.getOrder();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(90, result);
    }

    /**
     * Test of getFieldNames method, of class BaseFilter.
     */
    @Test
    void testGetFieldNames() {
        String[] result = this.instance.getFieldNames();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.length);
        Assertions.assertEquals("Authorization", result[0]);
    }
}
