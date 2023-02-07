package com.ebay.ejmask.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class ExecutorUtilTest {

    @Test
    void execute_execution_error() {
        String result = ExecutorUtil.execute(() -> {
            throw new RuntimeException("some exception");
        }, 100, TimeUnit.SECONDS);
        Assertions.assertNull(result);
    }


    @Test
    void execute() {
        String result = ExecutorUtil.execute(() -> "all good", 1, TimeUnit.SECONDS);
        Assertions.assertEquals("all good", result);
    }

    @Test
    void execute_timeout() {
        String result = ExecutorUtil.execute(() -> {
            this.wait(2000);
            return "test";
        }, 500, TimeUnit.MILLISECONDS);
        Assertions.assertNull(result);
    }
}