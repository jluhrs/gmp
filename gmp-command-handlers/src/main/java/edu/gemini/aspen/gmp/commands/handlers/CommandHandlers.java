package edu.gemini.aspen.gmp.commands.handlers;

import edu.gemini.aspen.giapi.commands.ConfigPath;

import java.util.List;

/**
 * Returns information about what command handlers have been registered
 */
public interface CommandHandlers {
    /**
     * Returns a list of the registered command handlers in terms of config path
     */
    List<ConfigPath> getApplyHandlers();
}
