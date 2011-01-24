package edu.gemini.jms.activemq.broker;

import org.apache.activemq.broker.BrokerService;

import java.util.logging.Logger;
import java.util.logging.Level;

import edu.gemini.jms.api.Broker;

/**
 * A JMS Broker implementation, based on the ActiveMQ JMS service
 */
public class ActiveMQBroker implements Broker {

    private static final Logger LOG = Logger.getLogger(ActiveMQBroker.class.getName());

    private final BrokerService _broker;

    private final String _url;
    private final String _name;
    private final boolean _useJmx;
    private final boolean _isPersistent;
    private final boolean _deleteMsgOnStartup;


    public ActiveMQBroker(Builder builder) {
        _url = builder.url;
        _useJmx = builder.useJmx;
        _name = builder.brokerName;
        _isPersistent = builder.isPersistent;
        _deleteMsgOnStartup = builder.deleteMsgOnStartup;
        _broker = new BrokerService();
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

        public Builder useJmx(boolean useJmx) {
            this.useJmx = useJmx;
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
        LOG.info("Starting up ActiveMQ Broker");
        try {
            _broker.setUseJmx(_useJmx);
            _broker.setPersistent(_isPersistent);
            _broker.setBrokerName(_name);
            _broker.addConnector(_url);
            _broker.setDeleteAllMessagesOnStartup(_deleteMsgOnStartup);
            _broker.start();
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
