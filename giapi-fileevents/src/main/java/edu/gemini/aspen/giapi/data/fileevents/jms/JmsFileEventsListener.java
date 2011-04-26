package edu.gemini.aspen.giapi.data.fileevents.jms;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.fileevents.FileEventException;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.data.FileEvent;
import edu.gemini.aspen.giapi.data.fileevents.FileEventAction;

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

    public static final String TOPIC_NAME = JmsKeys.GMP_DATA_FILEEVENT_DESTINATION;

    private static final Logger LOG = Logger.getLogger(JmsFileEventsListener.class.getName());

    private FileEventAction _action;

    public JmsFileEventsListener(FileEventAction action) {
        _action = action;
    }

    public void onMessage(Message message) throws FileEventException {

        try {
            if (!(message instanceof MapMessage)) {
                //invalid type of message, log and exit
                throw new FileEventException("Invalid JMS message type received by File Event Listener: " + message);
            }

            //We get the type, and use that to determine the handler to
            //call
            int type = message.getIntProperty(JmsKeys.GMP_DATA_FILEEVENT_TYPE);
            FileEvent fileEvent = FileEvent.getByCode(type);

            if (fileEvent == null) {
                throw new FileEventException("Invalid File Event type in message : " + type);
            }

            MapMessage mmsg = (MapMessage)message;

            //filename and dataLabel are common parts of all the file events
            String filename = mmsg.getString(JmsKeys.GMP_DATA_FILEEVENT_FILENAME);
            if (filename == null) {
                throw new FileEventException("Filename cannot be null for a file event");
            }
            DataLabel dataLabel;

            try {
                dataLabel = new DataLabel(mmsg.getString(JmsKeys.GMP_DATA_FILEEVENT_DATALABEL));
            } catch (IllegalArgumentException ex) {
                throw new FileEventException("Invalid dataLabel in file event", ex);
            }

            switch (fileEvent) {
                case ANCILLARY_FILE:
                    _action.onAncillaryFileEvent(filename, dataLabel);
                    break;
                case INTERMEDIATE_FILE:
                    String hint = null;
                    //The hint is optional; if it exists, we will get it
                    //from the message
                    if (mmsg.itemExists(JmsKeys.GMP_DATA_FILEEVENT_HINT)) {
                        hint = mmsg.getString(JmsKeys.GMP_DATA_FILEEVENT_HINT);
                    }
                    _action.onIntermediateFileEvent(filename, dataLabel, hint);
                    break;
            }
        } catch (JMSException e) {
            LOG.warning("Jms Exception: " + e.getMessage());
        }
    }
}
