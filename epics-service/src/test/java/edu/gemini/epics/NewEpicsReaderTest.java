package edu.gemini.epics;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class NewEpicsReaderTest {
    private NewEpicsReader epicsReader;

    @Test
    public void testGetDoubleValue() {
        // This may be problematic
        EpicsChannel<Double> channel = epicsReader.getChannel("tst:tst");

        assertEquals(1.0, channel.getValue(), 0.0);
        assertFalse(channel.isArray());
        assertTrue(channel.isValid());

        // Get array returns the first value anyway
        assertEquals(1.0, channel.getArrayValue(0), 0.0);
        assertEquals(0, channel.getArraySize());
    }

    @Test
    public void testGetDoubleValueInArray() {
        EpicsChannel<Double> channel = epicsReader.getChannel("tst:tst");

        assertTrue(channel.isArray());
        assertTrue(channel.isValid());
        assertEquals(2, channel.getArraySize());

        // Get Value returns always the first item if it is an array
        assertEquals(1.0, channel.getValue(), 0.0);

        assertEquals(2.0, channel.getArrayValue(1), 0.0);
    }
}
