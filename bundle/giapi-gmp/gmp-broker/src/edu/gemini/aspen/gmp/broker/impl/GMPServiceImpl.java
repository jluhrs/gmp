package edu.gemini.aspen.gmp.broker.impl;

import edu.gemini.aspen.gmp.broker.api.Broker;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.broker.jms.JMSSequenceCommandProducer;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

import java.util.logging.Logger;

/**
 * Implementatin of the GMP Service.
 */
public class GMPServiceImpl implements GMPService {


    private static final Logger LOG = Logger.getLogger(GMPServiceImpl.class.getName());

    private static int actionId = 0;

    private final Broker _broker = new ActiveMQBroker();
    private JMSSequenceCommandProducer _producer;

    public GMPServiceImpl() {
    }


    /**
     * Start the Gemini Master Process Service
     */
    public void start() {
        _broker.start();
        _producer = new JMSSequenceCommandProducer();
        LOG.info("GMP started up. Ready to dispatch messages");
    }


    /**
     * Shutdown the Gemini Master Process Service
     */
    public void shutdown() {
        _producer.shutdown();
        _broker.shutdown();
        LOG.info("GMP shut down.");
    }

    /**
     * Send a SequenceCommand with the specified activity to the registered
     * clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was
     * received and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @return a HandlerResponse, used to decide if the command was accepted
     *         by the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity) {
        return sendSequenceCommand(command, activity, null);

    }

    /**
     * Send a SequenceCommand with the specified activity and configuration
     * to the registered clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was
     * received and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param config   the configuration that will be send along with the
     *                 sequence command
     * @return a HandlerResponse, used to decide if the command was accepted
     *         by the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config) {
        return _producer.sendSequenceCommand(actionId++, command, activity, config);
    }
}
