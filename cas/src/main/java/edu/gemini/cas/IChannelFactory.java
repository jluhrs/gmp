package edu.gemini.cas;

/**
 * Interface IChannelFactory
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 2, 2010
 */
public interface IChannelFactory {

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
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Integer,
     * that is able to raise an alarm.
     *
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IAlarmChannel createIntegerAlarmChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Float,
     * that is able to raise an alarm.
     *
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IAlarmChannel createFloatAlarmChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Double,
     * that is able to raise an alarm.
     *
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IAlarmChannel createDoubleAlarmChannel(String name, int length);

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type String,
     * that is able to raise an alarm.
     *
     *
     * @param name name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     *
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    IAlarmChannel createStringAlarmChannel(String name, int length);

}
