package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;

import javax.management.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author cquiroz
 */
class JMXConsumerStateHolder implements ConsumerStateHolder {
   private static final Logger LOG = Logger.getLogger(HandlersStateService.class.getName());
    private MBeanServer mBeanServer;

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

    private void findConsumers() throws MalformedObjectNameException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
        ObjectName brokerMBean = findBrokerMBean();
        if (brokerMBean != null) {
            findTopicSubscribers(brokerMBean);
        } else {
            LOG.warning("No BrokerBean found, is JMX enabled?");
        }
    }

    private void findTopicSubscribers(ObjectName brokerMBean) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        Object subscribersNames = mBeanServer.getAttribute(brokerMBean, "TopicSubscribers");
        if (subscribersNames instanceof ObjectName[]) {
            ObjectName[] topicSubscribers = (ObjectName[])subscribersNames;
            for(ObjectName t:topicSubscribers) {
                String clientId = (String) mBeanServer.getAttribute(t, "ClientId");
                String destinationName = (String) mBeanServer.getAttribute(t, "DestinationName");
                LOG.info("Found subscriber "+ new MessageSubscriber(clientId, destinationName));
            }
        }
    }

    private ObjectName findBrokerMBean() throws MalformedObjectNameException {
        LOG.fine("Attempting to find MBeanServers");
        ObjectName brokerObjectName = null;
        List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        if (servers != null && !servers.isEmpty()) {
            ObjectName amqObjectNamePattern = new ObjectName("org.apache.activemq:BrokerName=*,Type=Broker");
            for (MBeanServer s:servers) {
                Set<ObjectName> brokersName = s.queryNames(amqObjectNamePattern, null);
                for (ObjectName n:brokersName) {
                    mBeanServer = s;
                    // There should be only one broker
                    brokerObjectName = n;
                }
            }
        } else {
            LOG.warning("No MBean Server found, impossible to find existing subscribers");
        }
        return brokerObjectName;
    }
}
