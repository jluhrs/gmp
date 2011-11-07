package edu.gemini.aspen.gmp.epics;

import com.gargoylesoftware.base.testing.EqualsTester;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EpicsUpdateImplTest {
    private String channelName = "X.val1";
    private List<Integer> channelData = ImmutableList.of(1);

    @Test
    public void testGetChannelName() {
        String channelName = "X.val1";
        List<Integer> channelData = ImmutableList.of(1);
        EpicsUpdateImpl<Integer> epicsUpdate = new EpicsUpdateImpl<Integer>(channelName, channelData);
        assertEquals(channelName, epicsUpdate.getChannelName());
    }

    @Test
    public void testGetChannelData() {
        EpicsUpdateImpl<Integer> epicsUpdate = new EpicsUpdateImpl<Integer>(channelName, channelData);
        assertEquals(channelData, epicsUpdate.getChannelData());
    }

    @Test
    public void testEquality() {
        EpicsUpdateImpl<Integer> a = new EpicsUpdateImpl<Integer>(channelName, channelData);
        EpicsUpdateImpl<Integer> b = new EpicsUpdateImpl<Integer>(channelName, channelData);
        EpicsUpdateImpl<Integer> c = new EpicsUpdateImpl<Integer>("X.val2", channelData);
        EpicsUpdateImpl<Integer> d = new EpicsUpdateImpl<Integer>(channelName, channelData) {
        };

        new EqualsTester(a, b, c, d);
    }
}
