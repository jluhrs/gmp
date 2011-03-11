package edu.gemini.epics.impl;

import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsException;
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
import org.apache.felix.ipojo.annotations.Validate;

import java.util.Arrays;
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
    private Map<IEpicsClient, String[]> pendingClients = Maps.newConcurrentMap();

    protected EpicsServiceImpl() {
        _ctx = null;
    }

    public EpicsServiceImpl(Context context) {
        _ctx = context;
    }

    @Validate
    public void startService() {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.17.2.255");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        try {
            _ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            LOG.info("JCALibrary built to connect to addressList: " + addressList);
            for(Map.Entry<IEpicsClient, String[]> pendingClient: pendingClients.entrySet()) {
                bindClientToContext(pendingClient.getKey(), pendingClient.getValue());
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
    }

    @Bind
    public void bindEpicsClient(IEpicsClient epicsClient, Map<String, Object> serviceProperties) {
        LOG.info("Arrived Epics Client " + epicsClient + " and properties " + serviceProperties + ", binding it to the JCA Context");
        if (serviceHasValidProperties(serviceProperties)) {
            // This may be called before or after the startService method
            bindContextOrSave(epicsClient, serviceProperties);
        }
    }

    private boolean serviceHasValidProperties(Map<String, Object> serviceProperties) {
        return serviceProperties.containsKey(IEpicsClient.EPICS_CHANNELS) && serviceProperties.get(IEpicsClient.EPICS_CHANNELS) instanceof String[];
    }

    private void bindContextOrSave(IEpicsClient epicsClient, Map<String, Object> serviceProperties) {
        String[] channels = (String[]) serviceProperties.get(IEpicsClient.EPICS_CHANNELS);
        if (isContextReady()) {
            saveClientForBinding(epicsClient, channels);
        } else {
            bindClientToContext(epicsClient, channels);
        }
    }

    private void saveClientForBinding(IEpicsClient epicsClient, String[] channels) {
        LOG.info("Saving client " + epicsClient + " for binding channels " + Arrays.toString(channels));
        pendingClients.put(epicsClient, channels);
    }

    private void bindClientToContext(IEpicsClient epicsClient, String[] channels) {
        try {
            LOG.info("Binding client " + epicsClient + " to channels " + Arrays.toString(channels));
            ChannelBindingSupport cbs = new ChannelBindingSupport(_ctx, epicsClient);
            for (String channel : channels) {
                cbs.bindChannel(channel);
            }
            epicsClient.connected();
        } catch (EpicsException cae) {
            LOG.log(Level.SEVERE, "Could not connect to EPICS.", cae);
        }
    }

    private boolean isContextReady() {
        return _ctx == null;
    }

    @Override
    public Context getJCAContext() {
        if (isContextReady()) {
            throw new IllegalStateException("Illegal State, attempt to read the Context before it has been made available");
        }
        return _ctx;
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
}
