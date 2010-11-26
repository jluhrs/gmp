package edu.gemini.aspen.giapi.cas.osgi;

import edu.gemini.aspen.giapi.cas.GiapiCas;
import gov.aps.jca.dbr.DBR_Int;
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


    private GiapiCas _cas;

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
        _cas = new GiapiCas();

        //advertise the cas into OSGi
        _registration = bundleContext.registerService(
                GiapiCas.class.getName(),
                _cas, null);
         _cas.start();

        //Test code!!!
        _cas.addVariable("test", DBR_Int.TYPE,new int[]{-1});

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
        _cas.stop();
        _cas = null;

        _registration.unregister();
    }
}
