package edu.gemini.gmp.commands.records;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.gmp.commands.records.generated.ConfigRecordType;
import edu.gemini.gmp.commands.records.generated.ConfigSetType;
import edu.gemini.gmp.top.Top;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */

public class ApplyRecord {
    private final Logger LOG;
    private final String name;

    private final Top epicsTop;

    private ReadOnlyChannel<Dir> dir;
    private Channel<Integer> val;
    private Channel<String> mess;
    private Channel<String> omss;
    private Channel<Integer> clid;

    private final CarRecord car;

    private final ChannelAccessServer cas;
    private final List<CadRecord> cads = new ArrayList<CadRecord>();

    /**
     * indicates that the record is currently processing a directive
     */
    volatile private boolean processing = false;


    /**
     * Constructor
     *
     * @param cas      Channel Access Server to use
     * @param cs       Command Sender to use
     * @param epicsTop The Top level for the Epics Channel
     */
    protected ApplyRecord(ChannelAccessServer cas,
                          CommandSender cs,
                          Top epicsTop,
                          Collection<SequenceCommand> seqComs,
                          Collection<ConfigRecordType> configs,
                          String name,
                          long timeout) {
        LOG = Logger.getLogger("APPLY Record " + epicsTop.buildEpicsChannelName(name));

        LOG.info("Constructing APPLY Record " + epicsTop.buildEpicsChannelName(name));
        this.cas = cas;
        this.epicsTop = epicsTop;
        this.name = name;
        car = new CarRecord(cas, epicsTop.buildEpicsChannelName(name + "C"));

        for (SequenceCommand seq : seqComs) {
            if (seq.equals(SequenceCommand.OBSERVE)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName(), Lists.<String>newArrayList(seq.getName() + ".DATA_LABEL"), timeout));
            } else if (seq.equals(SequenceCommand.REBOOT)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName(), Lists.<String>newArrayList(seq.getName() + ".REBOOT_OPT"), timeout));
            } else if (seq.equals(SequenceCommand.ENGINEERING)) {
                //We do not want to expose engineering command to EPICS at this moment
            } else {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName(), new ArrayList<String>(), timeout));
            }
        }
        for (ConfigRecordType configRecord : configs) {
            List<String> attributes = new ArrayList<String>();
            for (ConfigSetType configSet : configRecord.getConfigSet()) {
                for (String field : configSet.getField()) {
                    attributes.add(configSet.getName() + "." + field);
                }
            }
            cads.add(new CadRecordImpl(cas, cs, epicsTop, configRecord.getName(), attributes, timeout));
        }
    }

    /**
     * Create Channels and start CAR
     */
    public void start() {
        synchronized (car) {
            LOG.fine("Validating APPLY Record");
            try {
                dir = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".DIR"), Dir.CLEAR);
                dir.registerListener(new DirListener());
                val = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".VAL"), 0);
                mess = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".MESS"), "");
                omss = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".OMSS"), "");
                clid = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".CLID"), 0);

                car.start();
                for (CadRecord cad : cads) {
                    cad.start();
                    cad.getCar().registerListener(new CarListener());
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Destroy Channels and stop CAR
     */
    public void stop() {
        synchronized (car) {
            LOG.info("Invalidating APPLY Record");
            try {
                cas.destroyChannel(dir);
                cas.destroyChannel(val);
                cas.destroyChannel(mess);
                cas.destroyChannel(omss);
                cas.destroyChannel(clid);

                car.stop();
                for (CadRecord cad : cads) {
                    cad.stop();
                }
            } catch (Exception e) {
                LOG.warning("Exception while shutting down apply record " + e.getMessage());
            }
        }
    }

    private boolean processDir(Dir dir) throws CAException, TimeoutException {
        synchronized (car) {
            processing = true;
            if (dir == Dir.START) {
                incAndGetClientId();
            }
            car.setBusy(getClientId());
            boolean retVal = processInternal(dir);

            if (retVal) {
                boolean idle = true;
                for (CadRecord cad : cads) {
                    if (!cad.getCar().getState().equals(Optional.fromNullable(CarRecord.Val.IDLE))) {
                        idle = false;
                    }
                }
                if (idle) {
                    car.setIdle(getClientId());
                }
            } else {
                car.setError(getClientId(), ((String[]) mess.getDBR().getValue())[0], ((int[]) val.getDBR().getValue())[0]);
            }
            processing = false;
            return retVal;
        }
    }


    private boolean processInternal(Dir dir) throws CAException, TimeoutException {
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
        for (CadRecord cad : cads) {
            UpdateListener listener = new UpdateListener();
            cad.getEpicsCad().registerValListener(listener);
            LOG.finer("listener registered");

            cad.getEpicsCad().setDir(dir, id);
            LOG.finer("commands sent");
            Integer value = -1;

            try {
                listener.await();
                LOG.finer("latch released");

                cad.getEpicsCad().unRegisterValListener(listener);
                value = listener.getValues().get(0);

            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
                cad.getEpicsCad().unRegisterValListener(listener);

            }
            LOG.finer("all ok");

            if (value != 0) {
                val.setValue(value);
                setMessage(cad.getEpicsCad().getMess());
                error = true;
                break;
            } else {
                val.setValue(id);
            }
        }
        return !error;
    }

    private void processCarStatusChange(CarRecord.Val state, String message, Integer errorCode, Integer id) {
        synchronized (car) {
            if (state == CarRecord.Val.ERROR) {
                car.setError(Math.max(id, car.getClId()), message, errorCode);
            }
            for (CadRecord cad : cads) {
                //if any CAR is in error, then global CAR should be in error
                if (cad.getCar().getState().equals(Optional.fromNullable(CarRecord.Val.ERROR))) {
                    car.setError(Math.max(id, cad.getCar().getClId()), message, errorCode);
                    return;
                }
            }
            if (!processing && state == CarRecord.Val.IDLE) {
                for (CadRecord cad : cads) {
                    if ( ! cad.getCar().getState().equals(Optional.fromNullable(CarRecord.Val.IDLE)) ) {
                        return;
                    }
                }
                car.setIdle(Math.max(id, car.getClId()));
            }
       }
    }


    private int getClientId() throws CAException, TimeoutException {
        synchronized (clid) {
            return clid.getFirst();
        }
    }

    private void setMessage(String message) throws CAException, TimeoutException {
        DBR dbr = mess.getDBR();
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
    static private <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException, TimeoutException {
        if (!value.equals(ch.getFirst())) {
            ch.setValue(value);
            return true;
        } else {
            return false;
        }
    }


    private int incAndGetClientId() throws CAException, TimeoutException {
        synchronized (clid) {
            int value = getClientId();
            clid.setValue(++value);
            return value;
        }
    }

    /**
     * This listener will be called when a directive is written to the DIR field
     */
    private class DirListener implements ChannelListener<Dir> {
        @Override
        public void valueChanged(String channelName, final List<Dir> values) {
            LOG.info("Received DIR write: " + values.get(0));
            try {
                processDir(values.get(0));
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            } catch (TimeoutException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }


    /**
     * This listener will be invoked when any CAR changes state, and will reflect the states in the main CAR
     */
    private class CarListener implements edu.gemini.gmp.commands.records.CarListener {
        @Override
        public void update(final CarRecord.Val state, final String message, final Integer errorCode, final Integer id) {
            LOG.info("Received CAR status change: " + state);
            processCarStatusChange(state, message, errorCode, id);
        }
    }

}
