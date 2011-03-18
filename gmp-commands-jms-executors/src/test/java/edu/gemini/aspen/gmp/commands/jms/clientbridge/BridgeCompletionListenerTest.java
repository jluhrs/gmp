package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.jms.MockedJmsArtifactsTestBase;
import org.junit.Test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BridgeCompletionListenerTest extends MockedJmsArtifactsTestBase {
    @Test
    public void testOnHandlerResponse() throws JMSException {
        super.createMockedObjects();

        Destination destination = mock(Destination.class);
        BridgeCompletionListener completionListener = new BridgeCompletionListener(destination);

        completionListener.startJms(provider);

        Configuration referenceConfiguration = configurationBuilder()
                .withPath(configPath("x:A"), "1")
                .withPath(configPath("x:B"), "2")
                .build();

        Command referenceCommand = new Command(SequenceCommand.APPLY, Activity.PRESET, referenceConfiguration);
        completionListener.onHandlerResponse(HandlerResponse.ACCEPTED, referenceCommand);

        verify(producer).send(eq(destination), any(MapMessage.class));
    }

    @Test
    public void testOnHandlerErrorResponse() throws JMSException {
        super.createMockedObjects();

        Destination destination = mock(Destination.class);
        BridgeCompletionListener completionListener = new BridgeCompletionListener(destination);

        completionListener.startJms(provider);

        Configuration referenceConfiguration = configurationBuilder()
                .withPath(configPath("x:A"), "1")
                .withPath(configPath("x:B"), "2")
                .build();

        Command referenceCommand = new Command(SequenceCommand.APPLY, Activity.PRESET, referenceConfiguration);
        completionListener.onHandlerResponse(HandlerResponse.createError("Error Message"), referenceCommand);

        verify(producer).send(eq(destination), any(MapMessage.class));
    }

    @Test
    public void testBuildContentFromConfiguration() throws JMSException {
        Configuration config = configurationBuilder()
                .withPath(configPath("gpi:dc.value1"), "one")
                .withPath(configPath("gpi:dc.value2"), "two")
                .build();

        Destination destination = mock(Destination.class);
        BridgeCompletionListener completionListener = new BridgeCompletionListener(destination);
        Map<String, String> contentsMap = completionListener.buildMessageContent(config);

        for (ConfigPath path : config.getKeys()) {
            assertEquals(config.getValue(path), contentsMap.get(path.getName()));
        }
    }

    @Test
    public void testBuildPropertiesForOnHandlerResponse() throws JMSException {
        String errorMsg = "Error Message";

        HandlerResponse response = HandlerResponse.createError(errorMsg);
        Command command = new Command(
                SequenceCommand.INIT,
                Activity.START,
                emptyConfiguration()
        );

        Destination destination = mock(Destination.class);
        BridgeCompletionListener completionListener = new BridgeCompletionListener(destination);
        Map<String, String> m = completionListener.buildProperties(response, command);

        assertEquals(HandlerResponse.Response.ERROR.toString(), m.get(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(errorMsg, m.get(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
        assertEquals(SequenceCommand.INIT.name(), m.get(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals(Activity.START.name(), m.get(JmsKeys.GMP_ACTIVITY_KEY));
    }

}
