package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertNotNull;

public class CommandTest {
    @Test
    public void testBuildApplyCommand() {
        Command applyCommand = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration());
        assertNotNull(applyCommand);
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
    }
}
