package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 */
public class StatusGetter implements ExceptionListener {

    private static final Logger LOG = Logger.getLogger(StatusGetter.class.getName());


    private MessageProducer _producer;
    private Session _session;

    public StatusGetter(BrokerConnection connection)
            throws TesterException {

        _session = connection.getSession();

        try {
            //This is a request/reply communication to the
            //GMP to get the latest value of a status item
            Destination destination = _session.createTopic(
                    GmpKeys.GW_STATUS_REQUEST_DESTINATION);
            _producer = _session.createProducer(destination);

        } catch (JMSException ex) {
            throw new TesterException(ex);
        }
    }


    public StatusItem getStatusItem(String statusName) throws TesterException {

        //request the value
        try {
            Message m = _session.createMessage();
            m.setStringProperty(GmpKeys.GW_STATUS_NAME_PROPERTY, statusName);

            //create a consumer to receive the answer
            Destination tempQueue = _session.createTemporaryQueue();
            m.setJMSReplyTo(tempQueue);
            MessageConsumer tempConsumer = _session.createConsumer(tempQueue);

            //send the message
            _producer.send(m);

            Message reply = tempConsumer.receive(1000); //1000 msec to answer.

            tempConsumer.close();

            return GmpJmsUtil.buildStatusItem(reply);
        } catch (JMSException e) {
            throw new TesterException(e);
        }
    }



    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Status Getter", e);
    }

    public void stop() {
        try {
            _producer.close();
        } catch (JMSException ex) {
            LOG.log(Level.WARNING, "Problem stopping Status Getter", ex);
        }

    }
}
