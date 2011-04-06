package edu.gemini.epics;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Updated;
import org.apache.felix.ipojo.annotations.Validate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The EpicsService is an iPojo Component that has a reference to a JCAContext
 * that other services need to use to talk to JCA
 * It manages the life cycle of the context
 */
@Component(managedservice = "edu.gemini.epics.EpicsService", publicFactory = false)
@Provides
@Instantiate(name="epicsService")
public class EpicsService implements JCAContextController {
    private static final Logger LOG = Logger.getLogger(EpicsService.class.getName());
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String PROPERTY_ADDRESS_LIST = "addressList";

    private String _addressList;

    private Context _ctx;

    /**
     * For iPojo
     */
    public EpicsService(@Property(name = PROPERTY_ADDRESS_LIST, value = "127.0.0.1", mandatory = true) String addressList) {
        LOG.info("EpicsService created with " + addressList);
        validateAddressToConnect(addressList);
        this._addressList = addressList;
    }

    public EpicsService(Context context) {
        Preconditions.checkArgument(context != null, "JCAContext cannot be null");
        _ctx = context;
        this._addressList = null;
    }

    private void validateAddressToConnect(String addressList) {
        Preconditions.checkArgument(addressList != null, "Address to connect cannot be null");
        Preconditions.checkArgument(addressList.matches(IPADDRESS_PATTERN), "Address list should be an IP address: " + addressList);
    }

    @Override
    public Context getJCAContext() {
        if (!isContextAvailable()) {
            throw new IllegalStateException("Illegal State, attempt to read the Context before it has been made available");
        }
        return _ctx;
    }

    public boolean isContextAvailable() {
        return _ctx != null;
    }

    @Updated
    public void changedAddress(Dictionary<String, String> properties) {
        if (properties.get(PROPERTY_ADDRESS_LIST) != null) {
            this._addressList = properties.get(PROPERTY_ADDRESS_LIST);
            LOG.info("Address List changed, update JCA Context to " + this._addressList);
        }
    }

    /**
     * Called when the service is stopped, it informs the known clients and closes the context
     */
    @Invalidate
    public void stopService() {
        if (isContextAvailable()) {
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
        if (isContextAvailable()) {
            stopService();
        }
        LOG.info("EpicsService Validated, starting with:" + _ctx + " " + _addressList);
        validateAddressToConnect(_addressList);

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", _addressList);
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        try {
            _ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            _ctx.initialize();
            LOG.info("JCALibrary built connecting to: " + _addressList);

            logContextInfo();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
        LOG.info("Epics Service started on : " + _addressList);
    }

    private void logContextInfo() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bos);
        _ctx.printInfo(out);

        String ctxInfo = new String(bos.toByteArray());
        Iterable<String> ctxInfoLines = Splitter.on('\n').trimResults().split(ctxInfo);
        for (String line : ctxInfoLines) {
            if (!line.isEmpty()) {
                LOG.info("JCALibrary Info: " + line);
            }
        }
    }

}
