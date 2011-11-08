package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.ClientEpicsChannel;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.impl.NewEpicsReaderImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewEpicsReaderTest {
    private NewEpicsReaderImpl epicsReader;
    private EpicsService epicsService;
    private JCALibrary jca;
    private CAJContext context;
    private ChannelAccessServerImpl cas;
    private final String doubleName = "giapitest:double";
    private Channel doubleChannel;

    @Before
    public void setup() throws CAException {
        jca = JCALibrary.getInstance();
        context = (CAJContext) jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        cas = new ChannelAccessServerImpl();
        cas.start();
        doubleChannel = cas.createChannel(doubleName, 1.0);
        epicsReader = new NewEpicsReaderImpl(new EpicsService(context));
    }

    @After
    public void tearDown() throws CAException {
        cas.destroyChannel(doubleChannel);
        cas.stop();
        context.destroy();
    }

    @Test
    public void testGetWrongUnderlyingType() throws CAException, InterruptedException {
        try {
            ClientEpicsChannel<Integer> channel = epicsReader.getIntegerChannel(doubleName);
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Channel " + doubleName + " can be connected to, but is of incorrect type.");
        }
    }


    @Test
    public void testGetWrongType() throws CAException, InterruptedException {

        ClientEpicsChannel<Double> channel = epicsReader.getDoubleChannel(doubleName);
        assertTrue(channel.isValid());
        try {
            ClientEpicsChannel<Integer> channel2 = epicsReader.getIntegerChannel(doubleName);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Channel " + doubleName + " already exists, but is of incorrect type.");
        }
        epicsReader.destroyChannel(channel);
    }

    @Test
    public void testGetDoubleValue() throws CAException, InterruptedException {

        ClientEpicsChannel<Double> channel = epicsReader.getDoubleChannel(doubleName);
        assertTrue(channel.isValid());
        assertEquals((Double) 1.0, channel.getFirst());
        channel.destroy();
    }

}
