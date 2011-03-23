package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandTest {
    @Test
    public void testBuildApplyCommand() {
        Command applyCommand = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration());
        assertNotNull(applyCommand);
        assertTrue(applyCommand.isApply());
        assertFalse(applyCommand.toString().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildApplyWithoutConfiguration() {
        new Command(SequenceCommand.APPLY, Activity.START);
    }

    @Test
    public void testBuildDatumCommand() {
        Command rebootCommand = new Command(SequenceCommand.DATUM, Activity.START);
        assertNotNull(rebootCommand);
    }

    @Test
    public void testBuildRebootCommand() {
        Configuration rebootConfiguration = configurationBuilder().withPath(configPath("REBOOT_OPT"), "REBOOT").build();
        Command rebootCommand = new Command(SequenceCommand.REBOOT, Activity.START, rebootConfiguration);
        assertNotNull(rebootCommand);

        assertEquals(SequenceCommand.REBOOT, rebootCommand.getSequenceCommand());
        assertEquals(Activity.START, rebootCommand.getActivity());
        assertEquals(rebootConfiguration, rebootCommand.getConfiguration());
    }

    @Test
    public void testNoCommand() {
        Command noCommand = Command.noCommand();
        assertNotNull(noCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildRebootWithoutConfiguration() {
        new Command(SequenceCommand.REBOOT, Activity.START);
    } 

    @Test
    public void testEquals() {
        Command a = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration());
        Command b = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration());
        Command c = new Command(SequenceCommand.PARK, Activity.START, emptyConfiguration());
        Command d = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration()) {};
        new EqualsTester(a,b,c,d);

        Configuration simpleConfig = configurationBuilder().withPath(configPath("x:A.v"), "1").build();
        c = new Command(SequenceCommand.APPLY, Activity.START, simpleConfig);
        new EqualsTester(a,b,c,d);
    }
}
