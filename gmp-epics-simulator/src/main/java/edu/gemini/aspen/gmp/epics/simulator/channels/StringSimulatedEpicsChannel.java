package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

class StringSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public StringSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        String[] values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = "Random text " + random.nextInt();
        }
        return new EpicsUpdateImpl(name, values);
    }
}
