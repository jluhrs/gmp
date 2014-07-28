package edu.gemini.aspen.gmp.health.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.status.setter.StatusSetter;
import edu.gemini.aspen.gmp.health.BundlesDatabase;
import edu.gemini.aspen.gmp.health.Health;
import edu.gemini.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class HealthFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(HealthFactory.class.getName());
    private static final String STATUS_NAME = "healthName";

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final BundleContext context;
    private final Top top;
    private final StatusSetter ss;
    private final BundlesDatabase bundlesDatabase;

    private class ServiceRef {
        private final ServiceRegistration<JmsArtifact> registration;
        private final Health health;

        private ServiceRef(ServiceRegistration<JmsArtifact> registration, Health health) {
            this.registration = registration;
            this.health = health;
        }
    }

    public HealthFactory(BundleContext context, Top top, StatusSetter ss, BundlesDatabase bundlesDatabase) {
        this.context = context;
        this.top = top;
        this.ss = ss;
        this.bundlesDatabase = bundlesDatabase;
    }

    public String getName() {
        return "GMP Health factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).health.stopService();
            existingServices.get(pid).registration.unregister();
            existingServices.remove(pid);
            updated(pid, properties);
        } else {
            if (checkProperties(properties)) {
                Health health = createService(properties);
                ServiceRegistration<JmsArtifact> registration = context.registerService(JmsArtifact.class, health, new Hashtable<String, Object>());
                existingServices.put(pid, new ServiceRef(registration, health));
            } else {
                LOG.warning("Cannot build " + Health.class.getName() + " without the required properties");
            }
        }
    }

    private Health createService(Dictionary<String, ?> properties) {
        String configFile = properties.get(STATUS_NAME).toString();
        return new Health(configFile, top, ss, bundlesDatabase);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(STATUS_NAME) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).health.stopService();
            existingServices.get(pid).registration.unregister();
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }

}
