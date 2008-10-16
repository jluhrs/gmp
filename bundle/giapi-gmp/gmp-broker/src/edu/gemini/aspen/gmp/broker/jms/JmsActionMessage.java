package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.broker.commands.ActionMessage;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import javax.jms.Destination;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * A message representing an Action using JMS as the
 * underlying communication mechanism.
 * <p/>
 * The ActionMessage share a common session to dispatch
 * messages via JMS, so all the instances of this class
 * should call the <code>send()</code> method from the
 * same thread.
 */
public class JmsActionMessage implements ActionMessage {

    private static final Logger LOG = Logger.getLogger(JmsActionMessage.class.getName());

    private MapMessage _message;
    private Destination _destination;


    private JMSActionMessageProducer _jmsProducer = JMSActionMessageProducer.INSTANCE;

    /**
     * Constructor. Creates an Action message for the given action
     * @param action the Action representd by this messsage
     */
    public JmsActionMessage(Action action) {

        try {
            _message = _jmsProducer.createMapMessage();

            //activity is a property
            _message.setStringProperty(GMPKeys.GMP_ACTIVITY_PROP,
                    action.getActivity().getName());

            //action id is stored as a property as well
            _message.setIntProperty(GMPKeys.GMP_ACTIONID_PROP, action.getId());

            //destination is based on the action
            _destination = _jmsProducer.createDestination(action);

        } catch (JMSException e) {
            LOG.warning("Exception while creating action message: " + e);
        }

    }

    /**
     * Set the configuration to be sent using this message
     * @param config The Configuration to be stored in this message
     */
    public void setConfiguration(Configuration config) {
        try {
            if (config != null) {
                //set all the configuration elements in the map
                Enumeration<String> e = config.getKeys();
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    String value = config.getValue(key);
                    _message.setString(key, value);
                }
            }
        } catch (JMSException e) {
            LOG.warning("Can't set the configuration for this message: " + e);
        }
    }

    /**
     * Send this ActionMessage over JMS
     * @return HandlerResponse containing acknowledgment information from the
     *         receipients of this message
     */
    public HandlerResponse send() {
        try {
            return _jmsProducer.send(_destination, _message);
        } catch (JMSException e) {
            LOG.warning("Problem sending message: " + e);
        }
        return HandlerResponseImpl.createError("No answer received to action ");
    }

}
