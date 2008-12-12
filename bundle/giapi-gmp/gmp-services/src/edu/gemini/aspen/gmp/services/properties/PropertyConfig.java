package edu.gemini.aspen.gmp.services.properties;

import java.util.Map;

/**
 * Provides configuration information for the properties served by
 * the GMP
 */
public interface PropertyConfig {

    /**
     * Get a map with properties the GMP will use to answer
     * requests from clients.
     *
     * @return a Map with string properties. <code>null</code>
     * if there is no properties in the configuration.
     */
    Map<String, String> getProperties();

}
