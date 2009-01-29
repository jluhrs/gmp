package edu.gemini.aspen.gmp.statusgw.osgi;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.status.api.StatusDatabaseService;
import edu.gemini.aspen.gmp.statusgw.jms.StatusRequestProcessor;

import java.util.logging.Logger;

/**
 *  The supervisor tracks the necessary service required to start up
 * the status request processor. 
 */
public class Supervisor {

    private static final Logger LOG = Logger.getLogger(Supervisor.class.getName());

    private JmsProvider _provider;
    private StatusDatabaseService _databaseService;

    private StatusRequestProcessor _processor;

    public Supervisor() {
        _provider = null;
        _databaseService = null;
    }

    public void registerProvider(JmsProvider provider) {
        _provider = provider;
    }

    public void unregisterProvider() {
        _provider = null;
    }

    public void registerDatabase(StatusDatabaseService service) {
        _databaseService = service;
    }

    public void unregisterDatabase() {
        _databaseService = null;
    }

    public synchronized void start() {

        if (_provider != null && _databaseService != null) {
            LOG.info("Starting Status Gateway Service");
            _processor = new StatusRequestProcessor(_databaseService, _provider);
        }
    }

    public synchronized void stop() {
        if (_processor != null) {
            LOG.info("Stopping Status Gateway Service");
            _processor.stop();
            _processor = null;
        }
    }

}