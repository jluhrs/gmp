package edu.gemini.aspen.giapi.data.obsevents.jms;

import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.aspen.gmp.data.ObservationEventHandler;

import javax.jms.*;
import java.util.logging.Logger;

/**
 * This class is a listener for Observation Event messages. Whenever a new
 * message appears, the registered handler will be invoked. 
 */
public class JmsObservationEventListener implements MessageListener {

    public static final String TOPIC_NAME = GmpKeys.GMP_DATA_OBSEVENT_DESTINATION;

    private static final Logger LOG = Logger.getLogger(JmsObservationEventListener.class.getName());

    private ObservationEventHandler _action;

    public JmsObservationEventListener(ObservationEventHandler action) {
        _action = action;
    }

    public void onMessage(Message m) {

        if (m == null) LOG.warning("A null message was received through the observation event channel");

        try {
            String type = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_NAME);
            String file = m.getStringProperty(GmpKeys.GMP_DATA_OBSEVENT_FILENAME);
            ObservationEvent obsEvent = ObservationEvent.getObservationEvent(type);
            Dataset dataset = new Dataset(file);
            _action.onObservationEvent(obsEvent, dataset);
        } catch (JMSException e) {
            LOG.warning("Jms Exception: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            //an unexpected arg came in the meesages
            LOG.warning("Bad argument in message: " + ex.getMessage());
        }
    }
}
