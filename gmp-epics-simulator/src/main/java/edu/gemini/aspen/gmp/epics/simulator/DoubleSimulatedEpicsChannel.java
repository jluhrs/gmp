package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

class DoubleSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public DoubleSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        double[] values = new double[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextDouble();
        }
        return new EpicsUpdateImpl(name, values);
    }

}
