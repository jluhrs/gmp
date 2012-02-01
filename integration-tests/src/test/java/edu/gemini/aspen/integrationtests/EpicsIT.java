package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.impl.EpicsReaderImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.TimeoutException;
import org.junit.After;
import org.junit.Before;
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
    EpicsReader reader;

    @Before
    public void setup() throws CAException {
        jca = JCALibrary.getInstance();
        context = (CAJContext) jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        cas = new ChannelAccessServerImpl();
        cas.start();
        reader = new EpicsReaderImpl(new EpicsService(context));
    }

    @Test
    public void testBasic() throws CAException, TimeoutException {
        String name = "gpitest:test";
        Channel ch = cas.createChannel(name, 1);
        ReadOnlyClientEpicsChannel clientChannel=reader.getIntegerChannel(name);
        assertTrue(clientChannel.isValid());
        assertEquals(1, clientChannel.getFirst());
        clientChannel.destroy();
        cas.destroyChannel(ch);

    }

    @Test
    public void testLateBinding() throws CAException, InterruptedException, TimeoutException {
        String name = "gpitest:test";
        ReadOnlyClientEpicsChannel clientChannel=reader.getChannelAsync(name);
        assertFalse(clientChannel.isValid());

        Channel ch = cas.createChannel(name, 1);
        Thread.sleep(200);

        assertTrue(clientChannel.isValid());
        assertEquals(1, clientChannel.getFirst());

        clientChannel.destroy();
        cas.destroyChannel(ch);
    }


    @After
    public void teardown() throws CAException {
        cas.stop();
    }
}
