package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.gmp.commands.handlers.CommandHandlers;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.service.command.Descriptor;

/**
 * Adds a command to gogo to support reading the gmp properties
 */
@Component
@Instantiate
@Provides
public class CommandHandlersProxy implements GmpCommands {
    @ServiceProperty(name = "osgi.command.scope", value = "gmp")
    protected final String SCOPE = "gmp";
    @ServiceProperty(name = "osgi.command.function")
    protected final String[] FUNCTIONS = new String[]{"applyhandlers"};
    private final CommandHandlers commandHandlers;

    public CommandHandlersProxy(@Requires CommandHandlers commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Descriptor("List the registered apply command handlers known by gmp-server")
    public void applyhandlers() {
        for (ConfigPath handlerPaths: commandHandlers.getApplyHandlers()) {
            System.out.println(handlerPaths);
        }
    }

}