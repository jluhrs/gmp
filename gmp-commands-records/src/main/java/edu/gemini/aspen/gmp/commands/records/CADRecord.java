package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class CADRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */ //todo:make listeners, and maybe records, thread safe
public class CADRecord {
    private static char[] letters = new char[]{'A', 'B', 'C', 'D', 'E'};
    private static final Logger LOG = Logger.getLogger(CADRecord.class.getName());

    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            try {
                LOG.info("Dir Received: " + ((String[]) dbr.convert(DBRType.STRING).getValue())[0]);
                short newDir = ((short[]) dbr.getValue())[0];
                 car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());
                if (getState() == 0 &&
                        ((newDir == ApplyRecord.Dir.PRESET.ordinal()) ||
                                (newDir == ApplyRecord.Dir.START.ordinal()) ||
                                (newDir == ApplyRecord.Dir.STOP.ordinal()))) {
                    car.changeState(CARRecord.Val.IDLE,"",0,getClientId());
                    return;
                }
                boolean noError=true;
                switch (newDir) {
                    case 0://mark
                        setState(1);
                        break;
                    case 1://clear
                        setState(0);
                        break;
                    case 2://preset
                        noError=doPreset();
                        setState(2);
                        break;
                    case 3://start
                        if (getState() == 1) {
                            doPreset();
                            setState(2);
                        }
                        noError=doStart();
                        setState(0);
                        break;
                    case 4://stop
                        setState(0);
                        break;

                }
                if(!noError){
                   car.changeState(CARRecord.Val.ERR,((String[])mess.getValue().getValue())[0],((int[])val.getValue().getValue())[0],getClientId());
                }else{
                    car.changeState(CARRecord.Val.IDLE,"",0,getClientId());
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
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
    Channel<ApplyRecord.Dir> dir;
    /**
     * An integer value to be associated with the current command.
     */
    //private Channel<Long> icid;
    Channel<Integer> icid;


    //out
    /**
     * The return value is set within the user-supplied processing subroutine. Conventionally, a return value of zero
     * indicates success, while a non-zero value shows an error has occurred.
     */
    //private Channel<Long> val;
    Channel<Integer> val;
    /**
     * A return message from the CAD. This string will be empty if the return value is zero.
     */
    Channel<String> mess;
    /**
     * The previous message.
     */
    private Channel<String> omss;
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
    private CARRecord car;
    private CADRecord() {

    }
    public CADRecord(ChannelAccessServer cas, int numAttr){
        if(numAttr>letters.length){
            throw new IllegalArgumentException("Number of attributes must be less or equal than "+letters.length);
        }
        this.numAttr=numAttr;
        this.cas=cas;
    }
    @Validate
    public void start() {
        try {
            dir = cas.createChannel("gpi:observe.DIR", ApplyRecord.Dir.CLEAR);
            dir.registerListener(new DirListener());
            icid = cas.createChannel("gpi:observe.ICID", 0);

            val = cas.createChannel("gpi:observe.VAL", 0);
            mess = cas.createChannel("gpi:observe.MESS", "");
            omss = cas.createChannel("gpi:observe.OMSS", "");
            mark = cas.createChannel("gpi:observe.MARK", 0);
            for(int i=0;i<numAttr;i++){
                Channel<String> ch = cas.createChannel("gpi:observe." + letters[i], "");
                ch.registerListener(new AttributeListener());
                attributes.add(ch);

            }
             car = new CARRecord(cas,"gpi:observeC");
            car.start();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        cas.destroyChannel(dir);
        cas.destroyChannel(icid);
        cas.destroyChannel(val);
        cas.destroyChannel(mess);
        cas.destroyChannel(omss);
        cas.destroyChannel(mark);
        for(Channel<?> ch:attributes){
            cas.destroyChannel(ch);
        }
        car.stop();
    }

    private int getState() throws CAException {
        DBR dbr = mark.getValue();
        return ((int[])dbr.getValue())[0];
    }

    private void setState(int state) throws CAException {
        mark.setValue(state);
    }
    private List<String> getInputValues() throws CAException {
        List<String> values=new ArrayList<String>();
        for(Channel<String> ch:attributes){
            values.add(((String[])ch.getValue().getValue())[0]);
        }
        return values;
    }
    private boolean doPreset() throws CAException {

        LOG.info("PRESET: checking values: "+getInputValues());
        return true;
    }

    private boolean doStart() throws CAException {
        LOG.info("START: acting on values: "+getInputValues());
        return true;
    }
    private int getClientId() throws CAException {
         DBR dbr = icid.getValue();
         int value = ((int[])dbr.getValue())[0];
         return value;

     }

}
