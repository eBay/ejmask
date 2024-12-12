package com.ebay.ejmask.spring.core;

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

import com.ebay.ejmask.api.IContentProcessor;
import com.ebay.ejmask.api.IFilter;
import com.ebay.ejmask.api.MaskingPattern;
import com.ebay.ejmask.core.EJMaskInitializer;
import com.ebay.ejmask.core.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Component scan configuration for EJMask spring configuration.
 * Wiring this class will short circuit need of manuale wiring
 *
 * @author prakv
 */
@Configuration("com.ebay.ejmask.configuration")
public class EJMaskSpringCoreContextConfiguration implements InitializingBean {

    @Autowired(required = false)
    List<IFilter> filters;

    @Autowired(required = false)
    List<MaskingPattern> maskingPatterns;

    @Autowired(required = false)
    List<IContentProcessor> contentPreProcessors;

    /**
     * Execute post object construction
     */
    @Override
    public void afterPropertiesSet() {
        LoggerUtil.info("ejmask-spring-core", "context-configuration", "initialization started");
        EJMaskInitializer.addFilters(filters);
        EJMaskInitializer.addMaskingPatterns(maskingPatterns);
        EJMaskInitializer.addContentProcessors(contentPreProcessors);
        LoggerUtil.info("ejmask-spring-core", "context-configuration", "initialization completed");
    }
}