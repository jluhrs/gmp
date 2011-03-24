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
import java.util.logging.Logger;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
@Component
public class ApplyRecord {
    private static final Logger LOG = Logger.getLogger(ApplyRecord.class.getName());


    private Channel<Dir> dir;
    private Channel<Integer> val;
    private Channel<String> mess;
    private Channel<String> omss;
    private Channel<Integer> clid;

    private CARRecord car;

    private ChannelAccessServer cas;

    private String prefix;
    private String name;
    private boolean processing = false;
    private List<EpicsCad> cads = new ArrayList<EpicsCad>();
    private List<CARRecord> cars = new ArrayList<CARRecord>();

    protected ApplyRecord(@Requires ChannelAccessServer cas,
                          @Property(name = "prefix", value = "INVALID", mandatory = true) String prefix) {
        this.cas = cas;
        this.prefix = prefix;
        this.name = "apply";
        LOG.info("Constructor");

    }


    @Validate
    public void start() {
        LOG.info("Validate");
        try {
            dir = cas.createChannel(prefix + ":" + name + ".DIR", Dir.CLEAR);
            dir.registerListener(new DirListener());
            val = cas.createChannel(prefix + ":" + name + ".VAL", 0);
            mess = cas.createChannel(prefix + ":" + name + ".MESS", "");
            omss = cas.createChannel(prefix + ":" + name + ".OMSS", "");
            car = new CARRecord(cas, prefix + ":" + name + "C");
            car.start();

            clid = cas.createChannel(prefix + ":" + name + ".CLID", 0);

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("InValidate");
        cas.destroyChannel(dir);
        cas.destroyChannel(val);
        cas.destroyChannel(mess);
        cas.destroyChannel(omss);
        car.stop();
        cas.destroyChannel(clid);
//        for(CADRecord cad:cads){
//            cad.stop();
//        }
    }


    @Bind(aggregate = true, optional = true)
    public void bindCAD(CADRecord cad) {
        LOG.info("Bind");

        cads.add(cad.getEpicsCad());
        cars.add(cad.getCAR());
        LOG.info(cars.toString());
        cad.getCAR().registerListener(new CARListener());
    }

    @Unbind(aggregate = true, optional = true)
    public void unBindCAD(CADRecord cad) {
        LOG.info("Unbind");

        cads.remove(cad.getEpicsCad());
        cars.remove(cad.getCAR());
    }

    private boolean processDir(Dir dir) throws CAException {
        processing = true;
        if (dir == Dir.START) {
            incAndGetClientId();
        }
        car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());
        boolean retVal = processInternal(dir);

        if (retVal) {
            boolean idle = true;
            for (CARRecord otherCar : cars) {
                if (otherCar.getState() != CARRecord.Val.IDLE) {
                    idle = false;
                }
            }
            if (idle) car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
        } else {
            car.changeState(CARRecord.Val.ERR, ((String[]) mess.getValue().getValue())[0], ((int[]) val.getValue().getValue())[0], getClientId());
        }
        processing = false;
        return retVal;
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
        for (EpicsCad cad : cads) {
            UpdateListener listener = new UpdateListener();
            cad.registerValListener(listener);
            LOG.info("listener registered");

            cad.setDir(dir, id);
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


    private int getClientId() throws CAException {
        return clid.getFirst();
    }

    private void setMessage(String message) throws CAException {
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
    static private <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException {
        if (!value.equals(ch.getFirst())) {
            ch.setValue(value);
            return true;
        } else {
            return false;
        }
    }


    private int incAndGetClientId() throws CAException {
        int value = getClientId();
        clid.setValue(++value);
        return value;

    }

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

    private class CARListener implements CARRecord.CARListener {
        @Override
        public void update(CARRecord.Val state, String message, Integer errorCode, Integer id) {
            try {
                if (state == CARRecord.Val.ERR || state == CARRecord.Val.BUSY) {
                    car.changeState(state, message, errorCode, id);
                }
                if (!processing && state == CARRecord.Val.IDLE) {
                    for (CARRecord otherCar : cars) {
                        if (otherCar.getState() != CARRecord.Val.IDLE) {
                            return;
                        }
                    }
                    car.changeState(state, message, errorCode, id);
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
