package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import java.util.Set;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigPathNavigatorTest {
    @Test
    public void testConstruction() {
//             *   gpi:cc:filter.name = X
//     *   gpi:cc:mirror.pos = Y
//     *   gpi:dc:exposure = 1
//     *   gpi:dc:lamp = Z
//     *   gpi:ao:inUse = false
        Configuration config = configuration(configPath("gpi:cc:filter.name"), "x'");
        ConfigPathNavigator navigator = new ConfigPathNavigator(config);
        assertNotNull(navigator);
    }

    @Test
    public void testGetRootWithSinglePath() {
        Configuration config = configuration(configPath("gpi:cc:filter.name"), "x'");
        ConfigPathNavigator navigator = new ConfigPathNavigator(config);
        Set<ConfigPath> configPaths = navigator.getRoot();
        assertEquals(1, configPaths.size());
        assertTrue(configPaths.contains(configPath("gpi")));
    }
}
