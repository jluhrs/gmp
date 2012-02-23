package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.gmp.services.PropertyHolder;
import edu.gemini.aspen.gmp.services.properties.GmpProperties;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.service.command.Descriptor;

/**
 * Adds a command to gogo to support reading the gmp properties
 */
@Component
@Instantiate
@Provides
public class PropertiesCommandProxy implements GmpCommands {
    @ServiceProperty(name = "osgi.command.scope", value = "gmp")
    private final String SCOPE = "gmp";
    @ServiceProperty(name = "osgi.command.function")
    private final String[] FUNCTIONS = new String[]{"properties"};
    private final PropertyHolder propertyHolder;

    public PropertiesCommandProxy(@Requires PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @Descriptor("Shows the properties exposed by gmp-server")
    public void properties() {
        for (GmpProperties val : GmpProperties.values()) {
            System.out.println(val.name() + " = " + propertyHolder.getProperty(val.name()));
        }
    }

}
