package edu.gemini.aspen.gmp.handlersstate.impl;

import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.TopicSubscriptionView;
import org.apache.activemq.broker.jmx.TopicSubscriptionViewMBean;
import org.junit.Test;

import javax.management.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for consumer holder as found with JMX.
 */
public class JMXConsumerStateHolderTest {

    @Test
    public void testNoMBeanServer() {
        assertNoSubscribersFoundViaJMX();
    }

    private void assertNoSubscribersFoundViaJMX() {
        JMXConsumerStateHolder stateHolder = new JMXConsumerStateHolder();
        assertTrue(stateHolder.listSubscribers().isEmpty());
    }

    @Test
    public void testWithMBeanServerAndNoBrokerView() {
        MBeanServerFactory.createMBeanServer();
        assertNoSubscribersFoundViaJMX();
    }

    @Test
    public void testWithMBeanServerAndBrokerView() throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        ObjectName amqObjectNamePattern = new ObjectName("org.apache.activemq:BrokerName=gmp,Type=Broker");
        MBeanServer beanServer = MBeanServerFactory.createMBeanServer();
        BrokerViewMBean brokerViewMBean = mock(BrokerView.class);
        beanServer.registerMBean(brokerViewMBean, amqObjectNamePattern);
        assertNoSubscribersFoundViaJMX();
    }

    @Test
    public void testWithSubscribers() throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        ObjectName amqObjectNamePattern = new ObjectName("org.apache.activemq:BrokerName=gmp,Type=Broker");
        MBeanServer beanServer = MBeanServerFactory.createMBeanServer();
        BrokerViewMBean brokerViewMBean = mock(BrokerView.class);
        beanServer.registerMBean(brokerViewMBean, amqObjectNamePattern);

        ObjectName[] topicSubscribers = new ObjectName[] {
                new ObjectName("org.apache.activemq:BrokerName=gmp,Type=TopicSubscriber,name=a"),
                new ObjectName("org.apache.activemq:BrokerName=gmp,Type=TopicSubscriber,name=b")
        };
        when(brokerViewMBean.getTopicSubscribers()).thenReturn(topicSubscribers);

        TopicSubscriptionViewMBean topicSubscriberViewMBean = mock(TopicSubscriptionView.class);
        when(topicSubscriberViewMBean.getClientId()).thenReturn("client1");
        when(topicSubscriberViewMBean.getDestinationName()).thenReturn("destination1");
        beanServer.registerMBean(topicSubscriberViewMBean, topicSubscribers[0]);
        topicSubscriberViewMBean = mock(TopicSubscriptionView.class);
        when(topicSubscriberViewMBean.getClientId()).thenReturn("client2");
        when(topicSubscriberViewMBean.getDestinationName()).thenReturn("destination2`");
        beanServer.registerMBean(topicSubscriberViewMBean, topicSubscribers[1]);

        JMXConsumerStateHolder stateHolder = new JMXConsumerStateHolder();
        assertEquals(2, stateHolder.listSubscribers().size());
    }

}
