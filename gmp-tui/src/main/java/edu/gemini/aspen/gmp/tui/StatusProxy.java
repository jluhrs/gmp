package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.service.command.Descriptor;

/**
 * Adds a command to gogo to support reading the gmp properties
 */
@Component
@Instantiate
@Provides
public class StatusProxy implements GmpCommands {
    @ServiceProperty(name = "osgi.command.scope", value = "gmp")
    protected final String SCOPE = "gmp";
    @ServiceProperty(name = "osgi.command.function")
    protected final String[] FUNCTIONS = new String[]{"statusnames", "status"};
    private final StatusDatabaseService statusDatabase;

    public StatusProxy(@Requires StatusDatabaseService statusDatabase) {
        this.statusDatabase = statusDatabase;
    }

    @Descriptor("List the status items names known by gmp-server")
    public void statusnames() {
        for (String statusName: statusDatabase.getStatusNames()) {
            System.out.println(statusName);
        }
    }

    @Descriptor("List the status items names and values known by gmp-server")
    public void status() {
        for (StatusItem statusItem: statusDatabase.getAll()) {
            System.out.println(statusItem.getName() + " = " + statusItem.getValue().toString());
        }
    }

}