package edu.gemini.aspen.gmp.commands;
import java.util.*;

/**
 * A straightforward implementation of {@link Configuration}
 *
 */
public class DefaultConfiguration implements Configuration {

    private SortedMap<ConfigPath, String> _config;

    public DefaultConfiguration() {
        _config = new TreeMap<ConfigPath, String>();
    }

    public DefaultConfiguration(Configuration config) {
        _config = new TreeMap<ConfigPath, String>();
        Set<ConfigPath> keys = config.getKeys();
        for (ConfigPath key : keys) {
            _config.put(key, config.getValue(key));
        }
    }

    public DefaultConfiguration(SortedMap<ConfigPath, String> map) {
        _config = new TreeMap<ConfigPath, String>(map);
    }

    public void put(ConfigPath path, String value) {
        _config.put(path, value);
    }

    public String getValue(ConfigPath key) {
        return _config.get(key);
    }

    public Set<ConfigPath> getKeys() {
        return _config.keySet();
    }

    public Configuration getSubConfiguration(ConfigPath path) {

        if (path == null) return null;
        ConfigPath endPath = new ConfigPath(path, "\uFFFF");
        SortedMap<ConfigPath, String> map = _config.subMap(path, endPath);

        return new DefaultConfiguration(map);
    }

    @Override
    public String toString() {
        return "{config=" + _config +'}';
    }
}