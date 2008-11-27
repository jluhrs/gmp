package edu.gemini.aspen.gmp.gw.jms;

/**
 * 
 */
public class GatewayKeys {

    public final static String PREFIX = "GW";
    public final static String SEPARATOR = ":";

    //TOPICS
    public final static String COMMAND_TOPIC = PREFIX + SEPARATOR + "SC";
    

    //Message Keys
    public final static String SEQUENCE_COMMAND_KEY = "SEQUENCE_COMMAND";
    public final static String ACTIVITY_KEY = "ACTIVITY";

    public final static String HANDLER_RESPONSE_KEY = "HANDLER_RESPONSE";
    public final static String HANDLER_RESPONSE_ERROR_KEY = "HANDLER_RESPONSE_ERROR";
    

}
