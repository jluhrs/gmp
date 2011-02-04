package edu.gemini.aspen.giapi.commands;

import com.google.common.collect.ImmutableSortedMap;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A straightforward implementation of {@link Configuration}
 *
 * DefaultConfiguration is fully immutable it cannot be changed
 *
 * Empty Configurations are not allowed to be built
 *
 */
public final class DefaultConfiguration implements Configuration {

    private final ImmutableSortedMap<ConfigPath, String> _config;

    /**
     * Factory method to create a new configuration
     *
     * @param path The Path to the configuration, cannot be null
     * @param value The value of the configuration, cannot be null
     * @return A fully constructed configuration
     */
    public static Configuration configuration(ConfigPath path, String value) {
        return new DefaultConfiguration(path, value);
    }
    
    DefaultConfiguration() {
        _config = ImmutableSortedMap.of();
    }

    public DefaultConfiguration(Configuration config) {
        if (config.getKeys().isEmpty()) {
            throw new IllegalArgumentException("Configuration cannot be empty");
        }
        SortedMap previousConfig = new TreeMap<ConfigPath, String>();
        for (ConfigPath key : config.getKeys()) {
            previousConfig.put(key, config.getValue(key));
        }
        _config = ImmutableSortedMap.copyOfSorted(previousConfig);
    }

    public DefaultConfiguration(SortedMap<ConfigPath, String> map) {
        _config = ImmutableSortedMap.copyOfSorted(map);
    }

    private DefaultConfiguration(ConfigPath path, String value) {
        _config = ImmutableSortedMap.of(path, value);
    }

    public String getValue(ConfigPath key) {
        return _config.get(key);
    }

    public Set<ConfigPath> getKeys() {
        return _config.keySet();
    }

    public Configuration getSubConfiguration(ConfigPath path) {
        if (path == null) {
            return null;
        }
        ConfigPath endPath = new ConfigPath(path, "\uFFFF");
        return new DefaultConfiguration(_config.subMap(path, endPath));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultConfiguration that = (DefaultConfiguration) o;

        if (!_config.equals(that._config)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _config.hashCode();
    }

    @Override
    public String toString() {
        return "{config=" + _config +'}';
    }
}