package edu.gemini.aspen.giapi.statusservice.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class StatusServiceFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(StatusServiceFactory.class.getName());

    private final Map<String, ServiceRegistration<StatusService>> existingServices = Maps.newHashMap();
    private final StatusHandlerAggregate aggregate;
    private final BundleContext context;
    public static final String SERVICE_NAME = "serviceName";
    private static final String STATUS_FILTER = "statusName";

    public StatusServiceFactory(StatusHandlerAggregate aggregate, BundleContext context) {
        this.aggregate = aggregate;
        this.context = context;
    }

    public String getName() {
        return "StatusService factory";
    }

    public void updated(String pid, Dictionary<String, ?> properties) {
        if (checkProperties(properties)) {
            StatusService provider = createService(properties);
            ServiceRegistration<StatusService> serviceRegistration = context.registerService(StatusService.class, provider, new Hashtable<String, Object>());
            existingServices.put(pid, serviceRegistration);
        } else {
            LOG.warning("Cannot build " + StatusService.class.getName() + " without the " + SERVICE_NAME + " and " + STATUS_FILTER + " properties");
        }
    }

    private StatusService createService(Dictionary<String, ?> properties) {
        String serviceName = properties.get(SERVICE_NAME).toString();
        String statusName = properties.get(STATUS_FILTER).toString();
        LOG.info("Build " + StatusService.class.getName());
        return new StatusService(aggregate, serviceName, statusName);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(SERVICE_NAME) != null && properties.get(STATUS_FILTER) != null;
    }

    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRegistration<StatusService> serviceRef = existingServices.get(pid);
            serviceRef.unregister();
            existingServices.remove(pid);
        }
    }

}
