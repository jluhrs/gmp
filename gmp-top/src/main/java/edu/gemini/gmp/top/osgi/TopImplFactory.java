package edu.gemini.gmp.top.osgi;

import com.google.common.collect.Maps;
import edu.gemini.gmp.top.Top;
import edu.gemini.gmp.top.TopImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class TopImplFactory implements ManagedServiceFactory {
    private final static String EPICS_TOP = "epicsTop";
    private final static String GIAPI_TOP = "giapiTop";

    private static final Logger LOG = Logger.getLogger(Top.class.getName());

    private final Map<String, ServiceRegistration<Top>> existingServices = Maps.newHashMap();
    private final BundleContext context;

    public TopImplFactory(BundleContext context) {
        this.context = context;
    }

    public String getName() {
        return "Top factory";
    }

    public void updated(String pid, Dictionary<String, ?> properties) {
        if (checkProperties(properties)) {
            Top provider = createService(properties);
            ServiceRegistration<Top> serviceRegistration = context.registerService(Top.class, provider, new Hashtable<String, Object>());
            existingServices.put(pid, serviceRegistration);
        } else {
            LOG.warning("Cannot build top without the required properties");
        }
    }

    private TopImpl createService(Dictionary<String, ?> properties) {
        String epicsTop = properties.get(EPICS_TOP).toString();
        String giapiTop = properties.get(GIAPI_TOP).toString();
        LOG.info("Build GMP top with epicsTop: " + epicsTop + " and giapiTop: " + giapiTop);
        return new TopImpl(epicsTop, giapiTop);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(EPICS_TOP) != null && properties.get(GIAPI_TOP) != null;
    }

    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRegistration<Top> serviceRef = existingServices.get(pid);
            serviceRef.unregister();
            existingServices.remove(pid);
        }
    }

}
