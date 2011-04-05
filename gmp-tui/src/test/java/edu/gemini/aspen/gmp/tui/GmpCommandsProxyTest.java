package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
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
    public void testVerifyCommands() {
        commandsProxy.verify("START");

        verifyCommand(SequenceCommand.VERIFY);
    }

    @Test
    public void testEndVerifyCommands() {
        commandsProxy.endVerify("START");

        verifyCommand(SequenceCommand.END_VERIFY);
    }

    @Test
    public void testGuideCommands() {
        commandsProxy.guide("START");

        verifyCommand(SequenceCommand.GUIDE);
    }

    @Test
    public void testEndGuideCommands() {
        commandsProxy.endGuide("START");

        verifyCommand(SequenceCommand.END_GUIDE);
    }

    @Test
    public void testApplyCommands() {
        Configuration configuration = configurationBuilder()
                .withConfiguration("gpi:cc.x", "1")
                .build();

        commandsProxy.apply("START", "gpi:cc.x=1");
        verifyCommand(SequenceCommand.APPLY, configuration);
    }

    private void verifyCommand(SequenceCommand sequenceCommand, Configuration configuration) {
        Command command = new Command(sequenceCommand, Activity.START, configuration);
        verify(sender).sendCommand(eq(command), Matchers.<CompletionListener>anyObject());
    }
}
