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
    private JmsTcsContextDispatcher _dispatcher;

    /**
     * TCS Context fetcher, used to obtain the TCS Context. 
     */
    private TcsContextFetcher _fetcher;

    /**
     * Constructor. Takes as an argument the JMS dispatcher that will
     * be used to reply back to the requester.
     * @param dispatcher JMS Dispatcher that sends back the TCS Context
     */
    public TcsContextRequestListener(JmsTcsContextDispatcher dispatcher) {
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
     * @param message A message with a TCS Context request.
     */
    public void onMessage(Message message) {

        try {
            Destination d = message.getJMSReplyTo();

            if (d == null) {
                return; //nothing to do, since we don't know where to reply
            }

            double[] context = null;

            if (_fetcher != null) {
                context = _fetcher.getTcsContext();
            }

            if (context != null) {
                _dispatcher.send(context, d);
            }

        } catch (JMSException e) {
            LOG.warning("Problems sending TCS Context back");
        } catch (TcsContextException e) {
            LOG.log(Level.WARNING, "Problem obtaining TCS Context", e);
        }


    }
}
