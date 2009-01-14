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


}
