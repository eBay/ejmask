package com.ebay.pmt2.ejmask.extenstion.filter;

import com.ebay.pmt2.ejmask.core.BaseFilter;
import com.ebay.pmt2.ejmask.extenstion.builder.header.HeaderFieldPatternBuilder;

/**
 * Auth header filter
 *
 * @author prakv
 */
public class AuthHeaderFilter extends BaseFilter {

    /**
     * Create new instance of AuthHeaderFilter
     */
    AuthHeaderFilter() {
        super(HeaderFieldPatternBuilder.class, 10, 90, "Authorization");
    }
}
