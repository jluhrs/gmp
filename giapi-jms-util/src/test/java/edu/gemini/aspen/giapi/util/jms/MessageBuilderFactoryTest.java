package edu.gemini.aspen.giapi.util.jms;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.jms.api.MapMessageBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageBuilderFactoryTest {
    private String dataLabelValue;
    private Configuration config;
    private String errorMessage;
    private String correlationID;

    @Before
    public void setUp() throws Exception {
        dataLabelValue = "2011-03-21-210290239.dat";
        config = configurationBuilder()
                .withConfiguration("DATA_LABEL", dataLabelValue)
                .build();
        errorMessage = "Error Message";
        correlationID = "1";
    }

    @Test
    public void testHandlerResponseFactory() throws JMSException {
        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(response, correlationID);

        MapMessageMock message = new MapMessageMock();
        MapMessage mapMessage = messageBuilder.constructMessageBody(message);
        assertEquals(correlationID, mapMessage.getJMSCorrelationID());
        assertEquals("ACCEPTED", mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
    }

    @Test
    public void testHandlerResponseErrorFactory() throws JMSException {
        HandlerResponse response = HandlerResponse.createError(errorMessage);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(response, correlationID);
        assertNotNull(messageBuilder);

        MapMessageMock message = new MapMessageMock();
        MapMessage mapMessage = messageBuilder.constructMessageBody(message);
        assertEquals(correlationID, mapMessage.getJMSCorrelationID());
        assertEquals("ERROR", mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(errorMessage, mapMessage.getString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));
    }

    @Test
    public void testCompletionInformation() throws JMSException {
        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.COMPLETED);
        Command command = new Command(SequenceCommand.GUIDE, Activity.START);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(new CompletionInformation(response, command), correlationID);

        MapMessageMock message = new MapMessageMock();
        MapMessage mapMessage = messageBuilder.constructMessageBody(message);

        assertEquals(correlationID, mapMessage.getJMSCorrelationID());
        assertEquals("COMPLETED", mapMessage.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals("GUIDE", mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals("START", mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));
    }

    @Test
    public void testCompletionInformationWithConfiguration() throws JMSException {
        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.COMPLETED);
        Command command = new Command(SequenceCommand.OBSERVE, Activity.START, config);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(new CompletionInformation(response, command), correlationID);

        MapMessageMock message = new MapMessageMock();
        MapMessage mapMessage = messageBuilder.constructMessageBody(message);

        assertEquals(correlationID, mapMessage.getJMSCorrelationID());
        assertEquals("COMPLETED", mapMessage.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        validateObserveCommand(mapMessage);
    }

    private void validateObserveCommand(MapMessage mapMessage) throws JMSException {
        assertEquals("OBSERVE", mapMessage.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY));
        assertEquals("START", mapMessage.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY));
        assertEquals(dataLabelValue, mapMessage.getString("DATA_LABEL"));
    }

    @Test
    public void testCompletionInformationErrorAndConfiguration() throws JMSException {
        HandlerResponse response = HandlerResponse.createError(errorMessage);
        Command command = new Command(SequenceCommand.OBSERVE, Activity.START, config);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(new CompletionInformation(response, command), correlationID);

        MapMessageMock message = new MapMessageMock();
        MapMessage mapMessage = messageBuilder.constructMessageBody(message);

        assertEquals(correlationID, mapMessage.getJMSCorrelationID());
        assertEquals("ERROR", mapMessage.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY));
        assertEquals(errorMessage, mapMessage.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY));

        validateObserveCommand(mapMessage);
    }
}
