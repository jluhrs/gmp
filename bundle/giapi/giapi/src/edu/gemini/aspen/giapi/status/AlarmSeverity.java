package edu.gemini.aspen.giapi.status;

/**
 * The Severity of the alarms
 */
public enum AlarmSeverity {
    /**
     * No alarm.
     */
    ALARM_OK,
    /**
     * A WARNING is an alarm that subsystems see as important and should
     * be brought to the attention of the users. The subsystems should
     * be able to continue despite warning events.
     */
    ALARM_WARNING,
    /**
     * A FAILURE indicates that a component is in a state that will not
     * allow to continue operations.
     */
    ALARM_FAILURE;

    /**
     * Default severity
     */
    public static final AlarmSeverity DEFAULT = ALARM_OK;
}
