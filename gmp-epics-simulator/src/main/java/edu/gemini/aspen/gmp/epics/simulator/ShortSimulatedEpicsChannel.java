package edu.gemini.aspen.gmp.epics.simulator;

import java.util.Arrays;

class ShortSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final short[] values;

    public ShortSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new short[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = (short) random.nextInt();
        }
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

        ShortSimulatedEpicsChannel that = (ShortSimulatedEpicsChannel) o;

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

    @Override
    public SimulatedEpicsChannel getNextValue() {
        return new ShortSimulatedEpicsChannel(name, size, type, updateRate);

    }
}
