package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

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
        if (val == null) {
            throw new IllegalArgumentException("Empty configuration");
        }

        DefaultConfiguration.Builder builder = DefaultConfiguration.configurationBuilder();

        String[] items = val.split("\\s+");
        for (String item : items) {
            String[] arg = item.split("=");
            if (arg.length != 2)
                throw new IllegalArgumentException("Configuration item '" +
                        item + "' not in the form 'key=value'");
            builder.withConfiguration(arg[0], arg[1]);
        }
        _config = builder.build();

        return _config;
    }

}
