package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
@Component
@Instantiate
public class ApplyRecord {
    private static final Logger LOG = Logger.getLogger(ApplyRecord.class.getName());

    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            try {
                LOG.info("Dir Received: " + ((String[]) dbr.convert(DBRType.STRING).getValue())[0]);

                short newDir = ((short[]) dbr.getValue())[0];
                if (Dir.values()[newDir] == Dir.START) {
                    incAndGetClientId();
                }
                car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());

                if(!process(Dir.values()[newDir])){
                    car.changeState(CARRecord.Val.ERR,((String[])mess.getValue().getValue())[0],((int[])val.getValue().getValue())[0],getClientId());
                }else{
                    car.changeState(CARRecord.Val.IDLE,"",0,getClientId());
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    enum Dir {
        MARK,
        CLEAR,
        PRESET,
        START,
        STOP
    }
    //in
    /**
     * The value of this field is passed to all OUTx output links. If the directive is START, the directive PRESET is
     * first passed to all output links. If the directive is MARK, it is not sent to the OUTx fields and processing stops.
     */
    private Channel<Dir> dir;

    //out
    /**
     * This is the return value from the input links. If any link returns a non-zero, processing stops and the last
     * value is returned. If all links return zero, the value of the client ID field (CLID) is returned.
     */
//    private Channel<Long> val;
    private Channel<Integer> val;
    /**
     * This is the return message from an INMx input link. If the return value is 0, this field is empty. Otherwise, it
     * reads the error message from the INMx link.
     */
    private Channel<String> mess;
    /**
     * This is the old message string.
     */
    private Channel<String> omss;
    /**
     * This number is incremented every time a directive is loaded. The value is passed to all OCLx output links.
     */
//    private Channel<Long> clid;
    private Channel<Integer> clid;


    private CARRecord car;
    private List<CADRecord> cads = new ArrayList<CADRecord>();

    @Requires
    private ChannelAccessServer cas;

    private ApplyRecord() {


    }

    @Validate
    public void start() {
        try {
            dir = cas.createChannel("gpi:apply.DIR", Dir.CLEAR);
            dir.registerListener(new DirListener());
            val = cas.createChannel("gpi:apply.VAL", 0);
            mess = cas.createChannel("gpi:apply.MESS", "");
            omss = cas.createChannel("gpi:apply.OMSS", "");
            clid = cas.createChannel("gpi:apply.CLID", 0);
            car = new CARRecord(cas,"gpi:applyC");
            car.start();
            CADRecord cad = new CADRecord(cas,3);
            cad.start();
            cads.add(cad);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        cas.destroyChannel(dir);
        cas.destroyChannel(val);
        cas.destroyChannel(mess);
        cas.destroyChannel(omss);
        cas.destroyChannel(clid);
        car.stop();
        for(CADRecord cad:cads){
            cad.stop();
        }
    }

    private int incAndGetClientId() throws CAException {
        int value = getClientId();
        return value;

    }
      private int getClientId() throws CAException {
        DBR dbr = clid.getValue();
        int value = ((int[])dbr.getValue())[0];
        return value;

    }

    /**
     * @param directive
     * @return true to continue processing
     * @throws CAException
     */
    private boolean process(Dir directive) throws CAException {
        if (directive == Dir.MARK) {
            return false;
        }
        mess.setValue("");
        if (directive == Dir.START) {
            if (!process(Dir.PRESET)) {
                return false;
            }
        }
        //set clid and dir on all CADs
        int id = getClientId();
        for(CADRecord cad:cads){
            cad.icid.setValue(id);
            cad.dir.setValue(directive);
        }
        //todo:wait for CADs to process DIRs first. For that need to monitor their OCID channel?
        //get all CAD VALs, if negative, put it in val, and copy mess to mess and return false
        boolean error=false;
        for(CADRecord cad:cads){
            DBR dbr = cad.val.getValue();
            int val = ((int[])dbr.getValue())[0];
            if(val<0){
                dbr=cad.mess.getValue();
                String message = ((String[])dbr.getValue())[0];
                mess.setValue(message);
                error=true;
            }
        }
        //if zero then copy clid to val
        if(!error){
            val.setValue(id);
            return true;
        }else{
            return false;
        }
    }
}
