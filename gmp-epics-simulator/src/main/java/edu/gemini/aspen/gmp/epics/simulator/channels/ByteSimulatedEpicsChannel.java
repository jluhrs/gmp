package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ByteSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public ByteSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<Byte> buildEpicsUpdate() {
        List<Byte> values = new ArrayList<Byte>();
        byte[] byteValues = new byte[size];
        random.nextBytes(byteValues);
        for (int i = 0; i < size; i++) {
            values.add(byteValues[i]);
        }
        return new EpicsUpdateImpl<Byte>(name, values);
    }
}
