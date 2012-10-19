package edu.gemini.aspen.gmp.health;

import edu.gemini.aspen.gmp.top.Top;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;


/**
 * Class Health evaluates the health of the GMP
 */
@Component
@Provides
public class Health {
    private static final Logger LOG = Logger.getLogger(Health.class.getName());    //private final IStatusSetter heartbeatSetter;
    private final Top top;
    private final String heartbeatName;


    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;

    public Health(@Property(name = "healthName", value = "INVALID", mandatory = true) String heartbeatName,
            @Requires Top top) {
        LOG.info("Heartbeat Constructor");
        this.top = top;
        this.heartbeatName = heartbeatName;
    }

}
