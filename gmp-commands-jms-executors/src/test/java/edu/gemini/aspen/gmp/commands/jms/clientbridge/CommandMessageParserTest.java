package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Enumeration;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandMessageParserTest {
    private MapMessage msg;

    @Before
    public void buildMocks() {
        msg = mock(MapMessage.class);
    }

    @Test
    public void parseSequenceCommand() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn(SequenceCommand.APPLY.toString());
        SequenceCommand sequenceCommand = CommandMessageParser.parseSequenceCommand(msg);

        assertEquals(SequenceCommand.APPLY, sequenceCommand);
    }

    @Test(expected = FormatException.class)
    public void parseUnknownSequenceCommand() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn("NOSEQUENCE");
        CommandMessageParser.parseSequenceCommand(msg);
    }

    @Test(expected = FormatException.class)
    public void parseNoSequenceCommand() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn(null);
        CommandMessageParser.parseSequenceCommand(msg);
    }

    @Test
    public void parseActivity() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn(Activity.PRESET.toString());
        Activity activity = CommandMessageParser.parseActivity(msg);

        assertEquals(Activity.PRESET, activity);
    }

    @Test(expected = FormatException.class)
    public void parseUnknownActivity() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn("NOACTIVITY");
        CommandMessageParser.parseActivity(msg);
    }

    @Test(expected = FormatException.class)
    public void parseNoActivity() throws JMSException {
        when(msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn(null);
        CommandMessageParser.parseActivity(msg);
    }

    @Test
    public void parseNoConfiguration() throws JMSException {
        when(msg.getMapNames()).thenReturn(Iterators.asEnumeration(Iterators.<Object>emptyIterator()));
        Configuration parsedConfiguration = CommandMessageParser.parseConfiguration(msg);

        assertEquals(emptyConfiguration(), parsedConfiguration);
    }

    @Test
    public void parseConfiguration() throws JMSException {
        Configuration referenceConfiguration = configurationBuilder()
                .withPath(configPath("x:A"), "1")
                .withPath(configPath("x:B"), "2")
                .build();

        ImmutableMap<String, String> configurationItems = ImmutableMap.of("x:A", "1", "x:B", "2");
        Enumeration mapNames = Iterators.asEnumeration(configurationItems.keySet().iterator());
        when(msg.getMapNames()).thenReturn(mapNames);

        when(msg.getString("x:A")).thenReturn("1");
        when(msg.getString("x:B")).thenReturn("2");

        Configuration parsedConfiguration = CommandMessageParser.parseConfiguration(msg);
        assertEquals(referenceConfiguration, parsedConfiguration);
    }

}
