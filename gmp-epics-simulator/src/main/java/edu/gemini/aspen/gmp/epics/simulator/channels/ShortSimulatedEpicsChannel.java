package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.util.ArrayList;
import java.util.List;

class ShortSimulatedEpicsChannel extends SimulatedEpicsChannel {
    public ShortSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate<Short> buildEpicsUpdate() {
        List<Short> values = new ArrayList<Short>();
        for (int i = 0; i < size; i++) {
            values.add((short) random.nextInt());
        }
        return new EpicsUpdateImpl<Short>(name, values);
    }

}
