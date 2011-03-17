package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

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
    enum Val{
        IDLE,
        PAUSED,
        BUSY,
        ERROR
    }
    private Channel<Val> val;
    private Channel<String> omss;
    private Channel<String> oerr;
    private Channel<String> clid;

    @Requires
    private ChannelAccessServer cas;

    private CARRecord() {
    }

    public CARRecord(ChannelAccessServer cas){
        this.cas=cas;
    }

    @Validate
    public void start() {
        try {
            val = cas.createChannel("gpi:applyC.VAL", Val.IDLE);
            omss = cas.createChannel("gpi:applyC.OMSS", "");
            oerr = cas.createChannel("gpi:applyC.OERR", "");
            clid = cas.createChannel("gpi:applyC.CLID", "");
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        cas.destroyChannel(val);
        cas.destroyChannel(omss);
        cas.destroyChannel(oerr);
        cas.destroyChannel(clid);
    }

    void changeState(Val state) throws CAException {
        val.setValue(state);
    }
}
