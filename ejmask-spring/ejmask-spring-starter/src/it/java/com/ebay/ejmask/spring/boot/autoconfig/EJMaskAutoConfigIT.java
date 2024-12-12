package com.ebay.ejmask.spring.boot.autoconfig;

import com.ebay.ejmask.api.IContentProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootApplication
@ContextConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
class ConfigDefaultIT {

    @Autowired(required = false)
    EJMaskAutoConfig config;
 
    @Autowired(required = false)
    IContentProcessor contentPreProcessor;

    @Test
    public void testConfig() {
        Assertions.assertNotNull(this.config);
    }

    @Test
    public void testProcessor() {
        Assertions.assertNull(this.contentPreProcessor);
    }
}

@SpringBootApplication
@ContextConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
@TestPropertySource(properties = {
        "ejmask.autoconfig=enabled",
        "ejmask.processor.content-slicer=enabled",
        "ejmask.processor.content-slicer.priority=150",
        "ejmask.processor.content-slicer.max-size=100000",
        "ejmask.processor.content-slicer.new-size=40000"
})
class ConfigEnabledIT {

    @Autowired(required = false)
    EJMaskAutoConfig config;

    @Autowired(required = false)
    IContentProcessor contentPreProcessor;

    @Test
    public void testConfig() {
        Assertions.assertNotNull(this.config);
    }

    @Test
    public void testProcessor() {
        Assertions.assertNotNull(this.contentPreProcessor);
    }

    @Test
    public void testContentSlicerPriority() {
        Assertions.assertEquals(150, this.config.contentSlicerPriority);
    }

    @Test
    public void testContentSlicerMaxStringLimit() {
        Assertions.assertEquals(100000, this.config.contentSlicerMaxStringLimit);
    }

    @Test
    public void testContentSlicerNewSize() {
        Assertions.assertEquals(40000, this.config.contentSlicerNewSize);
    }
}

@SpringBootApplication
@ContextConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = EJMaskAutoConfig.class)
@TestPropertySource(properties = {"ejmask.autoconfig=disabled", "ejmask.ejmask.processor.content-slicer=disabled"})
class ConfigDisabledIT {

    @Autowired(required = false)
    EJMaskAutoConfig config;

    @Autowired(required = false)
    IContentProcessor contentPreProcessor;

    @Test
    public void testConfig() {
        Assertions.assertNull(this.config);
    }

    @Test
    public void testProcessor() {
        Assertions.assertNull(this.contentPreProcessor);
    }
}
