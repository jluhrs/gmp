package edu.gemini.util.telnetd.osgi;

import java.net.ServerSocket;
import java.util.Properties;

import net.wimpi.telnetd.TelnetD;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ungoverned.osgi.service.shell.ShellService;

import edu.gemini.util.telnetd.impl.OscarShell;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	private static final String PROP_PORT = "edu.gemini.util.telnetd.port";

	private BundleContext context;
	private TelnetD daemon;
	private ServiceTracker tracker;
	
	public void start(BundleContext context) throws Exception {

		this.context = context;
		
		// Load our default properties.
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/META-INF/telnetd.properties"));

		// Custom port.
		String port = context.getProperty(PROP_PORT);
		if (port != null)
			properties.setProperty("std.port", port);
		port = properties.getProperty("std.port").trim();
		
		// If port is zero, get an open one. This is lame, but I don't want to muck around
		// in the wimpi code.
		if (Integer.valueOf(port) == 0) {
			ServerSocket ss = new ServerSocket();
			ss.bind(null);
			properties.setProperty("std.port", Integer.toString(ss.getLocalPort()));
			ss.close();
		}
		
		// Start up. This only happens once.
		if (TelnetD.getReference() == null)
			TelnetD.createTelnetD(properties);
			
		daemon = TelnetD.getReference();
		daemon.start();
		
		// Set up a tracker.
		tracker = new ServiceTracker(context, ShellService.class.getName(), this);
		tracker.open();
		
	}

	public void stop(BundleContext context) throws Exception {

		// Done. Clean up.
		tracker.close();
		tracker = null;
		daemon.stop();
		daemon = null;
		this.context = null;
		
	}

	public Object addingService(ServiceReference ref) {
		ShellService ss = (ShellService) context.getService(ref);
		OscarShell.setShellService(ss);
		return ss;
	}

	public void modifiedService(ServiceReference ref, Object service) {
	}

	public void removedService(ServiceReference ref, Object service) {
		context.ungetService(ref);
		OscarShell.setShellService(null);
	}
	
}
