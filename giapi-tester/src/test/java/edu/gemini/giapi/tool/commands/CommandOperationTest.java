package edu.gemini.giapi.tool.commands;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.jms.client.CommandSenderClient;
import edu.gemini.giapi.tool.arguments.ActivityArgument;
import edu.gemini.giapi.tool.arguments.ConfigArgument;
import edu.gemini.giapi.tool.arguments.SequenceCommandArgument;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        buildApplyCommand(commandOperation);

        assertTrue(commandOperation.isReady());
    }

    private void buildApplyCommand(CommandOperation commandOperation) {
        SequenceCommandArgument sequenceCommandArgument = new SequenceCommandArgument();
        sequenceCommandArgument.parseParameter("APPLY");

        ActivityArgument activityArgument = new ActivityArgument();
        activityArgument.parseParameter("PRESET_START");

        ConfigArgument configArgument = new ConfigArgument();
        configArgument.parseParameter("x:A=1 x:B=2");

        commandOperation.setArgument(sequenceCommandArgument);
        commandOperation.setArgument(activityArgument);
        commandOperation.setArgument(configArgument);
    }

    @Test
    public void testErrorResponse() throws Exception {
        CommandSenderClient senderClient = mock(CommandSenderClient.class);
        CommandOperation commandOperation = new CommandOperation(senderClient);

        buildApplyCommand(commandOperation);

        when(senderClient.sendCommand(Matchers.<Command>anyObject(), Matchers.<CompletionListener>anyObject())).thenReturn(HandlerResponse.createError("Error"));

        assertEquals(1, commandOperation.execute());
    }

}
