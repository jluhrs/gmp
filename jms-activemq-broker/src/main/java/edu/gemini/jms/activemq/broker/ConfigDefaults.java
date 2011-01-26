package edu.gemini.jms.activemq.broker;

/**
 * Default configuration options for the broker
 */
public class ConfigDefaults {

    public static final int BROKER_PORT = 61616;
    public static final String BROKER_URL = "tcp://0.0.0.0:" + BROKER_PORT;

    public static final String BROKER_NAME = "gmp";
    // Set to true to enable the broker's JMX usage
    public static final boolean BROKER_USE_JMX = true;
    // Set to true if broker should use persistent storage
    public static final boolean BROKER_PERSISTENT = false;
    // Should all persistent messages be deleted on broker startup
    public static final boolean BROKER_DELETE_MESSAGES_ON_STARTUP = true;

}
