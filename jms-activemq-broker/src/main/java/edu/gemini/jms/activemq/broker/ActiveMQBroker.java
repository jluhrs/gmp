package edu.gemini.jms.activemq.broker;

import edu.gemini.jms.api.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JMS Broker implementation, based on the ActiveMQ JMS service
 */
public class ActiveMQBroker implements Broker {

    private static final Logger LOG = Logger.getLogger(ActiveMQBroker.class.getName());

    private final BrokerService _broker;

    public ActiveMQBroker(Builder builder) {
        _broker = new BrokerService();
        _broker.setUseJmx(builder.useJmx);
        _broker.setPersistent(builder.isPersistent);
        _broker.setBrokerName(builder.brokerName);
        _broker.setDeleteAllMessagesOnStartup(builder.deleteMsgOnStartup);
        _broker.setAdvisorySupport(builder.useAdvisoryMessages);
        if (builder.useJmx) {
            ManagementContext managementContext = new ManagementContext();
            managementContext.setCreateMBeanServer(false);
            managementContext.setFindTigerMbeanServer(true);

            managementContext.setRmiServerPort(builder.jmxRmiServerPort);
            managementContext.setConnectorPort(builder.jmxConnectorPort);
            _broker.setManagementContext(managementContext);
        }
        try {
            _broker.addConnector(builder.url);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "URL for ActiveMQ Broker not valid: " + builder.url + " aborting...", e);
            throw new IllegalArgumentException("Cannot start a broker with a url " + builder.url);
        }
    }

    /**
     * Utility static method to create a Builder object
     *
     * @return a new Builder object
     */
    public static Builder activemq() {
        return new Builder();
    }

    /**
     * A Builder class for the ActiveMQ Broker
     */
    public static class Builder {
        private boolean useJmx = ConfigDefaults.BROKER_USE_JMX;
        private boolean isPersistent = ConfigDefaults.BROKER_PERSISTENT;
        private String brokerName = ConfigDefaults.BROKER_NAME;
        private String url = ConfigDefaults.BROKER_URL;
        private boolean deleteMsgOnStartup = ConfigDefaults.BROKER_DELETE_MESSAGES_ON_STARTUP;
        private boolean useAdvisoryMessages = ConfigDefaults.BROKER_USE_ADVISORY_MESSAGES;
        private int jmxRmiServerPort = ConfigDefaults.BROKER_JMX_RMI_PORT;  
        private int jmxConnectorPort = ConfigDefaults.BROKER_JMX_CONNECTOR_PORT;

        public Builder useJmx(boolean useJmx) {
            this.useJmx = useJmx;
            return this;
        }

        public Builder useAdvisoryMessages(boolean useAdvisoryMessages) {
            this.useAdvisoryMessages = useAdvisoryMessages;
            return this;
        }

        public Builder jmxRrmiServerPort(int jmxRmiServerPort) {
            this.jmxRmiServerPort = jmxRmiServerPort;
            return this;
        }

        public Builder jmxConnectorPort(int jmxConnectorPort) {
            this.jmxConnectorPort = jmxConnectorPort;
            return this;
        }

        public Builder name(String name) {
            brokerName = name;
            return this;
        }

        public Builder persistent(boolean persistent) {
            isPersistent = persistent;
            return this;
        }

        public Builder deleteMsgOnStartup(boolean delete) {
            deleteMsgOnStartup = delete;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public ActiveMQBroker build() {
            return new ActiveMQBroker(this);
        }
    }


    public void start() {
        LOG.info("Starting up ActiveMQ AMQ Broker");
        try {
            _broker.start();
            if (_broker.waitUntilStarted()) {
                LOG.info("ActiveMQ Broker Component Started");
            } else {
                LOG.severe("ActiveMQ didn't start properly");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception while starting broker", e);
        }
    }

    public void shutdown() {
        LOG.info("Shutting down ActiveMQ Broker");
        try {
            _broker.stop();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception while stopping broker", e);
        }
    }
}
