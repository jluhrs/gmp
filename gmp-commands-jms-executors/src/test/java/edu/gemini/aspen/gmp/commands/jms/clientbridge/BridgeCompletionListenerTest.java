package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.jms.MockedJmsArtifactsTestBase;
import org.junit.Test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
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
}
