package edu.gemini.aspen.gmp.tui;

import com.google.common.base.Splitter;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;

import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;

public class ConfigurationParser {
    private final String _configurationArg;
    private static final Pattern CONFIGURATION_ITEM_PARSER = Pattern.compile("([\\w\\.:]+)=([\\w]+)");

    public ConfigurationParser(String configurationArg) {
        _configurationArg = configurationArg;
    }

    public Configuration parse() throws IllegalFormatException {

        DefaultConfiguration.Builder builder = configurationBuilder();

        Iterable<String> splitOnComma = Splitter.on(",").trimResults().split(_configurationArg);
        for (String configurationPair : splitOnComma) {
            Matcher matcher = CONFIGURATION_ITEM_PARSER.matcher(configurationPair);
            if (matcher.matches()) {
                String path = matcher.group(1);
                String value = matcher.group(2);
                builder.withConfiguration(path, value);
            }
        }

        return builder.build();
    }
}
