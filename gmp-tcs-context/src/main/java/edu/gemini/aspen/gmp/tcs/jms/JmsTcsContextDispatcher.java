package edu.gemini.aspen.gmp.tcs.jms;

import edu.gemini.jms.api.BaseMessageProducer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.BytesMessage;
import java.util.logging.Logger;

/**
 * The message producer used to send the TCS Context back to the requester.
 */
public class JmsTcsContextDispatcher extends BaseMessageProducer {
    private static final Logger LOG = Logger.getLogger(JmsTcsContextDispatcher.class.getName());

    public JmsTcsContextDispatcher(String clientName) {
        super(clientName, null);
    }

    /**
     * Packs the TCS context in a message and dispatch it via JMS to the
     * specified destination
     *
     * @param context     the TCS Context as an array of doubles
     * @param destination the destination to use to send the TCS Context
     * @throws JMSException In case there is a problem sending the
     *                      TCS Context via JMS
     */
    public void send(double[] context, Destination destination) throws JMSException {
        if (context == null) {
            LOG.warning("Can't send a null context. Problem accessing the TCS?");
            return; //nothing to do
        }

        BytesMessage msg = _session.createBytesMessage();
        //first, the number of elements, as an integer
        msg.writeInt(context.length);

        //and then all the values
        for (double value : context) {
            msg.writeDouble(value);
        }
        _producer.send(destination, msg);
    }
}
