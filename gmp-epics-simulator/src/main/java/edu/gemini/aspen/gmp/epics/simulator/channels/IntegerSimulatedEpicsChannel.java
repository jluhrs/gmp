package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.List;

class IntegerSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public IntegerSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<Integer> buildEpicsUpdate() {
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            values.add(random.nextInt());
        }
        return new EpicsUpdateImpl<Integer>(name, values);
    }
}
