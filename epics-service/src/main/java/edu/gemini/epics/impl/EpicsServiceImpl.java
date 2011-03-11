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
 * The EpicsServiceImpl is an iPojo Component that can be configured to connect to an
 * Epics Database
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

    protected EpicsServiceImpl() {
        _ctx = null;
    }

    public EpicsServiceImpl(Context context) {
        _ctx = context;
    }

    @Override
    public Context getJCAContext() {
        if (!isContextReady()) {
            throw new IllegalStateException("Illegal State, attempt to read the Context before it has been made available");
        }
        return _ctx;
    }

    private boolean isContextReady() {
        return _ctx != null;
    }

    @Invalidate
    public void stopService() {
        if (_ctx != null) {
            try {
                _ctx.destroy();
                _ctx = null;
            } catch (CAException e) {
                LOG.log(Level.SEVERE, "Exception destroying the JCA Context", e);
            }
        }
    }

    @Validate
    public void startService() {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.17.2.255");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        try {
            _ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            LOG.info("JCALibrary built to connect to addressList: " + addressList);
            epicsClientsHolder.bindPendingClients(_ctx);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
    }

    @Bind
    public void bindEpicsClient(IEpicsClient epicsClient, Map<String, Object> serviceProperties) {
        if (serviceHasValidProperties(serviceProperties)) {
            String[] channels = (String[]) serviceProperties.get(IEpicsClient.EPICS_CHANNELS);
            if (isContextReady()) {
                epicsClientsHolder.bindClientToContext(_ctx, epicsClient, channels);
            } else {
                // This may be called before or after the startService method
                epicsClientsHolder.saveClientForLateBinding(epicsClient, channels);
            }
        }
    }

    private boolean serviceHasValidProperties(Map<String, Object> serviceProperties) {
        return serviceProperties.containsKey(IEpicsClient.EPICS_CHANNELS) && serviceProperties.get(IEpicsClient.EPICS_CHANNELS) instanceof String[];
    }

    @Unbind
    public void unbindEpicsClient(IEpicsClient epicsClient) {
        epicsClientsHolder.unbindEpicsClient(epicsClient);
    }
}
