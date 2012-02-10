package edu.gemini.jms.activemq.broker;

import edu.gemini.jms.api.Broker;
import org.apache.felix.ipojo.annotations.*;

import java.io.Serializable;
import java.util.logging.Logger;

import static edu.gemini.jms.activemq.broker.ActiveMQBroker.activemq;
import static edu.gemini.jms.activemq.broker.ConfigDefaults.*;

/**
 * Thin wrapper of ActiveMQBroker to expose the configuration to the ConfigAdmin service
 *
 * @author cquiroz
 */
@Component
@Provides
public class ActiveMQBrokerComponent implements Serializable {
    private static final Logger LOG = Logger.getLogger(ActiveMQBrokerComponent.class.getName());

    private final Broker _broker;

    private final boolean useJmx;

    private final boolean isPersistent;

    private final String brokerName;

    private final String url;

    private final boolean deleteMsgOnStartup;

    private final boolean useAdvisoryMessages;

    private final int jmxRmiServerPort;

    private final int jmxConnectorPort;

    public ActiveMQBrokerComponent(@Property(name = "useJmx", value = "" + BROKER_PERSISTENT, mandatory = true) boolean useJmx,
            @Property(name = "persistent", value = "" + BROKER_PERSISTENT, mandatory = true) boolean persistent,
            @Property(name = "brokerName", value = BROKER_NAME, mandatory = true) String brokerName,
            @Property(name = "brokerUrl", value = BROKER_URL, mandatory = true) String url,
            @Property(name = "deleteMsgOnStartup", value = "" + BROKER_DELETE_MESSAGES_ON_STARTUP, mandatory = true) boolean deleteMsgOnStartup,
            @Property(name = "useAdvisoryMessages", value = "" + BROKER_USE_ADVISORY_MESSAGES, mandatory = true) boolean useAdvisoryMessages,
            @Property(name = "jmxRmiServerPort", value = "" + BROKER_JMX_RMI_PORT, mandatory = true) int jmxRmiServerPort,
            @Property(name = "jmxConnectorPort", value = "" + BROKER_JMX_CONNECTOR_PORT, mandatory = true) int jmxConnectorPort) {
        this.useJmx = useJmx;
        this.isPersistent = persistent;
        this.brokerName = brokerName;
        this.url = url;
        this.deleteMsgOnStartup = deleteMsgOnStartup;
        this.useAdvisoryMessages = useAdvisoryMessages;
        this.jmxRmiServerPort = jmxRmiServerPort;
        this.jmxConnectorPort = jmxConnectorPort;

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
    }

    @Validate
    public synchronized void startBroker() {
        LOG.info("Starting ActiveMQBroker broker with URL:" + url);
        // Start in a separate thread, otherwise there is a risk of a race condition
        // with other iPojo components
        new Thread(new Runnable() {
            @Override
            public void run() {
                _broker.start();
            }
        }).start();

    }

    @Invalidate
    public synchronized void stopBroker() {
        _broker.shutdown();
    }
}
