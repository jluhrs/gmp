package edu.gemini.aspen.gmp.gw.osgi;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.commands.api.CommandSender;
import edu.gemini.aspen.gmp.gw.core.Gateway;

import java.util.logging.Logger;

/**
 *
 */
public class Supervisor {

    private static final Logger LOG = Logger.getLogger(Supervisor.class.getName());
    private JmsProvider _provider;
    private CommandSender _service;

    private Gateway _gateway;

    public Supervisor() {
        _provider = null;
        _service = null;
    }

    public void registerProvider(JmsProvider provider) {
        _provider = provider;
    }

    public void unregisterProvider() {
        _provider = null;
    }

    public void registerGmpService(CommandSender service) {
        _service = service;
    }

    public void unregisterGmpService() {
        _service = null;
    }

    public synchronized void start() {

        if (_provider != null && _service != null) {
            LOG.info("Starting Gateway Service");
            _gateway = new Gateway(_service, _provider);
            _gateway.start();
        }

    }

    public synchronized void stop() {
        if (_gateway != null) {
            LOG.info("Stopping Gateway Service");
            _gateway.stop();
            _gateway = null;
        }
    }

}
