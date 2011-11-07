package edu.gemini.aspen.gmp.epics.simulator.channels;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.simulator.DataType;

import java.security.SecureRandom;
import java.util.Random;

/**
 * A very simple class to represent a simulated EPICS channel.
 * The description of a simulated channel includes its name, data type and
 * update rate.
 */
public abstract class SimulatedEpicsChannel {
    protected Random random = new SecureRandom();

    protected final String name;
    protected final int size;
    protected final DataType type;
    protected final long updateRate;

    public static SimulatedEpicsChannel buildSimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        SimulatedEpicsChannel result = null;
        switch (type) {
            case DOUBLE:
                result = new DoubleSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            case FLOAT:
                result = new FloatSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            case INT:
                result = new IntegerSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            case SHORT:
                result = new ShortSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            case STRING:
                result = new StringSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            case BYTE:
                result = new ByteSimulatedEpicsChannel(name, size, type, updateRate);
                break;
            default:
                throw new IllegalArgumentException("Cannot create simulated channel for datatype " + type);
        }
        return result;
    }

    /**
     * Constructor.
     *
     * @param name       Name of the epics channel to simulate
     * @param size       Number of elements this channel contain. All the elements
     *                   are of the same type
     * @param type       Type of the elements in the channel.
     * @param updateRate Update rate used to simulate this channel
     */
    public SimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.updateRate = updateRate;
    }

    /**
     * Return the update rate (in milliseconds) of this simulated channel.
     *
     * @return the update rate in milliseconds to be used when simulating
     *         this channel
     */
    public long getUpdateRate() {
        return updateRate;
    }

    /**
     * Returns the name of the simulated EPICS channel
     *
     * @return the name of the simulated EPICS channel
     */
    public String getName() {
        return name;
    }

    /**
     * Builds a new EpicsUpdate for this channel with simulated data
     * Every time this method is invoked a different EpicsUpdate is created
     *
     * @return a new simulated EpicsUpdate value
     */
    public abstract EpicsUpdate<?> buildEpicsUpdate();

    @Override
    public String toString() {
        return "SimulatedEpicsChannel{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", type=" + type +
                ", updateRate=" + updateRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof SimulatedEpicsChannel)) {
            return false;
        }
        SimulatedEpicsChannel that = (SimulatedEpicsChannel) o;
        if (updateRate != that.updateRate) {
            return false;
        }

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        //they are the same
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;//data != null ? data.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (int) (updateRate ^ (updateRate >>> 32));
        return result;
    }
}
