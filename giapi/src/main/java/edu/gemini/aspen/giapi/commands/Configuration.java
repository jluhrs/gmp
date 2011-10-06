package edu.gemini.aspen.giapi.commands;

import java.util.Set;

/**
 * Configuration interface.
 * <br>
 * A configuration is made up of parameters. Each parameter has a key and a
 * value. The OCS commands an instrument by demanding that the instrument
 * match a specified configuration.
 * <br>
 * This interface provides mechanisms to:
 * a) get the value associated to a given parameter (identified by key)
 * b) get all the keys present in the configuration.
 */
public interface Configuration {

    /**
     * Return the value associated to the given key. The key is the name
     * of the parameter associated to the value, like inst:filterA.
     *
     * @param key the name of the parameter whose value we need to retrieve
     * @return the value associated to the given key or <code>NULL</code>
     *         if there is no value associated for the key in the current
     *         configuration.
     */
    String getValue(ConfigPath key);

    /**
     * Return the keys contained in the configuration. If the configuration
     * does not contain any key, an empty set is returned.
     *
     * @return Set of strings representing the keys
     *         contained in the configuration. An empty set is returned
     *         if no keys are present.
     */
    Set<ConfigPath> getKeys();


    /**
     * Get the Configuration items that match the given path. The returned
     * object is a new Configuration instance.
     *
     * @param path ConfigPath used to filter the configuration items
     * @return a new Configuration whose items match the given path
     */
    Configuration getSubConfiguration(ConfigPath path);

    /**
     * Indicates whether the Configuration is empty meaning it doesn't
     * containing any paths
     */
    boolean isEmpty();
}
