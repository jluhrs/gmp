package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class CADRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */ //todo:make listeners, and maybe records, thread safe
public class CADRecord extends Record {
    private static char[] letters = new char[]{'A', 'B', 'C', 'D', 'E'};


    private class AttributeListener implements ChannelListener{

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
    //in
    /**
     * The directive to execute. This may be one of the following enumerated list values: MARK , CLEAR , PRESET ,
     * START , or STOP .
     */
    //Channel<ApplyRecord.Dir> dir;
    /**
     * An integer value to be associated with the current command.
     */
    //private Channel<Long> icid;
    //Channel<Integer> icid;
      Channel<Integer> ocid;

    //out
    /**
     * The return value is set within the user-supplied processing subroutine. Conventionally, a return value of zero
     * indicates success, while a non-zero value shows an error has occurred.
     */
    //private Channel<Long> val;
   // Channel<Integer> val;
    /**
     * A return message from the CAD. This string will be empty if the return value is zero.
     */
    //Channel<String> mess;
    /**
     * The previous message.
     */
    //private Channel<String> omss;
    /**
     * This field shows the current state of the CAD. It can be zero, indicating no MARK has been done; one, showing a
     * MARK; or two, showing a PRESET has been done.
     */
    //private Channel<Short> mark;
    private Channel<Integer> mark;

    /**
     * The 20 string input arguments.
     */
    private List<Channel<String>> attributes = new ArrayList<Channel<String>>();

    @Requires
    private ChannelAccessServer cas;

    @Property//todo:complete annotation
    private int numAttr;
    //private CARRecord car;

    public CADRecord(ChannelAccessServer cas, int numAttr) {
        super(cas, "gpi:observe");
        if (numAttr > letters.length) {
            throw new IllegalArgumentException("Number of attributes must be less or equal than " + letters.length);
        }
        this.numAttr = numAttr;
        this.cas = cas;
    }

    @Override
    protected boolean processDir(Dir dir) throws CAException {
        if (getState() == 0 &&
                ((dir == ApplyRecord.Dir.PRESET) ||
                        (dir == ApplyRecord.Dir.START) ||
                        (dir == ApplyRecord.Dir.STOP))) {
            return true;
        }
        boolean noError=true;
        switch (dir) {
            case MARK://mark
                noError=doMark();
                copyIcidToOcid();
                setState(1);
                break;
            case CLEAR://clear
                noError=doClear();
                copyIcidToOcid();
                setState(0);
                break;
            case PRESET://preset
                noError=doPreset();
                copyIcidToOcid();
                setState(2);
                break;
            case START://start
                if (getState() == 1) {
                    noError=doPreset();
                    copyIcidToOcid();
                    setState(2);
                    if(!noError){
                        break;
                    }
                }
                noError=doStart();
                copyIcidToOcid();
                setState(0);
                break;
            case STOP://stop
                noError=doStop();
                copyIcidToOcid();
                setState(0);
                break;
        }
        if(noError){
            setIfDifferent(mess,"");
            val.setValue(0);
            return true;
        }else{
            val.setValue(-1);
            return false;
        }
    }

    @Validate
    public void start() {
        try {
            super.start();
            clid = cas.createChannel(prefix + ".ICID", 0);
            ocid = cas.createChannel(prefix + ".OCID", 0);
            mark = cas.createChannel(prefix + ".MARK", 0);
            for (int i = 0; i < numAttr; i++) {
                Channel<String> ch = cas.createChannel(prefix + "." + letters[i], "");
                ch.registerListener(new AttributeListener());
                attributes.add(ch);

            }

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
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
