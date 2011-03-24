package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
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
 * Class CADRecordImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 3/24/11
 */
@Component
@Provides
public class CADRecordImpl implements CADRecord {
    private static final Logger LOG = Logger.getLogger(CADRecordImpl.class.getName());

    @Override
    public EpicsCad getEpicsCad() {
        return epicsCad;
    }

    @Override
    public CARRecord getCAR() {
        return car;
    }

    enum CadState {
        CLEAR {
            @Override
            public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CARRecord car) {
                car.setBusy(epicsCad.getClid());
                car.setIdle(epicsCad.getClid());
                epicsCad.setVal(0);
                try {
                    epicsCad.post();
                } catch (CAException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                }
                switch (dir) {
                    case MARK:
                        return MARKED;
                    case CLEAR:
                        return CLEAR;
                    case PRESET:
                    case START:
                    case STOP:
                        //do nothing
                        return CLEAR;
                    default://just so the compiler doesn't complain
                        return CLEAR;
                }
            }
        },
        MARKED {
            @Override
            public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CARRecord car) {
                car.setBusy(epicsCad.getClid());

                HandlerResponse resp;
                switch (dir) {
                    case MARK:
                        epicsCad.setVal(0);
                        try {
                            epicsCad.post();
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        car.setIdle(epicsCad.getClid());
                        return MARKED;
                    case CLEAR:
                        epicsCad.setVal(0);
                        try {
                            epicsCad.post();
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        car.setIdle(epicsCad.getClid());
                        return CLEAR;
                    case PRESET:
                        resp = doActivity(Activity.PRESET, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                                return IS_PRESET;
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                                return CLEAR;
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                            return CLEAR;
                        }
                    case START:
                        resp = doActivity(Activity.PRESET_START, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.STARTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                            } else if (resp.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return CLEAR;
                    case STOP:
                        resp = doActivity(Activity.CANCEL, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return CLEAR;
                    default://just so the compiler doesn't complain
                        return CLEAR;
                }
            }
        },
        IS_PRESET {
            @Override
            public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CARRecord car) {
                car.setBusy(epicsCad.getClid());
                HandlerResponse resp;
                switch (dir) {
                    case MARK:
                        epicsCad.setVal(0);
                        try {
                            epicsCad.post();
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return MARKED;
                    case CLEAR:
                        epicsCad.setVal(0);
                        try {
                            epicsCad.post();
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return CLEAR;
                    case PRESET:
                        resp = doActivity(Activity.PRESET, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                                return IS_PRESET;
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                                return CLEAR;
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                            return CLEAR;
                        }
                    case START:
                        resp = doActivity(Activity.START, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.STARTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                            } else if (resp.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return CLEAR;
                    case STOP:
                        resp = doActivity(Activity.CANCEL, cs, seqCom, epicsCad.getClid(), car);
                        try {
                            if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                                epicsCad.setVal(0);
                                epicsCad.post();
                                car.setIdle(epicsCad.getClid());
                            } else {
                                epicsCad.setVal(-1);
                                epicsCad.setMess(resp.getMessage());
                                epicsCad.post();
                                car.changeState(CARRecord.Val.ERR, resp.getMessage(), -1, epicsCad.getClid());
                            }
                        } catch (CAException e) {
                            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        }
                        return CLEAR;
                    default://just so the compiler doesn't complain
                        return CLEAR;
                }
            }
        };

        public abstract CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CARRecord car);

        private static HandlerResponse doActivity(Activity activity, CommandSender cs, SequenceCommand seqCom, Integer id, CARRecord car) {
            HandlerResponse resp = cs.sendCommand(new Command(seqCom, activity), new CADCompletionListener(id, car));
            LOG.info("Activity: "+activity+" ClientID: "+id+" Response: "+resp.getResponse().toString());
            return resp;

        }
    }

    static private class CADCompletionListener implements CompletionListener {
        final private Integer clientId;
        final private CARRecord car;

        CADCompletionListener(Integer clientId, CARRecord car) {
            this.clientId = clientId;
            this.car = car;
        }

        @Override
        public void onHandlerResponse(HandlerResponse response, Command command) {
            try {
                if (response.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                    car.changeState(CARRecord.Val.IDLE, response.hasErrorMessage() ? "" : response.getMessage(), 0, clientId);
                } else {
                    car.changeState(CARRecord.Val.ERR, response.hasErrorMessage() ? "" : response.getMessage(), -1, clientId);
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private CadState state = CADRecordImpl.CadState.CLEAR;


    private static String[] letters = new String[]{"A", "B", "C", "D", "E"};
    private CommandSender cs;
    private SequenceCommand seqCom;
    private EpicsCad epicsCad;
    private ChannelAccessServer cas;

    private String prefix;
    private String name;
    private Integer numAttributes;
    private List<String> attributeNames = new ArrayList<String>();
    private CARRecord car;

    protected CADRecordImpl(@Requires ChannelAccessServer cas,
                            @Requires CommandSender cs,
                            @Property(name = "prefix", value = "INVALID", mandatory = true) String prefix,
                            @Property(name = "name", value = "INVALID", mandatory = true) String name,
                            @Property(name = "numAttributes", value = "0", mandatory = true) Integer numAttributes) {
        if (numAttributes > letters.length) {
            throw new IllegalArgumentException("Number of attributes must be less or equal than " + letters.length);
        }
        this.cs = cs;
        this.numAttributes = numAttributes;
        this.cas = cas;
        this.prefix = prefix.toLowerCase();
        this.name = name.toLowerCase();
        seqCom = SequenceCommand.valueOf(name.toUpperCase());
        for (int i = 0; i < numAttributes; i++) {
            attributeNames.add(letters[i]);
        }
        epicsCad = new EpicsCad();
        car = new CARRecord(cas, prefix.toLowerCase() + ":" + name.toLowerCase() + "C");
        LOG.info("Constructor");
    }

    @Validate
    public synchronized void start() {
        LOG.info("Validate");

        epicsCad.start(cas, prefix, name, new AttributeListener(), new DirListener(), attributeNames);
        car.start();
    }

    @Invalidate
    public synchronized void stop() {
        LOG.info("InValidate");
        epicsCad.stop(cas);
        car.stop();

    }

    private synchronized CadState processDir(Dir dir){
        LOG.info("State: "+state+" Directive: "+dir);
        CadState newState = state.processDir(dir, epicsCad, cs, seqCom, car);
        LOG.info("State: "+newState);
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
    CadState getState(){
        return state;
    }

}
