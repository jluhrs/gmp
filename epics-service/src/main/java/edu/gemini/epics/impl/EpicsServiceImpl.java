package edu.gemini.epics.impl;

import edu.gemini.epics.EpicsService;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

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
        } catch (CAException e) {
            LOG.log(Level.SEVERE, "Cannot start JCALibrary", e);
        }
    }

    @Override
    public Context getJCAContext() {
        if (_ctx == null) {
            throw new IllegalStateException("Illegal State, attempt to read the Context before it has been made available");
        }
        return _ctx;
    }

    public void stopService() {
        if (_ctx != null) {
            try {
                _ctx.destroy();
            } catch (CAException e) {
                LOG.log(Level.SEVERE, "Exception destroying the JCA Context", e);
            }
        }
    }
}
