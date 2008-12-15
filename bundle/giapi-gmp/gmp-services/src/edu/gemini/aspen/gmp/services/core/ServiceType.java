package edu.gemini.aspen.gmp.services.core;

/**
 * The different type of services available
 */
public enum ServiceType {

    PROPERTY_SERVICE(0),
    LOGGING_SERVICE(1);
    
    private int _code;

    ServiceType(int code) {
        _code = code;
    }

    /**
     * Returns the integer code associated
     * to the type.
     * @return the integer code associated to the given service type
     */
    public int getCode() {
        return _code;
    }

}
