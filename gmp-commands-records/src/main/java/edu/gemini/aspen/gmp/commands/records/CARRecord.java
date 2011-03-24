package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import gov.aps.jca.CAException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class CARRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
public class CARRecord {
    private static final Logger LOG = Logger.getLogger(CARRecord.class.getName());

    enum Val {
        UNAVAILABLE,
        IDLE,
        PAUSED,
        ERR,
        BUSY,
        UNKNOWN
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
    //private Channel<Long> oerr;
    private Channel<Integer> oerr;
    /**
     * Value of the latest client ID
     */
    //private Channel<Long> clid;
    private Channel<Integer> clid;

    private ChannelAccessServer cas;

    private String prefix;

    public CARRecord(ChannelAccessServer cas, String prefix) {
        LOG.info("CAR constructor: "+prefix);
        this.cas = cas;
        this.prefix = prefix;
    }

    public synchronized void start() {
        LOG.info("CAR start: "+prefix);

        try {
            val = cas.createChannel(prefix + ".VAL", Val.IDLE);
            omss = cas.createChannel(prefix + ".OMSS", "");
            oerr = cas.createChannel(prefix + ".OERR", 0);
            clid = cas.createChannel(prefix + ".CLID", 0);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public synchronized void stop() {
        LOG.info("CAR stop: "+prefix);
        cas.destroyChannel(val);
        cas.destroyChannel(omss);
        cas.destroyChannel(oerr);
        cas.destroyChannel(clid);
    }

    synchronized void changeState(Val state, String message, int errorCode, int clientId) throws CAException {
        if (!val.getFirst().equals(state) || !clid.getFirst().equals(clientId)) {
            val.setValue(state);
            omss.setValue(message);
            oerr.setValue(errorCode);
            clid.setValue(clientId);
            notifyListeners();
        }
    }
    private List<CARListener> listeners = new ArrayList<CARListener>();
    private void notifyListeners() throws CAException {
        for(CARListener listener:listeners){
            listener.update(val.getFirst(), omss.getFirst(), oerr.getFirst(), clid.getFirst());
        }
    }
    interface CARListener{
        void update(Val state, String message, Integer errorCode, Integer id);
    }
    synchronized void registerListener(CARListener listener){
        listeners.add(listener);
    }
    synchronized void unRegisterListener(CARListener listener){
        listeners.remove(listener);
    }
    synchronized void setBusy(Integer id){
        try {
            changeState(Val.BUSY,"",0,id);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    synchronized void setIdle(Integer id){
        try {
            changeState(Val.IDLE,"",0,id);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public synchronized Val getState(){
        try {
            return val.getFirst();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return Val.UNKNOWN;
        }
    }
}
