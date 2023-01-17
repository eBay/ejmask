package com.ebay.pmt2.ejmask.spring.boot.autoconfig;

/**
 * Copyright (c) 2023 eBay Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.extenstion.preprocessor.ContentSlicerPreProcessor;
import com.ebay.pmt2.ejmask.spring.core.EJMaskSpringCoreContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import javax.inject.Named;

/**
 * Component scan configuration for ejmask auto configuration
 *
 * @author prakv
 * @link https://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch06s02.html
 */
@AutoConfigureOrder(105)
@Named("ejmask.autoconfig")
@ComponentScan(basePackageClasses = EJMaskSpringCoreContextConfiguration.class)
@ConditionalOnProperty(prefix = "ejmask.", name = "autoconfig", havingValue = "enabled", matchIfMissing = true)
public class EJMaskAutoConfig {

    @Value("ejmask.processor.content-slicer.priority:50")
    int contentSlicerPriority;
    @Value("ejmask.processor.content-slicer.max-size:10000")
    int contentSlicerMaxStringLimit;
    @Value("ejmask.processor.content-slicer.new-size:4000")
    int contentSlicerNewSize;


    /**
     * Inject an instance of IContentPreProcessor into context if enabled.
     *
     * @return as instance of ConditionalOnProperty
     */
    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnProperty(prefix = "ejmask.processor.", name = "content-slicer", havingValue = "enabled")
    public IContentProcessor getContentSlicerPreProcessor() {
        return new ContentSlicerPreProcessor(this.contentSlicerPriority, this.contentSlicerMaxStringLimit, this.contentSlicerNewSize);
    }
}
