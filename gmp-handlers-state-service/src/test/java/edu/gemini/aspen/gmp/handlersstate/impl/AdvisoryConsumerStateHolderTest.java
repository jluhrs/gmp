package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.jms.activemq.broker.ActiveMQBroker;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

import static edu.gemini.jms.activemq.broker.ActiveMQBroker.activemq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the AdvisoryConsumerStateHolder. This is a quite realistic test using an embbeded ActiveMQBroker
 */
public class AdvisoryConsumerStateHolderTest {
    private ActiveMQBroker broker;
    private ActiveMQJmsProvider provider;
    private AdvisoriesConsumerStateHolder stateHolder;
    private BaseMessageConsumer consumer;

    @Before
    public void setUpBroker() throws JMSException {
        broker = activemq().useJmx(false).persistent(false).url("vm://gmp").useAdvisoryMessages(true).build();
        broker.start();

        provider = new ActiveMQJmsProvider("vm://gmp");
        provider.getConnectionFactory().createConnection();

        stateHolder = new AdvisoriesConsumerStateHolder();
        stateHolder.startJms(provider);
        consumer = new BaseMessageConsumer("Listener", new DestinationData("GMP.status", DestinationType.TOPIC));
    }

    @After
    public void shutdownBroker() {
        broker.shutdown();
    }

    /**
     * Test the case a subscriber is added to GMP.status
     */
    @Test
    public void testAddedConsumer() throws JMSException, InterruptedException {
        assertTrue("Before start there is no state", stateHolder.listSubscribers().isEmpty());
        // Add a consumer so it will generate an advisory message
        consumer.startJms(provider);

        // Needs to wait a bit for the message ro arrive
        TimeUnit.MILLISECONDS.sleep(200);

        assertEquals("After the consumer is added one subscriber shows up", 1, stateHolder.listSubscribers().size());
    }

    /**
     * Test the case a subscriber is added and then removes
     */
    @Test
    public void testConsumerAddedAndRemoved() throws JMSException, InterruptedException {
        consumer.startJms(provider);

        // Needs to wait a bit for the message ro arrive
        TimeUnit.MILLISECONDS.sleep(200);

        // Stop the consumer
        consumer.stopJms();

        // Needs to wait a bit for the message ro arrive
        TimeUnit.MILLISECONDS.sleep(200);

        assertTrue("No consumers after consumer is stopped", stateHolder.listSubscribers().isEmpty());
    }

}
