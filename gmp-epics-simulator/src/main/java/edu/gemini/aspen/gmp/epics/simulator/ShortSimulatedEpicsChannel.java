package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

class ShortSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public ShortSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        short[] values = new short[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = (short) random.nextInt();
        }
        return new EpicsUpdateImpl(name, values);
    }

}
