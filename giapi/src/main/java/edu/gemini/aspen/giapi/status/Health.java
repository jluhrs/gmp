package edu.gemini.aspen.giapi.status;

/**
 * Definition of the Health values
 */
public enum Health {
    /**
     * Good health. The system/suybsystem is normal
     */
    GOOD,
    /**
     * Warning health. The system/subsystem is operating, but
     * not normally.
     */
    WARNING,
    /**
     * Bad health. The system/subsystem is not operating.
	 */
	BAD;

    public static final Health DEFAULT = GOOD;
}
