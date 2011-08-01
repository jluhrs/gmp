package edu.gemini.aspen.gmp.services;

/**
 * Storage infrastructure for properties.
 */
public interface PropertyHolder {

    /**
     * Get the value associated to the given property.
     * 
     * @param key The property key
     * @return Value associated to the property key, or
     * <code>null</code> if there is no property associated
     * to the key.
     */
    String getProperty(String key);

}
