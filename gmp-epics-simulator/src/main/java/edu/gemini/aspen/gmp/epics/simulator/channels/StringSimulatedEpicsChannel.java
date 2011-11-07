package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.List;

class StringSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public StringSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<String> buildEpicsUpdate() {
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            values.add("Random text " + random.nextInt());
        }
        return new EpicsUpdateImpl<String>(name, values);
    }
}
