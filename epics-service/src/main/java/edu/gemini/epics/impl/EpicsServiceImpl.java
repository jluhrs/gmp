package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsService;
import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The EpicsServiceImpl is an iPojo Component that has a reference to a JCAContext
 * and that lets EpicsClients to receive updates when an EPICS channel changes
 *
 */
@Component
@Provides
@Instantiate
public class EpicsServiceImpl implements EpicsService {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private String addressList = "edu.gemini.epics.addr_list";
    private String autoAddressList = "edu.gemini.epics.auto_addr_list";
    private Context _ctx;
    private final EpicsClientsHolder epicsClientsHolder = new EpicsClientsHolder();

    /**
     * For iPojo
     */
    protected EpicsServiceImpl() {
        _ctx = null;
    }

    public EpicsServiceImpl(Context context) {
        _ctx = context;
    }

    @Override
    public Context getJCAContext() {
        if (!isContextAvailable()) {
            throw new IllegalStateException("Illegal State, attempt to read the Context before it has been made available");
        }
        return _ctx;
    }

    private boolean isContextAvailable() {
        return _ctx != null;
    }

    /**
     * Called when the service is stopped, it informs the known clients and closes the context
     */
    @Invalidate
    public void stopService() {
        if (isContextAvailable()) {
            epicsClientsHolder.disconnectAllClients();
            try {
                _ctx.destroy();
                _ctx = null;
            } catch (CAException e) {
                LOG.log(Level.SEVERE, "Exception destroying the JCA Context", e);
            }
        }
    }

    /**
     * Starts the context and binds IEpicClients already found
     */
    @Validate
    public void startService() {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.17.2.255");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        try {
            _ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            LOG.info("JCALibrary built to connect to addressList: " + addressList);
            epicsClientsHolder.connectAllPendingClients(_ctx);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
    }

    /**
     * Called when an IEpicsClient appears. It will try to bind to the client right away if possible or it will
     * save it to be started later on
     *
     * The services properties of the epicsClient will determine which channels will listen to
     *
     * @param epicsClient An OSGi service implementing IEpicsClient that appears in the system
     * @param serviceProperties The properties of the service registration
     */
    @Bind
    public void bindEpicsClient(IEpicsClient epicsClient, Map<String, Object> serviceProperties) {
        if (serviceHasValidProperties(serviceProperties)) {
            String[] channels = (String[]) serviceProperties.get(IEpicsClient.EPICS_CHANNELS);
            if (isContextAvailable()) {
                epicsClientsHolder.connectNewClient(_ctx, epicsClient, channels);
            } else {
                // This may be called before or after the startService method
                epicsClientsHolder.saveClientForConnectionLater(epicsClient, channels);
            }
        }
    }

    private boolean serviceHasValidProperties(Map<String, Object> serviceProperties) {
        return serviceProperties.containsKey(IEpicsClient.EPICS_CHANNELS) && serviceProperties.get(IEpicsClient.EPICS_CHANNELS) instanceof String[];
    }

    /**
     * Called when an IEpicsClient disappears. This method will unbind the context to the client
     * 
     * @param epicsClient An OSGi service implementing IEpicsClient that disappears
     */
    @Unbind
    public void unbindEpicsClient(IEpicsClient epicsClient) {
        epicsClientsHolder.disconnectEpicsClient(epicsClient);
    }
}
