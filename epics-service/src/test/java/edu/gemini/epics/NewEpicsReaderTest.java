package edu.gemini.epics;

import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.CAException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class NewEpicsReaderTest {
    private NewEpicsReader epicsReader;

    @Test
    public void testGetDoubleValue() throws CAException {
        // This may be problematic
        ReadOnlyChannel<Double> channel = epicsReader.getChannel("tst:tst");

        assertEquals(1.0, channel.getFirst(), 0.0);
        assertTrue(channel.isValid());
    }

    @Test
    public void testGetDoubleValueInArray() throws CAException {
        ReadOnlyChannel<Double> channel = epicsReader.getChannel("tst:tst");

        assertTrue(channel.isValid());
        List<Double> channelArray = channel.getAll();
        assertEquals(2, channelArray.size());

        // Get Value returns always the first item if it is an array
        assertEquals(1.0, channelArray.get(0), 0.0);
        assertEquals(1.1, channelArray.get(1), 0.0);
    }
}
