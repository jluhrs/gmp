package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.apache.felix.ipojo.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ApplyRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
@Component
@Instantiate
public class ApplyRecord {
    private static final Logger LOG = Logger.getLogger(ApplyRecord.class.getName());
        private class TestChannelListener implements ChannelListener {
        public TestChannelListener() {
        }

        @Override
        public void valueChange(DBR dbr) {
            try {
                LOG.info("Received: " + ((String[])dbr.convert(DBRType.STRING).getValue())[0]);
                car.changeState(CARRecord.Val.BUSY);
                car.changeState(CARRecord.Val.IDLE);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
    enum Dir{
        MARK,
        CLEAR,
        PRESET,
        START,
        STOP
    }

    private Channel<Dir> dir;
    private Channel<String> omss;
    private CARRecord car;
    private CADRecord cad;

    @Requires
    private ChannelAccessServer cas;

    private ApplyRecord() {


    }

    @Validate
    public void start() {
        try {
            dir = cas.createChannel("gpi:apply.DIR", Dir.CLEAR);
            dir.registerListener(new TestChannelListener());
            omss = cas.createChannel("gpi:apply.OMSS", "");
            car = new CARRecord(cas);
            car.start();
            cad = new CADRecord(cas,3);
            cad.start();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Invalidate
    public void stop(){
        cas.destroyChannel(dir);
        cas.destroyChannel(omss);
        car.stop();
        cad.stop();
    }
}
