package edu.gemini.aspen.gmp.epics.simulator;

import java.util.Arrays;

class FloatSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final float[] values;

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

        FloatSimulatedEpicsChannel that = (FloatSimulatedEpicsChannel) o;

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

    public FloatSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new float[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextFloat();
        }
    }

    @Override
    public SimulatedEpicsChannel getNextValue() {
        return new FloatSimulatedEpicsChannel(name, size, type, updateRate);
    }
}
