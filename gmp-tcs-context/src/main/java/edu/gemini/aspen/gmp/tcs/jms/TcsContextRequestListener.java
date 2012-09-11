package edu.gemini.aspen.gmp.tcs.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.tcs.model.TcsContextFetcher;
import edu.gemini.aspen.gmp.tcs.model.TcsContextException;

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.Destination;
import javax.jms.JMSException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Listener to receive TCS Context requests
 */
public class TcsContextRequestListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(TcsContextRequestListener.class.getName());
    public static final String DESTINATION_NAME = JmsKeys.GMP_TCS_CONTEXT_DESTINATION;

    /**
     * Message producer used to send the TCS Context back to the requester
     */
    private final JmsTcsContextDispatcher _dispatcher;

    /**
     * TCS Context fetcher, used to obtain the TCS Context.
     */
    private TcsContextFetcher _fetcher;

    /**
     * Constructor. Takes as an argument the JMS dispatcher that will
     * be used to reply back to the requester.
     *
     * @param dispatcher JMS Dispatcher that sends back the TCS Context
     */
    public TcsContextRequestListener(JmsTcsContextDispatcher dispatcher) {
        if (dispatcher == null) {
            throw new IllegalArgumentException("Cannot construct TcsContextRequestListener with a null dispatcher");
        }
        _dispatcher = dispatcher;
    }

    /**
     * Register the TCS Context Fetcher that will be used by this listener.
     * If <code>null</code>, this listener won't reply back.
     *
     * @param fetcher TCS Context Fetcher to use to obtaing the TCS
     *                Context
     */
    public void registerTcsContextFetcher(TcsContextFetcher fetcher) {
        _fetcher = fetcher;
    }

    /**
     * Receives the request. Gets the destination to reply,
     * obtains the TCS context (if possible) and send it
     * back to the requester.
     *
     * @param message A message with a TCS Context request.
     */
    public void onMessage(Message message) {
        try {
            sendContextIfAllowed(message);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problems sending TCS Context back", e);
        } catch (TcsContextException e) {
            LOG.log(Level.WARNING, "Problem obtaining TCS Context", e);
        }
    }

    private void sendContextIfAllowed(Message message) throws JMSException, TcsContextException {
        Destination replyDestination = message.getJMSReplyTo();
        if (canDispatchContext(replyDestination)) {
            dispatchContext(replyDestination);
        } else {
            LOG.severe("TCS Context could not be sent properly:" + _fetcher);
        }
    }

    private boolean canDispatchContext(Destination d) throws TcsContextException {
        return _fetcher != null && _fetcher.getTcsContext() != null && d!= null;
    }

    private void dispatchContext(Destination replyDestination) throws TcsContextException, JMSException {
        double[] context = _fetcher.getTcsContext();
        _dispatcher.send(context, replyDestination);
    }
}
