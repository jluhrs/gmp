package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;

/**
 * Argument to get a Configuration
 */
public class ConfigArgument extends AbstractArgument {

    private Configuration _config;

    public ConfigArgument() {
        super("config");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            _config = _parseConfiguration(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal configuration: " + arg + " (" + ex.getMessage() + ")");
        }
    }

    public String getInvalidArgumentMsg() {
        return "What configuration? Try -config <configuration>";
    }

    public Configuration getConfiguration() {
        return _config;
    }

    private Configuration _parseConfiguration(String val) throws IllegalArgumentException {

        if (val == null)
            throw new IllegalArgumentException("Empty configuration");

        String[] items = val.split("\\s+");
        for (String item : items) {
            String[] arg = item.split("=");
            if (arg.length != 2)
                throw new IllegalArgumentException("Configuration item '" +
                        item + "' not in the form 'key=value'");
            if (_config == null)
                _config = emptyConfiguration();
            DefaultConfiguration dc = (DefaultConfiguration) _config;
            _config = DefaultConfiguration.copy(_config).withPath(configPath(arg[0]), arg[1]).build();
        }
        return _config;
    }

}
