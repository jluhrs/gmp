package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class CADRecordImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */ //todo:make listeners, and maybe records, thread safe
@Component
@Provides
public class CADRecordImpl extends Record implements CADRecord {
    private static char[] letters = new char[]{'A', 'B', 'C', 'D', 'E'};

    @Override
    public void setClid(Integer id) throws CAException {
        clid.setValue(id);
    }

    @Override
    public void setDir(Dir d) throws CAException {
        dir.setValue(d);
    }

    @Override
    public List<Integer> getVal() throws CAException {
        return val.getVal();
    }

    @Override
    public List<String> getMess() throws CAException {
        return mess.getVal();
    }


    private class AttributeListener implements ChannelListener {

        @Override
        public void valueChange(DBR dbr) {
            LOG.info("Attribute Received: " + ((String[]) dbr.getValue())[0]);
            try {
                mark.setValue(1);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    Channel<Integer> ocid;

    private Channel<Integer> mark;

    /**
     * The 20 string input arguments.
     */
    private List<Channel<String>> attributes = new ArrayList<Channel<String>>();

    @Property(name = "prefix", value = "INVALID", mandatory = true)
    private String myPrefix;
    @Property(name = "recordname", value = "INVALID", mandatory = true)
    private String myRecordname;

    @Property(name = "numAttributes", value = "0", mandatory = true)
    private Integer numAttributes;

    private CADRecordImpl(@Requires ChannelAccessServer cas) {
        super(cas);
        if (numAttributes > letters.length) {
            throw new IllegalArgumentException("Number of attributes must be less or equal than " + letters.length);
        }
        LOG.info("Constructor");

    }
    public CADRecordImpl(@Requires ChannelAccessServer cas, String prefix, String recordname, Integer numAttributes) {
        super(cas);
        if (numAttributes > letters.length) {
            throw new IllegalArgumentException("Number of attributes must be less or equal than " + letters.length);
        }
        myPrefix=prefix;
        myRecordname=recordname;
        this.numAttributes=numAttributes;
        LOG.info("Constructor");

    }
    @Override
    protected boolean processDir(Dir dir) throws CAException {
        if (getState() == 0 &&
                ((dir == ApplyRecord.Dir.PRESET) ||
                        (dir == ApplyRecord.Dir.START) ||
                        (dir == ApplyRecord.Dir.STOP))) {
            return true;
        }
        boolean noError = true;
        switch (dir) {
            case MARK://mark
                noError = doMark();
                copyIcidToOcid();
                setState(1);
                break;
            case CLEAR://clear
                noError = doClear();
                copyIcidToOcid();
                setState(0);
                break;
            case PRESET://preset
                noError = doPreset();
                copyIcidToOcid();
                setState(2);
                break;
            case START://start
                if (getState() == 1) {
                    noError = doPreset();
                    copyIcidToOcid();
                    setState(2);
                    if (!noError) {
                        break;
                    }
                }
                noError = doStart();
                copyIcidToOcid();
                setState(0);
                break;
            case STOP://stop
                noError = doStop();
                copyIcidToOcid();
                setState(0);
                break;
        }
        if (noError) {
            setIfDifferent(mess, "");
            val.setValue(0);
            return true;
        } else {
            val.setValue(-1);
            return false;
        }
    }

    @Validate
    public void start() {
        LOG.info("Validate");

        try {
            super.start(myPrefix, myRecordname);
            clid = cas.createChannel(prefix + recordname + ".ICID", 0);
            ocid = cas.createChannel(prefix + recordname + ".OCID", 0);
            mark = cas.createChannel(prefix + recordname + ".MARK", 0);
            for (int i = 0; i < numAttributes; i++) {
                Channel<String> ch = cas.createChannel(prefix + recordname + "." + letters[i], "");
                ch.registerListener(new AttributeListener());
                attributes.add(ch);

            }

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("InValidate");

        super.stop();
        cas.destroyChannel(clid);
        cas.destroyChannel(ocid);
        cas.destroyChannel(mark);
        for (Channel<?> ch : attributes) {
            cas.destroyChannel(ch);
        }
    }

    private int getState() throws CAException {
        DBR dbr = mark.getValue();
        return ((int[]) dbr.getValue())[0];
    }

    private void setState(int state) throws CAException {
        mark.setValue(state);
    }

    private List<String> getInputValues() throws CAException {
        List<String> values = new ArrayList<String>();
        for (Channel<String> ch : attributes) {
            values.add(((String[]) ch.getValue().getValue())[0]);
        }
        return values;
    }

    private boolean doMark() throws CAException {
        LOG.info("MARK: values: " + getInputValues());
        return true;
    }

    private boolean doClear() throws CAException {
        LOG.info("CLEAR: values: " + getInputValues());
        return true;
    }

    private boolean doPreset() throws CAException {
        LOG.info("PRESET: values: " + getInputValues());
        return true;
    }

    private boolean doStart() throws CAException {
        LOG.info("START: values: " + getInputValues());
        return true;
    }

    private boolean doStop() throws CAException {
        LOG.info("STOP: values: " + getInputValues());
        return false;
    }

    private void copyIcidToOcid() throws CAException {
        ocid.setValue(clid.getVal());
    }


}
