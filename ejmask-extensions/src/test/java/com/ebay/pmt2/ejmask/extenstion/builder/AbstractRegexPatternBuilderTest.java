package com.ebay.pmt2.ejmask.extenstion.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author prakv
 */
public class AbstractRegexPatternBuilderTest {

    AbstractRegexPatternBuilder instance = new AbstractRegexPatternBuilderImpl();


    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_null() {
        String[] fieldNames = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.instance.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_empty() {
        String[] fieldNames = new String[]{};
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.instance.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_blanks_and_nulls() {
        String[] fieldNames = new String[]{"", null, "", null};
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.instance.buildFieldNamesForRegexOr(fieldNames));
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_single() {
        String[] fieldNames = new String[]{"firstName"};
        String result = this.instance.buildFieldNamesForRegexOr(fieldNames);
        Assertions.assertEquals("firstName", result);
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_single_as_duplicate() {
        String[] fieldNames = new String[]{"firstName", "firstName"};
        String result = this.instance.buildFieldNamesForRegexOr(fieldNames);
        Assertions.assertEquals("firstName", result);
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_multiple() {
        String[] fieldNames = new String[]{"cardNumber", "expDate", "cvv"};
        String result = this.instance.buildFieldNamesForRegexOr(fieldNames);
        Assertions.assertEquals("cardNumber|expDate|cvv", result);
    }

    /**
     * Test of buildFieldNamesForRegexOr method, of class
     * AbstractRegexPatternBuilder.
     */
    @Test
    public void testBuildFieldNamesForRegexOr_multiple_with_duplicates_and_nulls() {
        String[] fieldNames = new String[]{"cardNumber", "expDate", "", null, "cvv", "cardNumber", "expDate", "cvv"};
        String result = this.instance.buildFieldNamesForRegexOr(fieldNames);
        Assertions.assertEquals("cardNumber|expDate|cvv", result);
    }
}

@Disabled
class AbstractRegexPatternBuilderImpl extends AbstractRegexPatternBuilder {

    @Override
    public String buildPattern(int visibleCharacters, String... fieldNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
