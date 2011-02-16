package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

class ByteSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public ByteSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        byte[] values = new byte[size];
        random.nextBytes(values);
        return new EpicsUpdateImpl(name, values);
    }
}
