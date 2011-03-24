package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.jms.api.FormatException;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandMessageParserTest {
    private MapMessageMock msg;
    private Configuration referenceConfiguration;

    @Before
    public void buildMocks() {
        msg = new MapMessageMock();
        referenceConfiguration = configurationBuilder()
                .withPath(configPath("x:A"), "1")
                .withPath(configPath("x:B"), "2")
                .build();
    }

    @Test
    public void testReadCommand() throws JMSException {
        msg.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        msg.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        msg.setString("x:A", "1");
        msg.setString("x:B", "2");
        msg.setJMSCorrelationID("1");

        Command command = new CommandMessageParser(msg).readCommand();
        Command referenceCommand = new Command(SequenceCommand.APPLY, Activity.PRESET, referenceConfiguration);
        assertEquals(referenceCommand, command);
    }

    @Test(expected = FormatException.class)
    public void parseUnknownSequenceCommand() throws JMSException {
        msg.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, "NOSEQUENCE");
        msg.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        new CommandMessageParser(msg).readCommand();
    }

    @Test(expected = FormatException.class)
    public void parseMessageWithoutCorrelationID() throws JMSException {
        msg.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.DATUM.toString());
        msg.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        new CommandMessageParser(msg).readCommand();
    }

    @Test(expected = FormatException.class)
    public void parseMessageWithoutSequenceKey() throws JMSException {
        msg.setJMSCorrelationID("1");
        msg.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        new CommandMessageParser(msg).readCommand();
    }

    @Test(expected = FormatException.class)
    public void parseMessageWithUnknownSequenceKey() throws JMSException {
        msg.setJMSCorrelationID("1");
        msg.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, "FAKECOMMAND");
        new CommandMessageParser(msg).readCommand();
    }

    @Test(expected = FormatException.class)
    public void parseNonMapMessage() throws JMSException {
        Message msg = mock(Message.class);
        new CommandMessageParser(msg);
    }

    @Test(expected = FormatException.class)
    public void testJmsExceptionsConvertedToFormatException() throws JMSException {
        MapMessage msg = mock(MapMessage.class);
        when(msg.getJMSCorrelationID()).thenThrow(new JMSException("Exception"));
        new CommandMessageParser(msg).readCommand();
    }
}
