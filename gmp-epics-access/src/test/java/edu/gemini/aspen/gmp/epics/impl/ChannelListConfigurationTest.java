package edu.gemini.aspen.gmp.epics.impl;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelListConfigurationTest {
    @Test
    public void testParsingChannelList() {
        String fileName = getClass().getResource("epics-channels.xml").getFile();
        ChannelListConfiguration channelList = new ChannelListConfiguration(fileName);
        assertEquals(ImmutableSet.of("ws:wsFilter.VALL", "ws:cpWf"), channelList.getValidChannelsNames());
    }
}
