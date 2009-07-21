package edu.gemini.aspen.gmp.commands.api;

/**
 * A class to represent and manipultate the REBOOT arguments in a
 * sequence command. 
 */
public enum RebootArgument {

    REBOOT,
    GMP,
    NONE;

    private static final ConfigPath REBOOT_OPT = new ConfigPath("REBOOT_OPT");


    /**
     * Return the Reboot Argument from a given configuration.
     * @param config the Reboot sequence command configuration.
     * @return the Reboot Argument contained in the configuration. If the
     * configuration is empty, the argument is assumed to be
     * NONE. If the configuration contains an invalid entry for the
     * Reboot argument, the result will be NULL.
     */
    public static RebootArgument parse(Configuration config) {

        if (config == null) return NONE;

        String value = config.getValue(REBOOT_OPT);

        if (value == null) return NONE;

        RebootArgument arg;
        try {
             arg = RebootArgument.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            arg = null;
        }

        return arg;
    }
}
