package edu.gemini.aspen.giapi.commands;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigPathNavigatorTest {
    private Configuration singlePathConfiguration;
    private Configuration multiplePathConfiguration;

    @Before
    public void setUp() throws Exception {
        singlePathConfiguration = configuration(configPath("gpi:cc:filter.name"), "x'");
        
        multiplePathConfiguration = configurationBuilder()
                .withPath(configPath("gpi:cc:filter.name"), "x")
                .withPath(configPath("gpi:cc:mirror.pos"), "Y")
                .withPath(configPath("gpi:dc:exposure"), "1")
                .withPath(configPath("gpi:dc:lamp"), "Z")
                .withPath(configPath("gpi:ap:inUse"), "false").build();
    }

    @Test
    public void testConstruction() {
        ConfigPathNavigator navigator = new ConfigPathNavigator(singlePathConfiguration);
        assertNotNull(navigator);
    }

    @Test
    public void testGetRootWithSinglePath() {
        ConfigPathNavigator navigator = new ConfigPathNavigator(singlePathConfiguration);
        Set<ConfigPath> configPaths = navigator.getRoot();
        assertEquals(1, configPaths.size());
        assertTrue(configPaths.contains(configPath("gpi")));
    }

    @Test
    public void testGetRootWithMultiplePaths() {
        ConfigPathNavigator navigator = new ConfigPathNavigator(multiplePathConfiguration);
        Set<ConfigPath> configPaths = navigator.getRoot();
        assertEquals(1, configPaths.size());
        assertTrue(configPaths.contains(configPath("gpi")));
    }

    @Test
    public void testChildPaths() {
        ConfigPathNavigator navigator = new ConfigPathNavigator(multiplePathConfiguration);
        Set<ConfigPath> childPaths = navigator.getChildPaths(configPath("gpi"));
        assertEquals(3, childPaths.size());
        assertTrue(childPaths.contains(configPath("gpi:dc")));
        assertTrue(childPaths.contains(configPath("gpi:cc")));
        assertTrue(childPaths.contains(configPath("gpi:ap")));
    }
}
