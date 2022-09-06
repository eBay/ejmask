package com.ebay.pmt2.ejmask.core;

/**
 * Exception thrown when the EJMash initialization fails.
 *
 * @author prakv
 */
public class BuilderInitializerException extends RuntimeException {
    
    /**
     * Create new instance of BuilderInitializerException
     *
     * @param message as error message.
     * @param e       as exception case.
     */
    public BuilderInitializerException(String message, Exception e) {
        super(message, e);
    }
}
