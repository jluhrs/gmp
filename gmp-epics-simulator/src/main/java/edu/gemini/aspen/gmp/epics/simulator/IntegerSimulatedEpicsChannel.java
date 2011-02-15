package edu.gemini.aspen.gmp.epics.simulator;

import java.util.Arrays;

class IntegerSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final int[] values;

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
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    public IntegerSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new int[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
    }

    @Override
    public SimulatedEpicsChannel getNextValue() {
        return new IntegerSimulatedEpicsChannel(name, size, type, updateRate);
    }
}
