package com.ebay.pmt2.ejmask.spring.boot.autoconfig;


import com.ebay.pmt2.ejmask.api.IContentProcessor;
import org.junit.Assert;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Objective of this Integration test is ensure that all spring bean wiring works as expected.
 * The test to verify execution SLA must pass for this test.
 *
 * @author prakv
 */
@RunWith(Enclosed.class)
public class EJMaskAutoConfigIT {

    @SpringBootApplication
    @ContextConfiguration
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
    public static class ConfigDefaultIT {

        @Autowired(required = false)
        EJMaskAutoConfig config;

        @Autowired(required = false)
        IContentProcessor contentPreProcessor;

        @Test
        public void testConfig() {
            Assert.assertNotNull(this.config);
        }

        @Test
        public void testProcessor() {
            Assert.assertNull(this.contentPreProcessor);
        }
    }

    @SpringBootApplication
    @ContextConfiguration
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
    @TestPropertySource(properties = {"ejmask.autoconfig=enabled", "ejmask.processor.content-slicer=enabled"
            , "ejmask.content-slicer.priority=150", "ejmask.content-slicer.max-size=100000", "ejmask.content-slicer.new-size=40000"})
    public static class ConfigEnabledIT {

        @Autowired(required = false)
        EJMaskAutoConfig config;

        @Autowired(required = false)
        IContentProcessor contentPreProcessor;

        @Test
        public void testConfig() {
            Assert.assertNotNull(this.config);
        }

        @Test
        public void testProcessor() {
            Assert.assertNotNull(this.contentPreProcessor);
        }

        @Test
        public void testContentSlicerPriority() {
            Assert.assertEquals(150, this.config.contentSlicerPriority);
        }

        @Test
        public void testContentSlicerMaxStringLimit() {
            Assert.assertEquals(100000, this.config.contentSlicerMaxStringLimit);
        }

        @Test
        public void testContentSlicerNewSize() {
            Assert.assertEquals(40000, this.config.contentSlicerNewSize);
        }
    }

    @SpringBootApplication
    @ContextConfiguration
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
    @TestPropertySource(properties = {"ejmask.autoconfig=disabled", "ejmask.ejmask.processor.content-slicer=disabled"})
    public static class ConfigDisabledIT {

        @Autowired(required = false)
        EJMaskAutoConfig config;

        @Autowired(required = false)
        IContentProcessor contentPreProcessor;

        @Test
        public void testConfig() {
            Assert.assertNull(this.config);
        }

        @Test
        public void testProcessor() {
            Assert.assertNull(this.contentPreProcessor);
        }
    }
}
