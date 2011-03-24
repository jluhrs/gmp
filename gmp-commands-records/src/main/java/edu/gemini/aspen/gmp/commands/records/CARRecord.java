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
    protected Channel<Val> val;
    /**
     * Output message
     */
    protected Channel<String> omss;
    /**
     * Output error code
     */
    //private Channel<Long> oerr;
    protected Channel<Integer> oerr;
    /**
     * Value of the latest client ID
     */
    //private Channel<Long> clid;
    protected Channel<Integer> clid;

    private ChannelAccessServer cas;

    private String prefix;

    public CARRecord(ChannelAccessServer cas, String prefix) {
        this.cas = cas;
        this.prefix = prefix;
    }

    public void start() {
        try {
            val = cas.createChannel(prefix + ".VAL", Val.IDLE);
            omss = cas.createChannel(prefix + ".OMSS", "");
            oerr = cas.createChannel(prefix + ".OERR", 0);
            clid = cas.createChannel(prefix + ".CLID", 0);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void stop() {
        cas.destroyChannel(val);
        cas.destroyChannel(omss);
        cas.destroyChannel(oerr);
        cas.destroyChannel(clid);
    }

    void changeState(Val state, String message, int errorCode, int clientId) throws CAException {
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
    void registerListener(CARListener listener){
        listeners.add(listener);
    }
    void unRegisterListener(CARListener listener){
        listeners.remove(listener);
    }

}
