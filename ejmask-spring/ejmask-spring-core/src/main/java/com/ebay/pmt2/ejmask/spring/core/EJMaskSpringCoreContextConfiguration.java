package com.ebay.pmt2.ejmask.spring.core;

import com.ebay.pmt2.ejmask.api.IContentProcessor;
import com.ebay.pmt2.ejmask.api.IFilter;
import com.ebay.pmt2.ejmask.api.MaskingPattern;
import com.ebay.pmt2.ejmask.core.EJMaskInitializer;
import com.ebay.pmt2.ejmask.core.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Component scan configuration for EJMask spring configuration.
 * Wiring this class will short circuit need of manuale wiring
 *
 * @author prakv
 */
@Configuration("ejMaskSpringCoreContextConfiguration")
public class EJMaskSpringCoreContextConfiguration {

    @Autowired(required = false)
    List<IFilter> filters;

    @Autowired(required = false)
    List<MaskingPattern> maskingPatterns;

    @Autowired(required = false)
    List<IContentProcessor> contentPreProcessors;

    /**
     * Execute post object construction
     */
    @PostConstruct
    public void init() {
        LoggerUtil.info("ejmask-spring-core", "context-configuration", "initialization started");
        EJMaskInitializer.addFilters(this.filters);
        EJMaskInitializer.addMaskingPatterns(this.maskingPatterns);
        EJMaskInitializer.addContentProcessors(this.contentPreProcessors);
        LoggerUtil.info("ejmask-spring-core", "context-configuration", "initialization completed");
    }
}
