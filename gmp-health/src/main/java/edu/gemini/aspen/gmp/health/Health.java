package edu.gemini.aspen.gmp.health;

import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class Health evaluates the health of the GMP
 */
@Component
@Provides
public class Health {
    private static final Logger LOG = Logger.getLogger(Health.class.getName());
    private final IStatusSetter statusSetter;
    private final Top top;
    private final String healthStatusName;


    public Health(@Property(name = "healthName", value = "INVALID", mandatory = true) String healthStatusName,
            @Requires Top top, @Requires IStatusSetter statusSetter) {
        LOG.info("Health Constructor");
        this.top = top;
        this.statusSetter = statusSetter;
        this.healthStatusName = healthStatusName;
    }

    @Validate
    public void start() {
        try {
            statusSetter.setStatusItem(new HealthStatus(top.buildStatusItemName(healthStatusName), edu.gemini.aspen.giapi.status.Health.GOOD));
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, "Error setting up health", e);
        }
    }

}
