package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.RebootArgument;

/**
 * Reboot Manager interface. This interfaces defines how to
 * Reboot the TLC in the instrument.
 */
public interface RebootManager {
    /**
     * Perform a reboot of the system based on the given argument
     * @param arg argument to define what system must be rebooted.
     */
    void reboot(RebootArgument arg);
}
