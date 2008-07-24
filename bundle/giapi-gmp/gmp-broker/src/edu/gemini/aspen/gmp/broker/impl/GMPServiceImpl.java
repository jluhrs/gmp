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
 *
 */
public class GMPServiceImpl implements GMPService {


    private static final Logger LOG = Logger.getLogger(GMPServiceImpl.class.getName());

    private static int actionId = 0;

    private final Broker _broker = new ActiveMQBroker();
    private JMSSequenceCommandProducer _producer;

    public GMPServiceImpl() {
    }


    public void start() {
        _broker.start();
        _producer = new JMSSequenceCommandProducer();
        LOG.info("GMP started up. Ready to dispatch messages");
    }

    public void shutdown() {
        _producer.shutdown();
        _broker.shutdown();
        LOG.info("GMP shut down.");
    }

    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity) {
        return sendSequenceCommand(command, activity, null);

    }

    public HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config) {
        return _producer.sendSequenceCommand(actionId++, command, activity, config);
    }
}
