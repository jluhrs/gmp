package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
import gov.aps.jca.CAException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This enum represent the three possible states a CAD record can be in, manages the transitions, and sends the commands.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/24/11
 */
enum CadState {
    CLEAR {
        @Override
        public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CarRecord car) {
            try {
                endProcessingNoCarUpdate(epicsCad);
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
        public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CarRecord car) {


            HandlerResponse resp;
            switch (dir) {
                case MARK:
                    try {
                        endProcessingNoCarUpdate(epicsCad);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return MARKED;
                case CLEAR:
                    try {
                        endProcessingNoCarUpdate(epicsCad);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return CLEAR;
                case PRESET:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.PRESET, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                            endProcessing(epicsCad, car);
                            return IS_PRESET;
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                            return CLEAR;
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
                            return CLEAR;
                        }
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        return CLEAR;
                    }
                case START:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.PRESET_START, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.STARTED)) {
                            endProcessingNoCarUpdate(epicsCad);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                            endProcessing(epicsCad, car);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
                        }
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return CLEAR;
                case STOP:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.CANCEL, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                            endProcessing(epicsCad, car);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
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
        public CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CarRecord car) {

            HandlerResponse resp;
            switch (dir) {
                case MARK:
                    try {
                        endProcessingNoCarUpdate(epicsCad);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return MARKED;
                case CLEAR:
                    try {
                        endProcessingNoCarUpdate(epicsCad);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return CLEAR;
                case PRESET:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.PRESET, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                            endProcessing(epicsCad, car);
                            return IS_PRESET;
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                            return CLEAR;
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
                            return CLEAR;
                        }
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        return CLEAR;
                    }
                case START:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.START, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.STARTED)) {
                            endProcessingNoCarUpdate(epicsCad);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                            endProcessing(epicsCad, car);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
                        }
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return CLEAR;
                case STOP:
                    try {
                        startProcessing(epicsCad, car);
                    } catch (CAException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    resp = doActivity(Activity.CANCEL, cs, seqCom, epicsCad.getClid(), car, epicsCad);
                    try {
                        if (resp.getResponse().equals(HandlerResponse.Response.ACCEPTED)) {
                            endProcessing(epicsCad, car);
                        } else if (resp.getResponse().equals(HandlerResponse.Response.ERROR)) {
                            endInError(epicsCad, car, resp.getMessage());
                        } else {
                            endInError(epicsCad, car, "Received unexpected " + resp.getResponse().toString() + " response.");
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
    private static final Logger LOG = Logger.getLogger(CadState.class.getName());

    /**
     * Process a given directive, send a command, update CAD and CAR.
     *
     * @param dir      directive to process
     * @param epicsCad the CAD that received the directive
     * @param cs       the command sender to use
     * @param seqCom   the sequence command this CAD represents
     * @param car      the CAR associated with the given CAD
     * @return the new state after processing the directive
     */
    public abstract CadState processDir(Dir dir, EpicsCad epicsCad, CommandSender cs, SequenceCommand seqCom, CarRecord car);

    private static HandlerResponse doActivity(Activity activity, CommandSender cs, SequenceCommand seqCom, Integer id, CarRecord car, EpicsCad epicsCad) {
        HandlerResponse resp;
        Map<String, String> config = epicsCad.getConfig();
        if (!config.isEmpty()) {
            DefaultConfiguration.Builder builder = DefaultConfiguration.configurationBuilder();
            for (String name : config.keySet()) {
                builder.withConfiguration(name, config.get(name));
            }
            resp = cs.sendCommand(new Command(seqCom, activity, builder.build()), new CadCompletionListener(id, car));
        } else {
            resp = cs.sendCommand(new Command(seqCom, activity), new CadCompletionListener(id, car));
        }
        LOG.info("Activity: " + activity + " ClientID: " + id + " Response: " + resp.getResponse().toString());
        return resp;
    }

    private static void startProcessing(EpicsCad epicsCad, CarRecord car) throws CAException {
        car.setBusy(epicsCad.getClid());
    }

    private static void endProcessing(EpicsCad epicsCad, CarRecord car) throws CAException {
        epicsCad.setVal(0);
        epicsCad.post();
        car.setIdle(epicsCad.getClid());
    }

    private static void endProcessingNoCarUpdate(EpicsCad epicsCad) throws CAException {
        epicsCad.setVal(0);
        epicsCad.post();
    }

    private static void endInError(EpicsCad epicsCad, CarRecord car, String errorMessage) throws CAException {
        epicsCad.setVal(-1);
        epicsCad.setMess(errorMessage);
        epicsCad.post();
        car.changeState(CarRecord.Val.ERR, errorMessage, -1, epicsCad.getClid());
    }
}
