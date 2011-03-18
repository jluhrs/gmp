package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationArgumentTest {
    @Test
    public void basicPropertiesTests() {
        ConfigArgument configArgument = new ConfigArgument();
        configArgument.parseParameter("x:A=1 x:B=2");

        assertTrue(configArgument.requireParameter());
        assertFalse(configArgument.getInvalidArgumentMsg().isEmpty());

        Configuration config = configurationBuilder()
                .withPath(ConfigPath.configPath("x:A"), "1")
                .withPath(ConfigPath.configPath("x:B"), "2")
                .build();

        assertEquals(config, configArgument.getConfiguration());
    }
}
