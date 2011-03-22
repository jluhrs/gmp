package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import gov.aps.jca.CAException;

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
        if (!val.getVal().get(0).equals(state) || !clid.getVal().get(0).equals(clientId)) {
            val.setValue(state);
            omss.setValue(message);
            oerr.setValue(errorCode);
            clid.setValue(clientId);
        }
    }

}
