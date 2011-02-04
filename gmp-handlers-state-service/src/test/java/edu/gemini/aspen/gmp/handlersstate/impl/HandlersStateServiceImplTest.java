package edu.gemini.aspen.gmp.handlersstate.impl;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;
import edu.gemini.jms.activemq.broker.ActiveMQBroker;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.SortedMap;

import static edu.gemini.jms.activemq.broker.ActiveMQBroker.activemq;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for the HandlersStateService, we need to start an ActiveMQ Broker and
 * add a few handlers as in GPI to detect them via AdvisoryMessages
 */
public class HandlersStateServiceImplTest {
    private HandlersStateService handlerStateService;
    private ActiveMQBroker broker;
    private ActiveMQJmsProvider provider;

    @Before
    public void setUp() throws JMSException {
        handlerStateService = new HandlersStateServiceImpl();
        broker = activemq().useJmx(false).persistent(false).url("vm://gmp").useAdvisoryMessages(true).build();
        broker.start();

        provider = new ActiveMQJmsProvider("vm://gmp");
        provider.getConnectionFactory().createConnection();
    }

    @After
    public void shutDownBroker() {
        broker.shutdown();
    }

    @Test
    public void testBasic() {
        assertNotNull(handlerStateService);
    }

    @Test
    public void testWithNoHandlers() {
        Configuration config = buildConfiguration();
        assertFalse(handlerStateService.isConfigurationHandled(config));
    }
    @Test
    public void testWithTopLevelHandler() throws JMSException {
        Configuration config = buildConfiguration();
        // Add a top level handler
        BaseMessageConsumer topLevelConsumer = new BaseMessageConsumer("X", new DestinationData("X", DestinationType.TOPIC));
        topLevelConsumer.startJms(provider);

//        assertTrue(handlerStateService.isConfigurationHandled(config));

        topLevelConsumer.stopJms();
    }

    private Configuration buildConfiguration() {
        SortedMap<ConfigPath, String> configuration = Maps.newTreeMap();

        configuration.put(new ConfigPath("X.val1"), "x1");
        configuration.put(new ConfigPath("X.val2"), "x2");
        configuration.put(new ConfigPath("X.val3"), "x3");

        configuration.put(new ConfigPath("X:A.val1"), "xa1");
        configuration.put(new ConfigPath("X:A.val2"), "xa2");
        configuration.put(new ConfigPath("X:A.val3"), "xa3");

        configuration.put(new ConfigPath("X:B.val1"), "xb1");
        configuration.put(new ConfigPath("X:B.val2"), "xb2");
        configuration.put(new ConfigPath("X:B.val3"), "xb3");

        configuration.put(new ConfigPath("X:C.val1"), "xc1");
        configuration.put(new ConfigPath("X:C.val2"), "xc2");
        configuration.put(new ConfigPath("X:C.val3"), "xc3");

        return new DefaultConfiguration(configuration);
    }
}

