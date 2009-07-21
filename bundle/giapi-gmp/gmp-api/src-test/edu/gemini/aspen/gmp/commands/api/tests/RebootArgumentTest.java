package edu.gemini.aspen.gmp.commands.api.tests;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.api.RebootArgument;
import edu.gemini.aspen.gmp.commands.api.DefaultConfiguration;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;

/**
 * Test class for the Reboot Argument
 */
public class RebootArgumentTest {


    @Test
    public void testParseReboot() {

        DefaultConfiguration config = new DefaultConfiguration();
        config.put(new ConfigPath("REBOOT_OPT"), "REBOOT");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.REBOOT, arg);

    }

    @Test
    public void testParseGmp() {
        DefaultConfiguration config = new DefaultConfiguration();
        config.put(new ConfigPath("REBOOT_OPT"), "GMP");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.GMP, arg);

    }


    @Test
    public void testParseNone() {
        DefaultConfiguration config = new DefaultConfiguration();
        config.put(new ConfigPath("REBOOT_OPT"), "NONE");

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
        DefaultConfiguration config = new DefaultConfiguration();
        config.put(new ConfigPath("REBOOT_OPT"), "InvalidArg");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(null, arg);

    }

     @Test
     public void testParseLowerCase() {

        DefaultConfiguration config = new DefaultConfiguration();
        config.put(new ConfigPath("REBOOT_OPT"), "reboot");

        RebootArgument arg = RebootArgument.parse(config);
        assertEquals(RebootArgument.REBOOT, arg);

    }



}
