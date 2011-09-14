package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import gov.aps.jca.*;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class BareEpicsTest
 *
 * @author Nicolas A. Barriga
 *         Date: 9/12/11
 */
public class BareEpicsTest {
    private static final Logger LOG = Logger.getLogger(BareEpicsTest.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext = null;
    private ExecutorService executor;
    private final JCALibrary jca = JCALibrary.getInstance();

    private void setupServer() throws CAException {
        server = new DefaultServerImpl();
        serverContext = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
        executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            public void run() {
                try {
                    serverContext.run(0);
                } catch (IllegalStateException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }

            }
        });
    }

    @Before
    public void before() throws CAException, TimeoutException, InterruptedException {
        setupServer();
    }

    CyclicBarrier barrier = new CyclicBarrier(2);

    @Ignore
    @Test
    public void destroyAndReCreateVariable() throws CAException, InterruptedException, TimeoutException, BrokenBarrierException, java.util.concurrent.TimeoutException {

        //create process variable and register it
        MemoryProcessVariable pv = new MemoryProcessVariable("test", null, DBR_Int.TYPE, new int[]{2});
        server.registerProcessVaribale(pv);

        //Create a client context
        Context clientContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);

        //Create a channel
        Channel ch = clientContext.createChannel("test", new ConnectionListener() {
            @Override
            public void connectionChanged(ConnectionEvent ev) {
                LOG.info("State change to: " + ev.isConnected());
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                } catch (BrokenBarrierException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        //wait at most 5 seconds for channel to change state (hopefully to CONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is connected
        assertEquals(Channel.ConnectionState.CONNECTED, ch.getConnectionState());
        //check that value can be read
        DBR dbr = ch.get();
        clientContext.pendIO(0);
        assertEquals(2, ((int[]) dbr.getValue())[0]);

        //unregister process variable and destroy it
        server.unregisterProcessVaribale("test");
        pv.destroy();

        //wait at most 5 seconds for channel to change state (hopefully to DISCONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is not connected
        assertNotSame(Channel.ConnectionState.CONNECTED, ch.getConnectionState());

        //create new process variable and register it with server
        pv = new MemoryProcessVariable("test", null, DBR_Int.TYPE, new int[]{1});
        server.registerProcessVaribale(pv);

        //wait at most 5 seconds for channel to change state (hopefully to CONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is connected
        assertEquals(Channel.ConnectionState.CONNECTED, ch.getConnectionState());
        //check that value can be read, and it is the new value
        dbr = ch.get();
        clientContext.pendIO(0);
        assertEquals(1, ((int[]) dbr.getValue())[0]);

    }

    @Ignore
    @Test
    public void withDestroyAndCreateServer() throws CAException, InterruptedException, TimeoutException, BrokenBarrierException, java.util.concurrent.TimeoutException {

        //create process variable and register it
        MemoryProcessVariable pv = new MemoryProcessVariable("test", null, DBR_Int.TYPE, new int[]{2});
        server.registerProcessVaribale(pv);

        //Create a client context
        Context clientContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);

        //Create a channel
        Channel ch = clientContext.createChannel("test", new ConnectionListener() {
            @Override
            public void connectionChanged(ConnectionEvent ev) {
                LOG.info("State change to: " + ev.isConnected());
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                } catch (BrokenBarrierException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        //wait at most 5 seconds for channel to change state (hopefully to CONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is connected
        assertEquals(Channel.ConnectionState.CONNECTED, ch.getConnectionState());
        //check that value can be read
        DBR dbr = ch.get();
        clientContext.pendIO(0);
        assertEquals(2, ((int[]) dbr.getValue())[0]);

        //unregister process variable and destroy it
        server.unregisterProcessVaribale("test");
        pv.destroy();

        //destroy the server and start it up again
        tearDownServer();
        setupServer();


        //wait at most 5 seconds for channel to change state (hopefully to DISCONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is not connected
        assertNotSame(Channel.ConnectionState.CONNECTED, ch.getConnectionState());

        //create new process variable and register it with server
        pv = new MemoryProcessVariable("test", null, DBR_Int.TYPE, new int[]{1});
        server.registerProcessVaribale(pv);

        //wait at most 5 seconds for channel to change state (hopefully to CONNECTED)
        barrier.await(5, TimeUnit.SECONDS);
        //check that channel is connected
        assertEquals(Channel.ConnectionState.CONNECTED, ch.getConnectionState());
        //check that value can be read, and it is the new value
        dbr = ch.get();
        clientContext.pendIO(0);
        assertEquals(1, ((int[]) dbr.getValue())[0]);

    }

    private void tearDownServer() throws CAException {
        executor.shutdown();
        serverContext.destroy();
    }

    @After
    public void after() throws CAException {
        tearDownServer();
    }
}
