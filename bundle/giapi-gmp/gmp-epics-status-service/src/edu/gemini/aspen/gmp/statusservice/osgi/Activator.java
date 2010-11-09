package edu.gemini.aspen.gmp.statusservice.osgi;


import edu.gemini.aspen.giapi.cas.GiapiCas;
import edu.gemini.aspen.gmp.statusservice.EpicsStatusService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.io.File;
import java.util.logging.Logger;

/**
 * Class Activator
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class Activator  implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());


    private EpicsStatusService _epicsSS;

    private ServiceRegistration _registration;

    private GiapiCas _cas;

    private static final String confFileProperty= "gmp.epics.statusservice.conf";

    /**
     * Called when starting a bundle
     *
     * @param bundleContext  needed to register services
     * @throws Exception if something goes wrong when starting the bundle
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception{
        LOG.info("Starting gmp-epics-status-service");

        _cas = (GiapiCas)bundleContext.getService(bundleContext.getServiceReference(GiapiCas.class.getName()));

        _epicsSS= new EpicsStatusService(_cas);

        String confFileName = bundleContext.getProperty(confFileProperty);
        if (confFileName == null) {
            _epicsSS=null;
            throw new RuntimeException("Missing configuration: " + confFileProperty);
        }
        File confFile = new File(confFileName);
        if (!confFile.exists()) {
            throw new RuntimeException("Missing properties config file: " + confFileName);
        }
        EpicsStatusServiceConfiguration conf=  new EpicsStatusServiceConfiguration(bundleContext);
        for(EpicsStatusServiceConfiguration.StatusConfigItem item: conf.getSimulatedChannels()){
            _epicsSS.addVariable(item.giapiName, item.epicsName, item.type, item.initialValue);
        }

        //advertise the cas into OSGi
        _registration = bundleContext.registerService(
                EpicsStatusService.class.getName(),
                _epicsSS, null);
    }

    /**
     * Called when stopping the bundle
     *
     * @param bundleContext
     * @throws Exception if something goes wrong when stopping the bundle
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping gmp-epics-status-service");
        _epicsSS.dump();
        _epicsSS = null;

        _registration.unregister();
    }
}
