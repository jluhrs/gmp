package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.List;

class DoubleSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public DoubleSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<Double> buildEpicsUpdate() {
        List<Double> values = new ArrayList<Double>();
        for (int i = 0; i < size; i++) {
            values.add(random.nextDouble());
        }
        return new EpicsUpdateImpl<Double>(name, values);
    }

}
