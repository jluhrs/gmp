package edu.gemini.aspen.integrationtests;

import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.impl.EpicsReaderImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

//import java.lang.ref.Reference;
//import java.lang.ref.WeakReference;

public class NewEpicsReaderTest extends NewEpicsTestBase {
    private EpicsReader epicsReader;

    @Before
    public void setup() throws CAException {
        super.setup();
        epicsReader = new EpicsReaderImpl(new EpicsService(context));
    }

    @Test
    public void testGetWrongUnderlyingType() {
        try {
            ReadOnlyClientEpicsChannel<Integer> channel = epicsReader.getIntegerChannel(doubleName);
        } catch (IllegalArgumentException ex) {
            assertEquals("Channel " + doubleName + " can be connected to, but is of incorrect type.", ex.getMessage());
        }
    }


    @Test
    public void testGetWrongType() throws CAException {

        ReadOnlyClientEpicsChannel<Double> channel = epicsReader.getDoubleChannel(doubleName);
        assertTrue(channel.isValid());
        try {
            ReadOnlyClientEpicsChannel<Integer> channel2 = epicsReader.getIntegerChannel(doubleName);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Channel " + doubleName + " can be connected to, but is of incorrect type.", ex.getMessage());
        }
        epicsReader.destroyChannel(channel);
    }

    @Test
    public void testGetWrongTypeAsync() throws CAException, InterruptedException {

        ReadOnlyClientEpicsChannel<?> channel = epicsReader.getChannelAsync(doubleName);
        Thread.sleep(500);
        assertTrue(channel.isValid());
        try {
            ReadOnlyClientEpicsChannel<Integer> channel2 = epicsReader.getIntegerChannel(doubleName);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Channel " + doubleName + " can be connected to, but is of incorrect type.", ex.getMessage());
        }
        epicsReader.destroyChannel(channel);
    }

    @Test
    public void testGetValues() throws CAException, TimeoutException {
        ReadOnlyClientEpicsChannel<Double> dChannel = epicsReader.getDoubleChannel(doubleName);
        ReadOnlyClientEpicsChannel<Integer> iChannel = epicsReader.getIntegerChannel(intName);
        ReadOnlyClientEpicsChannel<Float> fChannel = epicsReader.getFloatChannel(floatName);
        ReadOnlyClientEpicsChannel<String> sChannel = epicsReader.getStringChannel(stringName);
        assertTrue(dChannel.isValid());
        assertEquals((Double) 1.0, dChannel.getFirst());
        dChannel.destroy();
        assertTrue(iChannel.isValid());
        assertEquals((Integer) 1, iChannel.getFirst());
        iChannel.destroy();
        assertTrue(fChannel.isValid());
        assertEquals((Float) 1.0f, fChannel.getFirst());
        fChannel.destroy();
        assertTrue(sChannel.isValid());
        assertEquals("1", sChannel.getFirst());
        sChannel.destroy();
    }

    @Test
    public void testGetValueAsync() throws CAException, InterruptedException, TimeoutException {
        ReadOnlyClientEpicsChannel<?> dChannel = epicsReader.getChannelAsync(doubleName);
        Thread.sleep(500);
        assertTrue(dChannel.isValid());
        assertEquals(1.0, dChannel.getFirst());
        dChannel.destroy();
    }


//    @Test
//    public void testAutoDestroy() throws CAException, TimeoutException, InterruptedException {
//        ReadOnlyClientEpicsChannel<Double> dChannel = epicsReader.getDoubleChannel(doubleName);
//
//        assertTrue(dChannel.isValid());
//        assertEquals((Double) 1.0, dChannel.getFirst());
//
//
//        dChannel = null;
//        System.gc();
//        Thread.sleep(100);
//        //todo:check if destroyed. how?
//    }
//
//    @Test
//    public void testReconnect() throws CAException, TimeoutException, InterruptedException {
//        ReadOnlyClientEpicsChannel<Double> dChannel = epicsReader.getDoubleChannel(doubleName);
//
//        assertTrue(dChannel.isValid());
//        assertEquals((Double) 1.0, dChannel.getFirst());
//
//        ReadOnlyClientEpicsChannel<Double> ddChannel = epicsReader.getDoubleChannel(doubleName);
//
//
//        dChannel = null;
//        System.gc();
//        Thread.sleep(100);
//        //todo:check if destroyed. how?
//
//        assertTrue(ddChannel.isValid());
//        assertEquals((Double) 1.0, ddChannel.getFirst());
//
//        ddChannel = null;
//        System.gc();
//        Thread.sleep(100);
//        //todo:check if destroyed. how?
//
//    }
//
//        @Test
//    public void testDoubleDestroy() throws CAException, TimeoutException, InterruptedException {
//        ReadOnlyClientEpicsChannel<Double> dChannel = epicsReader.getDoubleChannel(doubleName);
//
//        assertTrue(dChannel.isValid());
//        assertEquals((Double) 1.0, dChannel.getFirst());
//
//        ReadOnlyClientEpicsChannel<Double> ddChannel = epicsReader.getDoubleChannel(doubleName);
//
//        dChannel.destroy();
//
//        assertTrue(ddChannel.isValid());
//        assertEquals((Double) 1.0, ddChannel.getFirst());
//
//            dChannel = null;
//        System.gc();
//        Thread.sleep(100);
//        //todo:check if destroyed. how?
//
//        assertTrue(ddChannel.isValid());
//        assertEquals((Double) 1.0, ddChannel.getFirst());
//
//        ddChannel = null;
//        System.gc();
//        Thread.sleep(100);
//        //todo:check if destroyed. how?
//
//    }
}
