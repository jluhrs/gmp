package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.epics.api.Channel;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.impl.EpicsReaderImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class EpicsIT
 *
 * @author Nicolas A. Barriga
 *         Date: 9/7/11
 */
public class EpicsIT {
    JCALibrary jca;
    CAJContext context;
    ChannelAccessServerImpl cas;
    EpicsReaderImpl reader;

    @Before
    public void setup() throws CAException {
        jca = JCALibrary.getInstance();
        context = (CAJContext) jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        cas = new ChannelAccessServerImpl();
        cas.start();
        reader = new EpicsReaderImpl(new EpicsService(context));
    }

    @Test
    public void testBasic() throws CAException {
        String name = "gpitest:test";
        Channel ch = cas.createChannel(name, 1);
        reader.bindChannel(name);
        assertTrue(reader.isChannelConnected(name));
        assertEquals(1, ((int[]) reader.getValue(name))[0]);
        reader.unbindChannel(name);
        cas.destroyChannel(ch);

    }

    @Test
    public void testLateBinding() throws CAException, InterruptedException {
        String name = "gpitest:test";
        reader.bindChannelAsync(name);
        assertFalse(reader.isChannelConnected(name));

        Channel ch = cas.createChannel(name, 1);
        Thread.sleep(200);

        assertTrue(reader.isChannelConnected(name));
        assertEquals(1, ((int[]) reader.getValue(name))[0]);

        reader.unbindChannel(name);
        cas.destroyChannel(ch);
    }

    @Ignore
    @Test
    public void testDisconnect() throws CAException, InterruptedException {
        String name = "gpitest:test";
        String name2 = "gpitest:test2";
        Channel ch2 = cas.createChannel(name2, 2);
        reader.bindChannelAsync(name, new ConnectionListener() {
            @Override
            public void connectionChanged(ConnectionEvent ev) {
                System.out.println("State: " + ev.isConnected());
            }
        });
        Channel ch = cas.createChannel(name, 1);
        reader.bindChannelAsync(name2);

        Thread.sleep(500);
        assertTrue(reader.isChannelConnected(name));
        assertTrue(reader.isChannelConnected(name2));

        cas.destroyChannel(ch);

        Thread.sleep(500);

        assertFalse(reader.isChannelConnected(name));
        assertTrue(reader.isChannelConnected(name2));
        assertEquals(2, ((int[]) reader.getValue(name2))[0]);

        reader.unbindChannel(name);
        reader.unbindChannel(name2);
        assertFalse(reader.isChannelConnected(name));
        assertFalse(reader.isChannelConnected(name2));
        cas.destroyChannel(ch2);
    }

    @Ignore
    @Test
    public void testIntermitentChannel() throws CAException, InterruptedException {
        String name = "gpitest:test";
        reader.bindChannelAsync(name);
        assertFalse(reader.isChannelConnected(name));

        Channel ch = cas.createChannel(name, 1);
        Thread.sleep(500);

        assertTrue(reader.isChannelConnected(name));
        assertEquals(1, ((int[]) reader.getValue(name))[0]);
        cas.destroyChannel(ch);

        Thread.sleep(500);

        assertFalse(reader.isChannelConnected(name));
        ch = cas.createChannel(name, 2);
        Thread.sleep(5000);

        assertTrue(reader.isChannelConnected(name));
        assertEquals(2, ((int[]) reader.getValue(name))[0]);

        reader.unbindChannel(name);
        cas.destroyChannel(ch);
    }

    @After
    public void teardown() throws CAException {
        reader.close();
        cas.stop();
    }
}
