package edu.gemini.epics;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import edu.gemini.epics.impl.EpicsClientsHolder;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The EpicsService is an iPojo Component that has a reference to a JCAContext
 * and that lets EpicsClients to receive updates when an EPICS channel changes
 *
 * The Service can be configured using the ConfigAdmin service and it takes
 * a single parameter in the form of an IP address
 * 
 */
@Component
@Provides
public class EpicsService implements JCAContextController {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Property(name = "addressList", value = "127.0.0.1", mandatory = true)
    private String _addressList;

    @Property(name = "pid", value = "epicsService", mandatory = true)
    private String _pid = "epicsService";

    private Context _ctx;
    private final EpicsClientsHolder epicsClientsHolder = new EpicsClientsHolder();

    /**
     * For iPojo
     */
    protected EpicsService() {
    }

    public EpicsService(Context context, String addressList) {
        Preconditions.checkArgument(context != null, "JCAContext cannot be null");
        validateAddressToConnect(addressList);
        _ctx = context;
        this._addressList = addressList;
    }

    private void validateAddressToConnect(String addressList) {
        Preconditions.checkArgument(addressList != null, "Address to connect cannot be null");
        Preconditions.checkArgument(addressList.matches(IPADDRESS_PATTERN), "Address list should be an IP address: " + addressList);
    }

    public EpicsService(String addressList) {
        validateAddressToConnect(addressList);
        this._addressList = addressList;
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
        validateAddressToConnect(_addressList);

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", _addressList);
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        try {
            _ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            LOG.info("JCALibrary built connecting to: " + _addressList);

            logContextInfo();

            epicsClientsHolder.connectAllPendingClients(_ctx);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
    }

    private void logContextInfo() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bos);
        _ctx.printInfo(out);
        
        String ctxInfo = new String(bos.toByteArray());
        Iterable<String> ctxInfoLines = Splitter.on('\n').trimResults().split(ctxInfo);
        for(String line: ctxInfoLines) {
            LOG.info("JCALibrary Info: " + line);
        }
    }

    /**
     * Called when an EpicsClient appears. It will try to bind to the client right away if possible or it will
     * save it to be started later on
     * <p/>
     * The services properties of the epicsClient will determine which channels will listen to
     *
     * @param epicsClient       An OSGi service implementing EpicsClient that appears in the system
     * @param serviceProperties The properties of the service registration
     */
    @Bind(optional = true)
    public void bindEpicsClient(EpicsClient epicsClient, Map<String, Object> serviceProperties) {
        if (serviceHasValidProperties(serviceProperties)) {
            String[] channels = (String[]) serviceProperties.get(EpicsClient.EPICS_CHANNELS);
            if (isContextAvailable()) {
                epicsClientsHolder.connectNewClient(_ctx, epicsClient, channels);
            } else {
                // This may be called before or after the startService method
                epicsClientsHolder.saveClientForConnectionLater(epicsClient, channels);
            }
        }
    }

    private boolean serviceHasValidProperties(Map<String, Object> serviceProperties) {
        return serviceProperties.containsKey(EpicsClient.EPICS_CHANNELS) && serviceProperties.get(EpicsClient.EPICS_CHANNELS) instanceof String[];
    }

    /**
     * Called when an EpicsClient disappears. This method will unbind the context to the client
     *
     * @param epicsClient An OSGi service implementing EpicsClient that disappears
     */
    @Unbind
    public void unbindEpicsClient(EpicsClient epicsClient) {
        epicsClientsHolder.disconnectEpicsClient(epicsClient);
    }
}
