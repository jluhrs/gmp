package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Reboot Argument
 */
public class RebootArgumentTest {
    @Test
    public void testParseReboot() {
        Configuration config = DefaultConfiguration.configuration(new ConfigPath("REBOOT_OPT"), "REBOOT");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.REBOOT, arg);
    }

    @Test
    public void testParseGmp() {
        Configuration config = DefaultConfiguration.configuration(new ConfigPath("REBOOT_OPT"), "GMP");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.GMP, arg);
    }

    @Test
    public void testParseNone() {
        Configuration config = DefaultConfiguration.configuration(new ConfigPath("REBOOT_OPT"), "NONE");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(arg, RebootArgument.NONE);

        //it should also return NONE if the configuration is empty.

        config = new DefaultConfiguration();
        arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.NONE, arg);

        //or null.
        arg = RebootArgument.parse(null);
        assertEquals(RebootArgument.NONE, arg);
    }

    @Test
    public void testParseInvalidArg() {
        Configuration config = DefaultConfiguration.configuration(new ConfigPath("REBOOT_OPT"), "InvalidArg");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(null, arg);

        config = DefaultConfiguration.configuration(new ConfigPath("INVALID_KEY"), "REBOOT");

        arg = RebootArgument.parse(config);
        assertEquals(null, arg);
    }

    @Test
    public void testParseLowerCase() {
        Configuration config = DefaultConfiguration.configuration(new ConfigPath("REBOOT_OPT"), "reboot");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.REBOOT, arg);
    }
}
