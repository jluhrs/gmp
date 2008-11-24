package edu.gemini.jms.activemq.broker;

import org.apache.activemq.broker.BrokerService;

import java.util.logging.Logger;
import java.util.logging.Level;

import edu.gemini.jms.api.Broker;

/**
 *  A Broker implementation, based on the ActiveMQ JMS service
 */
public class ActiveMQBroker implements Broker {

    private static final Logger LOG = Logger.getLogger(ActiveMQBroker.class.getName());

    private final BrokerService _broker;

    public ActiveMQBroker() {
        _broker = new BrokerService();
    }


    public void start() {
        LOG.info("Starting up ActiveMQ Broker");
        try {
            _broker.setUseJmx(ConfigDefaults.BROKER_USE_JMX);
            // TODO: should possibly be an option
            _broker.setPersistent(ConfigDefaults.BROKER_PERSISTENT);
            _broker.setBrokerName(ConfigDefaults.BROKER_NAME);
            _broker.addConnector(ConfigDefaults.BROKER_URL);
            _broker.setDeleteAllMessagesOnStartup(ConfigDefaults.BROKER_DELETE_MESSAGES_ON_STARTUP);
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


    public static void main(String[] args) {

        Broker broker = new ActiveMQBroker();
        broker.start();

    }


}
