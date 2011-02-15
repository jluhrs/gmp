package edu.gemini.aspen.gmp.epics.simulator;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Before;
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
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.INT, updateRate);
        assertEquals(updateRate, channel.getUpdateRate());
    }

    @Test
    public void testGetName() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.BYTE, updateRate);
        assertEquals(name, channel.getName());
    }

    @Test
    public void testGetNextByteValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.BYTE, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof byte[]);
        assertEquals(size, ((byte[])channel.getNextValue()).length);
    }

    @Test
    public void testGetNextIntValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.INT, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof int[]);
        assertEquals(size, ((int[])channel.getNextValue()).length);
    }

    @Test
    public void testGetNextDoubleValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.DOUBLE, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof double[]);
        assertEquals(size, ((double[])channel.getNextValue()).length);
    }

    @Test
    public void testGetNextFloatValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.FLOAT, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof float[]);
        assertEquals(size, ((float[])channel.getNextValue()).length);
    }

    @Test
    public void testGetNextShortValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.SHORT, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof short[]);
        assertEquals(size, ((short[])channel.getNextValue()).length);
    }

    @Test
    public void testGetNextStringValue() {
        SimulatedEpicsChannel channel = new SimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        assertNotNull(name, channel.getNextValue());
        assertTrue(channel.getNextValue() instanceof String[]);
        assertEquals(size, ((String[])channel.getNextValue()).length);
    }

    @Test
    public void testEquality() {
        SimulatedEpicsChannel a = new SimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        SimulatedEpicsChannel b = new SimulatedEpicsChannel(name, size, DataType.STRING, updateRate);
        SimulatedEpicsChannel c = new SimulatedEpicsChannel("anothername", size, DataType.STRING, updateRate);
        SimulatedEpicsChannel d = new SimulatedEpicsChannel(name, size, DataType.STRING, updateRate) {};

        new EqualsTester(a,b,c,d);
    }
}
