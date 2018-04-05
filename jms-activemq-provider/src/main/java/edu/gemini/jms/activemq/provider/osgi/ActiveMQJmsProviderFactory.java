package edu.gemini.jms.activemq.provider.osgi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ActiveMQJmsProviderFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(ActiveMQJmsProviderFactory.class.getName());

    private static final class ServiceRef {
        final ServiceRegistration<JmsProvider> serviceRegistration;
        final ActiveMQJmsProvider provider;

        private ServiceRef(ServiceRegistration<JmsProvider> serviceRegistration, ActiveMQJmsProvider provider) {
            this.serviceRegistration = serviceRegistration;
            this.provider = provider;
        }
    }

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final List<JmsArtifact> existingArtifacts = Lists.newArrayList();
    private final BundleContext context;
    private static final String BROKER_URL_PROPERTY = "brokerUrl";
    private static final String CLOSE_TIMEOUT_PROPERTY = "closeTimeout";

    ActiveMQJmsProviderFactory(BundleContext context) {
        this.context = context;
    }

    public String getName() {
        return "JmsProvider factory";
    }

    public void updated(String pid, Dictionary<String, ?> properties) {
        if (checkProperties(properties)) {
            ActiveMQJmsProvider provider = createService(properties);
            ServiceRegistration<JmsProvider> serviceRegistration = context.registerService(JmsProvider.class, provider, new Hashtable<>());
            provider.startConnection();
            existingServices.put(pid, new ServiceRef(serviceRegistration, provider));
            notifyJmsArtifactArrival();
        } else {
            LOG.warning("Cannot build " + ActiveMQJmsProvider.class.getName() + " without the " + BROKER_URL_PROPERTY + " property");
        }
    }

    private ActiveMQJmsProvider createService(Dictionary<String, ?> properties) {
        String url = properties.get(BROKER_URL_PROPERTY).toString();
        Object closeTimeout = properties.get(CLOSE_TIMEOUT_PROPERTY);
        if (closeTimeout != null) {
            try {
                LOG.info("Build " + ActiveMQJmsProvider.class.getName() + " with url " + url + " property and timeout " + closeTimeout);
                return new ActiveMQJmsProvider(url, Integer.parseInt(closeTimeout.toString()));
            } catch (NumberFormatException e) {
                return new ActiveMQJmsProvider(url);
            }
        } else {
            LOG.info("Build " + ActiveMQJmsProvider.class.getName() + " with url " + url + " property");
            return new ActiveMQJmsProvider(url);
        }
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(BROKER_URL_PROPERTY) != null;
    }

    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRef serviceRef = existingServices.get(pid);
            serviceRef.serviceRegistration.unregister();
            serviceRef.provider.stopConnection();
            existingServices.remove(pid);
        }
    }

    public void bindJmsArtifact(JmsArtifact jmsArtifact) {
        existingArtifacts.add(jmsArtifact);
        notifyJmsArtifactArrival(jmsArtifact);
    }

    public void unbindJmsArtifact(JmsArtifact jmsArtifact) {
        if (existingArtifacts.contains(jmsArtifact)) {
            existingArtifacts.remove(jmsArtifact);
            for (ServiceRef ref: existingServices.values()) {
                ref.provider.unbindJmsArtifact(jmsArtifact);
            }
        }
    }

    private void notifyJmsArtifactArrival() {
        for (ServiceRef ref: existingServices.values()) {
            for (JmsArtifact a: existingArtifacts) {
                ref.provider.bindJmsArtifact(a);
            }
        }
    }

    private void notifyJmsArtifactArrival(JmsArtifact a) {
        for (ServiceRef ref: existingServices.values()) {
            ref.provider.bindJmsArtifact(a);
        }
    }
}
