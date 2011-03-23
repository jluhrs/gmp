package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.ChannelAccessServer;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
@Component
public class ApplyRecord extends Record {

    @Override
    protected boolean processDir(Dir dir) throws CAException {
        if (dir == Dir.START) {
            incAndGetClientId();
        }
        car.changeState(CARRecord.Val.BUSY, "", 0, getClientId());
        boolean retVal = processInternal(dir);

        if (retVal) {
            car.changeState(CARRecord.Val.IDLE, "", 0, getClientId());
        } else {
            car.changeState(CARRecord.Val.ERR, ((String[]) mess.getValue().getValue())[0], ((int[]) val.getValue().getValue())[0], getClientId());
        }
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
        for (CADRecord cad : cads) {
            cad.setClid(id);
            cad.setDir(dir);

            //wait for VAL change, use a listener
            int cadVal = cad.getVal();
            if (cadVal < 0) {
                val.setValue(cadVal);
                setMessage(cad.getMess());
                error = true;
                break;
            } else {
                val.setValue(id);
            }
        }
        return !error;
    }

    private List<CADRecord> cads = new ArrayList<CADRecord>();

    protected ApplyRecord(@Requires ChannelAccessServer cas,
                        @Property(name = "prefix", value = "INVALID", mandatory = true)String prefix) {
        super(cas,prefix,"apply");
        LOG.info("Constructor");

    }


    @Validate
    public void start() {
        LOG.info("Validate");
        try {
            super.start();

            clid = cas.createChannel(prefix +":"+ name + ".CLID", 0);

        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("InValidate");
        super.stop();
        cas.destroyChannel(clid);
//        for(CADRecord cad:cads){
//            cad.stop();
//        }
    }

    @Bind(aggregate = true, optional = true)
    public void bindCAD(CADRecord cad) {
        LOG.info("Bind");

        cads.add(cad);
    }

    @Unbind(aggregate = true, optional = true)
    public void unBindCAD(CADRecord cad) {
        LOG.info("Unbind");

        cads.remove(cad);
    }

    private int incAndGetClientId() throws CAException {
        int value = getClientId();
        clid.setValue(++value);
        return value;

    }

}
