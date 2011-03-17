package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class CADRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
public class CADRecord {
       private static char[] letters=new char[]{'A','B','C'};
       private static final Logger LOG = Logger.getLogger(CADRecord.class.getName());
        private class TestChannelListener implements ChannelListener {
        public TestChannelListener() {
        }

        @Override
        public void valueChange(DBR dbr) {
            try {
                LOG.info("Received: " + ((String[]) dbr.convert(DBRType.STRING).getValue())[0]);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
    private Channel<ApplyRecord.Dir> dir;
    private Channel<String> mess;
    private Channel<String> icid;
    private List<Channel<?>> attributes = new ArrayList<Channel<?>>();

    @Requires
    private ChannelAccessServer cas;

    @Property//todo:complete annotation
    private int numAttr;

    private CADRecord() {

    }
    public CADRecord(ChannelAccessServer cas, int numAttr){
        if(numAttr>letters.length){
            throw new IllegalArgumentException("Number of attributes must be less or equal than "+letters.length);
        }
        this.numAttr=numAttr;
        this.cas=cas;
    }
    @Validate
    public void start() {
        try {
            dir = cas.createChannel("gpi:observe.DIR", ApplyRecord.Dir.CLEAR);
            dir.registerListener(new TestChannelListener());
            mess = cas.createChannel("gpi:observe.MESS", "");
            icid = cas.createChannel("gpi:observe.ICID", "");
            for(int i=0;i<numAttr;i++){
                attributes.add(cas.createChannel("gpi:observe."+letters[i],1));
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        cas.destroyChannel(dir);
        cas.destroyChannel(mess);
        cas.destroyChannel(icid);
        for(Channel<?> ch:attributes){
            cas.destroyChannel(ch);
        }
    }
}
