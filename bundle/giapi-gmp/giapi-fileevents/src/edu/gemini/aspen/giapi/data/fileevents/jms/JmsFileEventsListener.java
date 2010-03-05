package edu.gemini.aspen.giapi.data.fileevents.jms;

import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.data.FileEvent;
import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.aspen.giapi.data.fileevents.FileEventHandlerComposite;

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.logging.Logger;

/**
 * JMS listener for File Events messages. This listener will
 * take care of receiving file event messages and invoke the corresponding
 * handlers through a file event action object.
 */
public class JmsFileEventsListener implements MessageListener {

    public static final String TOPIC_NAME = GmpKeys.GMP_DATA_FILEEVENT_DESTINATION;

    private static final Logger LOG = Logger.getLogger(JmsFileEventsListener.class.getName());

    private FileEventHandlerComposite _action;

    public JmsFileEventsListener(FileEventHandlerComposite action) {
        _action = action;
    }

    public void onMessage(Message message) {

        try {
            if (!(message instanceof MapMessage)) {
                //invalid type of message, log and exit
                LOG.warning("Invalid JMS messagetype received by File Event Listener:  " + message);
                return;
            }

            //We get the type, and use that to determine the handler to
            //call
            int type = message.getIntProperty(GmpKeys.GMP_DATA_FILEEVENT_TYPE);
            FileEvent fileEvent = FileEvent.getByCode(type);

            if (fileEvent == null) {
                LOG.warning("Invalid File Event type in message : " + type);
                return;
            }

            MapMessage mmsg = (MapMessage)message;

            //filename and dataset are common parts of all the file events
            String filename = mmsg.getString(GmpKeys.GMP_DATA_FILEEVENT_FILENAME);
            Dataset dataset = new Dataset(mmsg.getString(GmpKeys.GMP_DATA_FILEEVENT_DATALABEL));

            switch (fileEvent) {
                case ANCILLARY_FILE:
                    _action.onAncillaryFileEvent(filename, dataset);
                    break;
                case INTERMEDIATE_FILE:
                    String hint = null;
                    //The hint is optional; if it exists, we will get it
                    //from the message
                    if (mmsg.itemExists(GmpKeys.GMP_DATA_FILEEVENT_HINT)) {
                        hint = mmsg.getString(GmpKeys.GMP_DATA_FILEEVENT_HINT);
                    }
                    _action.onIntermediateFileEvent(filename, dataset, hint);
                    break;
            }
        } catch (JMSException e) {
            LOG.warning("Jms Exception: " + e.getMessage());
        }
    }
}
