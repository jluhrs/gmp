package edu.gemini.aspen.gmp.util.jms;

/**
 *
 */
public class GmpKeys {

    //Common keys
    public final static String GMP_PREFIX  = "GMP";
    public final static String GMP_SEPARATOR = ":";


    //Handler Response Keys
    public final static String GMP_HANDLER_RESPONSE_KEY = "GMP_HANDLER_RESPONSE";
    public final static String GMP_HANDLER_RESPONSE_ERROR_KEY = "GMP_HANDLER_RESPONSE_ERROR";

    public final static String GMP_SEQUENCE_COMMAND_KEY = "GMP_SEQUENCE_COMMAND";
    public final static String GMP_ACTIVITY_KEY = "GMP_ACTIVITY";


    //Services Requests keys
    public final static String GMP_UTIL_REQUEST_DESTINATION = GMP_PREFIX + GMP_SEPARATOR + "UTIL_REQUEST_DESTINATION";
    public final static String GMP_UTIL_REQUEST_TYPE = "REQUEST_TYPE";
    public final static int GMP_UTIL_REQUEST_PROPERTY = 0;
    public final static String GMP_UTIL_PROPERTY = "PROPERTY";


    //Status Keys
    public final static String GMP_STATUS_DESTINATION =  GMP_PREFIX + GMP_SEPARATOR + "STATUS_DESTINATION";


    //Gateway Keys

    private final static String GW_PREFIX  = "GW";

    //Gateway Status Keys
    public final static String GW_STATUS_REQUEST_DESTINATION = GW_PREFIX + GMP_SEPARATOR + "STATUS_REQUEST";
    public final static String GW_STATUS_NAME_PROPERTY = "STATUS_NAME";


    //Gateway Command Keys
    public final static String GW_COMMAND_TOPIC = GW_PREFIX + GMP_SEPARATOR + "SC";


    //EPICS Interface Keys
    public final static String GMP_GEMINI_EPICS_REQUEST_DESTINATION = GMP_PREFIX + GMP_SEPARATOR + "EPICS_REQUEST_DESTINATION";
    public final static String GMP_GEMINI_EPICS_CHANNEL_PROPERTY = "EPICS_CHANNEL";


}
