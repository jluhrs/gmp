package edu.gemini.aspen.gmp.pcs.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class receives PCS updates from the instrument
 * and dispatch them to registered {@link edu.gemini.aspen.gmp.pcs.model.PcsUpdater}
 * objects
 */
public class PcsUpdateListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(PcsUpdateListener.class.getName());

    /**
     * The destination name from where we receive PCS Updates messages
     */
    public static final String DESTINATION_NAME = JmsKeys.GMP_PCS_UPDATE_DESTINATION;

    /**
     * Object that will process the updates.
     */
    private final PcsUpdater _updater;

    /**
     * Constructor.
     *
     * @param updater The PcsUpdater object that will be notified whenever a
     *                new PCS update message is received
     */
    public PcsUpdateListener(PcsUpdater updater) {
        _updater = updater;
    }

    public void onMessage(Message message) {
        checkIsBytesMessages(message);
        try {
            updatePcs((BytesMessage) message);
        } catch (JMSException e) {
            throw new JmsPcsMessageException("Problem receiving message with PCS updates from instrument", e);
        } catch (PcsUpdaterException e) {
            LOG.log(Level.WARNING, "Problem sending updates to PcsUpdater objects", e);
        }
    }

    private void checkIsBytesMessages(Message message) {
        if (!(message instanceof BytesMessage)) {
            throw new JmsPcsMessageException("Invalid message received");
        }
    }

    private void updatePcs(BytesMessage message) throws JMSException, PcsUpdaterException {
        Double[] zernikes = readZernikes(message);
        _updater.update(new PcsUpdate(zernikes));
    }

    private Double[] readZernikes(BytesMessage message) throws JMSException {
        // Decode the message containing the zernikes
        int count = message.readInt();
        if (count <= 0) {
            throw new JmsPcsMessageException("Invalid PCS update Message: It has " + count + " zernikes coefficients");
        }
        Double[] zernikes = new Double[count];
        for (int i = 0; i < count; i++) {
            zernikes[i] = message.readDouble();
        }
        return zernikes;
    }
}
