package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class CADRecordImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */ //todo:make listeners, and maybe records, thread safe
@Component
@Provides
public class CADRecordImpl extends Record implements CADRecord {
    private static char[] letters = new char[]{'A', 'B', 'C', 'D', 'E'};

    @Override
    public void setClid(Integer id) throws CAException {
        clid.setValue(id);
    }

    @Override
    public void setDir(Dir d) throws CAException {
        dir.setValue(d);
    }

    @Override
    public Integer getVal() throws CAException {
        return val.getFirst();
    }

    @Override
    public String getMess() throws CAException {
        return mess.getFirst();
    }

    @Override
    public void registerValListener(ChannelListener listener) {
        val.registerListener(listener);
    }

    @Override
    public void unRegisterValListener(ChannelListener listener) {
        val.unRegisterListener(listener);
    }

    @Override
    public void registerCARListener(CARRecord.CARListener listener) {
        car.registerListener(listener);
    }

    @Override
    public void unRegisterCARListener(CARRecord.CARListener listener) {
        car.unRegisterListener(listener);
    }

    @Override
    public CARRecord getCAR(){
        return car;
    }


    private class AttributeListener implements ChannelListener {

        @Override
        public void valueChange(DBR dbr) {
            LOG.info("Attribute Received: " + ((String[]) dbr.getValue())[0]);
            try {
                processInternal(Dir.MARK);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    Channel<Integer> ocid;

    private Channel<Integer> mark;

    /**
     * The 20 string input arguments.
     */
    private List<Channel<String>> attributes = new ArrayList<Channel<String>>();

    private Integer numAttributes;


    private CommandSender cs;
    private SequenceCommand seqCom;
    protected CADRecordImpl(@Requires ChannelAccessServer cas,
                          @Requires CommandSender cs,
                          @Property(name = "prefix", value = "INVALID", mandatory = true)String prefix,
                          @Property(name = "name", value = "INVALID", mandatory = true)String command,
                          @Property(name = "numAttributes", value = "0", mandatory = true)Integer numAttributes) {
        super(cas,prefix,command.toLowerCase());
        if (numAttributes > letters.length) {
            throw new IllegalArgumentException("Number of attributes must be less or equal than " + letters.length);
        }
        this.cs=cs;
        this.numAttributes=numAttributes;
        seqCom=SequenceCommand.valueOf(name.toUpperCase());
        LOG.info("Constructor");
    }


    @Override
    protected boolean processDir(Dir dir) throws CAException {
        car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());
        HandlerResponse response = null;
        if (getState() == 0 &&
                ((dir == ApplyRecord.Dir.PRESET) ||
                        (dir == ApplyRecord.Dir.START) ||
                        (dir == ApplyRecord.Dir.STOP))) {
            val.setValue(0);
            car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
            return true;
        } else {
            response=processInternal(dir);
            if (!response.getResponse().equals(HandlerResponse.Response.ERROR)&&
                    !response.getResponse().equals(HandlerResponse.Response.NOANSWER)) { //no error
                setIfDifferent(mess, "");
                val.setValue(0);
                if(response.getResponse().equals(HandlerResponse.Response.ACCEPTED)||
                        response.getResponse().equals(HandlerResponse.Response.COMPLETED)){ //action ready
                    car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
                }
                return true;
            } else { //error
                val.setValue(-1);
                mess.setValue(response.hasErrorMessage()?response.getMessage():"");
                car.changeState(CARRecord.Val.ERR, ((String[]) mess.getValue().getValue())[0], ((int[]) val.getValue().getValue())[0], getClientId());
                return false;
            }
        }
    }

    private HandlerResponse processInternal(Dir dir) throws CAException {
        HandlerResponse response = HandlerResponse.NOANSWER;//to avoid inittializing to null;
        switch (dir) {
            case MARK://mark
                LOG.info("MARK: values: " + getInputValues());
                response = HandlerResponse.ACCEPTED;
                copyIcidToOcid();
                setState(1);
                break;
            case CLEAR://clear
                LOG.info("CLEAR: values: " + getInputValues());
                response = HandlerResponse.ACCEPTED;
                copyIcidToOcid();
                setState(0);
                break;
            case PRESET://preset
                LOG.info("PRESET: values: " + getInputValues());
                response = doActivity(Activity.PRESET);
                copyIcidToOcid();
                setState(2);
                break;
            case START://start
                LOG.info("START: values: " + getInputValues());
                if (getState() == 1) {
                    response = doActivity(Activity.PRESET);
                    copyIcidToOcid();
                    setState(2);
                    if (response.getResponse().equals(HandlerResponse.Response.ERROR) ||
                            response.getResponse().equals(HandlerResponse.Response.NOANSWER)) {
                        break;
                    }
                }
                response = doActivity(Activity.START);
                copyIcidToOcid();
                setState(0);
                break;
            case STOP://stop
                LOG.info("STOP: values: " + getInputValues());
                response = doActivity(Activity.CANCEL);//todo: is it ok to map STOP to CANCEL?
                copyIcidToOcid();
                setState(0);
                break;
        }
        return response;
    }
    @Validate
    public void start() {
        LOG.info("Validate");

        try {
            super.start();
            clid = cas.createChannel(prefix +":"+ name + ".ICID", 0);
            ocid = cas.createChannel(prefix +":"+ name + ".OCID", 0);
            mark = cas.createChannel(prefix +":"+ name + ".MARK", 0);
            for (int i = 0; i < numAttributes; i++) {
                Channel<String> ch = cas.createChannel(prefix +":"+ name + "." + letters[i], "");
                ch.registerListener(new AttributeListener());
                attributes.add(ch);

            }

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("InValidate");

        super.stop();
        cas.destroyChannel(clid);
        cas.destroyChannel(ocid);
        cas.destroyChannel(mark);
        for (Channel<?> ch : attributes) {
            cas.destroyChannel(ch);
        }
    }

    private int getState() throws CAException {
        DBR dbr = mark.getValue();
        return ((int[]) dbr.getValue())[0];
    }

    private void setState(int state) throws CAException {
        mark.setValue(state);
    }

    private List<String> getInputValues() throws CAException {
        List<String> values = new ArrayList<String>();
        for (Channel<String> ch : attributes) {
            values.add(((String[]) ch.getValue().getValue())[0]);
        }
        return values;
    }

    private HandlerResponse doActivity(Activity activity) throws CAException {
        return cs.sendCommand(new Command(seqCom,activity), new CADCompletionListener(getClientId()));
    }


    private void copyIcidToOcid() throws CAException {
        ocid.setValue(clid.getFirst());
    }

    private class CADCompletionListener implements CompletionListener {
        final private Integer clientId;

        CADCompletionListener(Integer clientId) {
            this.clientId = clientId;
        }

        @Override
        public void onHandlerResponse(HandlerResponse response, Command command) {
            try {
                if (response.getResponse().equals(HandlerResponse.Response.ERROR)) {
                    car.changeState(CARRecord.Val.ERR, response.hasErrorMessage()?"":response.getMessage(), -1, clientId);
                } else {
                    car.changeState(CARRecord.Val.IDLE, response.hasErrorMessage()?"":response.getMessage(), 0, clientId);
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
