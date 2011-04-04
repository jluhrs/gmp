package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class CadRecordImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 3/24/11
 */
public class CadRecordImpl implements CadRecord {
    private static final Logger LOG = Logger.getLogger(CadRecordImpl.class.getName());


    private CadState state = CadState.CLEAR;


    final private CommandSender cs;
    final private SequenceCommand seqCom;
    final private EpicsCad epicsCad;

    final private String epicsTop;
    final private String name;
    final private List<String> attributeNames = new ArrayList<String>();
    final private CarRecord car;

    /**
     * Constructor
     *
     * @param cas Channel Access Server to pass to CAR and EpicsCad
     * @param cs Command Sender to use
     * @param epicsTop instrument epicsTop. ex.: "gpi"
     * @param name CAD name. ex.: "park"
     * @param attributes attribute names this CAD has.
     */
    protected CadRecordImpl(ChannelAccessServer cas,
                            CommandSender cs,
                            String epicsTop,
                            String name,
                            Iterable<String> attributes) {
        this.cs = cs;
        this.epicsTop = epicsTop.toLowerCase();
        this.name = name.toLowerCase();
        if(name.equalsIgnoreCase("config")){
            seqCom = SequenceCommand.valueOf("APPLY");
        }else{
            seqCom = SequenceCommand.valueOf(name.toUpperCase());
        }
        for(String att: attributes){
            attributeNames.add(att);
        }
        epicsCad = new EpicsCad(cas);
        car = new CarRecord(cas, epicsTop.toLowerCase() + ":" + name.toLowerCase() + "C");
        LOG.info("Constructor");
    }

    @Override
    public synchronized void start() {
        LOG.info("Validate");

        epicsCad.start(epicsTop, name, new AttributeListener(), new DirListener(), attributeNames);
        car.start();
    }

    @Override
    public synchronized void stop() {
        LOG.info("InValidate");
        epicsCad.stop();
        car.stop();

    }

    @Override
    public EpicsCad getEpicsCad() {
        return epicsCad;
    }

    @Override
    public CarRecord getCar() {
        return car;
    }

    private synchronized CadState processDir(Dir dir) {
        LOG.info("State: " + state + " Directive: " + dir);
        CadState newState = state.processDir(dir, epicsCad, cs, seqCom, car);
        LOG.info("State: " + newState);
        return newState;
    }

    private class AttributeListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            LOG.info("Attribute Received: " + ((String[]) dbr.getValue())[0]);
            state = processDir(Dir.MARK);
        }
    }

    private class DirListener implements ChannelListener {
        @Override
        public void valueChange(DBR dbr) {
            state = processDir(Dir.values()[((short[]) dbr.getValue())[0]]);
        }
    }

    CadState getState() {
        return state;
    }

}
