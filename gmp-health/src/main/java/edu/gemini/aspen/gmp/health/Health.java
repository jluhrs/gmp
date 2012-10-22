package edu.gemini.aspen.gmp.health;

import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private final BundlesDatabase bundlesDatabase;

    private final ScheduledThreadPoolExecutor executor;
    private final ScheduledFuture future;
    private final HealthChecker checker = new HealthChecker();

    private edu.gemini.aspen.giapi.status.Health health = edu.gemini.aspen.giapi.status.Health.WARNING;

    public Health(@Property(name = "healthName", value = "INVALID", mandatory = true) String healthStatusName,
            @Requires Top top, @Requires IStatusSetter statusSetter, @Requires BundlesDatabase bundlesDatabase) {
        LOG.info("Health Constructor");
        this.top = top;
        this.statusSetter = statusSetter;
        this.healthStatusName = healthStatusName;
        this.bundlesDatabase = bundlesDatabase;
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        future = executor.scheduleAtFixedRate(checker, 0, 1000, TimeUnit.MILLISECONDS);

    }

    @Validate
    public void start() {
        setupHealthValue();
    }

    private void setupHealthValue() {
        try {
            edu.gemini.aspen.giapi.status.Health health = edu.gemini.aspen.giapi.status.Health.GOOD;
            if (bundlesDatabase.getPercentageActive().get() < 1.0) {
                health = edu.gemini.aspen.giapi.status.Health.WARNING;
            }
            if (!this.health.equals(health)) {
                this.health = health;
                statusSetter.setStatusItem(new HealthStatus(top.buildStatusItemName(healthStatusName), health));
            }
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, "Error setting up health", e);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("Health InValidate");
        future.cancel(false);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private class HealthChecker implements Runnable {

        @Override
        public void run() {
            setupHealthValue();
        }
    }
}
