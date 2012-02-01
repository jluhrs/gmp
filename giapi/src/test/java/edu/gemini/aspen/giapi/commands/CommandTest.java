package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.*;
import static org.junit.Assert.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void testBuildEngineeringWithWrongConfiguration() {
        new Command(SequenceCommand.ENGINEERING, Activity.START, configurationBuilder().withPath(configPath("REBOOT_OPT"), "REBOOT").build());
    }

    @Test
    public void testBuildEngineeringWithGoodConfiguration() {
        Configuration engConfiguration = configurationBuilder().withPath(configPath("COMMAND_NAME"), "anything").build();
        Command engCommand = new Command(SequenceCommand.ENGINEERING, Activity.START, engConfiguration);
        assertNotNull(engCommand);

        assertEquals(SequenceCommand.ENGINEERING, engCommand.getSequenceCommand());
        assertEquals(Activity.START, engCommand.getActivity());
        assertEquals(engConfiguration, engCommand.getConfiguration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildObserveWithoutConfiguration() {
        new Command(SequenceCommand.OBSERVE, Activity.START);
    }

    @Test
    public void testBuildApplyCommandSimple() {
        Configuration config = configuration(configPath("X.val1"), "value1");
        Command command = new Command(SequenceCommand.APPLY, Activity.START, config);
        assertTrue(command.isApply());
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
        Command d = new Command(SequenceCommand.APPLY, Activity.START, emptyConfiguration()) {
        };
        new EqualsTester(a, b, c, d);

        Configuration simpleConfig = configurationBuilder().withPath(configPath("x:A.v"), "1").build();
        c = new Command(SequenceCommand.APPLY, Activity.START, simpleConfig);
        new EqualsTester(a, b, c, d);
    }
}
