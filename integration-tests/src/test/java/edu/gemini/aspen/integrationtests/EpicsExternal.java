package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.cas.*;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.impl.EpicsReaderImpl;
import gov.aps.jca.*;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class EpicsExternal. This test can't be ran automatically, but I commit it nonetheless because it is useful to check connectivity with external servers.
 *
 * @author Nicolas A. Barriga
 *         Date: 9/27/11
 */
public class EpicsExternal {
    private static final Logger LOG = Logger.getLogger(EpicsExternal.class.getName());
    JCALibrary jca;
    CAJContext context;
    EpicsReaderImpl reader;
    EpicsService es;
    CyclicBarrier barrier = new CyclicBarrier(2);
    long timeout = 15;

    String name = "ws:wsDew";

    @Before
    public void setup() throws CAException {
        jca = JCALibrary.getInstance();
        es = new EpicsService("172.17.2.255");
        es.startService();
        reader = new EpicsReaderImpl(es);
        context = es.getJCAContext();

    }

    @After
    public void teardown() throws CAException {
        reader.close();
        es.stopService();
    }

    @Ignore
    @Test
    public void testIntermitentChannel() throws CAException, InterruptedException, BrokenBarrierException, java.util.concurrent.TimeoutException {
        reader.bindChannelAsync(name, new ConnectionListener() {
            @Override
            public void connectionChanged(ConnectionEvent ev) {
                LOG.info("State change to: " + ev.isConnected());
            }
        });
        Thread.sleep(timeout * 100);
        assertTrue(reader.isChannelConnected(name));
        LOG.info("Value read: " + ((double[]) reader.getValue(name))[0]);

        LOG.info("Now disconnect server");

        Thread.sleep(timeout * 1000);

        LOG.info("Connected? " + reader.isChannelConnected(name));
        try {
            LOG.info("Value read: " + ((double[]) reader.getValue(name))[0]);
        } catch (edu.gemini.epics.EpicsException ex) {
            //ok
        }
        LOG.info("Connected? " + reader.isChannelConnected(name));

        LOG.info("Now connect server");

        Thread.sleep(timeout * 1000);

        LOG.info("Connected? " + reader.isChannelConnected(name));
        LOG.info("Value read (should be different than the one before): " + ((double[]) reader.getValue(name))[0]);


        reader.unbindChannel(name);
    }


}
