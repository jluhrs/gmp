package edu.gemini.aspen.gmp.commands.api;

import java.util.Enumeration;
import java.io.Serializable;

/**
 * Configuration interface.
 * <p/>
 * A configuration is made up of parameters. Each parameter has a key and a
 * value. The OCS commands an instrument by demanding that the instrument
 * match a specified configuration.
 * <p/>
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
	 *
	 * @return the value associated to the given key or <code>NULL</code>
	 *         if there is no value associated for the key in the current
	 *         configuration.
	 */
    String getValue(String key);

    /**
	 * Return the keys contained in the configuration. If the configuration
	 * does not contain any key, an empty enumeration is returned.
	 *
	 * @return Enumeration of strings representing the keys
	 *         contained in the configuration. An empty enmeration is returned
	 *         if no keys are present.
	 */
	Enumeration<String> getKeys();

}
