package edu.gemini.giapi.tool.obsevents;

import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;

/**
 * A class to receive observation events, for teting purposes mainly.
 */
public class ObsEventMonitor {

    private static final Logger LOG = Logger.getLogger(ObsEventMonitor.class.getName());

    private MessageConsumer _consumer;

    public ObsEventMonitor(BrokerConnection connection) throws TesterException {

        Session session = connection.getSession();
         //read the status from the topic with the same name as the item
        try {
            Destination destination = session.createTopic(GmpKeys.GMP_DATA_OBSEVENT_DESTINATION);
            _consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            throw new TesterException(e);
        }

    }

    public void start() throws TesterException {
         while (true) {
            try {
                Message m = _consumer.receive(); //blocking method

                if (m != null) {
                    String type = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_NAME);
                    String file = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_FILENAME);
                    System.out.println("[" + type +"/" + file +"]");

                } else {
                    LOG.warning("Unexpected type of message received through the observation event channel: " + m);
                }
            } catch (JMSException e) {
                throw new TesterException(e);
            }
        }
    }

    public void stop() throws TesterException {

        try {
            _consumer.close();
        } catch (JMSException e) {
            throw new TesterException(e);
        }
    }

}
