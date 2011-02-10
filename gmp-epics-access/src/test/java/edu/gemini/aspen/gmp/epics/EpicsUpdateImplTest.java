package edu.gemini.aspen.gmp.epics;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EpicsUpdateImplTest {
    private String channelName = "X.val1";
    private Object channelData = Integer.valueOf(1);

    @Test
    public void testGetChannelName() {
        String channelName = "X.val1";
        Object channelData = Integer.valueOf(1);
        EpicsUpdateImpl epicsUpdate = new EpicsUpdateImpl(channelName, channelData);
        assertEquals(channelName, epicsUpdate.getChannelName());
    }

    @Test
    public void testGetChannelData() {
        EpicsUpdateImpl epicsUpdate = new EpicsUpdateImpl(channelName, channelData);
        assertEquals(channelData, epicsUpdate.getChannelData());
    }

    @Test
    public void testEquality() {
        EpicsUpdateImpl a = new EpicsUpdateImpl(channelName, channelData);
        EpicsUpdateImpl b = new EpicsUpdateImpl(channelName, channelData);
        EpicsUpdateImpl c = new EpicsUpdateImpl("X.val2", channelData);
        EpicsUpdateImpl d = new EpicsUpdateImpl(channelName, channelData) {
        };

        new EqualsTester(a, b, c, d);
    }
}
