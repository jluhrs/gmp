package edu.gemini.cas.osgi;

import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.IChannelAccessServer;
import edu.gemini.cas.IChannelFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

/**
 * Class Activator. Activates the giapi-cas bundle.
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class Activator  implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());


    private ChannelAccessServer _channelAccessServer;

    private ServiceRegistration _registration;

    /**
     * Called when starting a bundle
     *
     * @param bundleContext  needed to register services
     * @throws Exception if something goes wrong when starting the bundle
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception{
        LOG.info("Starting giapi-cas");
        _channelAccessServer = new ChannelAccessServer();

        //advertise the cas into OSGi
        _registration = bundleContext.registerService(
                IChannelAccessServer.class.getName(),
                _channelAccessServer, null);
         _channelAccessServer.start();
    }

    /**
     * Called when stopping the bundle
     *
     * @param bundleContext 
     * @throws Exception if something goes wrong when stopping the bundle
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping giapi-cas");
        _channelAccessServer.stop();
        _channelAccessServer = null;

        _registration.unregister();
    }
}
