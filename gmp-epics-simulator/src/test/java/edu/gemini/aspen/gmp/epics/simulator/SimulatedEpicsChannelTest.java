package edu.gemini.aspen.gmp.epics.simulator;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimulatedEpicsChannelTest {
    private long updateRate;
    private String name;
    private int size;

    @Before
    public void setUp() throws Exception {
        updateRate = 10L;
        name = "channelName";
        size = 5;
    }

    @Test
    public void testGetUpdateRate() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.INT, updateRate);
        assertEquals(updateRate, channel.getUpdateRate());
    }

    @Test
    public void testGetName() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.BYTE, updateRate);
        assertEquals(name, channel.getName());
    }

    @Test
    public void testGetNextByteValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.BYTE, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    public void testGetNextIntValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.INT, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    public void testGetNextDoubleValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.DOUBLE, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    public void testGetNextFloatValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.FLOAT, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    public void testGetNextShortValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.SHORT, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    public void testGetNextStringValue() {
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        assertFalse(channel.equals(channel.getNextSimulatedValue()));
    }

    @Test
    @Ignore
    public void testEquality() {
        SimulatedEpicsChannel a = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        SimulatedEpicsChannel b = SimulatedEpicsChannel.buildSimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        SimulatedEpicsChannel c = SimulatedEpicsChannel.buildSimulatedEpicsChannel("anothername", size, DataType.STRING, updateRate);
        SimulatedEpicsChannel d = new StringSimulatedEpicsChannel(name, size, DataType.STRING, updateRate) {};

        new EqualsTester(a,b,c,d);
    }
}
