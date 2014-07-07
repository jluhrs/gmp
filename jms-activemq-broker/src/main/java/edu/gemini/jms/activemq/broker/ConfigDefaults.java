package edu.gemini.jms.activemq.broker;

/**
 * Default configuration options for the broker
 */
public class ConfigDefaults {

    public static final String BROKER_NAME_PROPERTY = "brokerName";
    public static final String BROKER_URL_PROPERTY = "brokerUrl";
    public static final String BROKER_PERSISTENT_PROPERTY = "persistent";
    public static final String BROKER_DELETE_MESSAGES_ON_STARTUP_PROPERTY = "deleteMsgOnStartup";
    public static final String BROKER_USE_ADVISORY_MESSAGES_PROPERTY = "useAdvisoryMessages";
    public static final String BROKER_USE_JMX_PROPERTY = "useJmx";
    public static final String BROKER_JMX_RMI_PORT_PROPERTY = "jmxRmiServerPort";
    public static final String BROKER_JMX_CONNECTOR_PORT_PROPERTY = "jmxConnectorPort";

    public static final int BROKER_PORT = 61616;

    public static final String BROKER_URL = "tcp://0.0.0.0:" + BROKER_PORT;
    public static final String BROKER_NAME = "gmp";
    // Set to true to enable the broker's JMX usage
    public static final boolean BROKER_USE_JMX = true;
    // Set to true if broker should use persistent storage
    public static final boolean BROKER_PERSISTENT = false;
    // Should all persistent messages be deleted on broker startup
    public static final boolean BROKER_DELETE_MESSAGES_ON_STARTUP = true;
    // Should use advisory messages
    public static final boolean BROKER_USE_ADVISORY_MESSAGES = false;
    // Allow setting the JMX ports
    public static final int BROKER_JMX_RMI_PORT = 1616;
    // Allow setting the JMX ports
    public static final int BROKER_JMX_CONNECTOR_PORT = 1099;

}
