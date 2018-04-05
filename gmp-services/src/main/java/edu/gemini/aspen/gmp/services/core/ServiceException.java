package edu.gemini.aspen.gmp.services.core;

/**
 * Exception generated when processing service requests. 
 */
public class ServiceException extends Exception {

    public ServiceException(Exception e) {
        super(e);
    }
}
