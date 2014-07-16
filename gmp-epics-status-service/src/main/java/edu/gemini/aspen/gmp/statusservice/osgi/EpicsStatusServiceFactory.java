package edu.gemini.aspen.gmp.statusservice.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.gmp.statusservice.EpicsStatusService;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.gmp.top.Top;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class EpicsStatusServiceFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceFactory.class.getName());
    private static final String EPICS_CONFIG_PROPERTY = "xmlFileName";

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final BundleContext context;
    private final ChannelAccessServer channelFactory;
    private final Top top;

    private class ServiceRef {
        private final ServiceRegistration<?> serviceRegistration;
        private final EpicsStatusService heartbeat;

        private ServiceRef(ServiceRegistration<?> serviceRegistration, EpicsStatusService heartbeat) {
            this.serviceRegistration = serviceRegistration;
            this.heartbeat = heartbeat;
        }
    }

    public EpicsStatusServiceFactory(BundleContext context, ChannelAccessServer channelFactory, Top top) {
        this.context = context;
        this.channelFactory = channelFactory;
        this.top = top;
    }

    public String getName() {
        return "EpicsStatusService factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            try {
                existingServices.get(pid).heartbeat.shutdown();
                existingServices.get(pid).heartbeat.initialize();
            } catch (SAXException e) {
                LOG.severe("Error updating the Epics Status Service");
            } catch (JAXBException e) {
                LOG.severe("Error updating the Epics Status Service");
            }
        } else {
            if (checkProperties(properties)) {
                EpicsStatusService heartbeat = createService(properties);
                try {
                    heartbeat.initialize();
                } catch (SAXException e) {
                    LOG.severe("Error starting up the Epics Status Service");
                } catch (JAXBException e) {
                    LOG.severe("Error starting up the Epics Status Service");
                }
                ServiceRegistration<?> registration = context.registerService(StatusHandler.class, heartbeat, new Hashtable<String, Object>());
                existingServices.put(pid, new ServiceRef(registration, heartbeat));

            } else {
                LOG.warning("Cannot build " + EpicsStatusService.class.getName() + " without the required properties");
            }
        }
    }

    private EpicsStatusService createService(Dictionary<String, ?> properties) {
        String epicsChannel = properties.get(EPICS_CONFIG_PROPERTY).toString();
        return new EpicsStatusService(channelFactory, top, epicsChannel);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(EPICS_CONFIG_PROPERTY) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRef reference = existingServices.get(pid);
            reference.serviceRegistration.unregister();
            reference.heartbeat.shutdown();
            existingServices.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }



}
