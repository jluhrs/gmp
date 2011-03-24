package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
@Component
public class ApplyRecord extends Record {

    boolean processing=false;

    @Override
    protected boolean processDir(Dir dir) throws CAException {
        processing=true;
        if (dir == Dir.START) {
            incAndGetClientId();
        }
        car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());
        boolean retVal = processInternal(dir);

        if (retVal) {
            boolean idle=true;
            for (CADRecord cad : cads) {
                if (cad.getCAR().val.getFirst() != CARRecord.Val.IDLE) {
                    idle=false;
                }
            }
            if(idle)car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
        } else {
            car.changeState(CARRecord.Val.ERR, ((String[]) mess.getValue().getValue())[0], ((int[]) val.getValue().getValue())[0], getClientId());
        }
        processing=false;
        return retVal;
    }
    protected class UpdateListener extends CountDownLatch implements ChannelListener {

        public UpdateListener() {
            super(1);
        }

        private DBR dbr=null;

        @Override
        public void valueChange(DBR dbr) {
            if(getCount()==1){
                this.dbr = dbr;
                countDown();
            }
        }

        public DBR getDBR() {
            return dbr;
        }

    }
    void reflectState(CARRecord.Val state, String message, Integer errorCode, Integer id) throws CAException {
        if(state==CARRecord.Val.ERR || state==CARRecord.Val.BUSY){
            car.changeState(state,message,errorCode,id);
        }
        if (!processing && state == CARRecord.Val.IDLE) {
            for (CADRecord cad : cads) {
                if (cad.getCAR().val.getFirst() != CARRecord.Val.IDLE) {
                    return;
                }
            }
            car.changeState(state,message,errorCode,id);
        }
    }
    private class CARListener implements CARRecord.CARListener{
        @Override
        public void update(CARRecord.Val state, String message, Integer errorCode, Integer id) {
            try {
                reflectState(state,message,errorCode,id);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
    private boolean processInternal(Dir dir) throws CAException {
        if (dir == Dir.MARK) {
            return false;
        }
        setMessage("");
        if (dir == Dir.START) {
            if (!processInternal(Dir.PRESET)) {
                return false;
            }
        }
        //set clid and dir on all CADs
        int id = getClientId();
        boolean error = false;
        for (CADRecord cad : cads) {
            UpdateListener listener = new UpdateListener();
            cad.registerValListener(listener);
            LOG.info("listener registered");

            cad.setClid(id);
            cad.setDir(dir);
            LOG.info("commands sent");
            Integer value = -1;

            try {
                listener.await();
                LOG.info("latch released");

                cad.unRegisterValListener(listener);
                    value = ((int[]) listener.getDBR().getValue())[0];

            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                cad.unRegisterValListener(listener);

            }
            LOG.info("all ok");

            if (value != 0) {
                val.setValue(value);
                setMessage(cad.getMess());
                error = true;
                break;
            } else {
                val.setValue(id);
            }
        }
        return !error;
    }

    private List<CADRecord> cads = new ArrayList<CADRecord>();

    protected ApplyRecord(@Requires ChannelAccessServer cas,
                        @Property(name = "prefix", value = "INVALID", mandatory = true)String prefix) {
        super(cas,prefix,"apply");
        LOG.info("Constructor");

    }


    @Validate
    public void start() {
        LOG.info("Validate");
        try {
            super.start();

            clid = cas.createChannel(prefix +":"+ name + ".CLID", 0);

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("InValidate");
        super.stop();
        cas.destroyChannel(clid);
//        for(CADRecord cad:cads){
//            cad.stop();
//        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindCAD(CADRecord cad) {
        LOG.info("Bind");

        cads.add(cad);
        cad.registerCARListener(new CARListener());
    }

    @Unbind(aggregate = true, optional = true)
    public void unBindCAD(CADRecord cad) {
        LOG.info("Unbind");

        cads.remove(cad);
    }

    private int incAndGetClientId() throws CAException {
        int value = getClientId();
        clid.setValue(++value);
        return value;

    }

}
