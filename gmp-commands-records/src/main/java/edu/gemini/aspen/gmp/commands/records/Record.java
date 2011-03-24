package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;

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

    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            try {
                processDir(Dir.values()[((short[]) dbr.getValue())[0]]);

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

    protected String prefix;
    protected String name;

    protected Record(ChannelAccessServer cas, String prefix, String name) {
        this.cas = cas;
        this.prefix = prefix;
        this.name = name;

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
        dir = cas.createChannel(prefix +":"+ name + ".DIR", Dir.CLEAR);
        dir.registerListener(new DirListener());
        val = cas.createChannel(prefix +":"+ name + ".VAL", 0);
        mess = cas.createChannel(prefix +":"+ name + ".MESS", "");
        omss = cas.createChannel(prefix +":"+ name + ".OMSS", "");
        car = new CARRecord(cas, prefix +":"+ name + "C");
        car.start();
    }

    protected void stop() {
        cas.destroyChannel(dir);
        car.stop();

    }

    protected int getClientId() throws CAException {
        return clid.getFirst();
    }

    protected void setMessage(String message) throws CAException {
        DBR dbr = mess.getValue();
        String oldMessage = ((String[]) dbr.getValue())[0];
        if (setIfDifferent(mess, message)) {
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
    static protected <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException {
        if (!value.equals(ch.getFirst())) {
            ch.setValue(value);
            return true;
        } else {
            return false;
        }
    }

}
