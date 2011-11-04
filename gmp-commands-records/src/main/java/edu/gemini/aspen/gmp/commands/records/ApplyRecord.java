package edu.gemini.aspen.gmp.commands.records;

import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.records.generated.ConfigSetType;
import edu.gemini.aspen.gmp.commands.records.generated.ConfigSets;
import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.epics.api.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
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
    private final String name = "apply";
    //private final String epicsTop = "gpi";//to be read from elsewhere(cas?);

    private final EpicsTop epicsTop;

    private Channel<Dir> dir;
    private Channel<Integer> val;
    private Channel<String> mess;
    private Channel<String> omss;
    private Channel<Integer> clid;

    private final CarRecord car;

    private final ChannelAccessServer cas;
    private final CommandSender cs;
    private final List<CadRecord> cads = new ArrayList<CadRecord>();

    /**
     * indicates that the record is currently processing a directive
     */
    volatile private boolean processing = false;

    /**
     * Constructor
     *
     * @param cas         Channel Access Server to use
     * @param cs          Command Sender to use
     * @param epicsTop    The Top level for the Epics Channel
     * @param xmlFileName XML Configuration File
     * @param xsdFileName Schema of the configuration file
     */
    protected ApplyRecord(@Requires ChannelAccessServer cas,
                          @Requires CommandSender cs,
                          @Requires EpicsTop epicsTop,
                          @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName,
                          @Property(name = "xsdFileName", value = "INVALID", mandatory = true) String xsdFileName) {
        LOG.info("Constructor");
        this.cas = cas;
        this.cs = cs;
        this.epicsTop = epicsTop;
        car = new CarRecord(cas, epicsTop.buildChannelName(name + "C"));
        ConfigSets configSets;
        try {
            JAXBContext jc = JAXBContext.newInstance(ConfigSets.class);
            Unmarshaller um = jc.createUnmarshaller();
            SchemaFactory factory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File(xsdFileName));
            um.setSchema(schema); //to enable validation
            configSets = (ConfigSets) um.unmarshal(new File(xmlFileName));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error parsing xml file " + xmlFileName, ex);
            throw new IllegalArgumentException("Problem parsing XML", ex);
        }
        for (SequenceCommand seq : SequenceCommand.values()) {
            if (seq.equals(SequenceCommand.APPLY)) {
                List<String> attributes = new ArrayList<String>();
                for (ConfigSetType conf : configSets.getConfigSet()) {
                    for (String field : conf.getField()) {
                        attributes.add(conf.getName() + "." + field);
                    }
                }
                cads.add(new CadRecordImpl(cas, cs, epicsTop, "config", attributes));
            } else if (seq.equals(SequenceCommand.OBSERVE)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), Lists.<String>newArrayList(seq.getName().toLowerCase() + ".DATA_LABEL")));
            } else if (seq.equals(SequenceCommand.REBOOT)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), Lists.<String>newArrayList(seq.getName().toLowerCase() + ".REBOOT_OPT")));
            } else {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), new ArrayList<String>()));
            }

        }
    }

    /**
     * Create Channels and start CAR
     */
    @Validate
    public void start() {
        synchronized (car) {
            LOG.info("Validate");
            try {
                dir = cas.createChannel(epicsTop.buildChannelName(name + ".DIR"), Dir.CLEAR);
                dir.registerListener(new DirListener());
                val = cas.createChannel(epicsTop.buildChannelName(name + ".VAL"), 0);
                mess = cas.createChannel(epicsTop.buildChannelName(name + ".MESS"), "");
                omss = cas.createChannel(epicsTop.buildChannelName(name + ".OMSS"), "");
                clid = cas.createChannel(epicsTop.buildChannelName(name + ".CLID"), 0);

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
    @Invalidate
    public void stop() {
        synchronized (car) {
            LOG.info("InValidate");
            cas.destroyChannel(dir);
            cas.destroyChannel(val);
            cas.destroyChannel(mess);
            cas.destroyChannel(omss);
            cas.destroyChannel(clid);

            car.stop();
            for (CadRecord cad : cads) {
                cad.stop();
            }
        }
    }

//    /**
//     * Add a CAD to the list of CADs to be notified of new directives
//     *
//     * @param cad
//     */
//    @Bind(aggregate = true, optional = true)
//    public synchronized void bindCad(CadRecord cad) {
//        LOG.info("Bind");
//
//        cads.add(cad.getEpicsCad());
//        cars.add(cad.getCar());
//        LOG.info(cars.toString());
//        cad.getCar().registerListener(new CarListener());
//    }
//
//    @Unbind(aggregate = true, optional = true)
//    public synchronized void unBindCad(CadRecord cad) {
//        LOG.info("Unbind");
//
//        cads.remove(cad.getEpicsCad());
//        cars.remove(cad.getCar());
//    }


    private boolean processDir(Dir dir) throws CAException {
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
                    if (cad.getCar().getState() != CarRecord.Val.IDLE) {
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


    private int getClientId() throws CAException {
        return clid.getFirst();
    }

    private void setMessage(String message) throws CAException {
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

    /**
     * This listener will be called when a directive is written to the DIR field
     */
    private class DirListener implements ChannelListener<Dir> {
        @Override
        public void valueChange(String channelName, List<Dir> values) {
            LOG.info("Received DIR write: " + values.get(0));
            try {
                processDir(values.get(0));

            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * This listener will be invoked when any CAR changes state, and will reflect the states in the main CAR
     */
    private class CarListener implements edu.gemini.aspen.gmp.commands.records.CarListener {
        @Override
        public void update(CarRecord.Val state, String message, Integer errorCode, Integer id) {
            synchronized (car) {
                LOG.info("Received CAR status change: " + state);
                if (state == CarRecord.Val.ERR) {
                    car.setError(Math.max(id, car.getClId()), message, errorCode);
                }
                for (CadRecord cad : cads) {
                    //if any CAR is in error, then global CAR should be in error
                    if (cad.getCar().getState() == CarRecord.Val.ERR) {
                        car.setError(Math.max(id, cad.getCar().getClId()), message, errorCode);
                        return;
                    }
                }
                if (!processing && state == CarRecord.Val.IDLE) {
                    for (CadRecord cad : cads) {
                        if (cad.getCar().getState() != CarRecord.Val.IDLE) {
                            return;
                        }
                    }
                    car.setIdle(Math.max(id, car.getClId()));
                }

            }
        }
    }
}
