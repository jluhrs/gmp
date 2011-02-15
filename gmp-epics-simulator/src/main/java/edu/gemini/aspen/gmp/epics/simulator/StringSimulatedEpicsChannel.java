package edu.gemini.aspen.gmp.epics.simulator;

import java.util.Arrays;

class StringSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final String[] values;

    public StringSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = "Random text " + random.nextInt();
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

        StringSimulatedEpicsChannel that = (StringSimulatedEpicsChannel) o;

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
        return new StringSimulatedEpicsChannel(name, size, type, updateRate);

    }
}
