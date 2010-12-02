package edu.gemini.cas;

/**
 * Interface ICas
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 2, 2010
 */
public interface ICas {

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Integer
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IChannel createIntegerChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Float
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IChannel createFloatChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Double
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IChannel createDoubleChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type String
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IChannel createStringChannel(String name, int length);

    /**
     * Removes channel from internal Map, unregisters from server and destroys PV
     *
     * @param name name of the Channel to remove
     */
    void destroyChannel(String name);
}
