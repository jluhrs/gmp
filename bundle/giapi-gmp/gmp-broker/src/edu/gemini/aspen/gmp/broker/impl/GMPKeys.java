package edu.gemini.aspen.gmp.broker.impl;

/**
 * Keys to be used to construct messages that will be go to GMP clients. 
 */
public class GMPKeys {

    public final static String GMP_PREFIX  = "GMP";
    public final static String GMP_SEPARATOR = ":";
    public final static String GMP_SEQUENCE_COMMAND_PREFIX = GMP_PREFIX + GMP_SEPARATOR + "SC" + GMP_SEPARATOR;

    //Message topics or queues
    public final static String GMP_COMPLETION_INFO = GMP_PREFIX + GMP_SEPARATOR + "COMPLETION_INFO";

    //Message Properties
    public final static String GMP_ACTIVITY_PROP = "GMP_ACTIVITY_PROP";
    public final static String GMP_ACTIONID_PROP = "GMP_ACTIONID";

}
