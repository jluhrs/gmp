package edu.gemini.aspen.gmp.servlet.www;

import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;
import edu.gemini.aspen.gmp.commands.api.ConfigPathNavigator;
import edu.gemini.aspen.gmp.commands.api.DefaultConfiguration;

import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * A configuration parser takes a map representation of a configuration (as it comes from the servlet interface)
 * and transform it to a real Configuration object.
 */
public class ConfigurationParser {

    private static final Logger LOG  = Logger.getLogger(ConfigurationParser.class.getName());

    /**
     * Parse the given map into a real Configuration object
     * @param parameters map of strings to strings that represent a "textual" version of a configuration
     * @return a new Configuration for the given map.
     * @throws IllegalArgumentException if the map is <code>null</code>
     */
    public static Configuration parse(Map<String, String> parameters) throws IllegalArgumentException {

        if (parameters == null) return null;
        Set<String> keys = parameters.keySet();
        DefaultConfiguration config = new DefaultConfiguration();
        for (String key: keys) {
            config.put(new ConfigPath(key), parameters.get(key));
        }
        return config;
    }


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String> ();

        map.put("gpi:cc.filter", "red");
        map.put("gpi:cc.grating", "mirror");
        map.put("gpi:dc.grating", "mirror");
        map.put("gpi:dc.grating2", "mirror");
        map.put("gpi:dc.grating3", "mirror");
        map.put("dc:dd", "test");
        Configuration config = ConfigurationParser.parse(map);
        process(config, ConfigPath.EMPTY_PATH);
    }

    public static void process(Configuration config, ConfigPath path) {

        if (config == null) return;

        ConfigPathNavigator navigator = new ConfigPathNavigator(config);

        Set<ConfigPath> configPathSet = navigator.getChildPaths(path);

        for(ConfigPath cp: configPathSet) {

            Configuration c = config.getSubConfiguration(cp);
            StringBuilder sb = new StringBuilder("Path = ").append(cp);
            for (ConfigPath key : c.getKeys()) {
                sb.append("\n\t").append(key).append(" = ").append(c.getValue(key));
            }
            LOG.info(sb.toString());
            process(c, cp);
        }
    }
}
