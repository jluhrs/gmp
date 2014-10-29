package edu.gemini.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.gmp.top.Top;
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
    private final Logger LOG;
    private CadState state = CadState.CLEAR;

    final private CommandSender cs;
    final private SequenceCommand seqCom;
    final private EpicsCad epicsCad;

    final private Top epicsTop;
    final private String name;
    final private List<String> attributeNames = new ArrayList<String>();
    final private CarRecord car;
    private final long timeout;


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
                            Iterable<String> attributes,
                            long timeout) {
        this.timeout = timeout;
        LOG = Logger.getLogger("CAD Record " + epicsTop.buildEpicsChannelName(name));

        this.cs = cs;
        this.epicsTop = epicsTop;
        this.name = name;

        SequenceCommand seqComTemp;
        try {
            seqComTemp = SequenceCommand.getFromName(name);
        } catch (IllegalArgumentException ex) {
            seqComTemp = SequenceCommand.valueOf("APPLY");
        }
        seqCom = seqComTemp;

        for (String att : attributes) {
            attributeNames.add(att);
        }
        epicsCad = new EpicsCad(cas);
        car = new CarRecord(cas, epicsTop.buildEpicsChannelName(name + "C"));
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

    private synchronized void processDir(Dir dir) {
        if (state != CadState.CLEAR) {
            LOG.info("CAD Record: " + seqCom.getName() + " in State: " + state + " received Directive: " + dir);
        }
        CadState newState = state.processDir(dir, epicsCad, cs, seqCom, car, timeout);

        if (newState == CadState.CLEAR) {
            if (dir == Dir.START || dir == Dir.CLEAR || dir == Dir.STOP) {
                epicsCad.clearConfig();
            }
        }

        if (state != CadState.CLEAR) {
            LOG.info("CAD Record: " + seqCom.getName() + " now in State: " + newState);
        }

        state = newState;
    }

    private class AttributeListener implements ChannelListener<String> {
        @Override
        public void valueChanged(final String channelName, final List<String> values) {
            LOG.info("CAD Record: " + seqCom.getName() + " Attribute Received: " + values.get(0) + " on " + channelName);
            processDir(Dir.MARK);
        }
    }

    private class DirListener implements ChannelListener<Dir> {
        @Override
        public void valueChanged(String channelName, final List<Dir> values) {
            processDir(values.get(0));
        }
    }

    protected CadState getState() {
        return state;
    }

}
