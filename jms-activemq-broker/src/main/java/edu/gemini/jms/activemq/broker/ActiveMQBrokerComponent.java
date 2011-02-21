package edu.gemini.jms.activemq.broker;

import edu.gemini.jms.api.Broker;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Logger;

import static edu.gemini.jms.activemq.broker.ActiveMQBroker.activemq;
import static edu.gemini.jms.activemq.broker.ConfigDefaults.*;

/**
 * Thin wrapper of ActiveMQBroker to expose the configuration to the ConfigAdmin service
 *
 * @author cquiroz
 */
@Component
public class ActiveMQBrokerComponent {
    private static final Logger LOG = Logger.getLogger(ActiveMQBrokerComponent.class.getName());

    private Broker _broker = null;

    @Property(name = "useJmx", value = "" + BROKER_PERSISTENT, mandatory = true)
    private boolean useJmx;

    @Property(name = "persistent", value = "" + BROKER_PERSISTENT, mandatory = true)
    private boolean isPersistent;

    @Property(name = "brokerName", value = BROKER_NAME, mandatory = true)
    private String brokerName;

    @Property(name = "brokerUrl", value = BROKER_URL, mandatory = true)
    private String url;

    @Property(name = "deleteMsgOnStartup", value = "" + BROKER_DELETE_MESSAGES_ON_STARTUP, mandatory = true)
    private boolean deleteMsgOnStartup;

    @Property(name = "useAdvisoryMessages", value = "" + BROKER_USE_ADVISORY_MESSAGES, mandatory = true)
    private boolean useAdvisoryMessages;

    @Property(name = "jmxRmiServerPort", value = "" + BROKER_JMX_RMI_PORT, mandatory = true)
    private int jmxRmiServerPort;

    @Property(name = "jmxConnectorPort", value = "" + BROKER_JMX_CONNECTOR_PORT, mandatory = true)
    private int jmxConnectorPort;

    @Updated
    private void updated() {
        LOG.info("Updating configuration of ActiveMQ broker with URL:"+url);
        if (_broker == null) {
            _broker = activemq()
                    .name(brokerName)
                    .url(url)
                    .useJmx(useJmx)
                    .persistent(isPersistent)
                    .useAdvisoryMessages(useAdvisoryMessages)
                    .deleteMsgOnStartup(deleteMsgOnStartup)
                    .jmxConnectorPort(jmxConnectorPort)
                    .jmxRrmiServerPort(jmxRmiServerPort)
                    .build();
        } else {
            LOG.warning("Cannot reconfigure a running ActiveMQ Broker");
        }
        LOG.info("Updated configuration of ActiveMQ broker");
    }

    @Validate
    public void start() {
        LOG.info("Starting ActiveMQ broker: URL:" + url);
        _broker.start();
        LOG.info("Started ActiveMQ broker");
    }
}
