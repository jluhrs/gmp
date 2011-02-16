package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

import java.util.Arrays;

class IntegerSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final int[] values;

    public IntegerSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
    }

    @Override
    public SimulatedEpicsChannel getNextSimulatedValue() {
        return new IntegerSimulatedEpicsChannel(name, size, type, updateRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        IntegerSimulatedEpicsChannel that = (IntegerSimulatedEpicsChannel) o;

        if (!Arrays.equals(values, that.values)) {
            return false;
        }

        return true;
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        return new EpicsUpdateImpl(name, values);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
}
