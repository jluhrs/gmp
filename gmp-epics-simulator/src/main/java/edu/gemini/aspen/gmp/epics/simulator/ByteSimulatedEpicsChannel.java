package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;

import java.util.Arrays;

class ByteSimulatedEpicsChannel extends SimulatedEpicsChannel {
    private final byte[] values;

    public ByteSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        super(name, size, type, updateRate);
        values = new byte[size];
        random.nextBytes(values);
    }

    @Override
    public SimulatedEpicsChannel getNextSimulatedValue() {
        return new ByteSimulatedEpicsChannel(name, size, type, updateRate);
    }

    @Override
    public EpicsUpdate buildEpicsUpdate() {
        return new EpicsUpdateImpl(name, values);
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

        ByteSimulatedEpicsChannel that = (ByteSimulatedEpicsChannel) o;

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
}
