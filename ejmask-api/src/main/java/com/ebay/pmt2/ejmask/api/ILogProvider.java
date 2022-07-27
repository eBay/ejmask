package com.ebay.pmt2.ejmask.api;

/**
 * Defines a Log provider
 *
 * @author prakv
 */
public interface ILogProvider {

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    void info(String component, String flow, String message);

    /**
     * Log a message, as warning.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    default void warning(String component, String flow, String message) {
        this.info(component, flow, message);
    }

    /**
     * Log a message, as error.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    default void error(String component, String flow, String message) {
        this.info(component, flow, message);
    }
}
