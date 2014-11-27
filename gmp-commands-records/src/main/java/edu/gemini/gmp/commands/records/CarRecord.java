package edu.gemini.gmp.commands.records;

import com.google.common.base.Optional;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a CAR Record. Mostly encapsulating EPICS access.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
public class CarRecord {
    private final Logger LOG;

    enum Val {
        IDLE,
        PAUSED,
        BUSY,
        ERROR
    }

    /**
     * Current state
     */
    private Channel<Val> val;
    /**
     * Output message
     */
    private Channel<String> omss;
    /**
     * Output error code
     */
    private Channel<Integer> oerr;
    /**
     * Value of the latest client ID
     */
    private Channel<Integer> clid;

    private final ChannelAccessServer cas;

    private final String prefix;

    private final List<CarListener> listeners = new CopyOnWriteArrayList<CarListener>();

    /**
     * Constructor
     *
     * @param cas    Channel Access server to use
     * @param prefix name of the CAR. ex.: "gpi:applyC"
     */
    public CarRecord(ChannelAccessServer cas, String prefix) {
        LOG = Logger.getLogger("CAR Record " + prefix);

        LOG.info("CAR constructor: " + prefix);
        this.cas = cas;
        this.prefix = prefix;
    }

    /**
     * Create Channels
     */
    public synchronized void start() {
        LOG.info("CAR start: " + prefix);

        try {
            val = cas.createChannel(prefix + ".VAL", Val.IDLE);
            omss = cas.createChannel(prefix + ".OMSS", "");
            oerr = cas.createChannel(prefix + ".OERR", 0);
            clid = cas.createChannel(prefix + ".CLID", 0);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Destroy Channels
     */
    public synchronized void stop() {
        LOG.info("CAR stop: " + prefix);
        cas.destroyChannel(val);
        cas.destroyChannel(omss);
        cas.destroyChannel(oerr);
        cas.destroyChannel(clid);
    }

    /**
     * Switch to a new state and notify listeners.
     *
     * @param state     new state to switch to
     * @param message   error message
     * @param errorCode error code
     * @param clientId  client ID for the command we are providing feedback
     * @throws CAException if there is a problem accessing EPICS
     */
    void changeState(Val state, String message, int errorCode, int clientId) throws CAException, TimeoutException {
        synchronized (this) {
            if (!val.getFirst().equals(state) || !clid.getFirst().equals(clientId)) {
                val.setValue(state);
                omss.setValue(message);
                oerr.setValue(errorCode);
                clid.setValue(clientId);
            }
        }
        notifyListeners(state, message, errorCode, clientId);
    }

    private void notifyListeners(Val state, String message, int errorCode, int id) throws CAException {
        synchronized (listeners) {
            for (CarListener listener : listeners) {
                listener.update(state, message, errorCode, id);
            }
        }
    }

    /**
     * Register a listener.
     *
     * @param listener to be notified when the CAR state changes
     */
    void registerListener(CarListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Unregister a listener
     *
     * @param listener to unregister
     */
    void unRegisterListener(CarListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Convenience method to set CAR to BUSY
     *
     * @param id client ID for the command we are providing feedback
     */
    void setBusy(Integer id) {
        try {
            changeState(Val.BUSY, "", 0, id);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Convenience method to set CAR to IDLE
     *
     * @param id client ID for the command we are providing feedback
     */
    void setIdle(Integer id) {
        try {
            changeState(Val.IDLE, "", 0, id);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Convenience method to set CAR to ERROR
     *
     * @param id        client ID for the command we are providing feedback
     * @param message   error message
     * @param errorCode error code
     */
    void setError(Integer id, String message, int errorCode) {
        try {
            changeState(Val.ERROR, message, errorCode, id);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Get the state of the CAR
     *
     * @return state of the CAR
     */
    public synchronized Optional<Val> getState() {
        try {
            return Optional.fromNullable(val.getFirst());
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return Optional.absent();
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return Optional.absent();
        }
    }

    /**
     * Get the state of the CAR
     *
     * @return state of the CAR
     */
    public synchronized Integer getClId() {
        try {
            return clid.getFirst();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return -1;
        }
    }
}
