package edu.gemini.epics;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class NewEpicsReaderTest {
    private NewEpicsReader epicsReader;

    @Test
    public void testGetDoubleValue() {
        // This may be problematic
        EpicsChannel<Double> channel = epicsReader.getChannel("tst:tst");

        assertEquals(1.0, channel.getValue(), 0.0);
        assertTrue(channel.isValid());
    }

    @Test
    public void testGetDoubleValueInArray() {
        EpicsChannelArray<Double> channel = epicsReader.getArrayChannel("tst:tst");

        assertTrue(channel.isValid());
        Double[] channelArray = channel.getValue();
        assertEquals(2, channelArray.length);

        // Get Value returns always the first item if it is an array
        assertEquals(1.0, channelArray[0], 0.0);
        assertEquals(1.1, channelArray[1], 0.0);
    }
}
