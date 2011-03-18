package edu.gemini.giapi.tool.arguments;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigurationArgumentTest {
    @Test
    public void basicPropertiesTests() {
        ConfigArgument configArgument = new ConfigArgument();
        //configArgument.parseParameter("PARK");

        assertTrue(configArgument.requireParameter());
        //assertEquals(SequenceCommand.PARK, configArgument.getSequenceCommand());
    }
}
