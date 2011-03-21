package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Record
 *
 * @author Nicolas A. Barriga
 *         Date: 3/21/11
 */
public abstract class Record {
    protected static final Logger LOG = Logger.getLogger(Record.class.getName());
    protected String prefix;

    enum Dir {
        MARK,
        CLEAR,
        PRESET,
        START,
        STOP
    }

    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            try {
                car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());

                if (processDir(Dir.values()[((short[]) dbr.getValue())[0]])) {
                    car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
                } else {
                    car.changeState(CARRecord.Val.ERR, ((String[]) mess.getValue().getValue())[0], ((int[]) val.getValue().getValue())[0], getClientId());
                }

            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    protected Channel<Dir> dir;
    protected Channel<Integer> val;
    protected Channel<String> mess;
    protected Channel<String> omss;
    protected Channel<Integer> clid;

    protected CARRecord car;

    protected ChannelAccessServer cas;

    protected Record(ChannelAccessServer cas, String prefix) {
        this.cas = cas;
        this.prefix = prefix;
    }

    /**
     * Process the command directive, set VAL and MESS.
     *
     * @param dir Directive to process
     * @return true if everything OK, false otherwise.
     * @throws CAException
     */
    protected abstract boolean processDir(Dir dir) throws CAException;

    protected void start() throws CAException {
        dir = cas.createChannel(prefix + ".DIR", Dir.CLEAR);
        dir.registerListener(new DirListener());
        val = cas.createChannel(prefix + ".VAL", 0);
        mess = cas.createChannel(prefix + ".MESS", "");
        omss = cas.createChannel(prefix + ".OMSS", "");
        car = new CARRecord(cas,prefix+"C");
        car.start();
    }

    protected void stop() {
        cas.destroyChannel(dir);
        car.stop();

    }
    protected int getClientId() throws CAException {
         return clid.getVal().get(0);
    }
    protected void setMessage(String message) throws CAException {
        DBR dbr=mess.getValue();
        String oldMessage = ((String[])dbr.getValue())[0];
        if(setIfDifferent(mess,message)){
            omss.setValue(oldMessage);
        }
    }

    /**
     * Set the Channel value if the new value is different than the old.
     *
     * @param ch
     * @param value
     * @param <T>
     * @return true if channel was written to, false otherwise
     * @throws CAException
     */
    protected <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException {
        if(!value.equals(ch.getVal().get(0))){
            ch.setValue(value);
            return true;
        }else{
            return false;
        }
    }

}
