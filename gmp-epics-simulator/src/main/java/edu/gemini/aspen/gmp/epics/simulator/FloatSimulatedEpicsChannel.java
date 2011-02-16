package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

class FloatSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public FloatSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        float[] values = new float[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextFloat();
        }
        return new EpicsUpdateImpl(name, values);
    }

}
