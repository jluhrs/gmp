package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

class IntegerSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public IntegerSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        int[] values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
        return new EpicsUpdateImpl(name, values);
    }
}
