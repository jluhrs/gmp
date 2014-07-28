package edu.gemini.aspen.gmp.heartbeat.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.status.setter.StatusSetter;
import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
import edu.gemini.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class HeartbeatFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(HeartbeatFactory.class.getName());
    private static final String STATUS_NAME = "heartbeatName";
    private static final String SEND_JMS = "sendJms";

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final BundleContext context;
    private final Top top;
    private final StatusSetter ss;

    private class ServiceRef {
        private final ServiceRegistration<JmsArtifact> registration;
        private final Heartbeat heartbeat;

        private ServiceRef(ServiceRegistration<JmsArtifact> registration, Heartbeat heartbeat) {
            this.registration = registration;
            this.heartbeat = heartbeat;
        }
    }

    public HeartbeatFactory(BundleContext context, Top top, StatusSetter ss) {
        this.context = context;
        this.top = top;
        this.ss = ss;
    }

    public String getName() {
        return "GMP Heartbeat factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).heartbeat.stopService();
            existingServices.get(pid).registration.unregister();
            existingServices.remove(pid);
            updated(pid, properties);
        } else {
            if (checkProperties(properties)) {
                Heartbeat health = createService(properties);
                ServiceRegistration<JmsArtifact> registration = context.registerService(JmsArtifact.class, health, new Hashtable<String, Object>());
                existingServices.put(pid, new ServiceRef(registration, health));
            } else {
                LOG.warning("Cannot build " + Heartbeat .class.getName() + " without the required properties");
            }
        }
    }

    private Heartbeat createService(Dictionary<String, ?> properties) {
        String configFile = properties.get(STATUS_NAME).toString();
        Boolean sendJms = Boolean.parseBoolean(properties.get(SEND_JMS).toString());
        return new Heartbeat(configFile, sendJms, top, ss);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(STATUS_NAME) != null && properties.get(SEND_JMS) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).heartbeat.stopService();
            existingServices.get(pid).registration.unregister();
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }

}
