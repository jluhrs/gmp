package edu.gemini.aspen.gmp.epics.simulator;

import java.util.Random;

/**
 * A very simple class to represent a simulated EPICS channel.
 * The description of a simulated channel includes its name, data type and
 * update rate.
 */
public class SimulatedEpicsChannel {

    private Object data;
    private String name;

    private Random ran = new Random();
    private DataType type;
    private long updateRate;

    /**
     * Constructor.
     * @param name Name of the epics channel to simulate
     * @param size Number of elements this channel contain. All the elements
     * are of the same type
     * @param type Type of the elements in the channel.
     * @param updateRate Update rate used to simulate this channel
     */
    public SimulatedEpicsChannel(String name, int size, DataType type, long updateRate) {

        this.name = name;
        this.type = type;
        this.updateRate = updateRate;

        switch (type) {

            case DOUBLE:
                data = new double[size];
                break;
            case FLOAT:
                data = new float[size];
                break;
            case INT:
                data = new int[size];
                break;
            case SHORT:
                data = new short[size];
                break;
            case STRING:
                data = new String[size];
                break;
            case BYTE:
                data = new byte[size];
                break;
        }


    }

    /**
     * Return the update rate (in milliseconds) of this simulated channel.
     * @return the update rate in milliseconds to be used when simulating
     * this channel
     */
    public long getUpdateRate() {
        return updateRate;
    }

    /**
     * Returns the name of the simulated EPICS channel
     * @return the name of the simulated EPICS channel
     */
    public String getName() {
        return name;
    }

    /**
     * Get an updated value of this channel. The value is an array
     * of primitives that depends on the data type defined when
     * this channel was constructed
     * @return a new randomly generated set of values for this
     * simulated channel.
     */
    public Object getNextValue() {

        switch (type) {

            case BYTE:
                byte[] b = (byte[])data;
                ran.nextBytes(b);
                break;
            case DOUBLE:
                double[] d = (double [])data;
                for (int i = 0; i < d.length; i++) {
                    d[i] = ran.nextDouble();
                }
                break;
            case FLOAT:
                float[] f = (float [])data;
                for (int i = 0; i < f.length; i++) {
                    f[i] = ran.nextFloat();
                }

                break;
            case INT:
                int[] ia = (int[])data;
                for (int i = 0; i < ia.length; i++) {
                    ia[i] = ran.nextInt();
                }

                break;
            case SHORT:
                short[] s = (short [])data;
                for (int i = 0; i < s.length; i++) {
                   s[i] = (short)ran.nextInt();
                }

                break;
            case STRING:
                break;
        }
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimulatedEpicsChannel that = (SimulatedEpicsChannel) o;

        if (updateRate != that.updateRate) return false;
        if (data != null ? !data.equals(that.data) : that.data != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (type != that.type) return false;
        //they are the same
        return true;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (int) (updateRate ^ (updateRate >>> 32));
        return result;
    }


}
