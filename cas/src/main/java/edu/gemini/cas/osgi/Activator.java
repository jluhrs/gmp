package edu.gemini.cas.osgi;

import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ChannelAccessServerImpl channelAccessServer;
    private ServiceRegistration<ChannelAccessServer> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        channelAccessServer = new ChannelAccessServerImpl();
        channelAccessServer.start();
        registration = context.registerService(ChannelAccessServer.class, channelAccessServer, new Hashtable<String, Object>());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        if (channelAccessServer != null) {
            channelAccessServer.stop();
            channelAccessServer = null;
        }
    }
}
