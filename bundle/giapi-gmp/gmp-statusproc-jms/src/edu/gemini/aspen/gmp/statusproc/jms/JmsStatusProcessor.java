package edu.gemini.aspen.gmp.statusproc.jms;

import edu.gemini.aspen.gmp.status.StatusProcessor;
import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;

/**
 * A Status Processor that sends the Status Items to the JMS provider.
 */
public class JmsStatusProcessor implements StatusProcessor, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(JmsStatusProcessor.class.getName());

    private Connection _connection;
    private Session _session;
    private MessageProducer _producer;

    private Map<String, Destination> _destinations = new HashMap<String, Destination>();


    public JmsStatusProcessor(JmsProvider provider) {
        ConnectionFactory connectionFactory = provider.getConnectionFactory();
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("Status Dispatcher connection");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            _producer = _session.createProducer(null);
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        LOG.info("JMS Status Processor started");

    }

    public String getName() {
        return "JMS dispatcher status processor";
    }

    /**
     * This processor will take the status item and will send it to the JMS
     * broker so external tools in the OCS can pick them up.
     * @param item The status item that will be sent to the JMS provider
     */
    public void process(StatusItem item) {


        try {
            Destination destination = _getDestination(item.getName());

            Message m = GmpJmsUtil.buildStatusItemMessage(_session, item);
            _producer.send(destination, m);

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Can't process status item " + item, e);
        }


    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Problem on status dispatcher", e);
    }

    private Destination _getDestination(String itemName) throws JMSException {

        if (_destinations.containsKey(itemName)) {
            return _destinations.get(itemName);
        }
        Destination destination = _session.createTopic(itemName);
        _destinations.put(itemName, destination);
        return destination;
        
    }


    public void stop() {
         try {
            _producer.close();
            _session.close();
            _connection.close();
            _destinations.clear();
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        LOG.info("JMS Status Processor stopped");
    }

}
