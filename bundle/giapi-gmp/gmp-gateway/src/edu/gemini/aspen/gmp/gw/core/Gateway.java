package edu.gemini.aspen.gmp.gw.core;

import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.gw.jms.CommandConsumer;
import edu.gemini.jms.api.JmsProvider;

import java.util.logging.Logger;

/**
 *
 */
public class Gateway {

    private static final Logger LOG = Logger.getLogger(Gateway.class.getName());

    private CommandConsumer _commandConsumer;


    public Gateway(GMPService service, JmsProvider provider) {
        _commandConsumer = new CommandConsumer(provider, service);
    }

    public void start() {
        LOG.info("GMP Gateway started");
    }

    public void stop() {
        _commandConsumer.close();
        LOG.info("GMP Gateway stopped");
    }


    



}
