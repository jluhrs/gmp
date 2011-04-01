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
@Component
@Provides
public class CadRecordImpl implements CadRecord {
    private static final Logger LOG = Logger.getLogger(CadRecordImpl.class.getName());


    private CadState state = CadState.CLEAR;


    final private CommandSender cs;
    final private SequenceCommand seqCom;
    final private EpicsCad epicsCad;

    final private String prefix;
    final private String name;
    final private List<String> attributeNames = new ArrayList<String>();
    final private CarRecord car;

    /**
     * Constructor
     *
     * @param cas Channel Access Server to pass to CAR and EpicsCad
     * @param cs Command Sender to use
     * @param prefix instrument prefix. ex.: "gpi"
     * @param name CAD name. ex.: "park"
     * @param attributes attribute names this CAD has.
     */
    protected CadRecordImpl(@Requires ChannelAccessServer cas,
                            @Requires CommandSender cs,
                            @Property(name = "prefix", value = "INVALID", mandatory = true) String prefix,
                            @Property(name = "name", value = "INVALID", mandatory = true) String name,
                            @Property(name = "attributes", value = "", mandatory = true) String attributes) {
        this.cs = cs;
        this.prefix = prefix.toLowerCase();
        this.name = name.toLowerCase();
        seqCom = SequenceCommand.valueOf(name.toUpperCase());
        for(String att: attributes.split(",")){
            attributeNames.add(att.trim());
        }
        epicsCad = new EpicsCad(cas);
        car = new CarRecord(cas, prefix.toLowerCase() + ":" + name.toLowerCase() + "C");
        LOG.info("Constructor");
    }

    @Validate
    public synchronized void start() {
        LOG.info("Validate");

        epicsCad.start(prefix, name, new AttributeListener(), new DirListener(), attributeNames);
        car.start();
    }

    @Invalidate
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
