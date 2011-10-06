package edu.gemini.aspen.gmp.handlersstate;

import edu.gemini.aspen.giapi.commands.Configuration;

/**
 * Main exportable interface of the bundle
 * <br>
 * HandlersStateService defines a service that can indicate the availability
 * of handlers or consumers for a given configuration. It can be used to detect
 * whether a configuration is fully handled by message consumers or if some parts are
 * left unhandled
 *
 * @author cquiroz
 */
public interface HandlersStateService {
    /**
     * Indicates whether the given configuration is being handled by one ore more JMS client
     *
     * This will indicate if the full configuration has listeners able to respond in such a way that
     * one or more handles can
     *
     * @param path The {@link Configuration} being tested
     * @return <code>true</code> if there is a handler registered for the path,
     *         <code>false</code> otherwise
     */
    public boolean isConfigurationHandled(Configuration path);
}
