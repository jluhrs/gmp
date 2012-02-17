package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.ChannelListener;

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

    final private Top epicsTop;
    final private String name;
    final private List<String> attributeNames = new ArrayList<String>();
    final private CarRecord car;

    /**
     * Constructor
     *
     * @param cas        Channel Access Server to pass to CAR and EpicsCad
     * @param cs         Command Sender to use
     * @param epicsTop
     * @param name       CAD name. ex.: "park"
     * @param attributes attribute names this CAD has.
     */
    protected CadRecordImpl(ChannelAccessServer cas,
                            CommandSender cs,
                            Top epicsTop,
                            String name,
                            Iterable<String> attributes) {
        this.cs = cs;
        this.epicsTop = epicsTop;
        this.name = name.toLowerCase();
        if (name.equalsIgnoreCase("config")) {
            seqCom = SequenceCommand.valueOf("APPLY");
        } else {
            seqCom = SequenceCommand.valueOf(name.toUpperCase());
        }
        for (String att : attributes) {
            attributeNames.add(att);
        }
        epicsCad = new EpicsCad(cas);
        car = new CarRecord(cas, epicsTop.buildEpicsChannelName(name.toLowerCase() + "C"));
        LOG.fine("Finished constructing CAD record " + name);
    }

    @Override
    public synchronized void start() {
        LOG.fine("Validate " + seqCom.getName() + " CAD record");

        epicsCad.start(epicsTop, name, new AttributeListener(), new DirListener(), attributeNames);
        car.start();
    }

    @Override
    public synchronized void stop() {
        LOG.fine("InValidate " + seqCom.getName() + " CAD record");
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
        if (state != CadState.CLEAR)
            LOG.info("CAD Record: " + seqCom.getName() + " in State: " + state + " received Directive: " + dir);
        CadState newState = state.processDir(dir, epicsCad, cs, seqCom, car);
        if (state != CadState.CLEAR) LOG.fine("CAD Record: " + seqCom.getName() + " now in State: " + newState);
        return newState;
    }

    private class AttributeListener implements ChannelListener<String> {
        @Override
        public void valueChanged(String channelName, List<String> values) {
            LOG.fine("CAD Record: " + seqCom.getName() + " Attribute Received: " + values.get(0));
            state = processDir(Dir.MARK);
        }
    }

    private class DirListener implements ChannelListener<Dir> {
        @Override
        public void valueChanged(String channelName, List<Dir> values) {
            state = processDir(values.get(0));
        }
    }

    CadState getState() {
        return state;
    }

}
