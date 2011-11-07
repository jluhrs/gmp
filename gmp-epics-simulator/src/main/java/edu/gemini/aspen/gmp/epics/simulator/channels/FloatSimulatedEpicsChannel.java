package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.List;

class FloatSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public FloatSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<Float> buildEpicsUpdate() {
        List<Float> values = new ArrayList<Float>();
        for (int i = 0; i < size; i++) {
            values.add(random.nextFloat());
        }
        return new EpicsUpdateImpl<Float>(name, values);
    }

}
