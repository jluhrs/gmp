package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;

public class ConfigurationParserTest {
    @Test
    public void parseSimpleConfiguration() {
        Configuration referenceConfiguration = DefaultConfiguration.configurationBuilder()
                .withConfiguration("REBOOT_OPT", "reboot")
                .build();

        Configuration configuration = new ConfigurationParser("REBOOT_OPT=reboot").parse();

        assertEquals(referenceConfiguration, configuration);
    }

    @Test
    public void parseLongerConfiguration() {
        Configuration referenceConfiguration = DefaultConfiguration.configurationBuilder()
                .withConfiguration("X.val1", "1")
                .withConfiguration("X.val2", "10")
                .build();

        Configuration configuration = new ConfigurationParser("X.val1=1,X.val2=10").parse();

        assertEquals(referenceConfiguration, configuration);
    }

    @Test
    public void parseConfigurationWithSpaces() {
        Configuration referenceConfiguration = DefaultConfiguration.configurationBuilder()
                .withConfiguration("X.val1", "1")
                .withConfiguration("X.val2", "10")
                .build();

        Configuration configuration = new ConfigurationParser("X.val1=1,  X.val2=10 ").parse();

        assertEquals(referenceConfiguration, configuration);
    }

    @Test
    public void parseMissingPartConfiguration() {
        Configuration referenceConfiguration = DefaultConfiguration.configurationBuilder()
                .withConfiguration("X.val2", "10")
                .build();

        Configuration configuration = new ConfigurationParser("X.val1=,  X.val2=10 ").parse();

        assertEquals(referenceConfiguration, configuration);
    }

    @Test
    public void parseBadConfiguration() {
        Configuration referenceConfiguration = emptyConfiguration();

        Configuration configuration = new ConfigurationParser("A").parse();

        assertEquals(referenceConfiguration, configuration);
    }
}
