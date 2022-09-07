package com.ebay.pmt2.ejmask.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.api.IFilter;
import com.ebay.pmt2.ejmask.api.IPatternBuilder;
import com.ebay.pmt2.ejmask.api.MaskingPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author prakv
 */
public class EJMaskInitializerTest extends EJMaskBaseTest {

    static String toString(String partition, String... array) {
        StringBuilder b = new StringBuilder();
        for (String s : array) {
            b.append(b.length() == 0 ? "" : partition).append(s);
        }
        return b.toString();
    }

    /**
     * Test of addFilter method, of class EJMaskInitializer.
     */
    @Test
    public void testAddFilter() {
        EJMaskInitializer.addMaskingPattern(10, "(firstName|lastName)", "xxx");
        Assertions.assertTrue(EJMask.getContentPreProcessors().isEmpty());
    }

    /**
     * Test of setMaskingPatterns method, of class EJMaskInitializer.
     */
    @Test
    public void testSetMaskingPatterns_null() {
        EJMaskInitializer.addMaskingPatterns(null);
        Assertions.assertTrue(EJMask.getMaskingPatterns().isEmpty());
    }

    /**
     * Test of setMaskingPatterns method, of class EJMaskInitializer.
     */
    @Test
    public void testSetMaskingPatterns() {
        EJMaskInitializer.addMaskingPatterns(Arrays.asList(new MaskingPattern(10, "(firstName|lastName)", "xxx")));
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=10;pattern=(firstName|lastName);replacement=xxx", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setContentPreProcessors method, of class EJMaskInitializer.
     */
    @Test
    public void testSetContentPreProcessors_null() {
        List<IContentProcessor> contentPreProcessors = null;
        EJMaskInitializer.addContentProcessors(contentPreProcessors);
        Assertions.assertTrue(EJMask.getContentPreProcessors().isEmpty());
    }

    /**
     * Test of setContentPreProcessors method, of class EJMaskInitializer.
     */
    @Test
    public void testSetContentPreProcessors_empty() {
        List<IContentProcessor> contentPreProcessors = new ArrayList<>();
        EJMaskInitializer.addContentProcessors(contentPreProcessors);
        Assertions.assertTrue(EJMask.getContentPreProcessors().isEmpty());
    }

    /**
     * Test of setContentPreProcessors method, of class EJMaskInitializer.
     */
    @Test
    public void testSetContentPreProcessors_values() {
        List<IContentProcessor> contentPreProcessors = Arrays.asList(mock(IContentProcessor.class), mock(IContentProcessor.class), mock(IContentProcessor.class));
        EJMaskInitializer.addContentProcessors(contentPreProcessors);
        Assertions.assertFalse(EJMask.getContentPreProcessors().isEmpty());
        Assertions.assertEquals(3, EJMask.getContentPreProcessors().size());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_null() throws Exception {
        List<IFilter> filters = null;
        EJMaskInitializer.addFilters(filters);
        Assertions.assertTrue(EJMask.getMaskingPatterns().isEmpty());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_empty() throws Exception {
        List<IFilter> filters = new LinkedList<>();
        EJMaskInitializer.addFilters(filters);
        Assertions.assertTrue(EJMask.getMaskingPatterns().isEmpty());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_single() throws Exception {
        List<IFilter> filters = Arrays.asList(
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate() throws Exception {
        List<IFilter> filters = Arrays.asList(
                //duplicate
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_nonGroupable() throws Exception {
        List<IFilter> filters = Arrays.asList(
                //duplicate
                this.buildFilter(50, 1, JsonPatternBuilder.class, "user", "name"),
                this.buildFilter(50, 1, JsonPatternBuilder.class, "buyer", "name"),
                this.buildFilter(50, 1, JsonPatternBuilder.class, "payer", "name")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(3, EJMask.getMaskingPatterns().size());
    }
    /**
     * All the bellow test requires auto adjusting capability. this will be introduced in next iteration.
     */

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate_with_different_priority() throws Exception {
        List<IFilter> filters = Arrays.asList(
                //duplicate
                this.buildFilter(70, 1, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(80, 1, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate_with_different_visibility() throws Exception {
        List<IFilter> filters = Arrays.asList(
                //duplicate
                this.buildFilter(50, 15, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 5, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate_with_different_priority_n_visibility() throws Exception {
        List<IFilter> filters = Arrays.asList(
                this.buildFilter(50, 15, TestPatternBuilder.class),
                //duplicate
                this.buildFilter(50, 15, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 5, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName"),
                //
                this.buildFilter(70, 1, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(80, 1, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(1, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate_with_different_builder() throws Exception {
        List<IFilter> filters = Arrays.asList(
                //duplicate
                this.buildFilter(50, 1, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, DummyPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, DummyPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        Assertions.assertFalse(EJMask.getMaskingPatterns().isEmpty());
        Assertions.assertEquals(2, EJMask.getMaskingPatterns().size());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(0).toString());
        Assertions.assertEquals("order=50;pattern=DummyPatternBuilder|vc:1|(firstName|lastName);replacement=DummyPatternBuilder_1_firstName_lastName", EJMask.getMaskingPatterns().get(1).toString());
    }

    /**
     * Test of setFilters method, of class EJMaskInitializer.
     */
    @Test
    public void testSetFilters_duplicate_with_different_priority_n_visibility_with_multiple_builder() throws Exception {
        List<IFilter> filters = Arrays.asList(
                this.buildFilter(1, 15, TestPatternBuilder.class),
                //duplicate
                this.buildFilter(1, 1, TestPatternBuilder.class, "cardNumber", "cvv"),
                this.buildFilter(1, 15, TestPatternBuilder.class, "firstName", "lastName", "cvv"),
                this.buildFilter(70, 1, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(80, 1, TestPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(65, 5, TestPatternBuilder.class, "lastName", "firstName", "token"),
                this.buildFilter(50, 1, TestPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 10, TestPatternBuilder.class, "firstName", "lastName", "cardNumber"),
                //
                this.buildFilter(50, 15, DummyPatternBuilder.class),
                //duplicate
                this.buildFilter(50, 15, DummyPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(50, 5, DummyPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, DummyPatternBuilder.class, "firstName", "lastName"),
                //
                this.buildFilter(70, 1, DummyPatternBuilder.class, "firstName", "lastName"),
                this.buildFilter(80, 1, DummyPatternBuilder.class, "lastName", "firstName"),
                this.buildFilter(50, 1, DummyPatternBuilder.class, "firstName", "lastName")
        );
        EJMaskInitializer.addFilters(filters);
        List<MaskingPattern> maskingPatterns = EJMask.getMaskingPatterns();
        Assertions.assertFalse(maskingPatterns.isEmpty());
        Assertions.assertEquals(4, maskingPatterns.size());
        Assertions.assertEquals("order=1;pattern=TestPatternBuilder|vc:1|(cardNumber|cvv);replacement=TestPatternBuilder_1_cardNumber_cvv", maskingPatterns.get(0).toString());
        Assertions.assertEquals("order=50;pattern=TestPatternBuilder|vc:1|(firstName|lastName);replacement=TestPatternBuilder_1_firstName_lastName", maskingPatterns.get(1).toString());
        Assertions.assertEquals("order=50;pattern=DummyPatternBuilder|vc:1|(firstName|lastName);replacement=DummyPatternBuilder_1_firstName_lastName", maskingPatterns.get(2).toString());
        Assertions.assertEquals("order=65;pattern=TestPatternBuilder|vc:5|(token);replacement=TestPatternBuilder_5_token", maskingPatterns.get(3).toString());
    }

    private IFilter buildFilter(int priority, int visibleCharacters, Class builderClass, String... fieldNames) {
        IFilter filter = mock(IFilter.class);
        when(filter.getGroup()).thenReturn("group");
        when(filter.getOrder()).thenReturn(priority);
        when(filter.getVisibleCharacters()).thenReturn(visibleCharacters);
        when(filter.getFieldNames()).thenReturn(fieldNames);
        when(filter.getPatternBuilder()).thenReturn(builderClass);
        return filter;
    }
}

@Disabled
class TestPatternBuilder implements IPatternBuilder {

    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        return this.getClass().getSimpleName() + "|vc:" + visibleCharacters + "|" + "(" + EJMaskInitializerTest.toString("|", fieldNames) + ")";
    }

    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return this.getClass().getSimpleName() + "_" + visibleCharacters + "_" + EJMaskInitializerTest.toString("_", fieldNames);
    }

    @Override
    public boolean isGroupable() {
        return true;
    }

}

@Disabled
class DummyPatternBuilder implements IPatternBuilder {

    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        return this.getClass().getSimpleName() + "|vc:" + visibleCharacters + "|" + "(" + EJMaskInitializerTest.toString("|", fieldNames) + ")";
    }

    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return this.getClass().getSimpleName() + "_" + visibleCharacters + "_" + EJMaskInitializerTest.toString("_", fieldNames);
    }

    @Override
    public boolean isGroupable() {
        return true;
    }
}

