package com.ebay.ejmask.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CommonUtilsTest {

    @Test
    void isEmpty_whenCollectionNullOrEmpty_returnTrue() {
        boolean result = CommonUtils.isEmpty(null);
        Assertions.assertTrue(result);

        boolean result2 = CommonUtils.isEmpty(new ArrayList<>());
        Assertions.assertTrue(result2);

    }

    @Test
    void isNotEmpty_whenCollectionIsNotEmpty_returnTrue() {
        List<String> list = new ArrayList<>();
        list.add("content");

        boolean result = CommonUtils.isNotEmpty(list);
        Assertions.assertTrue(result);

    }

    @Test
    void emptyIfNull_ifNullCollection_returnEmptyCollection() {
        Collection<String> collection = CommonUtils.emptyIfNull(null);
        Assertions.assertNotNull(collection);
        Assertions.assertEquals(0, collection.size());

    }

    @Test
    void isNotAnEmptyArray_ifEmptyArray_returnsFalse() {
        boolean result = CommonUtils.isNotAnEmptyArray(new String[0]);
        Assertions.assertFalse(result);

    }

    @Test
    void isAnEmptyArray_ifEmptyArray_returnsFalse() {
        boolean result = CommonUtils.isAnEmptyArray(new String[0]);
        Assertions.assertTrue(result);

    }

    @Test
    void isNotBlank_ifBlankString_returnsFalse() {
        boolean result = CommonUtils.isNotBlank(" ");
        Assertions.assertFalse(result);

    }

    @Test
    void isBlank_ifBlankString_returnsTrue() {
        boolean result = CommonUtils.isBlank(" ");
        Assertions.assertTrue(result);
    }

    @Test
    void getStackTrace_whenExceptionThrown_shouldPrintStackTrace() {
        Exception e = new Exception("some exception");
        String result = CommonUtils.getStackTrace(e);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("java.lang.Exception: some exception", result.split("\n")[0]);
    }
}