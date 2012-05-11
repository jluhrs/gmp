package edu.gemini.aspen.gmp.commands.handlers;

import java.util.List;

/**
 * Returns information about what command handlers have been registered
 */
public interface CommandHandlers {
    /**
     * Returns a list of the registered command handlers in terms of config path
     */
    List<String> getApplyHandlers();
}
