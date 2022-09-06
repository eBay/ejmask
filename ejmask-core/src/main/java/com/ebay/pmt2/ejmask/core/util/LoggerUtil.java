package com.ebay.pmt2.ejmask.core.util;

import com.ebay.pmt2.ejmask.api.ILogProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * customizable logging util
 */
public class LoggerUtil {

    private static ILogProvider logger = new DefaultLogProvider();

    /**
     * Set Own ILogProvider Instance
     *
     * @param logger as instance of ILogProvider
     */
    public static void register(ILogProvider logger) {
        LoggerUtil.logger = logger;
    }

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    public static void info(String component, String flow, String message) {
        LoggerUtil.logger.info(component, flow, message);
    }


    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    public static void warning(String component, String flow, String message) {
        LoggerUtil.logger.warning(component, flow, message);
    }

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    public static void error(String component, String flow, String message) {
        LoggerUtil.logger.error(component, flow, message);
    }
}

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