package edu.gemini.aspen.gmp.broker.impl;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Apr 2, 2008
 * Time: 8:53:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDefaults {


    public static final int    BROKER_PORT = 61616;
    public static final String BROKER_URL = "tcp://localhost:" + BROKER_PORT;

    public static final String BROKER_NAME = "GMP";
    // Set to true to enable the broker's JMX usage
    public static final boolean BROKER_USE_JMX = true;
    // Set to true if broker should use persistent storage
    public static final boolean BROKER_PERSISTENT = false;
    // Should all persistent messages be deleted on broker startup
    public static final boolean BROKER_DELETE_MESSAGES_ON_STARTUP = true;
    

}
