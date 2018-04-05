package edu.gemini.aspen.integrationtests;

import edu.gemini.epics.EpicsService;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.epics.ReadWriteClientEpicsChannel;
import edu.gemini.epics.impl.EpicsWriterImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class NewEpicsWriterTest
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
public class NewEpicsWriterTest extends NewEpicsTestBase {
    private EpicsWriter epicsWriter;

    @Before
    public void setup() throws CAException {
        super.setup();
        epicsWriter = new EpicsWriterImpl(new EpicsService(context));
    }

    @Test
    public void testWriteDouble() throws CAException, TimeoutException {
        ReadWriteClientEpicsChannel<Double> ch = epicsWriter.getDoubleChannel(doubleName);
        assertTrue(ch.isValid());
        assertEquals((Double) 1.0, ch.getFirst());
        assertEquals((Double) 1.0, doubleChannel.getFirst());

        ch.setValue(2.0);

        assertEquals((Double) 2.0, ch.getFirst());
        assertEquals((Double) 2.0, doubleChannel.getFirst());
    }

    @Test
    public void testWriteInteger() throws CAException, TimeoutException {
        ReadWriteClientEpicsChannel<Integer> ch = epicsWriter.getIntegerChannel(intName);
        assertTrue(ch.isValid());
        assertEquals((Integer) 1, ch.getFirst());
        assertEquals((Integer) 1, intChannel.getFirst());

        ch.setValue(2);

        assertEquals((Integer) 2, ch.getFirst());
        assertEquals((Integer) 2, intChannel.getFirst());
    }

    @Test
    public void testWriteFloat() throws CAException, TimeoutException {
        ReadWriteClientEpicsChannel<Float> ch = epicsWriter.getFloatChannel(floatName);
        assertTrue(ch.isValid());
        assertEquals((Float) 1.0f, ch.getFirst());
        assertEquals((Float) 1.0f, floatChannel.getFirst());

        ch.setValue(2.0f);

        assertEquals((Float) 2.0f, ch.getFirst());
        assertEquals((Float) 2.0f, floatChannel.getFirst());
    }

    @Test
    public void testWriteString() throws CAException, TimeoutException {
        ReadWriteClientEpicsChannel<String> ch = epicsWriter.getStringChannel(stringName);
        assertTrue(ch.isValid());
        assertEquals("1", ch.getFirst());
        assertEquals("1", stringChannel.getFirst());

        ch.setValue("2");

        assertEquals("2", ch.getFirst());
        assertEquals("2", stringChannel.getFirst());
    }
}
