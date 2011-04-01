package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GmpCommandsProxyTest {

    @Test
    public void testGenericCommands() {
        CommandSender sender = mock(CommandSender.class);

        GmpCommandsProxy commandsProxy = new GmpCommandsProxy(sender);
        commandsProxy.command("INIT", "START");

        Command command = new Command(SequenceCommand.INIT, Activity.START);

        verify(sender).sendCommand(eq(command), Matchers.<CompletionListener>anyObject());
    }
}
