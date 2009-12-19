package edu.gemini.epics.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import edu.gemini.epics.IEpicsClient;
import edu.gemini.epics.IEpicsWriter;
import edu.gemini.epics.IEpicsReader;
import edu.gemini.epics.impl.ChannelBindingSupport;
import edu.gemini.epics.impl.EpicsWriter;
import edu.gemini.epics.impl.EpicsReader;
import gov.aps.jca.CAException;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	static {
		System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.16.2.22");
		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");		
	}
	
	private static final Logger LOGGER = Logger.getLogger(Activator.class.getName());
	private ServiceTracker tracker = null;
	private BundleContext context = null;

    private ServiceRegistration _writterRegistration;
    private ServiceRegistration _readerRegistration;


    public void start(BundleContext context) throws Exception {
		this.context = context;
		tracker = new ServiceTracker(context, IEpicsClient.class.getName(), this);
		tracker.open();
        IEpicsWriter epicsWriter = new EpicsWriter();
        //Register the EpicsWriter service
        _writterRegistration = context.registerService(
                IEpicsWriter.class.getName(),
                epicsWriter, null);
        //Register the EpicsReader service
        IEpicsReader epicsReader = new EpicsReader();
        _readerRegistration = context.registerService(
                IEpicsReader.class.getName(),
                epicsReader, null
        );
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
		tracker = null;
		this.context = null;
        _writterRegistration.unregister();
        _readerRegistration.unregister();
		ChannelBindingSupport.destroy();
        EpicsWriter.destroy();
        EpicsReader.destroy();
	}

	public Object addingService(ServiceReference ref) {

	
		IEpicsClient client = (IEpicsClient) context.getService(ref);		
		LOGGER.info("IEpicsClient added: " + client);

		try {
		
			ChannelBindingSupport cbs = new ChannelBindingSupport(client);
			String[] channels = (String[]) ref.getProperty(IEpicsClient.EPICS_CHANNELS);
            for (String channel : channels) {
                cbs.bindChannel(channel);
            }
			client.connected();
			return cbs;
			
		} catch (CAException cae) {
			LOGGER.log(Level.SEVERE, "Could not connect to EPICS.", cae);
			return null;
		}
		
	}

	public void modifiedService(ServiceReference ref, Object obj) {
		// TODO: rebind channels
	}

	public void removedService(ServiceReference ref, Object obj) {

		IEpicsClient client = (IEpicsClient) context.getService(ref);		
		LOGGER.info("IEpicsClient removed: " + client);

		ChannelBindingSupport cbs = (ChannelBindingSupport) obj;		
		try {
			cbs.close();
			client.disconnected();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not close channel binder.", e);
		}		
		
	}


}
