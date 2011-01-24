package edu.gemini.aspen.giapi.status.dispatcher.osgi;

import edu.gemini.aspen.giapi.status.beans.BaseStatusBean;
import edu.gemini.aspen.giapi.status.dispatcher.StatusDispatcher;
import edu.gemini.aspen.giapi.status.StatusHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.logging.Logger;

/**
 * Activator class for the Status Database bundle
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());
    private StatusDispatcher _dispatcher;

    private ServiceRegistration _handlerRegistration;

    private ServiceTracker _tracker = null;

    private BundleContext context = null;


    public void start(BundleContext bundleContext) throws Exception {

        context = bundleContext;

        //create the Status Dispatcher, a status Handler that
        //receives all status items. 
        _dispatcher = new StatusDispatcher();

        //advertise the handler into OSGi, so it start receiving stuff
        _handlerRegistration = bundleContext.registerService(
                StatusHandler.class.getName(),
                _dispatcher, null);


        _tracker = new ServiceTracker(bundleContext, BaseStatusBean.class.getName(), this);
        _tracker.open();




    }

    public void stop(BundleContext bundleContext) throws Exception {

        _dispatcher = null;

        _handlerRegistration.unregister();

        _tracker.close();
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        BaseStatusBean bean = (BaseStatusBean)context.getService(serviceReference);
        LOG.info("Registering new Bean " + bean.getClass().getName());
        _dispatcher.registerBean(bean);
        return bean;
    }

    @Override
    public void modifiedService(ServiceReference serviceReference, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}