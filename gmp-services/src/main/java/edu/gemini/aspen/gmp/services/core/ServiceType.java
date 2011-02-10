package edu.gemini.aspen.gmp.services.core;

/**
 * The different type of services available
 */
public enum ServiceType {

    PROPERTY_SERVICE("Property Service"),
    TIME_SERVICE("Time Service");
    
    private String _name;

    ServiceType(String name) {
        _name = name;
    }

    /**
     * Returns the display name associated
     * to the type.
     * @return the display name for the given service.
     */
    public String getName() {
        return _name;
    }

}
