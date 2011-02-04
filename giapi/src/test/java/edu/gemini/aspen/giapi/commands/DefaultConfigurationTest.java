package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configuration;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link DefaultConfiguration} class
 */
public class DefaultConfigurationTest {
    @Test
    public void testBasicConfiguration() {
        Configuration configuration = configuration(configPath("X"), "value1");
        assertEquals(1, configuration.getKeys().size());
        assertEquals("{config={X=value1}}", configuration.toString());
        assertEquals("value1", configuration.getValue(configPath("X")));
        assertNull("value1", configuration.getValue(configPath("Y")));
        assertTrue(configuration.getKeys().contains(configPath("X")));
        assertFalse(configuration.getKeys().contains(configPath("Y")));
        assertEquals(configuration, configuration.getSubConfiguration(configPath("X")));
        assertNull(configuration.getSubConfiguration(null));
    }

    @Test
    public void testEquality() {
        Configuration a = configuration(configPath("X", "val1"), "1");
        Configuration b = configuration(configPath("X", "val1"), "1");
        Configuration c = configuration(configPath("X", "val2"), "1");

        new EqualsTester(a, b, c, null);
    }
}
