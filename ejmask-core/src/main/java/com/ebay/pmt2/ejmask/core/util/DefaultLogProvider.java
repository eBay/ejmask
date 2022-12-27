package com.ebay.pmt2.ejmask.core.util;

import com.ebay.pmt2.ejmask.api.ILogProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author prakv
 */
class DefaultLogProvider implements ILogProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultLogProvider.class.getName());

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    @Override
    public void info(String component, String flow, String message) {
        LOGGER.log(Level.INFO, String.format("%s - %s : %s", component, flow, message));
    }
}
