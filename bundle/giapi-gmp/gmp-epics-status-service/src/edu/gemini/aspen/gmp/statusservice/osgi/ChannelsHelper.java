package edu.gemini.aspen.gmp.statusservice.osgi;

/**
 * Helper class to convert the initial value of a channel to the appropriate type.
 */
public class ChannelsHelper {

    /**
     * Gets the initial value of a given Channels.ChannelConfig in a correct type.
     *
     * @param cf ChannelConfig to get the value from
     * @return initial value of correct type(Float, Integer, Double or String)
     * @throws IllegalArgumentException if either the type is not one of the supported ones, or the value cannot be interpreted as the given type
     */
    static public Object getInitial(Channels.ChannelConfig cf) {
        Object init = null;
        try {
            if (cf.getType().equals("INT")) {
                init = Integer.parseInt(cf.getInitial());
            } else if (cf.getType().equals("FLOAT")) {
                init = Float.parseFloat(cf.getInitial());
            } else if (cf.getType().equals("DOUBLE")) {
                init = Double.parseDouble(cf.getInitial());
            } else if (cf.getType().equals("STRING")) {
                init = cf.getInitial();
            } else {
                throw new IllegalArgumentException("Unsupported data type " + cf.getType());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No valid value for simulated EPICS channel " + cf.getEpicsname());
        }
        return init;
    }
}
