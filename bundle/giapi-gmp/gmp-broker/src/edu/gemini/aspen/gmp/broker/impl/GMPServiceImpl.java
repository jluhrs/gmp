package edu.gemini.aspen.gmp.broker.impl;

import edu.gemini.aspen.gmp.broker.api.Broker;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.broker.jms.JMSSequenceCommandProducer;
import edu.gemini.aspen.gmp.broker.commands.ActionManager;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.commands.api.*;

import java.util.logging.Logger;

/**
 * Implementatin of the GMP Service.
 */
public class GMPServiceImpl implements GMPService {


    private static final Logger LOG = Logger.getLogger(
            GMPServiceImpl.class.getName());

    private final Broker _broker = new ActiveMQBroker();

    private final ActionManager _manager = new ActionManager();

    private JMSSequenceCommandProducer _producer;

    public GMPServiceImpl() {
    }


    /**
     * Start the Gemini Master Process Service
     */
    public void start() {
        _broker.start();
        _producer = new JMSSequenceCommandProducer();
        _manager.start();
        LOG.info("GMP started up. Ready to dispatch messages");
    }


    /**
     * Shutdown the Gemini Master Process Service
     */
    public void shutdown() {
        _manager.stop();
        _producer.shutdown();
        _broker.shutdown();
        LOG.info("GMP shut down.");
    }

    /**
     * Send a SequenceCommand with the specified activity to the registered
     * clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               CompletionListener listener) {
        return sendSequenceCommand(command, activity, null, listener);

    }

    /**
     * Send a SequenceCommand with the specified activity and configuration to
     * the registered clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param config   the configuration that will be send along with the
     *                 sequence command
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    public HandlerResponse sendSequenceCommand(SequenceCommand command,
                                               Activity activity,
                                               Configuration config,
                                               CompletionListener listener) {
        Action action = _manager.registerCommand(command, activity, config,
                                               listener);

        //TODO: Probably I want to analize here the handler response. In case it's
        //"STARTED", then I need to keep track of it. Otherwise, I don't care :/

        return _producer.dispatchAction(action);
    }


    public void updateOcs(int actionId, HandlerResponse response) {
        //make the completion information available for the Action Manager
        //to notify the clients.
        _manager.registerCompletionInformation(actionId, response);
    }
}
