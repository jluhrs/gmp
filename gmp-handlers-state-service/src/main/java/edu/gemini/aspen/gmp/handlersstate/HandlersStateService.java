package edu.gemini.aspen.gmp.handlersstate;

import edu.gemini.aspen.giapi.commands.ConfigPath;

/**
 * Main exportable interface of the bundle
 *
 * HandlersStateService defines an interface for a service that can indicate the availability
 * of handlers or consumers for a given path. It can be used to detect whether a given
 * given config path is being handled by a message consumer in ActiveMQ
 *
 * @cquiroz
 */
public interface HandlersStateService {
    /**
     * Indicates whether the given path is being handled by a given JMS client
     *
     * @param path The {@link ConfigPath} being tested
     * @return <code>true</code> if there is a handler registered for the path,
     *         <code>false</code> otherwise
     */
    public boolean isPathHandled(ConfigPath path);
}
