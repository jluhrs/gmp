package edu.gemini.aspen.gmp.handlersstate.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.management.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * ConsumerStateHolder that retrieves the set of MessageSubscribers using JMX through
 * the exposed ActiveMQ MBeans
 *
 * @author cquiroz
 */
class JMXConsumerStateHolder implements ConsumerStateHolder {
    private static final Logger LOG = Logger.getLogger(JMXConsumerStateHolder.class.getName());
    private MBeanServer mBeanServer;
    private final List<MessageSubscriber> jmxBasedSubscribers = Lists.newArrayList();

    public JMXConsumerStateHolder() {
        try {
            findConsumers();
        } catch (MalformedObjectNameException e) {
            LOG.severe("Exception build subscribers list" + e.getMessage());
        } catch (ReflectionException e) {
            LOG.severe("Exception build subscribers list" + e.getMessage());
        } catch (MBeanException e) {
            LOG.severe("Exception build subscribers list" + e.getMessage());
        } catch (AttributeNotFoundException e) {
            LOG.severe("Exception build subscribers list" + e.getMessage());
        } catch (InstanceNotFoundException e) {
            LOG.severe("Exception build subscribers list" + e.getMessage());
        }
    }

    protected List<MessageSubscriber> getJmxBasedSubscribers() {
        return ImmutableList.copyOf(jmxBasedSubscribers);
    }

    private void findConsumers() throws MalformedObjectNameException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
        ObjectName brokerMBean = findBrokerMBean();
        if (brokerMBean != null) {
            findTopicSubscribers(brokerMBean);
        }
    }

    private void findTopicSubscribers(ObjectName brokerMBean) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        Object subscribersNames = mBeanServer.getAttribute(brokerMBean, "TopicSubscribers");
        if (subscribersNames instanceof ObjectName[]) {
            ObjectName[] topicSubscribers = (ObjectName[]) subscribersNames;
            for (ObjectName t : topicSubscribers) {
                String clientId = (String) mBeanServer.getAttribute(t, "ClientId");
                String destinationName = (String) mBeanServer.getAttribute(t, "DestinationName");
                MessageSubscriber messageSubscriber = new MessageSubscriber(clientId, destinationName);
                LOG.info("Found subscriber " + messageSubscriber);
                jmxBasedSubscribers.add(messageSubscriber);
            }
        }
    }

    private ObjectName findBrokerMBean() throws MalformedObjectNameException {
        LOG.fine("Attempting to find MBeanServers");
        ObjectName brokerObjectName = null;
        List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        if (isThereAnMBeanServer(servers)) {
            ObjectName amqObjectNamePattern = new ObjectName("org.apache.activemq:BrokerName=*,Type=Broker");
            for (MBeanServer s : servers) {
                Set<ObjectName> brokersName = s.queryNames(amqObjectNamePattern, null);
                for (ObjectName n : brokersName) {
                    mBeanServer = s;
                    // There should be only one broker
                    brokerObjectName = n;
                }
            }
            if (brokerObjectName == null) {
                LOG.warning("No Broker MBean found");
            }
        } else {
            LOG.warning("No MBean Server found, is JMX enabled?");
        }
        return brokerObjectName;
    }

    private boolean isThereAnMBeanServer(List<MBeanServer> servers) {
        return servers != null && !servers.isEmpty();
    }
}
