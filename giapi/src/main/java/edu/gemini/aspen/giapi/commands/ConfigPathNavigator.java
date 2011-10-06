package edu.gemini.aspen.giapi.commands;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.util.*;

/**
 * A ConfigPathNavigator allows to iterate over a Configuration by
 * looking at the different ConfigPath as a tree.
 * <br>
 * The ConfigPathNavigator allows to get several ConfigPath elements
 * out of a given configuration. However, these objects are used
 * mostly to traverse a Configuration rather than accessing a particular
 * item in the Configuration. 
 *
 */
public class ConfigPathNavigator {

    private final Map<ConfigPath, String> _treeMap;

    /**
     * Construct using the given configuration
     * @param config the configuration to be navigated
     * by this ConfigPathNavigator
     */
    public ConfigPathNavigator(Configuration config) {
        TreeMap<ConfigPath, String> configMap = Maps.newTreeMap();
        Set<ConfigPath> keys = config.getKeys();
        for (ConfigPath key: keys) {
            configMap.put(key, config.getValue(key));
        }
        _treeMap = ImmutableMap.copyOf(configMap);
    }

    /**
     * Returns the set of ConfigPath elements for the given
     * configuration.
     * @return set of ConfigPath elements for the given configuration
     */
    public Set<ConfigPath> getRoot() {
        return getChildPaths(ConfigPath.EMPTY_PATH);
    }

    /**
     * Get the child paths for the given configuration, starting
     * with the path argument.
     * <br>
     * For instance, if the configuration contains:
     * <code>
     *   gpi:cc:filter.name = X
     *   gpi:cc:mirror.pos = Y
     *   gpi:dc:exposure = 1
     *   gpi:dc:lamp = Z
     *   gpi:ao:inUse = false
     * </code>
     *
     * a call to <code>getChildPaths(new ConfigPath("gpi")) </code>
     * will return a set containing:
     * <code>
     *   gpi:ao
     *   gpi:cc
     *   gpi:dc
     * </code>
     *
     * @param path parent path to be used to filter the current configuration
     * @return a set of all the child paths that can be obtained from the
     * current configuration
     */
    public Set<ConfigPath> getChildPaths(final ConfigPath path) {
        //ordered set of all the keys
        Set<ConfigPath> set = _treeMap.keySet();

        Set<ConfigPath> results = new HashSet<ConfigPath>();
        for (ConfigPath key: set) {
            ConfigPath nextPath = key.getChildPath(path);
            if (nextPath != null) {
                results.add(nextPath);
            }
        }
        return results;
    }

}
