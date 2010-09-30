package edu.gemini.aspen.giapi.cas.osgi;

import edu.gemini.aspen.giapi.cas.GiapiCas;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

/**
 * Class Activator
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class Activator  implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());


    private GiapiCas _cas;

    private ServiceRegistration _registration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Starting giapi-cas");
        _cas = new GiapiCas();

        //advertise the cas into OSGi
        _registration = bundleContext.registerService(
                GiapiCas.class.getName(),
                _cas, null);



    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping giapi-cas");
        _cas = null;

        _registration.unregister();
    }
}
