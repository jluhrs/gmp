package edu.gemini.giapi.tool.commands;

import edu.gemini.giapi.tool.arguments.ActivityArgument;
import edu.gemini.giapi.tool.arguments.ConfigArgument;
import edu.gemini.giapi.tool.arguments.SequenceCommandArgument;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandOperationTest {
    @Test
    public void testConstructionOfPark() {
        CommandOperation commandOperation = new CommandOperation();

        SequenceCommandArgument sequenceCommandArgument = new SequenceCommandArgument();
        sequenceCommandArgument.parseParameter("PARK");

        ActivityArgument activityArgument = new ActivityArgument();
        activityArgument.parseParameter("PRESET");

        assertFalse(commandOperation.isReady());

        commandOperation.setArgument(sequenceCommandArgument);
        assertFalse(commandOperation.isReady());

        commandOperation.setArgument(activityArgument);
        assertTrue(commandOperation.isReady());
    }

    @Test
    public void testConstructionOfApply() {
        CommandOperation commandOperation = new CommandOperation();

        SequenceCommandArgument sequenceCommandArgument = new SequenceCommandArgument();
        sequenceCommandArgument.parseParameter("APPLY");

        ActivityArgument activityArgument = new ActivityArgument();
        activityArgument.parseParameter("PRESET_START");

        ConfigArgument configArgument = new ConfigArgument();
        configArgument.parseParameter("x:A=1 x:B=2");

        commandOperation.setArgument(sequenceCommandArgument);
        commandOperation.setArgument(activityArgument);
        commandOperation.setArgument(configArgument);

        assertTrue(commandOperation.isReady());
    }
}
