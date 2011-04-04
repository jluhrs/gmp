package edu.gemini.aspen.gmp.commands.records;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.records.generated.ConfigSetType;
import edu.gemini.aspen.gmp.commands.records.generated.ConfigSets;
import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
    private final String epicsTop = "gpi";//to be read from elsewhere(cas?);

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
     * Constructor
     *
     * @param cas Channel Access Server to use
     * @param cs  Command Sender to use
     */
    protected ApplyRecord(@Requires ChannelAccessServer cas,
                          @Requires CommandSender cs,
                          @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName,
                          @Property(name = "xsdFileName", value = "INVALID", mandatory = true) String xsdFileName) {
        LOG.info("Constructor");
        this.cas = cas;
        this.cs = cs;
        car = new CarRecord(cas, epicsTop + ":" + name + "C");
        ConfigSets configSets = new ConfigSets();
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
                for(ConfigSetType conf:configSets.getConfigSet()){
                    for(String field:conf.getField()){
                        attributes.add(conf.getName()+"."+field);
                    }
                }
//                gpi:configAo
//                        useAo
//                        magnitudeI
//                        r0
//                        optimize
//                        useLastVals
//
//                gpi:configCal
//                        useCal
//                        magnitudeH
//
//                gpi:configIfs
//                        integrationTime
//                        readoutMode
//                        numReads
//                        numCoadds
//                        startx
//                        starty
//                        endx
//                        endy
//
//                gpi:configFOVIfsOffset
//                        xTarget
//                        yTarget
//
//                gpi:configPolarizer
//                        deploy
//                        angle
//
//                gpi:observationMode
//                        modeName
//                gpi:selectAdc
//                        deploy
//                        overrideCas
//                        overrideZen
//                        orientation
//                        power
//
//                gpi:selectPupilCamera
//                        deploy
//                gpi:selectSource
//                        source
//                        intensity
//
//                gpi:selectShutter
//                        entranceShutter
//                        calExitShutter
//                        calEntranceShutter
//                        calReferenceShutter
//                        calScienceShutter
//
//                gpi:takeExposure
//                        selection
//                        intTime
//                        filename
//                //Apply Sequence Command Calibration Sets
//                cal:acquireWhiteFringe
//                        mark
//                gpi:calToAoAlign
//                        phase
//                gpi:centerPinhole
//                        mark
//                ao:dmShape
//                        filename
//                        dmFlag
//
//                ao:measureAOWFSCentroids
//                        mark
//                cal:measureCalCentroids
//                        mark
//                cal:measureHowfsOffsets
//                        filename
//                gpi:takeDark
//                        selection
//                        intTime
//                        filename
//
//                gpi:takeFlat
//                        selection
//                        intTime
//                        filename
//
//                cal:transMaps
//                        filename
//                //Apply Sequence Command Engineering Sets (Miscellaneous access functions)
//                gpi:configAoSpatialFilter
//                        mode
//                        target
//                        now
//
//                gpi:configSteeringMirrors
//                        selection
//                        track
//                        tip
//                        tilt
//                        focus
//
//                gpi:correct
//                        selection
//                ifs:log
//                        temperatures
//                        filename
//                        rate
//
//                ifs:selectIfsFilter
//                        maskStr
//                gpi:selectFocalPlaneMask
//                        maskStr
//                gpi:selectLyotMask
//                        maskStr
//                gpi:selectPupilPlaneMask
//                        maskStr
//                gpi:statistics
//                        statStat
//                        statName
//                        statSelected


                //implement apply attributes
                cads.add(new CadRecordImpl(cas, cs, epicsTop, "config", attributes));
            } else if (seq.equals(SequenceCommand.OBSERVE)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), Lists.<String>newArrayList(seq.getName().toLowerCase()+".DATA_LABEL")));
            } else if (seq.equals(SequenceCommand.REBOOT)) {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), Lists.<String>newArrayList(seq.getName().toLowerCase()+".REBOOT_OPT")));
            } else {
                cads.add(new CadRecordImpl(cas, cs, epicsTop, seq.getName().toLowerCase(), new ArrayList<String>()));
            }

        }
    }

    /**
     * Create Channels and start CAR
     */
    @Validate
    public synchronized void start() {
        LOG.info("Validate");
        try {
            dir = cas.createChannel(epicsTop + ":" + name + ".DIR", Dir.CLEAR);
            dir.registerListener(new DirListener());
            val = cas.createChannel(epicsTop + ":" + name + ".VAL", 0);
            mess = cas.createChannel(epicsTop + ":" + name + ".MESS", "");
            omss = cas.createChannel(epicsTop + ":" + name + ".OMSS", "");
            clid = cas.createChannel(epicsTop + ":" + name + ".CLID", 0);

            car.start();
            for (CadRecord cad : cads) {
                cad.start();
                cad.getCar().registerListener(new CarListener());
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Destroy Channels and stop CAR
     */
    @Invalidate
    public synchronized void stop() {
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

    /**
     * indicates that the record is currently processing a directive
     */
    volatile private boolean processing = false;

    private synchronized boolean processDir(Dir dir) throws CAException {
        processing = true;
        if (dir == Dir.START) {
            incAndGetClientId();
        }
        car.changeState(CarRecord.Val.BUSY, "", 0, getClientId());
        boolean retVal = processInternal(dir);

        if (retVal) {
            boolean idle = true;
            for (CadRecord cad : cads) {
                if (cad.getCar().getState() != CarRecord.Val.IDLE) {
                    idle = false;
                }
            }
            if (idle) {
                car.changeState(CarRecord.Val.IDLE, "", 0, getClientId());
            }
        } else {
            car.changeState(CarRecord.Val.ERR, ((String[]) mess.getDBR().getValue())[0], ((int[]) val.getDBR().getValue())[0], getClientId());
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
        for (CadRecord cad : cads) {
            UpdateListener listener = new UpdateListener();
            cad.getEpicsCad().registerValListener(listener);
            LOG.info("listener registered");

            cad.getEpicsCad().setDir(dir, id);
            LOG.info("commands sent");
            Integer value = -1;

            try {
                listener.await();
                LOG.info("latch released");

                cad.getEpicsCad().unRegisterValListener(listener);
                value = ((int[]) listener.getDBR().getValue())[0];

            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                cad.getEpicsCad().unRegisterValListener(listener);

            }
            LOG.info("all ok");

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
    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            LOG.info("Received DIR write: " + Dir.values()[((short[]) dbr.getValue())[0]]);
            try {
                processDir(Dir.values()[((short[]) dbr.getValue())[0]]);

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
            synchronized (ApplyRecord.this) {
                LOG.info("Received CAR status change: " + state);
                try {
                    if (state == CarRecord.Val.ERR) {
                        car.changeState(state, message, errorCode, id);
                    }
                    if (!processing && state == CarRecord.Val.IDLE) {
                        for (CadRecord cad : cads) {
                            if (cad.getCar().getState() != CarRecord.Val.IDLE) {
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
}
