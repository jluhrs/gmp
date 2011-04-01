package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GmpCommandsProxyTest {

    private CommandSender sender;
    private GmpCommandsProxy commandsProxy;

    @Before
    public void setUp() throws Exception {
        sender = mock(CommandSender.class);
        commandsProxy = new GmpCommandsProxy(sender);
    }

    @Test
    public void testParkCommands() {
        commandsProxy.park("START");

        verifyCommand(SequenceCommand.PARK);
    }

    private void verifyCommand(SequenceCommand sequenceCommand) {
        Command command = new Command(sequenceCommand, Activity.START);
        verify(sender).sendCommand(eq(command), Matchers.<CompletionListener>anyObject());
    }

    @Test
    public void testGenericCommands() {
        commandsProxy.command("INIT", "START");

        verifyCommand(SequenceCommand.INIT);
    }

    @Test
    public void testDatumCommands() {
        commandsProxy.datum("START");

        verifyCommand(SequenceCommand.DATUM);
    }

    @Test
    public void testInitCommands() {
        commandsProxy.init("START");

        verifyCommand(SequenceCommand.INIT);
    }

    @Test
    public void testTestCommands() {
        commandsProxy.test("START");

        verifyCommand(SequenceCommand.TEST);
    }
}
