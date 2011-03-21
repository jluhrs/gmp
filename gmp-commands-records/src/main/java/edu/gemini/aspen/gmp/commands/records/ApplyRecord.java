package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.ChannelAccessServer;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
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
public class ApplyRecord extends Record{

    @Override
    protected boolean processDir(Dir dir) throws CAException {
             if (dir == Dir.MARK) {
            return false;
        }
        setMessage("");
        if (dir == Dir.START) {
            incAndGetClientId();
            if (!processDir(Dir.PRESET)) {
                return false;
            }
        }
        //set clid and dir on all CADs
        int id = getClientId();
        boolean error=false;
        for(CADRecord cad:cads){
            cad.setClid(id);
            cad.setDir(dir);

            //wait for VAL change, use a listener
            int cadVal = cad.getVal().get(0);
            if(cadVal<0){
                val.setValue(cadVal);
                setMessage(cad.getMess().get(0));
                error=true;
                break;
            }else{
                val.setValue(id);
            }
        }
        return !error;
    }




    //in
    /**
     * The value of this field is passed to all OUTx output links. If the directive is START, the directive PRESET is
     * first passed to all output links. If the directive is MARK, it is not sent to the OUTx fields and processing stops.
     */
    //private Channel<Dir> dir;

    //out
    /**
     * This is the return value from the input links. If any link returns a non-zero, processing stops and the last
     * value is returned. If all links return zero, the value of the client ID field (CLID) is returned.
     */
//    private Channel<Long> val;
    //private Channel<Integer> val;
    /**
     * This is the return message from an INMx input link. If the return value is 0, this field is empty. Otherwise, it
     * reads the error message from the INMx link.
     */
    //private Channel<String> mess;
    /**
     * This is the old message string.
     */
    //private Channel<String> omss;
    /**
     * This number is incremented every time a directive is loaded. The value is passed to all OCLx output links.
     */
//    private Channel<Long> clid;
    //private Channel<Integer> clid;


    //private CARRecord car;
    private List<CADRecord> cads = new ArrayList<CADRecord>();

    //@Requires
    //private ChannelAccessServer cas;

    private ApplyRecord(@Requires ChannelAccessServer cas) {
        super(cas);
        LOG.info("Constructor");

    }
    @Property(name = "prefix", value = "INVALID", mandatory = true)
    private String myPrefix;
    @Property(name = "recordname", value = "INVALID", mandatory = true)
    private String myRecordname;

    @Validate
    public void start() {
        LOG.info("Validate");
        try {
            super.start(myPrefix,myRecordname);

            clid = cas.createChannel(prefix + recordname +".CLID", 0);


        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        LOG.info("InValidate");
        super.stop();
        cas.destroyChannel(clid);
//        for(CADRecord cad:cads){
//            cad.stop();
//        }
    }

    @Bind(aggregate=true,optional=true)
    public void bindCAD(CADRecord cad){
        LOG.info("Bind");

            cads.add(cad);
    }

    @Unbind(aggregate=true,optional=true)
    public void unBindCAD(CADRecord cad){
        LOG.info("Unbind");

            cads.remove(cad);
    }
    private int incAndGetClientId() throws CAException {
        int value = getClientId();
        clid.setValue(++value);
        return value;

    }

}
