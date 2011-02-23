package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configuration;

public class ConfigPathNavigatorTest {
    @Test
    public void testConstruction() {
//             *   gpi:cc:filter.name = X
//     *   gpi:cc:mirror.pos = Y
//     *   gpi:dc:exposure = 1
//     *   gpi:dc:lamp = Z
//     *   gpi:ao:inUse = false
        Configuration config = configuration(configPath("gpi:cc:filter.name"), "x'");
        //config
        ConfigPathNavigator navigator = new ConfigPathNavigator(config);
       }
}
