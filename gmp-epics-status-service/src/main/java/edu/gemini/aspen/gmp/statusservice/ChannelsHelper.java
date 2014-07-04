package edu.gemini.aspen.gmp.statusservice;

import edu.gemini.aspen.gmp.statusservice.generated.SimpleChannelType;

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
    static public Object getInitial(SimpleChannelType cf) {
        Object init = null;
        try {
            switch(cf.getType()){
                case INT:
                    init = Integer.parseInt(cf.getInitial());
                    break;
                case FLOAT:
                    init = Float.parseFloat(cf.getInitial());
                    break;
                case DOUBLE:
                    init = Double.parseDouble(cf.getInitial());
                    break;
                case STRING:
                    init = cf.getInitial();
                    break;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No valid value for simulated EPICS channel " + cf.getEpicsname(),e);
        }
        return init;
    }
}
